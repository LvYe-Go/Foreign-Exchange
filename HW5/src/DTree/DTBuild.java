package DTree;

import Cassandra.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import com.datastax.driver.core.Configuration;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import Cassandra.cassandra;
/**
 * 
 * @author jingyu
 * DT Build Class 
 */




import org.apache.hadoop.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;

public class DTBuild {
      public static void main(String[] args){
    	  // get connect to cassandra db  
    	  cassandra.init(DTUtli.SESSION_NAME); // init 
    	  
    	  
          List<String> featureList = new ArrayList<String>(); // Init lit to store all current all features
          DTUtli.map = new HashMap<String, Integer>();
          // Store the feature
          for(int i = 0; i < DTUtli.FEATURE_NUM; i++){
        	  featureList.add(DTUtli.features[i]);
        	  DTUtli.map.put(DTUtli.features[i], i);
          }
          // Training using 80 % data 
          TreeNode root =  genNode(DTUtli.TrainFileName, featureList, false);
          
          // To Test the rest 20% data 
          double res = Test(root, DTUtli.TestFileName, DTUtli.map);
          System.out.println("The accuracy is :" + res);
      }

      
      /**
       * Test the decision tree
       * @param root : The tree root pointer 
       * @param filename:  filename to readIn from testData
       * @param map : store the feature to index 
       * @return the accuracy 
       * @throws NumberFormatException
       * @throws IOException
       */
      
      public static double Test(TreeNode root, String filename, HashMap<String, Integer> map) {
    		return DTTest.testTree(root, filename, map);
    	}
	  /**
	   * Filter out the dirt data in the input file 
	   * @param str
	   * @return
	   */
	  public static boolean isDirtData(String str){
	  		if(str.isEmpty() || str.contains("Infinity")){
			  return true;
	  		}
	  		String[] parts = str.split(" ");
	  		if(parts == null || parts.length < 8) {
	  			return true;
	  		}
	  		return false;
	  }
	 /**
	  * 
	  * @param filename: Input from the data
	  * @param feature: The feature from the parent 
	  * @return
	  * @throws IOException
	  */
      public static TreeNode genNextLevelNodes(String filename, String feature, String label) {
    	  int num_increase = 0, num_decrease = 0;
    	  // get from cassandra instead of from file i/o
    	  ResultSet res = cassandra.session.execute("SELECT * FROM "+ filename);
    	  for(Row row : res) {
				if((row).getInt(DTUtli.map.get(feature) + 3) == Integer.parseInt(label)){
					// If the same with the label, increase count added 
					if((row).getInt(DTUtli.map.get(feature)) == Integer.parseInt(DTUtli.IS_INCREASE)) 
						num_increase++; //If the bid_direction is upward 
					else num_decrease++; //If the bid_direction is downward 
				}
		  }
		  TreeNode node = new TreeNode();
		  // We do not accept double in label , so not avg here, 
		  // Take the  1 as upward  and -1 as downward 
		  node.setFeature(DTUtli.LABEL);
		  if(num_increase >= num_decrease) node.label = 1; 
		  else node.label = -1;
		  return node;
      }
      
      /**
	   * genNode to use the rest feature and input from the different files to generate new node ,and
	   * give value to the new label;
	   * @param filename: filename for put in 
	   * @param featureList
	   * @return
	   * @throws IOException
	   */
	  public static TreeNode genNode(String filename, List<String> featureList, boolean isRandomForestData){
		  if(featureList.size() == 1) {
			  // The last feature in Test
			  TreeNode node = new TreeNode(DTUtli.count, featureList.get(0));
			  DTUtli.count++; // the node count increases
			  if(DTUtli.label.equals(DTUtli.IS_INCREASE)) {
				  // Give different file name for the node data 
				  node.left = genNextLevelNodes(DTUtli.FILE_PREFIX + (node.num - 1) 
						  	+ DTUtli.label, node.feature, DTUtli.IS_INCREASE);
				  node.right = genNextLevelNodes(DTUtli.FILE_PREFIX + (node.num - 1)
						    + DTUtli.label, node.feature,DTUtli.IS_DECREASE);
				  DTUtli.label = DTUtli.IS_DECREASE;
			  }else{
				// Give different file name for the node data 
				  node.left = genNextLevelNodes(DTUtli.FILE_PREFIX + (node.num - 2) 
						    + DTUtli.label, node.feature, DTUtli.IS_INCREASE);
				  node.right = genNextLevelNodes(DTUtli.FILE_PREFIX + (node.num - 2)
						    + DTUtli.label, node.feature, DTUtli.IS_DECREASE);
				  DTUtli.label = DTUtli.IS_INCREASE;
			  }
			  return node;
		  }else{
			  // Get the max Information gain among the rest of feature list
			  int splitIndex = getMaxIGIndex(filename, featureList, isRandomForestData);
			  
			  TreeNode node = new TreeNode(DTUtli.count, featureList.get(splitIndex));
			  splitFiles(node, filename);
			  DTUtli.count++;
			  
			  List<String> newFeatureList_left = new ArrayList<String>(featureList);
			 // If is randomDorest can use it again in the future 
				  newFeatureList_left.remove(node.feature);  // remove the found feature, not remove if isRandomForestData 
			  
			  List<String> newFeatureList_right = new ArrayList<String>(featureList);
				  newFeatureList_right.remove(node.feature);   // remove the found feature, not remove if isRandomForestData 
			  
			  node.left = genNode(DTUtli.FILE_PREFIX + node.num + DTUtli.IS_INCREASE, newFeatureList_left, isRandomForestData);
			  node.right = genNode(DTUtli.FILE_PREFIX + node.num + DTUtli.IS_DECREASE, newFeatureList_right, isRandomForestData);
			  return node;
		  }
	  }
      
      /**
       * get the max IG Index (which feature) from the list of rest features
       * @param filename: filename input from the left generating file and right generating file
       * @param feature,
       * @param feature,
       * @return isRandomForestData,
       */

  	public static int getMaxIGIndex(String filename, List<String> featureList, boolean isRandomForestData){
  		int featureSize = featureList.size();
  		
  		if(featureSize == 0){
  			return 0; // corner cases
  		}
  		
  		int parents_count[][] = new int[featureSize][2];
  		// The total count for corresponding children
  		int children_count[][] = new int[featureSize][4];
  		/* x = 0, y = 0 =>  children_count[feature_index][0]
  		 * x = 0, y = 1 =>  children_count[feature_index][1]
  		 * x = 1, y = 0 =>  children_count[feature_index][2]
  		 * x = 1, y = 1 =>  children_count[feature_index][3]
  		 * */
  		
  		// Read data from the parts data

		HashMap<Integer, Integer> cur_map = new HashMap<Integer, Integer>();
  		for(int i=0; i< featureSize; i++){
  			cur_map.put(i, DTUtli.map.get(featureList.get(i)));
  		}
		// Calculate the ratio 
		double total_line = 0; 
  	   // get from cassandra instead of from file i/o
  	    ResultSet res = cassandra.session.execute("SELECT * FROM "+ filename);
  	    for(Row row : res) {
			if(isRandomForestData){
				Random rand = new Random();
				if(rand.nextInt(3) == 1){
					continue;  // 1/3 ratio to skip this line 
				}
			}
		   
			for(int i = 0; i < featureSize; i++){
				int value = new Integer(row.getInt(cur_map.get(i) + 3));
				if(value == Integer.parseInt(DTUtli.IS_INCREASE)){
					// parent counts increase 
					parents_count[i][1]++;
					// corresponding children counts increase 
					if(row.getInt(cur_map.get(7)) == Integer.parseInt(DTUtli.IS_INCREASE)) 
						children_count[i][3]++;
					else children_count[i][2]++;
				}else{
					// parent counts increase 
					parents_count[i][0]++;
					// corresponding children counts increase 
					if(row.getInt(cur_map.get(7)) == Integer.parseInt(DTUtli.IS_INCREASE)) 
						children_count[i][1]++;
					else children_count[i][0]++;
				}
			}
		}
  		
		// IG = H(Y)-H(Y|X_i), here H(Y) is same for them
  		double[] prob = new double[featureSize];
  		for(int i=0; i < featureSize; i++){
  			int count_x0 = 0, count_x1 = 0 ;
            double p_x0, p_x1,p_x0_y0, p_x0_y1, p_x1_y0, p_x1_y1;
            
            count_x0 = children_count[i][0] + children_count[i][1];
            count_x1 = children_count[i][2] + children_count[i][3];
            

            p_x0 = (double) count_x0/total_line;  p_x1 = 1.0 - p_x0;

            p_x0_y0 = (double) children_count[i][0]/(count_x0); p_x1_y0 = (double) children_count[i][2]/(count_x1);
            p_x0_y1 = (double) children_count[i][1]/(count_x0); p_x1_y1 = (double) children_count[i][3]/(count_x1);

           double prob1 = p_x0*(-p_x0_y0*Math.log(p_x0_y0) - p_x0_y1*Math.log(p_x0_y1));
           double prob2 = p_x1*(-p_x1_y0*Math.log(p_x1_y0) - p_x1_y1*Math.log(p_x1_y1));

           prob[i] = prob1 + prob2; 
            
  		}
  		// get the minH (maxIG) index
  		int minHIndex = DTUtli.getMinIndex(prob, featureSize);
  		return minHIndex; 
  	}
  	
    /**
     * Split files
     * @param node: the node from which to split files 
     * @param filename
     */
  	public static void splitFiles(TreeNode node, String filename) {
  		String file_name_increase = DTUtli.FILE_PREFIX + node.num+ DTUtli.IS_INCREASE;
  		String file_name_decrease = DTUtli.FILE_PREFIX + node.num+ DTUtli.IS_DECREASE;
  		String createQuery = "(id int, avg_bid int, bid_diff int, avg_spread int, "
  				             + "aid_direction int, label int, PRIMARY KEY (id) )";
  	    // left children from no-sql 
  		String createTable = "create table if not exists ";
  		cassandra.session.execute(createTable + file_name_increase+ " " + createQuery);
  		// right_children from no-sql 
  		cassandra.session.execute(createTable + file_name_decrease+ " " + createQuery);
  		
  		cassandra.session.execute("TRUNCATE exchange."+ file_name_increase);
  		cassandra.session.execute("TRUNCATE exchange."+ file_name_decrease);
		
  		cassandra.output(filename, node);
  	}
 }
 
