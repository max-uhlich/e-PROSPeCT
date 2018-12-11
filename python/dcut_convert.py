import csv
import sys
import os
import re
import time
import datetime
import gen_define
import gen_populate
import gen_derive
import gen_xml
import pickle
from helpers import print_to_log, elapsed

# Results will contain:
#		psql_define_schema_name.txt
#		psql_populate_schema_name.txt
#		psql_derive_schema_name.txt
#		psql_xml_schema_name.xml
# FYI will contain:
#		fyi_define.txt
#		fyi_derive.txt

def save_obj(obj, name ):
	with open('obj/'+ name + '.pkl', 'wb') as f:
		pickle.dump(obj, f, pickle.HIGHEST_PROTOCOL)

def load_obj(name ):
	with open('obj/' + name + '.pkl', 'rb') as f:
		return pickle.load(f)

import sys
import inspect

def get_size(obj, seen=None):
    """Recursively finds size of objects in bytes"""
    size = sys.getsizeof(obj)
    if seen is None:
        seen = set()
    obj_id = id(obj)
    if obj_id in seen:
        return 0
    # Important mark as seen *before* entering recursion to gracefully handle
    # self-referential objects
    seen.add(obj_id)
    if hasattr(obj, '__dict__'):
        for cls in obj.__class__.__mro__:
            if '__dict__' in cls.__dict__:
                d = cls.__dict__['__dict__']
                if inspect.isgetsetdescriptor(d) or inspect.ismemberdescriptor(d):
                    size += get_size(obj.__dict__, seen)
                break
    if isinstance(obj, dict):
        size += sum((get_size(v, seen) for v in obj.values()))
        size += sum((get_size(k, seen) for k in obj.keys()))
    elif hasattr(obj, '__iter__') and not isinstance(obj, (str, bytes, bytearray)):
        size += sum((get_size(i, seen) for i in obj))
    return size

def run_gen(mdir,dcut_file,schema_name):

	print_to_log("Checking for a new Datacut...")
	name = os.listdir(dcut_file)
	if name.__len__() < 1:
		print_to_log("No Datacut Found. Exiting.")
		sys.exit()
	else:
		name = name[0]

	date = re.search('\d{4}-\d{2}-\d{2}',name).group(0)
	dcut = os.path.join(dcut_file,name)

	print_to_log("Datacut found from {0}".format(date))

	print_to_log("Starting conversion...")
	go(date,mdir,dcut,schema_name)

	print_to_log("Conversion complete.")
	print_to_log("Moving used Datacut...")

	os.rename(dcut, os.path.join(mdir,name))

def go(date,mdir,dcut,schema):

	# Global Variables
	csv_file = "CSV"
	owner = 'btaw_admin'
	link_issue = 'ISSUE'
	privileges = ['','']
	ldir = os.path.join(mdir,'FYI')
	rdir = os.path.join(mdir,'Results')
	book = os.path.join(csv_file,'')
	roll = os.path.join(csv_file,'var_rollover.csv')
	tables = os.path.join(csv_file,'tables.csv')
	der_tables = os.path.join(csv_file,'derive_tables.csv')
	der_methods = os.path.join(csv_file,'derive_methods.csv')
	der_params = os.path.join(csv_file,'derive_parameters.csv')
	xml_spec = os.path.join(csv_file,'xml_spec.csv')

	if not os.path.exists(ldir):
		os.makedirs(ldir)
	if not os.path.exists(rdir):
		os.makedirs(rdir)

	print_to_log("1. Gen_Define...")
	start = time.time()
	[dcut_len, db_struc] = gen_define.start(dcut,book,roll,tables,link_issue,schema,rdir,ldir,owner,privileges)
	end = time.time()
	print_to_log("Completed %s" % elapsed(start,end))

	#save_obj(db_struc,'db_struc')
	#db_struc = load_obj('db_struc')

	print_to_log("2. Gen_Populate...")
	start = time.time()
	[id_dict, f_to_i] = gen_populate.start(dcut,db_struc,dcut_len,schema,rdir,link_issue)
	end = time.time()
	print_to_log("Completed %s" % elapsed(start,end))

	#save_obj([dcut_len,db_struc,id_dict,f_to_i],'req_vars')
	#[dcut_len,db_struc,id_dict,f_to_i] = load_obj('req_vars')
	
	print_to_log("3. Gen_Derive...")
	start = time.time()
	db_struc = gen_derive.start(dcut,db_struc,der_tables,der_methods,der_params,schema,rdir,ldir,owner,privileges,id_dict,f_to_i,link_issue)
	end = time.time()
	print_to_log("Completed %s" % elapsed(start,end))

	#save_obj(db_struc,'xml_vars')
	#db_struc = load_obj('xml_vars')

	print_to_log("4. Gen_XML...")
	start = time.time()
	gen_xml.start(date,db_struc,schema,xml_spec,rdir)
	end = time.time()
	print_to_log("Completed %s" % elapsed(start,end))