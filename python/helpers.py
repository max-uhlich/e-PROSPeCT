import sys
import re
import datetime
import logging

date_match = '%Y-%m-%d'

def elapsed(start,end):
	hours, rem = divmod(end-start, 3600)
	minutes, seconds = divmod(rem, 60)
	return "{:0>2}:{:0>2}:{:05.2f}".format(int(hours),int(minutes),seconds)

def print_to_log(str):
	print str
	logging.info(str)

def printProgress(iteration, total, prefix = '', suffix = '', decimals = 1, barLength = 100):
	"""
	Call in a loop to create terminal progress bar
	@params:
		iteration   - Required  : current iteration (Int)
		total	   	- Required  : total iterations (Int)
		prefix	  	- Optional  : prefix string (Str)
		suffix	  	- Optional  : suffix string (Str)
		decimals	- Optional  : positive number of decimals in percent complete (Int)
		barLength   - Optional  : character length of bar (Int)
	"""
	formatStr = "{0:." + str(decimals) + "f}"
	percent = formatStr.format(100 * (iteration / float(total)))
	filledLength = int(round(barLength * iteration / float(total)))
	bar = '-' * filledLength + ' ' * (barLength - filledLength)
	sys.stdout.write('\r%s |%s| %s%s %s' % (prefix, bar, percent, '%', suffix)),
	if iteration == total:
		sys.stdout.write('\n')
	sys.stdout.flush()

def parse_category(str):
	# Use regex to find each category and then split and trim
	cats = [x.strip() for x in re.sub('-?[0-9]+,', '', str).split('|')]
	vals = [x.strip(',') for x in re.findall('-?[0-9]+,', str)]
	return [vals, cats]

def test_gleason_exception(vals):
	int8_rgx = re.compile('[+-]?[0-9]+$')
	flt8_rgx = re.compile('[+-]?([0-9]*[.])?[0-9]+$')

	int8s = filter(int8_rgx.match, vals)
	flt8s = filter(flt8_rgx.match, vals)

	if len(vals) > 1 and len(vals) - len(int8s) == 1 and len(vals) - len(flt8s) == 1 and 'Missing' in vals:
		# Ex: ib_010 (1, 1 | 2, 2 | 3, 3 | 4, 4 | 5, 5 | -77, Missing)
		return True
	else:
		return False

def classify(vals):
	# Will classify the set of vals and return one of {int8, float8, bool, date, varchar(255), text}
	# I have not included the {text} data type as we have never seen a string longer than 255 characters
	# Update Aug 7th 2017: in the unshifted data, we do have strings longer than 255 characters
	dtype = 'varchar(255)'

	date_rgx = re.compile('\d{4}-\d{2}-\d{2}$')
	bool_rgx = re.compile('(0|1)$')
	int8_rgx = re.compile('[+-]?[0-9]+$')
	flt8_rgx = re.compile('[+-]?([0-9]*[.])?[0-9]+$')

	dates = filter(date_rgx.match, vals)
	bools = filter(bool_rgx.match, vals)
	int8s = filter(int8_rgx.match, vals)
	flt8s = filter(flt8_rgx.match, vals)
	
	if len(vals) == len(dates):
		dtype = 'date'
	elif len(vals) == len(bools):
		dtype = 'bool'
	elif len(vals) == len(int8s):
		dtype = 'int8'
	elif len(vals) == len(flt8s):
		dtype = 'float8'

	if dtype == 'varchar(255)' and max(vals, key=len) > 255:
		dtype = 'text'

	return dtype

def settle(a,b):
	# Settles the datatype of a and b. Picks the closest one that can contain both.
	# The tree structure of the data types is below. Each node can contain the datatypes below it
	#			text
	#			varchar(255)
	# float8					date
	# int8
	# bool

	# If both a and b are dates, return date
	# If one of a and b is a date, return varchar or text
	if (a == 'date' and b == 'date'):
		return 'date'

	if (a == 'date' or b == 'date'):
		if (a == 'text' or b == 'text'):
			return 'text'
		else:
			return 'varchar(255)'

	# Now dates are out of the way. Neither a nor b is a date at this point.
	# We can simply return the dtype of lower index
	dtypes = ['text','varchar(255)','float8','int8','bool']

	if dtypes.index(a) <= dtypes.index(b):
		return a
	else:
		return b

def categorize_exp(res,cat):
	if '1' in res:
		ret = ''
		for i,r in enumerate(res):
			if r == '1':
				ret += cat[1][i] + ', '
		return ret.strip(', ')
	else:
		return 'NULL'

def categorize_val(res,cat):
	if res[0] in cat[0]:
		return cat[1][cat[0].index(res[0])]
	else:
		return 'NULL'

def write_boilerplate(out,tbls,owner,schema,privileges):
	# Write out owner statements
	for cur_tbl in tbls:
		out.write('ALTER TABLE %s.%s OWNER TO %s;\n' % (schema,cur_tbl,owner))

	# Write out grant privilege statements
	for priv in privileges:
		start = 'GRANT ALL PRIVILEGES ON '
		finish = ','
		for j in range(0,len(tbls)):
			if j != 0:
				start = '\t\t'
			if j == len(tbls)-1:
				finish = ' TO %s;' % priv
			out.write('%s%s.%s%s\n' % (start,schema,list(tbls)[j],finish))

	out.write('\n')

def uniqify(seq):
	seen = set()
	seen_add = seen.add
	return [x for x in seq if not (x in seen or seen_add(x))]

def consolidate(grp,g):
	# grp is a list of n lists [L1, L2, ..., Ln]
	# The lists must be consolidated sorted
	# These operations must not move lists around, or otherwise disturb the existing dimensions of grp

	if g[0] == 'p':
		# Partial consolidate
		# Treat L1 as our template
		# If an element in L1 is '', we will remove this element from all lists
		templates = [grp[0]]
	elif g[0] == 'f':
		# Full consolidate
		# If an element in any list Lx is '', we will remove this element from all lists
		templates = grp

	for L in templates:
		non_zero_indices = [i for i,x in enumerate(L) if x != '']
		for n,_ in enumerate(grp):
			grp[n] = [grp[n][i] for i in non_zero_indices]

	# We will sort all lists by L1
	if len(grp[0]) > 0 and classify(grp[0]) == 'date':
		res = sorted(zip(*grp), key=lambda x: datetime.datetime.strptime(x[0], date_match))
		grp = [list(i) for i in zip(*res)]

	return grp

def equal_or_after(dates,tx_date):
	# Find the closest date in dates which is equal to or after tx_date but not before it
	tx_date = datetime.datetime.strptime(tx_date, date_match)
	def func(x):
		d =  datetime.datetime.strptime(x, date_match)
		delta = tx_date - d if d >= tx_date else datetime.timedelta.min
		return delta
	r = max(dates, key = func)
	if datetime.datetime.strptime(r, date_match) >= tx_date:
		return r
	else:
		return ''

def equal_or_before(dates,tx_date):
	# Find the closest date in dates which is equal to or before tx_date but not after it
	tx_date = datetime.datetime.strptime(tx_date, date_match)
	def func(x):
		d =  datetime.datetime.strptime(x, date_match)
		delta =  d - tx_date if d <= tx_date else datetime.timedelta.min
		return delta
	r = max(dates, key = func)
	if datetime.datetime.strptime(r, date_match) <= tx_date:
		return r
	else:
		return ''