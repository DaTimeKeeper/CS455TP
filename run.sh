#!/bin/bash

$HADOOP_HOME/bin/hadoop jar ./app/build/libs/app.jar TP.App $1 /TP/inputs /TP/outputs


$HADOOP_HOME/bin/hadoop fs -get /TP/outputs/part-r-00000 ~/cs455/TP/CS455TP/outputs/
rm ~/cs455/TP/CS455TP/outputs/$1
mv ~/cs455/TP/CS455TP/outputs/part-r-00000 ~/cs455/TP/CS455TP/outputs/$1
$HADOOP_HOME/bin/hadoop fs -rm -r /TP/outputs/