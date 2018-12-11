import os
import sys
import re
import datetime
import logging
import dcut_convert
import remote_ops
from helpers import print_to_log

def main():

	if len(sys.argv) > 1 and sys.argv[1] == 'unshifted':
		print 'Unshifted data, correct? Press any key to continue.'
		schema_name = ''
	else:
		print 'Shifted data, correct? Press any key to continue.'
		schema_name = ''

	raw_input()

	todays_date = datetime.date.today().strftime('%b_%d_%Y');
	dcut_file = "Datacut"

	mdir = os.path.join("Output",'%s_%s' % (todays_date, schema_name))

	if not os.path.exists(mdir):
		os.makedirs(mdir)

	logging.basicConfig(filename=os.path.join(mdir,'py_log.log'),level=logging.INFO,format='%(asctime)s %(message)s',datefmt='%b_%d_%Y %I:%M:%S %p')
	
	# Begin
	print_to_log("Passing control to conversion...")

	dcut_convert.run_gen(mdir,dcut_file,schema_name)

	print_to_log("Returned from conversion.")
	print_to_log("Passing control to remote rebuild...")

	remote_ops.rebuild(mdir, schema_name)

	print_to_log("Returned from remote rebuild.")
	print_to_log("Passing control to remote restart...")

	remote_ops.restart(mdir)

	print_to_log("Returned from remote restart. Press any key to exit.")
	raw_input()

if __name__ == '__main__':
	main()