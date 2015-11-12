package DTree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import Cassandra.cassandra;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

/**
 * Test the decision tree
 * @author jingyu
 *
 */
public class DTTest {
		public static double testTree(TreeNode root, String filename, HashMap<String, Integer> map){
			int totalLines = 0, correctNum =0;
			// Input from test data
			ResultSet res = cassandra.session.execute("SELECT * FROM "+filename);
			for (Row row : res) {
				totalLines++;
				TreeNode temp = root;
				for(int i=0; i < map.size(); i++){
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
