Java ILP master repository [![Build Status](https://travis-ci.org/everis-innolab/java-ilp-master.svg?branch=master)](https://travis-ci.org/everis-innolab/java-ilp-master)
========================================================

This project serves as an umbrella for all java-related projects, ensuring all quality rules (eg: checkstyle) and common usages applies.



#### Install:

Just clone and get all submodules up-to-date:

``` 
git submodule foreach git pull origin master

```

#### Build:
Main build system is gradle based, but there is a task -writePom- in order to get a maven pom file. That pom file is also provided already in this and child subprojects, but issuing a ***gradle writePom*** will update all pom's. On every change to [gradle.build](gradle.build) don't forget to execute the *writePom* task.


Gradle:
``` 
gradle clean install check

```

Maven: 
``` 
mvn clean install checkstyle:check

```

Contributors
---------------
Any contribution is very much appreciated! [![Join the chat at https://gitter.im/interledger/Lobby](https://badges.gitter.im/interledger/java.svg)](https://gitter.im/interledger/Lobby)


License
---------------
This code is released under the Apache 2.0 License. Please see [LICENSE](LICENSE) for the full text.