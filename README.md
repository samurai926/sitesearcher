# sitesearcher
This program reads in a file with URLs and scrapes each target 
for a given search term(s).  Resulting counts are then written 
to a local output file.

### Prerequisites

To build and run the executable jar file you'll need:

```
Maven
JRE 1.8+
```

### Binary distribution

For convenience I uploaded sitesearcher.jar to my Drive:

```
https://drive.google.com/open?id=1NGXvtRAAgVaLagP-lTmebb9sW2QOc-Ll
```

Steps to compile and run the program :
1. Download the git project
2. Run maven install
3. The resulting executable jar file is /target/sitesearcher.jar
4. To run the jar file cd to jar directory and issue:  java -jar ./sitesearcher.jar
5. The output is written to results.txt in the working directory

Sample output:
```
[INFO] Thread-16 {url: https://www.economist.com/, matches: 72}
[INFO] Thread-11 {url: https://www.cafepress.com/, matches: 13}
[INFO] Thread-7 {url: https://www.upenn.edu/, matches: 11}
[INFO] Thread-17 {url: https://www.meetup.com/, matches: 31}
[INFO] Thread-15 {url: https://www.ehow.com/, matches: 1122}
[INFO] Thread-13 {url: https://www.netvibes.com/, matches: 7}
[INFO] Thread-8 {url: https://www.wiley.com/, matches: 8}
[INFO] Thread-4 {url: https://www.liveinternet.ru/, matches: 0}
[INFO] Thread-18 {url: https://www.skyrock.com/, matches: 5}
...
```

## Automated Tests

There is a JUnit test suite included in the project that exercises all the
supporting functions of the entry point.  

### Running the tests

The test suite gets automatically executed during the Maven install process 
but you can run the suite manually by running the following class from your 
favorite IDE:

```
/sitesearcher/src/test/java/dev/sitesearcher/AppTest.java
```

## This project was built with

* [Eclipse](https://www.eclipse.org/) - The IDE
* [Maven](https://maven.apache.org/) - Dependency Management

