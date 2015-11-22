Author: Jing Yu
Andrew ID: jingyu
HW6  spark + Scala 

1 New tools and lib learned

Scala : In RandomForest.scala

 (1) Use Scala Eclipse Version 

 (2) Spark 1.5.2

 (3) sbt-0.13.8

2 Method

In  RandomForest.java 

(1) Import packages 

(2) Set Configure , Init Spark , application , port 127.0.01 

(3) Fetch Data from different Cassandra Table 

(4) Iterate traindata to RDD format 

(5) Iterate traindata to RDD format 

(6) Train the model use RandomForest APIs
Reference : https://spark.apache.org/docs/1.2.0/mllib-ensembles.html

(7) Write result into Cassandra

3 Files Layout
RandomForest.scala: Init, featch data, converting foramting, calling mllib APIs to train and test random forest model.

build.sbt: Indicates the version used

4 Performance Result： 

TreeNum |  Accuracy      
--------+----------
3       |    0.5118

5       |    0.5120 

5       |    0.5120 
    
7       |    0.5120 
        
9       |    0.5119 
        
10      |    0.5120
        
10      |    0.5120 

5 Analyse 
The result is little better than my java implementation before but I still not see much difference with the number of tree trained. More number of tree trained the statistics be more stable.  

6 Improvement
Need more experiement to gig out the rule coefficiency with the number of trees used. 

7  Reference: 
1. MLLib 
http://spark.apache.org/docs/latest/mllib-guide.html#mllib-types-algorithms-and-utilities
2. Scala Codes Reference : https://spark.apache.org/docs/1.5.1/mllib-ensembles.html   
3. http://spark.apache.org/docs/latest/ml-guide.<!DOCTYPE html>
4. http://spark.apache.org/docs/latest/mllib-guide.html
