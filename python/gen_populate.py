import csv
import os
from helpers import printProgress, categorize_exp, categorize_val

def start(dcut_fn, db_struc, dcut_len, schema, rdir, link_issue):

	with open(dcut_fn, 'rb') as dcut, open(os.path.join(rdir, 'psql_populate_%s.txt' % schema), 'wb') as out:

		# Read in the datacut header
		dcut_reader = csv.reader(dcut, delimiter=',')
		dcut_header = dcut_reader.next()

		# Build a dictionary mappying sql fields to their indices in the datacut header
		# A field will have exactly one index unless it has suffixes, in which case it will have more than 1
		f_to_i = {}

		# Build a dictionary mapping each pid to the number of rows it has in the dcut
		id_dict = {}

		# Keep track of the size of each tables max lookup, so we don't have to do it during the main loop
		lup_dims = {}

		for tbl in db_struc.keys():

			sufs = db_struc[tbl][5]
			lups = db_struc[tbl][6]

			if len(lups) > 0:

				lup_dims[tbl] = len(max(lups, key=len))

				for i,lup in enumerate(lups):
					for f in lup:
						if f != link_issue:
							if len(sufs[i]) > 0:
								f_to_i[f] = []
								for s in sufs[i]:
									f_to_i[f].append(dcut_header.index(f+s))
							else:
								f_to_i[f] = [dcut_header.index(f)]

		if 'internal_id' in dcut_header:
			pid_index = dcut_header.index('internal_id')
		else:
			pid_index = dcut_header.index('\xef\xbb\xbfinternal_id') # Byte order mark

		event_index = dcut_header.index('redcap_event_name')

		it_tot = dcut_len
		it = 0

		for row in dcut_reader:

			if row[pid_index] in id_dict.keys():
				id_dict[row[pid_index]] += 1
			else:
				id_dict[row[pid_index]] = 1

			for tbl in db_struc.keys():
				# For each table we will need to create at most x insertion statements, where x is the length of the largest list of lookups for this table
				# Any fields in a table with rollover links which does not have rollover links should only be looked up once and then duplicated

				# Case 1: A table which does not have rollover links
				#	This tables lookups is a list of arrays each containing one field, none of these are link issues
				#	Our batch for this table is simply a list of those fields
				# 	We will need to look up all of those fields once per row of the datacut
				#	This table will yield a maximum of 1 insertion per row of the datacut, unless its all NULL, in which case we will write 0 insertions for that row
				# Case 2: A table which does have rollover links
				#	This tables lookups is a list of arrays containing 1 or more field
				#	The arrays contain either a single normal field, or a list of rollover fields
				#	If the array contains a single normal field, it cannot be a link issue
				#	If the array contains a list of rollover fields, one or more might be a link issue
				#	There can be a maximum of n-1 link issues, if the array of rollover fields is of length n
				# 	Our batch for this table is a list of the non link-issue fields compiled from the tables lookups

				# For each table, the batch is simply the lookups list. We must go through the lookups list array by array.
				# Loop through every member of each array, using the cats and the suffixes to replace each field with a single value and each link issue with NULL
				# The new list replicates the structure of the lookup list exactly, but contains all the assignments instead of field names and link issues
				# We will then print at most 1 insertion for every element of the longest array in this list
				# If an insertion is all NULL, we will not print it

				# We should ideally do the following in advance, so as to not have to repeat the operations so much
				# 	For every row, we will need to find indices from the header for the same exact fields, this will happen ~2000 times per row
				# 	We will also need to rebuild each field using its sql+suffixes if it has any, this will happen ~30 times per row
				# 	We could perhaps create a dictionary mapping sql fields to their indices
				#	If a field has suffixes it will have a longer list of indices
				# 	Then we can operate as above only without ever needing to check the header or put suffixes on anything

				typs = db_struc[tbl][2]
				cats = db_struc[tbl][4]
				lups = db_struc[tbl][6]

				if len(lups) > 0:

					lup_dim = lup_dims[tbl]

					insertions = [[] for _ in range(0,lup_dim)]

					for i,lup in enumerate(lups):

						cat = cats[i]

						for j,f in enumerate(lup):

							if f != link_issue:

								indices = f_to_i[f]
								res = [row[x].strip() for x in indices]

								if len([x for x in res if x != '']) > 0:
									if cat == [[],[]]:
										# This field has no categories
										val = res[0]
									else:
										# This field has categories
										if len(res) > 1:
											# This field is an expansion
											val = categorize_exp(res,cat)
										else:
											# This field is not an expansion
											val = categorize_val(res,cat)
								else:
									val = 'NULL'

							else:
								val = 'NULL'

							insertions[j].append(val)

					if lup_dim > 1:
						# This value will tell us how many fields we need to duplicate
						diff_ins = len(insertions[0]) - len(insertions[-1])

					for i,insert in enumerate(insertions):

						if len([x for x in insert if x != 'NULL']) > 0:
							# We can now build an insertion statement to be written out

							if i > 0:
								# This means we are inserting a column of links now
								# Therefore we must duplicate the normal fields from the first insert
								out_lines = out_lines[0:(diff_ins*2)+3]
								data_types = typs[diff_ins:]
							else:
								out_lines = []
								out_lines.append('INSERT INTO %s.%s VALUES (\n' % (schema, tbl))
								out_lines.append('\t%s,\n' % row[pid_index])
								out_lines.append('\t\'%s\'' % row[event_index])
								data_types = typs

							for j,val in enumerate(insert):
								# Print out the datatypes according to the following examples:
								# {int8, 	float8, 	bool, 	date, 			varchar(255)}
								#  9,		7.69,		't',	'2013-08-14',	'String',

								if val != 'NULL':
									dtype = data_types[j]

									if dtype == 'bool':
										if val == '1':
											val = '\'t\''
										else:
											val = '\'f\''
									elif dtype == 'date' or dtype == 'varchar(255)' or dtype == 'text':
										val = '\'%s\'' % val.replace('\'',';')

								out_lines.append(',\n')
								out_lines.append('\t%s' % val)

							out_lines.append(');\n\n')

							for out_line in out_lines:
								out.write(out_line)

			it += 1
			printProgress(it, it_tot, prefix = 'Progress:', suffix = 'Complete', barLength = 30)

		return [id_dict, f_to_i]