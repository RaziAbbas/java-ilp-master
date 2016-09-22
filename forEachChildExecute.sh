#!/bin/bash
DIR=`pwd`
SUBMODULES="./java-crypto-conditions ./java-ilp-common ./java-ilp-common-api ./java-ilp-core ./java-ilp-ledger-api ./java-ilp-ledger-simple"
for dirI in $SUBMODULES ; do
   cd $dirI
   pwd
   $*
   cd $DIR
done

