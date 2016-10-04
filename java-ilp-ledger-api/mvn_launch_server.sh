#!/bin/bash
mvn -X exec:java -DdefaultVersion=0.3.0-SNAPSHOT -Dexec.mainClass="org.interledger.ilp.ledger.api.Main"
