package RandomForest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import DTree.DTBuild;
import DTree.DTUtli;
import DTree.TreeNode;

public class RadomForest {

	public static void main(String[] args) throws IOException { // TODO Auto-generated method stub
		System.out.println("How many random trees do you want to plant? Please input a integer \n");
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		int N = sc.nextInt();  // get input from the user 
		if(N <= 0) return;
		
		List<TreeNode> rootList  = new ArrayList<TreeNode>();
		List<Boolean> resultList = new ArrayList<Boolean>();
		
		while( N-- > 0) {
			 // Store the random sqrt(m) feature
			 // Will do SQRT in randomChooseFeature. 
			 List<String> randomfeatureList = randomChooseFeature(DTUtli.features, DTUtli.FEATURE_NUM);
			 
			 DTUtli.map = new HashMap<String, Integer>();
	          // Store the feature
	         for(int i = 0; i < randomfeatureList.size(); i++){
	        	 DTUtli.map.put(randomfeatureList.get(i), i);
	         }
	          // Training using 2/3 * 80 % data  and  sqrt(m) feature, 
			  // Use 1/3 ratio do not read them in the file !!  
	          // Set the last parameter as true. s
	          TreeNode root = DTBuild.genNode(DTUtli.TrainFileName, randomfeatureList, true);
	          
	          rootList.add(root);
			  double accuracy = RandomForestTest.testRandomForest(DTUtli.TestFileName, root);
			  // Pick the majority 
			  if(accuracy >= RandomForestUtli.THRESHOLD ) resultList.add(true);	
			  else resultList.add(false);			
			}
		    double accuracy = RandomForestTest.Test(rootList, DTUtli.TestFileName, resultList);
		    System.out.println("Random Forest accuracy is : "+accuracy);
	}
	
	/**
	 * Randomly choose Sqrt(n) number of 
	 * @param features
	 * @param n
	 * @return
	 */
	public static List<String> randomChooseFeature(String[] features, int n) {
		List<String> list = new ArrayList<String>();
		int m = (int) Math.sqrt((double) n);
		 System.out.println(m +" random features in total");
		Random rnd = new Random();
		for(int i = 0; i < m; i++){
			int random = rnd.nextInt(n);  // pick from 0 to n-1 
			String feature = features[random];
			if(list.contains(feature)) {
				i--;
				continue;
			}
			list.add(feature); // add to the list
		}
		return list;
	}

}
