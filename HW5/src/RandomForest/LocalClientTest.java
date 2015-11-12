package RandomForest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import DTree.DTUtli;
import DTree.TreeNode;

import com.google.gson.Gson;
/**
 * Like the previous Random Forest tree but need deserailze, localClient to Test 
 * @author jingyu
 */
public class LocalClientTest {
	public static void main(String[] args) throws IOException{
		List<TreeNode> rootList = new ArrayList<TreeNode>();
		List<Boolean>  resultList = new ArrayList<Boolean>();
		BufferedReader bufferReader = new BufferedReader(new 
					FileReader(RandomForestUtli.OUTPUT_PATH));
		String str = null;
		TreeNode root = null;
		
	    while ((str= bufferReader.readLine()) != null) {
		   root = RandomForestUtli.treeDer(str);
		   rootList.add(root);
		}
	
	    double accuracy = RandomForestTest.testRandomForest(DTUtli.TestFileName, root);
	     // Pick the majority 
	    if(accuracy >= RandomForestUtli.THRESHOLD ) resultList.add(true);	
	    else resultList.add(false);			
	  
	    double final_accuracy = RandomForestTest.Test(rootList, DTUtli.TestFileName, resultList);
	    System.out.println("Random Forest accuracy is : "+final_accuracy);
	 }
}
