## Insurance Enrollment

Author: Oliver Kaljuvee

#### Overview

This Java program reads the content of an insurance enrollment file and separates the enrollees by insurance company name 
and by writing the output into separate files. The contents of each file are sorted by last and first name (ascending). 
The input file may contain multiple versioned records per each user.  Only one record with the highest version will be 
included in the output file. 

The program demonstrates effective CSV file processing using an Apache Commons libraries, as well as filtering and 
sorting the data using built-in Java data structures, e.g. `PriorityQueue`. 

#### Installing and Running
These commands assume that you have installed **Java JDK 8+** on Windows:

```commandline
> git clone https://github.com/okaljuvee/InsuranceEnrollment.git
> cd InsuranceEnrollment
> mkdir output
> gradlew runParser --args "enrollments.csv output/"
```
Optionally, you can pass absolute paths for both the input file and the output directories:  

```commandline
> gradlew runParser --args "C:/tmp/enrollments.csv C:/tmp/"`
```

#### Input

The input data is located in the `resources` folder under the `src` directory and it is a CSV file consisting of randomly 
generated data with user identifier, version, and insurance company columns.  There can be multiple rows for each user 
with different version numbers:

```csv
user_id,name,version,insurance_company
6c9c2175-6fc9-4ff0-8c39-f69f6e08cba1,Janiah Mueller,1,AmeriHealth - New Jersey
01097544-7213-45e9-a09a-88fa5b042bce,Marin Griffin,1,Horizon Blue Cross Blue Shield of New Jersey
...
81787f26-6147-4dda-8d4a-13191b9cda2d,Jasiah Tate,1,Sutter Health Plus
...
fe9091ff-ae92-488e-850e-95417a67fa9f,Pamela Yang,4,Western Health Advantage
5dda5a5c-7fe9-4b2d-b5f6-6d165cae20a5,Maxim Walker,5,Sutter Health Plus
fe9091ff-ae92-488e-850e-95417a67fa9f,Pamela Yang,5,Western Health Advantage
5dda5a5c-7fe9-4b2d-b5f6-6d165cae20a5,Maxim Walker,6,Sutter Health Plus
5dda5a5c-7fe9-4b2d-b5f6-6d165cae20a5,Maxim Walker,7,Sutter Health Plus
...
```

#### Output  
  
The program processes the input data while: 
 
1. Separating the users by insurance company names;
2. Keeping only the latest version entry of each user; and
3. Sorting the entries in each file by last and then first name. 

This is the output for `amerihealth-new-jersey.csv`:

```csv
user_id,name,version,insurance_company
f18107f4-740e-4c23-b87e-3acc30c19856,Destiney Gallegos,1,AmeriHealth - New Jersey
f7ea7674-9eb2-4805-844e-f2c763cb042b,Jaylin Holt,2,AmeriHealth - New Jersey
89209c5d-f6ec-4818-b9c7-18f1fa9a06eb,Skyla Mcintyre,1,AmeriHealth - New Jersey
6c9c2175-6fc9-4ff0-8c39-f69f6e08cba1,Janiah Mueller,1,AmeriHealth - New Jersey
```

The output files get written into `\output` directory.
