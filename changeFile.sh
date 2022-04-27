#!/bin/bash
$HADOOP_HOME/bin/hadoop fs -rm /TP/inputs/$1

$HADOOP_HOME/bin/hadoop fs -put ~/cs455/TP/CS455TP/$2 /TP/inputs