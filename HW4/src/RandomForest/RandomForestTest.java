package RandomForest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import Cassandra.cassandra;
import DTree.DTBuild;
import DTree.DTUtli;
import DTree.TreeNode;

public class RandomForestTest {
	/**
	 * Test the every result for the testfile and store the result in a boolean list
	 * @param TreeNode
	 * @param bool
	 * @param filename
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static double Test(List<TreeNode> rootList, String filename, List<Boolean> resultList){
		int totalLines = 0, correctNum =0; // total correctNum
		// Input from test date from canssdra instead of frile i/o
		ResultSet res = cassandra.session.execute("SELECT * FROM "+filename);
		for (Row row : res) {
			totalLines++;
			int matchNum = 0, notMatchNum = 0;
			for(int i = 0; i < rootList.size() ; i++){
				boolean result = resultList.get(i);
				TreeNode temp = rootList.get(i);
			    if(row.getInt(temp.feature)== Integer.parseInt(DTUtli.IS_INCREASE)) temp = temp.left;
				else temp = temp.right;
			 // add to corresponding counter
				if(temp.label == 0) {  
					if(result) {
						matchNum++;
					} else{
						notMatchNum++;
					}
				}else {
					if(!result) {
						matchNum++;
					} else { 
						notMatchNum++;
					}
				}
			}
			int major_res = (matchNum >= notMatchNum)? 1:0; //choose majority
			// Whether it is match according to the result of this tree
			if(row.getInt(DTUtli.map.get(7)) ==  major_res) correctNum++;
		}
		return correctNum/(double)totalLines;
	}
	
	/**
	 * Check the ratio to match the real result
	 * @param root
	 * @param filename
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static double testRandomForest(String filename, TreeNode root) {
		int totalLines = 0, correctNum =0;
		// Input from test date from canssdra instead of frile i/o
		ResultSet res = cassandra.session.execute("SELECT * FROM "+filename);
		for (Row row : res) {
			totalLines++;
			TreeNode temp = root;
			for(int i=0; i < DTUtli.map.size(); i++){
				if(row.getInt(temp.feature)== Integer.parseInt(DTUtli.IS_INCREASE)) 
					temp = temp.left;
				else temp = temp.right;
			}
			// If label matches with real one
			if(temp.label ==row.getInt(DTUtli.map.get(7))) correctNum++;
		}
	    return correctNum/(double)totalLines;
	}
}