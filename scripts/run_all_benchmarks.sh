#!/bin/bash

RUN_SCRIPT=`dirname $0`/run_benchmark.sh

runTests() {
  $RUN_SCRIPT org.samples.trading.basic.BasicPerformanceTest
  $RUN_SCRIPT org.samples.trading.actor.ActorPerformanceTest
  $RUN_SCRIPT org.samples.trading.akka.AkkaPerformanceTest
  $RUN_SCRIPT org.samples.trading.actorbang.ActorBangPerformanceTest
  $RUN_SCRIPT org.samples.trading.akkabang.AkkaBangPerformanceTest
  $RUN_SCRIPT org.samples.trading.akkahawt.AkkaHawtPerformanceTest
}

# All tests with tx logging
export BENCH_PROPS='-DuseTxLogFile=true -Dbenchmark=true'
runTests

# All tests without tx logging
export BENCH_PROPS='-DuseTxLogFile=false -Dbenchmark=true'
runTests

