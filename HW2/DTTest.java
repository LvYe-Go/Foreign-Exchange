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
			int correct=0;
			int count=0;
			File file = new File(filename);
			FileReader reader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String str = null;			
			// Input from test data
			while ((str = bufferedReader.readLine()) != null) {
				if(DTBuild.isDirtData(str)) continue; // filter out the incomplete data
				count++;
				String[] parts = str.split(" ");
				TreeNode node = root;
				for(int i=0; i < DTUtli.FEATURE_NUM; i++){
					if(parts[map.get(node.feature)].equals("1")) node = node.left;
					else node = node.right;
				}
				// If the value is same as the real world one 
				// if yes, count increase 
				if(node.label ==Integer.parseInt(parts[7])) correct++;
			}
			reader.close();
		    return correct/(double)count;
		}
}
