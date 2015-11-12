package RandomForest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.xml.soap.Text;

import org.apache.hadoop.mapreduce.Job;

import com.datastax.driver.core.Configuration;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import Cassandra.cassandra;
import DTree.DTBuild;
import DTree.DTUtli;
import DTree.TreeNode;

import org.apache.hadoop.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

/* Reference Using To handle many lines input 
 * Using org.apache.hadoop.mapred.lib   libary 
 * 
 * https://hadoop.apache.org/docs/r2.7.0/api/org/apache/hadoop/mapred/lib/NLineInputFormat.html
 * 
 */
	
public class RadomForest {

	// Pass gloab conf info like : TreeNum
	static Configuration configure = null; 
	public static void main(String[] args) throws IOException { // TODO Auto-generated method stub
		System.out.println("How many random trees do you want to plant? Please input a integer \n");
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		int N = sc.nextInt();  // get input from the user 
		if(N <= 0) return;
		
		configure = new Configuration();
  	    Job job = new Job(configure, DTUtli.MR_JOB_NAME);
  	    conf.set("TreeNum", N);
  	  
		// Set job parameter and handle input 
		DTUtli.InitJob(job);
	}
	
	public static class Map extends Mapper<LongWritable, LongWritable, Text,  Text> {
		// Init feature maps 
		 public void init(Context context){
			List<TreeNode> rootList  = new ArrayList<TreeNode>();
			List<Boolean> resultList = new ArrayList<Boolean>();
			 while( configure.get("TreeNum") -- > 0) {
				 // Store the random sqrt(m) feature
					 // Will do SQRT in randomChooseFeature. 
					 List<String> randomfeatureList = randomChooseFeature(DTUtli.features, DTUtli.FEATURE_NUM);
					 
					 DTUtli.map = new HashMap<String, Integer>();
			          // Store the feature
			         for(int i = 0; i < randomfeatureList.size(); i++){
			        	 DTUtli.map.put(randomfeatureList.get(i), i);
			         }
			          // Training using 2/3 * 80 % data  and  sqrt(m) feature,
			          // to build decision tree by map reduce 
				}
		 }
		 
		 /**
		  * Fetch data from train column and buid each decision tree
		  * @param key
		  * @param value
		  * @param context
		  * @throws IOException
		  */
		 public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			  String str = null;
			  cassandra.init(DTUtli.SESSION_NAME); // init 
			  ResultSet res = cassandra.session.execute("SELECT * FROM Train");
	    	  List<String> list = new ArrayList<String>();
			  for(Row row : res) {
					list.add(res.toString());
	    	  }
	          TreeNode root = DTBuild.genNode(value.toString(), list, true);
	          
	          // Serilze the decisionTree root 
	          String rootToString = RandomForestUtli.treeSer(root);
	          context.write(key, new Text(rootToString));
		 }
	}
		 
		/**
		 * Reducer:  Get written oupput from mapper(above) ,  shuffle by keyword and create forests
		 * @author jingyu
		 */
	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws InterruptedException, IOException {
			String res = null;
			for (Text value : values) {
					res += value.toString() +"\n";
				}
			    Text output = new Text(res);
				context.write(key, output);
			}
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
