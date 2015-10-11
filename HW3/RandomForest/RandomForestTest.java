package RandomForest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

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
	public static double Test(List<TreeNode> rootList, String filename, List<Boolean> resultList) throws NumberFormatException, IOException{
		RandomForestUtli.file = new File(filename);
		RandomForestUtli.fileReader = new FileReader(RandomForestUtli.file);
		RandomForestUtli.bufferedReader = new BufferedReader(RandomForestUtli.fileReader);
		int totalLines = 0, correctNum =0; // total correctNum
		String str = null;			
		while ((str = RandomForestUtli.bufferedReader.readLine()) != null) {
			if(DTBuild.isDirtData(str)) continue;
			totalLines++;
			String[] parts = str.split(" ");
			int matchNum = 0, notMatchNum = 0;
			for(int i = 0; i < rootList.size() ; i++){
				boolean result = resultList.get(i);
				TreeNode temp = rootList.get(i);
			    if(parts[7].equals(DTUtli.IS_INCREASE)) temp = temp.left;
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
			int res = (matchNum >= notMatchNum)? 1:0; //choose majority
			// Whether it is match according to the result of this tree
			if(Integer.parseInt(parts[7]) == res) correctNum++;
		}
		RandomForestUtli.fileReader.close();
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
	public static double testRandomForest(String filename, TreeNode root) throws NumberFormatException, IOException{
		// read data from the test file
		RandomForestUtli.file = new File(filename);
		RandomForestUtli.fileReader = new FileReader(RandomForestUtli.file);
		RandomForestUtli.bufferedReader = new BufferedReader(RandomForestUtli.fileReader);
		int totalLines = 0, correctNum =0;
		String str = null;			
		while ((str = RandomForestUtli.bufferedReader.readLine()) != null) {
			if(DTBuild.isDirtData(str)) continue;  // filter dirty words 
			totalLines++;
			String[] parts = str.split(" ");  // split by space 
			TreeNode temp = root;  // iterate to find 
			for(int i=0; i < DTUtli.map.size(); i++){
				if(parts[7].equals(DTUtli.IS_INCREASE)) temp = temp.left;
				else temp = temp.right;
			}
			if(temp.label ==Integer.parseInt(parts[7])) correctNum++;
		}
		RandomForestUtli.fileReader.close();
	    return correctNum/(double)totalLines;
	}
}