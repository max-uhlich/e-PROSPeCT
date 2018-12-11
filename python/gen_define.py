import csv
import re
import os
from collections import OrderedDict
from helpers import printProgress, parse_category, classify, test_gleason_exception, settle, write_boilerplate

def start(dcut_fn, book_fn, roll_fn, tables_fn, link_issue, schema, rdir, ldir, owner, privileges):

	with open(dcut_fn, 'rb') as dcut, open(book_fn, 'rb') as book, open(roll_fn, 'rb') as roll, open(tables_fn, 'rb') as tbls, open(os.path.join(ldir, 'fyi_define.txt'), 'wb') as fyi, open(os.path.join(rdir, 'psql_define_%s.txt' % schema), 'wb') as out:

		# FYI notes
		gleason_exceptn = []
		imbalanced_cats = []
		field_not_exist	= []
		field_empty_col	= []
		catfull_expnson = []
		catless_expnson = []
		suffix_mismatch = []
		dtype_resettled = []
		different_dtype = []
		different_categ = []
		different_suffx = []

		# This is the structure of the database db_struc
		# All fields in this structure will have been vetted, and all their values settled with any links
		# The lookups will be guaranteed to exist in the datacut
		#	[table_sql]:
		#			[0]: English name 			'Medications and Supplements'
		#			[1]: SQL fields 			['internal_id', 									'up_date', 			'up_000', 			... ]
		#			[2]: Datatypes				['int8', 											'date', 			'varchar(255)', 	... ]
		#			[3]: English names 			['Internal Reference'								'Date Last Up',		'Date Entry Up',	... ]
		#			[4]: Category strings		[[['0', '1', '-77'], ['No', 'Yes', 'Missing']],		[[],[]],			[[],[]],			... ]
		#			[5]: Suffixes 				[[__1,__2,___77],									[],					[],					... ]
		#			[6]: Lookups				[[internal_id, i_006, ISSUE, ps_010, ps_012]		[],					[],					... ]

		# This is the structure of an individual field contained in field_struc
		#	[field_sql]:
		#			Booleans:									Values:
		#				[0]: Exists?								[5]: English name 		''			ie 'Internal Reference'
		#				[1]: Nonempty?								[6]: Data type 			''			ie 'int8'
		#				[2]: Categories exist?						[7]: Category string 	[[],[]]		ie [['0', '1', '-77'], ['No', 'Yes', 'Missing']]
		#				[3]: Expansion?								[8]: Suffixes 			[]			ie [__1,__2,___77]
		#				[4]: Categories match suffixes?				[9]: Lookups 			[]			ie [ps_004, ps_006, ps_008, ISSUE, ps_012]

		# A note on the lookups:
		#	Each sql field f will have a list of lookups associated with it. If f is not a head with rollover links, its lookups will just be [f].
		#	If f is a head with 5 links, its lookups will be [f, l1, l2, l3, l4, l5]
		#	f will be included in db_struc if and only if at least one of f and any links is extant and nonempty in the datacut
		#	Lets say only link 2 is extant and nonempty, we will then be sending gen_populate the following lookups for f: [ISSUE,ISSUE,l2,ISSUE,ISSUE,ISSUE]
		#	gen_populate will not bother checking any ISSUE link, it will simply assign it a null value.

		db_struc = OrderedDict()
		field_struc = {}
		tbl_col = {} 			# Maps a table name to a list of its fields
		repetitious_tbls = {}	# Maps a table name in var_rollover.csv to its list of heads

		# Read in all the selected tables
		tbls_reader = csv.reader(tbls, delimiter=',')

		tbls = OrderedDict()
		for row in tbls_reader:
			tbls[row[0]] = row[1]

		# Read in the datacut
		dcut_reader = csv.reader(dcut, delimiter=',')
		dcut_header = dcut_reader.next()
		dcut_all = list(dcut_reader)
		dcut_len = len(dcut_all)

		# Read in relevant columns from the codebook
		book_reader = csv.reader(book, delimiter=',')
		book_header = book_reader.next()
		tbl_index = book_header.index('Form Name')
		fld_index = book_header.index('Variable / Field Name')
		eng_index = book_header.index('Field Label')
		cat_index = book_header.index('Choices, Calculations, OR Slider Labels')

		it = 0
		it_tot = len(tbls.keys())

		for row in book_reader:
			# We only need to look at tables which we have selected for use in tables.csv
			if row[tbl_index] in tbls.keys():
				
				f = row[fld_index]

				# Create the structure of this field
				field_struc[f] = [False, False, False, False, False, '', '', [[],[]], [], []]

				if row[tbl_index] not in tbl_col.keys():
					it += 1
					printProgress(it, it_tot, prefix = 'Progress:', suffix = 'Complete', barLength = 30)
					tbl_col[row[tbl_index]] = []
				tbl_col[row[tbl_index]].append(f)
				
				if row[cat_index] != '':
					parsed = parse_category(row[cat_index])
					if len(parsed[0]) == len(parsed[1]):
						# Here we must filter out category strings like (1, 1 | 2, 2 | 3, 3 | 4, 4 | 5, 5 | -77, Missing)
						# These fields must be classified based on their values, not their categories
						# Therefore we will not add these categories
						# This currently (May 15th 2017) affects {af_011, af_012, ib_010, ib_011}, all Gleason scores, which we want to be integers
						# Without this check, they would be later classified as string because of the 'Missing' category
						if not test_gleason_exception(parsed[1]):
							field_struc[f][2] = True
							field_struc[f][7] = parsed
						else:
							# We will record the field if it satisfies this exception.
							gleason_exceptn.append('%s: %s' % (f,row[cat_index]))
					else:
						# This category string is imbalanced. Record it
						imbalanced_cats.append('%s: %s' % (f,row[cat_index]))
				
				if row[eng_index] != '':
					field_struc[f][5] = row[eng_index]
				else:
					field_struc[f][5] = f
				
				# Now we find out if this field exists and if its nonempty
				# If its a binary expansion, we check its suffixes and categories
				found = [dcut_header[i] for i,v in enumerate(dcut_header) if re.search('^%s_.*|^%s$' % (f,f),v)]
				# Shall we check ^%s_.* and ^%s$ separately?

				vals = set()
				if len(found) < 1:
					# This field does not exist in the datacut
					field_struc[f][9] = [link_issue]
					field_not_exist.append(f)
				else:
					# The field exists in the datacut
					field_struc[f][0] = True

					# Now check to make sure its datacut columns are not empty
					indices = [dcut_header.index(x) for x in found]
					for row in dcut_all:
						vals.update([row[x].strip() for x in indices])

					if '' in vals:
						vals.remove('')

					if len(vals) < 1:
						# The field exists but is empty, record it
						field_struc[f][9] = [link_issue]
						field_empty_col.append(f)
					else:
						# The field exists and is not empty
						field_struc[f][1] = True

						if len(found) == 1:
							if field_struc[f][2]:
								# This is a normal field with categories
								# Classify it based on its categories
								field_struc[f][6] = classify(field_struc[f][7][1])
							else:
								# This is a normal field without categories
								# Classify it based on its column in the datacut
								field_struc[f][6] = classify(list(vals))
							field_struc[f][9] = [f]
						else:
							# This is a binary expansion
							field_struc[f][3] = True
							field_struc[f][8] = [item.replace(f,'') for item in found]

							if field_struc[f][2]:
								# This is a binary expansion with categories
								# Expansions will always be varchar because more than one category may be present
								# First make sure that this expansions suffixes match its categories
								if [item.replace(f+'___','').replace('_','-') for item in found] == field_struc[f][7][0]:
									# This expansions suffixes match its categories
									field_struc[f][4] = True
									field_struc[f][6] = 'varchar(255)'
									field_struc[f][9] = [f]
									catfull_expnson.append('%s: %s, %s' % (f,found,field_struc[f][7][0]))
								else:
									# If not, we record the situation. This should not happen.
									field_struc[f][9] = [link_issue]
									suffix_mismatch.append('%s: %s, %s' % (f,found,field_struc[f][7][0]))
							else:
								# This is a binary expansion without categories
								# This should not happen, if it does we need to record it.
								field_struc[f][9] = [link_issue]
								catless_expnson.append('%s: %s' % (f,found))

		# Read in var_rollovers information and settle all differences between any heads and their links
		roll_reader = csv.reader(roll, delimiter=',')

		sqls = []
		for row in roll_reader:
			elem = row[0]
			if elem != '':
				if elem in tbls.keys():
					table_name = elem
				else:
					sqls.append(elem)
					links = filter(lambda a: a != '', row[1:])
					if len(links) > 0:
						# Here we have to settle any differences between a head, h, and its links, and add the links to the heads lookup
						h = field_struc[elem]
						for link in links:
							l = field_struc[link]
							h[9].extend(l[9])

						good = [x for x in h[9] if x != link_issue]

						if len(good) < 1:
							# All lookups for this head were unusable
							# Nothing to be done, this head will not be written out
							pass
						else:
							target = good[0]
							if len(good) == 1:
								# There is only one usable lookup for this head
								# Nothing to be done
								pass
							else:
								# There is more than one internally consistent lookup for this head, we have make sure they all agree
								# We will compare all the lookups to the first element
								for g in good[1:]:
									if field_struc[g][7] == field_struc[target][7]:
										# These two have matching category strings
										if field_struc[g][8] == field_struc[target][8]:
											# These two have matching suffixes
											if field_struc[g][6] == field_struc[target][6]:
												# These two have matching datatypes
												# Nothing needs to be done here, the lookups are consistent.
												pass
											else:
												# These two do not match in datatype and must be settled before continuing.
												# Record this situation.
												settled = settle(field_struc[g][6],field_struc[target][6])

												if settled != field_struc[target][6]:
													dtype_resettled.append('Head: %s, Target %s: %s, Field %s: %s, Settled: %s' % (elem,target,field_struc[target][6],g,field_struc[g][6],settled))
													field_struc[target][6] = settled
												else:
													different_dtype.append('Head: %s, Target %s: %s, Field %s: %s, Settled: %s' % (elem,target,field_struc[target][6],g,field_struc[g][6],settled))
										else:
											# This head cannot be included because its lookups do not agree in suffixes. Record this
											h[9] = [link_issue for x in h[9]]
											different_suffx.append('Head: %s, Target %s: %s, Field %s: %s' % (elem,target,field_struc[target][8],g,field_struc[g][8]))
											break
									else:
										# This head cannot be included because its lookups do not agree in category strings. Record this
										h[9] = [link_issue for x in h[9]]
										different_categ.append('Head: %s, Target %s: %s, Field %s: %s' % (elem,target,field_struc[target][7],g,field_struc[g][7]))
										break

							# Change the details of the head to match the target
							# If the target is the head, it won't make a difference
							h[6] = field_struc[target][6]	# Change the heads datatype
							h[7] = field_struc[target][7]	# Change the heads category string
							h[8] = field_struc[target][8]	# Change the heads suffixes

			else:
				repetitious_tbls[table_name] = sqls
				sqls = []

		# Write out the tables and build db_struc for gen_populate
		for cur_tbl in tbls.keys():
			# Empty fields are not included, therefore we may also have some empty tables since every selected table in tables.csv will be included
			out.write('CREATE TABLE %s.%s(\n' % (schema, cur_tbl))
			out.write('\tpid \t\t\tint8 NOT NULL,\n')
			out.write('\tredcap_event_%s \t\t\tvarchar(255)' % cur_tbl)

			# Start building the structure of this table to return
			db_struc[cur_tbl] = [tbls[cur_tbl], [], [], [], [], [], []]

			if cur_tbl in repetitious_tbls:
				# Get a list of fields from the rollovers
				fields = repetitious_tbls[cur_tbl]
			else:
				# Get a list of fields from the codebook
				fields = tbl_col[cur_tbl]

			for f in fields:

				h = field_struc[f]
				good = [x for x in h[9] if x != link_issue]

				if len(good) > 0:
					# We have a field to write out
					out.write(',\n')
					out.write('\t%s \t\t\t%s' % (f,h[6]))

					db_struc[cur_tbl][1].append(f)		# Append this field to the table structure
					db_struc[cur_tbl][2].append(h[6])	# Append this fields data type
					db_struc[cur_tbl][3].append(h[5])	# Append this fields English name
					db_struc[cur_tbl][4].append(h[7])	# Append this fields category string if it exists ie. [['0', '1', '-77'], ['No', 'Yes', 'Missing']]
					db_struc[cur_tbl][5].append(h[8])	# Append this fields expansion suffixes if they exist ie. [__1,__2,___77]
					db_struc[cur_tbl][6].append(h[9])	# Append this fields lookups

			out.write('\n);\r\n')

		# Log all FYIs
		fyi.write('* The following fields satisfied the gleason exception and were therefore classified based on their values instead of their categories (should be ib_010, ib_011, af_011, af_012):\n')
		for item in gleason_exceptn:
			fyi.write('\t%s\n' % item)
		fyi.write('* The following fields had imbalanced category strings:\n')
		for item in imbalanced_cats:
			fyi.write('\t%s\n' % item)
		fyi.write('* The following fields from the codebook do not exist in the datacut:\n')
		for item in field_not_exist:
			fyi.write('\t%s\n' % item)
		fyi.write('* The following fields from the codebook exist but are totally empty:\n')
		for item in field_empty_col:
			fyi.write('\t%s\n' % item)
		fyi.write('* The following fields were found to be binary expansions with category strings whose suffixes matched:\n')
		for item in catfull_expnson:
			fyi.write('\t%s\n' % item)
		fyi.write('* The following fields were found to be binary expansions without category strings:\n')
		for item in catless_expnson:
			fyi.write('\t%s\n' % item)
		fyi.write('* The following fields were found to be binary expansions with category strings whose suffixes did not match:\n')
		for item in suffix_mismatch:
			fyi.write('\t%s\n' % item)
		fyi.write('* The datatype of each of the following head fields was changed to accommodate the datatype of a link:\n')
		for item in dtype_resettled:
			fyi.write('\t%s\n' % item)
		fyi.write('* The following link fields were found to differ in datatype:\n')
		for item in different_dtype:
			fyi.write('\t%s\n' % item)
		fyi.write('* The following link fields were found to differ in categories:\n')
		for item in different_categ:
			fyi.write('\t%s\n' % item)
		fyi.write('* The following link fields were found to differ in suffixes:\n')
		for item in different_suffx:
			fyi.write('\t%s\n' % item)

		# Write out owner statements and grant privileges
		write_boilerplate(out,tbls.keys(),owner,schema,privileges)

		return [dcut_len, db_struc]