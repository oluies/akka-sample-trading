#!/bin/bash

TEST_CLASSES=$@
VMARGS='-server -Xms512m -Xmx1024m -XX:+UseConcMarkSweepGC -DuseTxLogFile=true -Dbenchmark=false'

# JAVA_HOME can optionally be set here
#JAVA_HOME=/usr/local/jdk6
if [ -n "$JAVA_HOME" ] ; then
  JAVA=$JAVA_HOME/bin/java
else 
  JAVA=java
fi

BENCHMARK_HOME=`dirname $0`/..
DIRLIBS=`ls $BENCHMARK_HOME/lib/*`


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

$JAVA ${VMARGS} -classpath ${LOCALCLASSPATH} org.junit.runner.JUnitCore $TEST_CLASSES