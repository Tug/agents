#!/bin/bash

HOME=./bin
JDOM=./lib/jdom.jar
MATH=./lib/commons-math-1.1.jar
REPAST=../../Repast-3.1/RepastJ/repast.jar
LOCAL_CLASSPATH=$HOME:$JDOM:$REPAST:$MATH

# pass the configuration name as an argument
java -cp $LOCAL_CLASSPATH epfl.lia.logist.LogistPlatform config/default.xml $1