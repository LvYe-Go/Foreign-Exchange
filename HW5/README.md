Author: Jing Yu

Andrew ID: jingyu

1   New tools and lib learned 
 
 Map -> shuffle -> reduce  : In RandomForest.java 

 Serailze/ Deserailze : RandomForestUtil.java 

(1) Gson 2.2 to convert tree into String 
(2) Hadoop Mapreduce Client Core » 2.7.1
(3)Handling plits N lines of input as one split.
=> https://hadoop.apache.org/docs/r2.7.0/api/org/apache/hadoop/mapred/lib/NLineInputFormat.html

2   Method 

Implement Map-Reduce in RandomForest.java 
(1) Import packages
(2) Set Configure 
(3) Set Job including input, outputform, mapper calss, reduce calss 
(4) Mapper result into Reducer
(5) Serailze the decisionTree root
(6) Deserailze the decisionTree root into string

3  Files  Layout

(1)cassandra.java provide: init cassandra cluster and specific session timeout .
Decision Tree Package: 
(1) DecisionTree.java : main to init parameters and call other functions 

(2) DecisionTree.java : Test decision tree input from canssdra instead of File I/O

(3) DecisionTree.java : Utlis parameters for decision tree

RandomForest Package:

(1) RandomForest.java : MapReduce process. main to init parameters and call other functions

(2) RandomForestTest.java : Test random forest tree input from canssdra instead of File I/O

(3) RandomForestUtil.java : Serailze/ Deserailze process. Utlis parameters for random forest tree

====> Bonus Part: LocalClientTest.java

4 Need Improvement 

Can compare speed with implementing Map Reduce method on desion tree and need furthere analyse.
