# Java ILP master repository [![gitter][gitter-image]][gitter-url] [![CI][CI-image]][CI-url] 

[gitter-image]: https://badges.gitter.im/interledger/java.svg
[gitter-url]: https://gitter.im/interledger/java

[CI-image]: https://travis-ci.org/everis-innolab/java-ilp-master.svg?branch=master
[CI-url]: https://travis-ci.org/everis-innolab/java-ilp-master

> This project serves as an umbrella for all java-related projects, ensuring all quality rules (eg: checkstyle) and common usages applies.

Some note about sub-projects:
* java-crypto-conditions: reference (git submodule) to external project. After cloning this project just execute:
```
  $ git submodule init
  $ git submodule update
```
* java-ilp-ledger-api/java-ilp-ledger-simple: Basic code for the Java ILP ledger simple implementation. The idea is to make it compliant with the rest API and Websocket API found in five-bells-ledger. 
  Note: the -api suffix can be misleading since it's actually the REST service API as seen by external clients, while the internal java API is in java-ilp-ledger-simple. Probably renaming to java-ilp-ledger-simple-rest-api / java-ilp-ledger-simple-api will be more appropiate.
* java-ilp-core: Core interfaces / entities.
* java-ilp-common / java-ilp-common-api: Common conf.  
* java-ilp-connector-api: Not yet started (java implementation of ILP connector).

## Usage

### Step 1: Clone repo

``` sh
git clone https://github.com/interledger/java-ilp-master

cd java-ilp-master
```


### Step 2: Install

Either use gradle:
```
    $ gradle install
```
or maven:
```
    $ gradle writePom # <- Optional if poms are not up-to-date with gradle (someone forgot to update/commit poms)
    $ mvn install
```
Note: executing gradle writePom in java-ilp-master will automatically update all poms in child projects.

On every change to [gradle.build](gradle.build) don't forget to execute the *writePom* task.

To create the eclipse .project / .classpath files:
```
    $ gradle eclipse
```
(Then use File -> Import ... -> Existing projects from workspace and select the "Search for nested projects")

### Step 3: Execute java-ilp-ledger 
With Eclipse/Netbeans:
   Run/debug java-ilp-ledger-api/src/main/java/org/interledger/ilp/ledger/api/Main.java as a java application.

With gradle: 
```
     $ gradle :java-ilp-ledger-api:launchServer
```
With maven: (TODO: gradle execution and manual execution pending)
```
     $ mvn install # (if not yet done)
     $ cd java-ilp-ledger-api  
     $ ./mvn_launch_server.sh
```

#### Gradle:

``` 
./gradlew clean install check

```

#### Maven: 
``` 
mvn clean install checkstyle:check

```

## Contributors

Any contribution is very much appreciated! [![gitter][gitter-image]][gitter-url]

## License

This code is released under the Apache 2.0 License. Please see [LICENSE](LICENSE) for the full text.
