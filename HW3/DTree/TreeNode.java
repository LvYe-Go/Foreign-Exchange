package DTree;
import java.util.List;

/**
 * TreeNode for decisionTree
 * @author jingyu
 *
 */
public class TreeNode {
	public TreeNode left = null; // left child
	public TreeNode right = null; // right child
	public String feature = null; // The feature to split on this node 
	public int label = 0;  // 
	int num = 0; // node number 
	List<String> list = null; // The rest of list of feature to split
	public TreeNode(){}
	
	// constructor
	public TreeNode(int num, String feature) {
		this.num = num;
		this.feature = feature;
	}
	// set feature
	public void setFeature(String feature){
		this.feature = feature;
	}
}
