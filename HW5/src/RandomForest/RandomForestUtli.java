package RandomForest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.xml.soap.Text;

import org.apache.hadoop.mapred.lib.NLineInputFormat;
import org.apache.hadoop.mapreduce.Job;

import DTree.DTBuild;
import DTree.Map;
import DTree.Reduce;
import DTree.TreeNode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RandomForestUtli {
      public final static double THRESHOLD = 0.5;
      public static File file = null;
	  public static FileReader fileReader = null;
	  public static BufferedReader bufferedReader = null;
	  public final static String OUTPUT_PATH = "/DTtrees";
	  
	  /**
	   * DecisionTree serialize
	   * @param rootNode
	   * @return
	   */
	  public static String treeSer(TreeNode rootNode){
		   Gson gson = new GsonBuilder().create();
		   return gson.toJson(rootNode);
	  }

	  /**
	   * Deserialize 
	   * @param str
	   * @return
	   */
	  public static TreeNode treeDer(String str){
		  return new Gson().fromJson(str, TreeNode.class);
	  }
	  
	  public static void InitJob(Job job){
		  job.setJarByClass(DTBuild.class);
		  job.setOutputValueClass(Text.class);
		  job.setOutputKeyClass(Text.class);
		  
		  job.setMapperClass(Map.class);
		  job.setReducerClass(Reduce.class);
		  // NLineInput handle parallel lines input in mapreduce lib 
		  job.setInputFormatClass(NLineInputFormat.class);
		  job.setOutputFormatClass(TextOutputFormat.class);
		  //  MapReduce handle with parallel lines 
		  FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));
		  job.getConfiguration().setInt("mapreduce.input.lineinputformat.linespermap");
		  
		  job.waitForCompletion(true);
	  }
	  
}
