package DTree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Test the decision tree
 * @author jingyu
 *
 */
public class DTTest {
		public static double testTree(TreeNode root, String filename, HashMap<String, Integer> map) throws NumberFormatException, IOException{
			
			File file = new File(filename);
			FileReader reader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String str = null;			
			int totalLines = 0, correctNum =0;
			// Input from test data
			while ((str = bufferedReader.readLine()) != null) {
				if(DTBuild.isDirtData(str)) continue; // filter out the incomplete data
				totalLines++;
				String[] parts = str.split(" ");
				TreeNode temp = root;
				for(int i=0; i < map.size(); i++){
					if(parts[6].equals(DTUtli.IS_INCREASE)) temp = temp.left;
					else temp = temp.right;
				}
				// If label matches with real one
				if(temp.label ==Integer.parseInt(parts[7])) correctNum++;
			}
			reader.close();
		    return correctNum/(double)totalLines;
		}
}
