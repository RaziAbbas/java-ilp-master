#!/bin/sh

if [ ! -d ledger-grpc/ILP_gRPC_API ] ; then
    cd ledger-grpc && git clone https://github.com/everis-innolab/ILP_gRPC_API
fi
