# e-PROSPeCT
e-PROSPeCT is a bare bones, empty version of PROSPeCT, A Predictive Research Online System for Prostate Cancer Tasks, which can be divided into three main components:

## 1. The APCaRI dataset (NOT-INCLUDED)

This dataset is hosted on REDCap and includes over 5000 patients. REDCap currently does not allow automated exports of the data, so our dataset is exported manually and then passed to a python script which automates the rest of the process. The script also makes use of a data dictionary, which provides some limited meta information about the fields and tables existing in REDCap.

## 2. Python Modules

There are five python modules:
 
#### A.  Gen_Define

This module combs the datacut from REDCap and builds PostgreSQL table statements for use in the database which underlies PROSPeCT. The fields defined by this module are guaranteed to conform to some data regularity conditions required for the next modules, such as uniformity of datatype and non-emptiness. This module also handles various special cases which exist in REDCap but require conversion for our purposes. For example, some fields in REDCap are categorical, meaning the field itself contains integer values, but each integer maps into a set of categories which are expressed as strings. A given row might contain an integer in {1,2,3}, where 1=’Benign’, 2=’Adenocarcinoma’, and 3=’Other’. In special cases, a field like this will have been expanded into three fields, one for each category. These three fields will then each contain a boolean value {0,1} to indicate their respective categories. These binary expansion fields are often used when more than one category can be present at a time in a given datapoint. The Gen_Define module converts these types of field (and other special cases) into a single string field to be used by our application. The main objective of this module is to iron out any inconsistent or anomalous data and prepare a definition of the database to be populated by the next module. Any anomalies, which are usually a result of data entry error, are either corrected immediately, or flagged and excluded if necessary. This component takes about 26 seconds to complete.

#### B.  Gen_Populate

This module uses the previous modules database definition as its guide while creating PostgreSQL insertion statements for every row in the database. Every patient in the database can have 1 or more rows in the datacut, where each column is a REDCap field. This module parses every row of the datacut and generates at most one insertion statement per table. This module has the greatest workload of all the modules, so its efficient processing of each row is especially important. This module also handles a few of its own special cases, the details of which are too obscure for this document. It completes in around 42 seconds.

#### C.  Gen_Derive

PROSPeCT currently provides 10 fields which are computed from various REDCap fields. This module facilitates the creation and population of the derived tables and their fields. It passes once again through the REDCap datacut sorting, consolidating, and grouping sets of datapoints to be used as parameters according to several .csv files defining our derived fields. There is one python function for every derived field, accepting the relevant parameters and returning a particular result. These parameters are extracted from REDCap, sorted, consolidated, grouped, and sent to their respective functions according to the mappings expressed in the .csv files. This module is constructed in such a way to allow for easy extensibility. When a new derived field is introduced, the programmer simply creates a single function computing a desired result from any set of parameters, updates the .csv mappings, and the module does the rest. The code itself requires no modification when adding or removing derived fields. This component currently completes in around 18 seconds. Gen_Derive also passes a modified database definition (including the new derived fields and tables) to the next module to be referenced as the final state of the PROSPeCT database.

#### D.  Gen_XML

This module simply creates an .xml description of the final state of the dataset to be used by the PROSPeCT web application to define a programmatic connection to the underlying PostgreSQL database. The last run of this module completed in 2 milliseconds.

#### E.  Remote_Ops (NOT-INCLUDED)

This module facilitates all operations which must occur remotely on our server. It has two subprocesses: ‘rebuild’, and ‘restart’. The first process connects to the remote server through ssh, destroys the current version of the dataset contained in PostgreSQL and recreates it using the newly generated statements created by the previous 4 modules. Any anomalous behaviour is recorded and passed back to the home computer. After these tasks run to completion, this process cleans up after itself and logs out. The second process connects to the remote server once again through ssh, but this time using a special administrative account. This process simply restarts the server and logs out.

## 3. The PROSPeCT Web Application

The end result of this process is the Java GWT PROSPeCT web application. Many data visualization modules also make use of the D3.js javascript library. PROSPeCT is served by Apache Tomcat, and as mentioned above, sits on a PostgreSQL database.

