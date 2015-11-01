Author: Jing Yu
Andrew ID: jingyu

1. Install and Config Cassandra 
======================================================================
I use the dependencies provided from piazza :) 

Creating Cassandra:
sudo bin/cassandra -f
-udt=netty-transport-udt-4.0.23.Final.208198c]
INFO  05:43:19 Starting listening for CQL clients on localhost/127.0.0.1:9042...
INFO  05:43:19 Binding thrift service to localhost/127.0.0.1:9160
INFO  05:43:19 Listening for thrift clients...

➜  dsc-cassandra-2.1.11  bin/cqlsh 127.0.0.1
Connected to Test Cluster at 127.0.0.1:9042.
[cqlsh 5.0.1 | Cassandra 2.1.11 | CQL spec 3.2.1 | Native protocol v3]
Use HELP for help.
In another console.
cqlsh> describe keyspaces

system_traces  system

cqlsh>

2. DataSet
======================================================================
Create Two tables from input into Cassandra, test, train 
(1). id primary key for each row 
each column like map<key, map<key, value>> 

column_1  avg_bid : the avg of bid in one hour

column_2 bid_diff: the diff between the max_bid and min_bid

column_3 avg_spread: average of the spread in one hour

column_4 the aid_direction

Still 2/3 of the data of in training table, 1/3 data from test table.

3. Methods
======================================================================
   (1) Install config canssdra on local machine 

   (2) Init cassandra cluste, seesion , timeout etc

   (3) Output Code piece to write into cassandra instead of File I/O

   (4) Information Gain calculation, Decision Tree, Test random forest input from cassandra instead of   File I/O

   (5) Remove all I/O exception from input and out put 

4. File Management
======================================================================
Cassandra package: 

(1)cassandra.java provide: init cassandra cluster and specific session timeout .

Decision Tree Package: 
(1) DecisionTree.java : main to init parameters and call other functions 
(2) DecisionTree.java : Test decision tree input from canssdra instead of File I/O 

(3) DecisionTree.java : Utlis parameters for decision tree

RandomForest Package: 

(1) RandomForest.java : main to init parameters and call other functions 

(2) RandomForestTest.java  : Test random forest tree input from canssdra instead of File I/O 

(3) RandomForestUtil.java : Utlis parameters for random forest tree

5. Can be improved  
======================================================================
(1) Can find the most suitable random forest tree number

(2) Can use Maven let it more automatic 

(3) structure can be more splified