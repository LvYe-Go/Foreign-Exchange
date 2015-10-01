# Foreign-Exchange
Jing Yu  HW2

Andrew ID : jingyu

Feature Pick: 
(1) avg_bid : the avg of bid in one hour 
(2) bid_diff: the diff between the max_bid and min_bid
(3) avg_spread: average of the spread in one hour 
(4) the aid_direction
Like 
EUR/USD 20130101 21 1.3202500000000001 1.8178099934105748E-4 9.089049967052874E-5 1 1
EUR/USD 20130101 22 1.3207196377649404 0.0010687890847071529 2.6193855352800454E-4 1 1
EUR/USD 20130102 23 1.3196139165564933 9.926648884579174E-4 3.2838969011858983E-4 -1 -1
EUR/USD 20130102 0 1.3198704359605782 4.317985546111233E-4 4.

(2) Data: 8000+  as training data for from 201401 to 201505 ERU/USD 
          2000+  as test data  201301 and 201506 - 201510

(3)In every level of node in decision tree, infomation gain is 
   IG =H(Y)-H(Y|X_i).   Because children share the same H(Y), so the we cn just to get the minimun conditional enthropy mapping to the maximum IG. 
   I use two matrix to present them.

Accurcy: The accuracy is :0.5015234613040829

To Improve: Data not such pure and the model is not exact suitable and accuracy can be boost.

