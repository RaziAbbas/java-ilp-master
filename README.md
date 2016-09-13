# Java ILP master repository [![gitter][gitter-image]][gitter-url] [![CI][CI-image]][CI-url] 

[gitter-image]: https://badges.gitter.im/interledger/java.svg
[gitter-url]: https://gitter.im/interledger/java

[CI-image]: https://travis-ci.org/everis-innolab/java-ilp-master.svg?branch=master
[CI-url]: https://travis-ci.org/everis-innolab/java-ilp-master

> This project serves as an umbrella for all java-related projects, ensuring all quality rules (eg: checkstyle) and common usages applies.


## Usage

### Step 1: Clone repo

``` sh
git clone https://github.com/interledger/java-ilp-master

cd java-ilp-master
```
### Step 2: init and get all submodules up-to-date

``` 
git submodule init
git submodule update
git submodule foreach git pull origin master

```

### Step 3: Install

Main build system is gradle based, but there is a task -writePom- in order to get a maven pom file. That pom file is also provided already in this and child subprojects, but issuing a ***gradle writePom*** will update all pom's. On every change to [gradle.build](gradle.build) don't forget to execute the *writePom* task.


#### Gradle:
``` 
gradle clean install check

```

#### Maven: 
``` 
mvn clean install checkstyle:check

```

## Contributors

Any contribution is very much appreciated! [![gitter][gitter-image]][gitter-url]

## License

This code is released under the Apache 2.0 License. Please see [LICENSE](LICENSE) for the full text.
