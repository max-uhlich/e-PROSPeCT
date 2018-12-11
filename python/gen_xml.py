import csv
import os
from collections import OrderedDict
from helpers import printProgress, uniqify

def start(date, db_struc, schema, xml_spec_fn, rdir):

	with open(xml_spec_fn, 'rb') as spec, open(os.path.join(rdir, '%s_db.xml' % schema), 'wb') as out:

		index_table = '';

		spec_reader = csv.reader(spec, delimiter=',')

		trees = OrderedDict()
		tbls = []

		# Load the layout
		for row in spec_reader:
			tbls.append(row[2])
			if row[0] in trees.keys():
				trees[row[0]].append([row[1],row[2]])
			else:
				trees[row[0]] = [[row[1],row[2]]]

		tbls = uniqify(tbls)

		# Begin writing out the specification into xml
		out.write('<catalog>\n')
		out.write('\t<database name=\"%s\">\n' % schema)
		out.write('\t\t<date>%s</date>\n' % date)
		out.write('\t\t<tables>\n')

		for tbl in tbls:
			out.write('\t\t\t<table name=\"%s\"><sqlName>%s.%s</sqlName></table>\n' % (tbl,schema,tbl))

		out.write('\t\t</tables>\n\n')
		out.write('\t\t<pidColumn>\n')
		out.write('\t\t\t<name></name>\n')
		out.write('\t\t\t<sqlName></sqlName>\n')
		out.write('\t\t\t<tableOrigin>%s</tableOrigin>\n' % index_table)
		out.write('\t\t</pidColumn>\n\n')
		out.write('\t\t<categories>\n')

		it = 0
		it_tot = len(trees.keys())

		for cat in trees.keys():
			out.write('\t\t\t<category name=\"%s\">\n' % cat)

			for pair in trees[cat]:

				header = pair[0]
				sql = pair[1]

				tbl_struc = db_struc[sql]

				if sql == index_table:
					out.write('\t\t\t\t<column>\n')
					out.write('\t\t\t\t\t<type></type>\n')
					out.write('\t\t\t\t\t<name></name>\n')
					out.write('\t\t\t\t\t<sqlName></sqlName>\n')
					out.write('\t\t\t\t\t<tableOrigin>%s</tableOrigin>\n' % sql)
					out.write('\t\t\t\t\t<treeHeader>%s</treeHeader>\n' % header)
					out.write('\t\t\t\t</column>\n')

				if cat != 'Derived Fields' and len(tbl_struc[1]) > 0:
					out.write('\t\t\t\t<column>\n')
					out.write('\t\t\t\t\t<type>string</type>\n')
					out.write('\t\t\t\t\t<name>Redcap Event %s</name>\n' % sql)
					out.write('\t\t\t\t\t<sqlName>redcap_event_%s</sqlName>\n' % sql)
					out.write('\t\t\t\t\t<tableOrigin>%s</tableOrigin>\n' % sql)
					out.write('\t\t\t\t\t<treeHeader>%s</treeHeader>\n' % header)
					out.write('\t\t\t\t</column>\n')

				for i,f in enumerate(tbl_struc[1]):
					if tbl_struc[2][i] == 'int8':
						type = 'integer'
					elif tbl_struc[2][i] == 'float8':
						type = 'float'
					elif tbl_struc[2][i] == 'varchar(255)':
						type = 'string'
					elif tbl_struc[2][i] == 'date':
						type = 'date'
					elif tbl_struc[2][i] == 'bool':
						type = 'boolean'
					else:
						type = 'string'

					out.write('\t\t\t\t<column>\n')
					out.write('\t\t\t\t\t<type>%s</type>\n' % type)
					out.write('\t\t\t\t\t<name>%s</name>\n' % tbl_struc[3][i])
					out.write('\t\t\t\t\t<sqlName>%s</sqlName>\n' % f)
					out.write('\t\t\t\t\t<tableOrigin>%s</tableOrigin>\n' % sql)
					out.write('\t\t\t\t\t<treeHeader>%s</treeHeader>\n' % header)
					out.write('\t\t\t\t</column>\n')

			out.write('\t\t\t</category>\n')

			it += 1
			printProgress(it, it_tot, prefix = 'Progress:', suffix = 'Complete', barLength = 30)

		out.write('\t\t</categories>\n')
		out.write('\t</database>\n')
		out.write('</catalog>\n')