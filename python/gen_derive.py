import csv
import os
import derive_methods
from helpers import printProgress, write_boilerplate, uniqify, consolidate

def start(dcut_fn, db_struc, der_tables_fn, der_methods_fn, der_params_fn, schema, rdir, ldir, owner, privileges, id_dict, f_to_i, link_issue):

	with open(dcut_fn, 'rb') as dcut, open(der_tables_fn, 'rb') as tbls, open(der_methods_fn, 'rb') as mths, open(der_params_fn, 'rb') as prms, open(os.path.join(ldir, 'fyi_derive.txt'), 'wb') as fyi, open(os.path.join(rdir, 'psql_derive_%s.txt' % schema), 'wb') as out:

		# FYI notes
		f_to_i_not_good = set()
		f_to_i_not_here = set()
		set_of_failures = {}
		risk_levels_set = {}

		tbl_dic = {}	# This dict pairs derived tables to their methods, eg psa_failures: [[rp,rp,rp],[rt,rt,rt],[cryo,cryo,cryo],...]
		prm_dic = {}	# This dict pairs parameters to the dcut indices of their fields, eg psa_vals: [ind(ps_003),ind(ps_005),ind(ps_007),ind(ps_009),ind(ps_011),ind(ps_013)]
		grp_dic = {}	# This dict pairs group names to the parameters which should be consolidated and sorted together
		mth_dic = {}	# This dict pairs methods to their parameters, eg rp: ['psa_vals','psa_dates','rp_date']
		val_dic = {}	# This dict pairs parameter names to the list of ordered values grabbed from each PIDs chunk of the datacut

		# Read in the datacut header
		dcut_reader = csv.reader(dcut, delimiter=',')
		dcut_header = dcut_reader.next()

		# Open tables, methods, and parameters specification
		tbls_reader = csv.reader(tbls, delimiter=',')
		mths_reader = csv.reader(mths, delimiter=',')
		prms_reader = csv.reader(prms, delimiter=',')

		# Prepare a set of all the date fields to be found in db_struc to be used in the last_contact_date method
		# Since we are looking only at date fields from db_struc, they will all be present in f_to_i and there will be no duplicates and no expansions
		prm_dic['all_dates'] = []
		prm_dic['all_dates_sqls'] = []
		prm_dic['all_dates_engs'] = []
		grp_dic['p_gA'] = ['all_dates', 'all_dates_sqls', 'all_dates_engs']
		for tbl in db_struc.keys():
			for i,f in enumerate(db_struc[tbl][1]):
				if db_struc[tbl][2][i] == 'date':
					dates = [f_to_i[lup][0] for lup in db_struc[tbl][6][i] if lup != link_issue]
					prm_dic['all_dates_sqls'].extend([f for j in range(len(dates))])
					prm_dic['all_dates_engs'].extend([db_struc[tbl][3][i] for j in range(len(dates))])
					prm_dic['all_dates'].extend(dates)

		# Bypass is a set of parameters which we do not need to lookup
		bypass = ['all_dates_sqls', 'all_dates_engs']

		# 1. Read in the derived tables
		# 	2. Add them to db_struc
		# 	3. Define them and write their definitions out
		# 	4. Build the tables dictionary
		cur_tbl = None
		tbl_mth = []
		for row in tbls_reader:
			if row[0] != '':
				if cur_tbl:
					db_struc[cur_tbl][1].append(row[0])
					db_struc[cur_tbl][2].append(row[1])
					db_struc[cur_tbl][3].append(row[2])
					db_struc[cur_tbl][4].append([[],[]])
					db_struc[cur_tbl][5].append([])
					db_struc[cur_tbl][6].append(row[0])
					tbl_mth.append([x for x in row[3:] if x != ''])

					# We have a derived field to write out
					out.write(',\n')
					out.write('\t%s \t\t\t%s' % (row[0],row[1]))
				else:
					cur_tbl = row[0]
					db_struc[cur_tbl] = [row[1], [], [], [], [], [], []]
					
					# We have a derived table to write out
					out.write('CREATE TABLE %s.%s(\n' % (schema, cur_tbl))
					out.write('\tpid \t\t\tint8 NOT NULL')
			else:
				tbl_dic[cur_tbl] = [uniqify(list(x)) for x in zip(*tbl_mth)]
				cur_tbl = None
				tbl_mth = []

				# Close this table
				out.write('\n);\r\n')

		# 5. Write out owner statements and grant privileges
		write_boilerplate(out,tbl_dic.keys(),owner,schema,privileges)

		# 6. Build the parameters dictionary and the index dictionary
		for row in prms_reader:
			prm_dic[row[0]] = []

			if row[1] in grp_dic.keys():
				grp_dic[row[1]].append(row[0])
			else:
				grp_dic[row[1]] = [row[0]]

			for f in row[2:]:
				if f != '':
					if f in f_to_i.keys():
						if len(f_to_i[f]) == 1:
							prm_dic[row[0]].append(f_to_i[f][0])
						elif len(f_to_i[f]) > 1:
							# This field is an expansion (len>1)
							prm_dic[row[0]].append(f_to_i[f])
						elif len(f_to_i[f]) < 1:
							# This field is mapped to [] (len<1), which should never happen
							prm_dic[row[0]].append('')
							f_to_i_not_good.add(f)
					else:
						# This field was either determined by gen_define to be unusable, or gen_define did not vet this field
						# The latter case may arise if we try and derive a value using a field or fields which we did not include in our db definition
						prm_dic[row[0]].append('')
						f_to_i_not_here.add(f)

		# 7. Build the methods dictionary
		for row in mths_reader:
			mth_dic[row[0]] = [x for x in row[1:] if x != '']

		if 'internal_id' in dcut_header:
			pid_index = dcut_header.index('internal_id')
		else:
			pid_index = dcut_header.index('\xef\xbb\xbfinternal_id') # Byte order mark

		it_tot = len(id_dict)

		val_dic = {p: [] for p in prm_dic.keys()}
		id_cnt = 0
		it = 0

		for row in dcut_reader:
			id_cnt += 1
			
			# Collect all the fields we need using prm_dic
			for p in prm_dic.keys():
				for i in prm_dic[p]:
					if i != '':
						if p not in bypass:
							if isinstance(i, list):
								# This is an expansion
								res = ''.join([row[x].strip() for x in i])
								if uniqify(res) != ['0']:
									val_dic[p].append(res)
								else:
									val_dic[p].append('')
							else:
								# This is a single field
								val_dic[p].append(row[i])
						else:
							val_dic[p].append(i)
					else:
						val_dic[p].append('')

			if id_dict[row[pid_index]] - id_cnt == 0:

				#print row[pid_index]

				# Sort and consolidate groups of parameters according to grp_dic
				for g in grp_dic.keys():
					grp = []
					for p in grp_dic[g]:
						grp.append(val_dic[p])
					grp = consolidate(grp,g)
					for i,p in enumerate(grp_dic[g]):
						val_dic[p] = grp[i]

				for tbl in tbl_dic.keys():

					typs = db_struc[tbl][2]

					for mth_list in tbl_dic[tbl]:

						# Create one or more insertion statements for this mth_list
						results = []

						for mth in mth_list:

							# Call this method with all its parameters
							results.append(getattr(derive_methods, mth)(*[val_dic[p] for p in mth_dic[mth]]))

						insertions = [[] for _ in range(len(max(results, key=len)))]

						for i,insert in enumerate(insertions):
							for r in results:
								if i >= len(r):
									insert.extend(['NULL' for _ in range(len(r[0]))])
								else:
									insert.extend(r[i])

						for i,insert in enumerate(insertions):

							if i == 0:
								out_lines = [',\n\t%s' % 'NULL' for _ in range(len(insert)+3)]
								out_lines[0] = 'INSERT INTO %s.%s VALUES (\n' % (schema, tbl)
								out_lines[1] = '\t%s' % row[pid_index]

							if len([x for x in insert if x != 'NULL']) > 0:
								# We can now build an insertion statement to be written out

								if tbl == 'psa_failures':
									# Record psa_failures for easier verification
									if insert[0] in set_of_failures.keys():
										set_of_failures[insert[0]].append('%s, %s, %s, %s' % (row[pid_index], insert[2], insert[3], insert[1]))
									else:
										set_of_failures[insert[0]] = ['%s, %s, %s, %s' % (row[pid_index], insert[2], insert[3], insert[1])]
								elif tbl == 'miscellaneous':
									# Record risk level for easier verifications
									if insert[5] in risk_levels_set.keys():
										risk_levels_set[insert[5]].append(row[pid_index])
									else:
										risk_levels_set[insert[5]] = [row[pid_index]]

								for j,val in enumerate(insert):
									# Print out the datatypes according to the following examples:
									# {int8, 	float8, 	bool, 	date, 			varchar(255)}
									#  9,		7.69,		't',	'2013-08-14',	'String',

									if val != 'NULL':
										dtype = typs[j]

										if dtype == 'bool':
											if val == '1':
												val = '\'t\''
											else:
												val = '\'f\''
										elif dtype == 'date' or dtype == 'varchar(255)' or dtype == 'text':
											val = '\'%s\'' % val.replace('\'',';')

										out_lines[j+2] = ',\n\t%s' % val

								out_lines[-1] = ');\n\n'

								for out_line in out_lines:
									out.write(out_line)

				val_dic = {p: [] for p in prm_dic.keys()}
				id_cnt = 0
				it += 1
				printProgress(it, it_tot, prefix = 'Progress:', suffix = 'Complete', barLength = 30)

		# Log all FYIs
		fyi.write('* The following fields from derive_parameters.csv were not used because they were mapped to empty lists:\n')
		for item in f_to_i_not_good:
			fyi.write('\t%s\n' % item)
		fyi.write('* The following fields from derive_parameters.csv were not used because they were either determined by gen_define to be unusable, or were not examined by gen_define:\n')
		for item in f_to_i_not_here:
			fyi.write('\t%s\n' % item)
		for failure in set_of_failures.keys():
			fyi.write('* Failures of %s:\n' % failure)
			for item in set_of_failures[failure]:
				fyi.write('\t%s\n' % item)
		for risk_level in risk_levels_set.keys():
			fyi.write('* Patients classified %s risk:\n' % risk_level)
			for item in risk_levels_set[risk_level]:
				fyi.write('\t%s\n' % item)

		return db_struc