# Foreign-Exchange
Jing Yu  HW3

Andrew ID : jingyu

Feature Pick:  
Two of Four of original features, sqrt(n) 

Two among them. 

(1) avg_bid : the avg of bid in one hour 

(2) bid_diff: the diff between the max_bid and min_bid

(3) avg_spread: average of the spread in one hour 

(4) the aid_direction

 2/3 of the data of below , I implment it with  1/3 ratio to skip read in the line.

(2) Data: 5333+  as training data for from 201401 to 201505 ERU/USD 
          2000+  as test data  201301 and 201506 - 201510

(3) Files:  DTUtli class is to store public variable, TreeNde is decision TreeNode DTBuild is main build process

(4) Brave Try:   to use the feature again. I attempted it from random forest work. When pass true to the function, it will not remove the feature form the lsit. But in my naive model, it does not has higher accuracy than the former one, so I do not include it in my code currently.

 List<String> newFeatureList_right = new ArrayList<String>(featureList);
 if(!isRandomForestData) {
      newFeatureList_right.remove(node.feature);   // remove the found feature, not remove if isRandomForestData 
 }

(5) Accuracy: The avg accuracy is : 0.502 =  50.2%

(6)To be Improved: 

   There feature should be more reasonable , to add the affact from other countries. Also, should be more  feature. 

   Data not such pure not only use the average as standard and the model is not exact suitable and accuracy can be boost.

Thanks. 
