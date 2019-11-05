# Availity Exercises

### Parentheses Validator 

The Lisp code parentheses validator validates parentheses of either string input or file input. The program can be
run as follows (Gradle tasks):

`> gradlew runChecker --args "((foo)bar)"`\
`> gradlew runChecker --args "string ((foo)bar)"`\
`> gradlew runChecker --args "file C:/tmp/long-balanced.lisp"`\
`> gradlew runChecker --args "file long-balanced.lisp"`

Note that the unit tests are there only for the parser and they can be run by:

`> gradlew test`

### Enrollment File Parsing

The program reads the content of the file and separates enrollees by insurance company in separate files by writing
out the files. The contents of each file are sorted by last and first name (ascending).  In case of any duplicate
user identifiers, for the insurance company, only the record with the highest version will be included. 

`> gradlew runParser --args "enrollments.csv output/"`\
`> gradlew runParser --args "C:/tmp/enrollments.csv C:/tmp/"`
