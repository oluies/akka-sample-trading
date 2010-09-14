#!/bin/bash

TEST_CLASSES=$@
#BENCH_PROPS can be defined outside of this script
#export BENCH_PROPS='-DuseTxLogFile=false -Dbenchmark=true -DminClients=1 -DmaxClients=40 -DuseDummyOrderbook=false'
VMARGS='-server -Xms512m -Xmx1024m -XX:+UseConcMarkSweepGC'
VMARGS="$VMARGS $BENCH_PROPS"

# JAVA_HOME can optionally be set here, or outside script
#JAVA_HOME=/usr/local/jdk6
if [ -n "$JAVA_HOME" ] ; then
  JAVA=$JAVA_HOME/bin/java
else 
  JAVA=java
fi

BENCH_HOME=`dirname $0`/..
DIRLIBS=`ls $BENCH_HOME/lib/*`


    if [ -n "$CLASSPATH" ] ; then
      LOCALCLASSPATH=$CLASSPATH
    fi

    for i in ${DIRLIBS}; do 
	if [ "$i" != "${DIRLIBS}" ] ; then
	    if [ -z "$LOCALCLASSPATH" ] ; then
		LOCALCLASSPATH=$i
	    else
		LOCALCLASSPATH="$i":$LOCALCLASSPATH
	    fi
	fi
    done

LOCALCLASSPATH=$BENCH_HOME/conf:$LOCALCLASSPATH

echo "Running " ${VMARGS} ${TEST_CLASSES}
$JAVA ${VMARGS} -classpath ${LOCALCLASSPATH} org.junit.runner.JUnitCore ${TEST_CLASSES}