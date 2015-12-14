package lda_test;

import java.util.ArrayList;
import java.util.List;

/**
 * First level Node structure
 * @author jingyu
 *
 */
public class FLNode {
	 String name;
	 List<SLNode> children = new ArrayList<SLNode>(); 
     public FLNode(String name, List<SLNode> children){
    	 this.name = name;
         this.children = children;
     }
     
     public void setChildren(List<SLNode> children){
    	 this.children = new ArrayList<SLNode>(children);
     }
}