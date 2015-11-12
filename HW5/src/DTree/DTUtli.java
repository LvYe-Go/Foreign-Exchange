package DTree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.soap.Text;

import org.apache.hadoop.*;
import org.apache.hadoop.mapred.lib.NLineInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;

public class DTUtli {
	// The class for the utilites for the decesion tree
	  public static int FEATURE_NUM = 4; 
	  public final static double eps = 1e-8;
	  public final static String IS_INCREASE = "1";
	  public final static String IS_DECREASE = "-1";
	  public final static String TrainFileName = "DTData.txt";
	  public final static String TestFileName = "TestData.txt";
	  public final static String LABEL = "label";
	  public final static String FILE_PREFIX = "Data";
	  public final static String SESSION_NAME = "exchange";
	  public final static String MR_JOB_NAME = "DT";
	  public final static String OUTPUT_PATH = "/DTtrees";

	  
	  public static HashMap<String, Integer> map = null;
	  public static int count = 0;
	  public static String[] features = {"avg_bid", "bid_diff", "avg_spread", "aid_direction"};
	  public static String label = "1";
	  public static FileWriter fw_l = null, fw_r = null;
	  public static BufferedWriter writer_l = null, writer_r = null;
	  
	  public static double[] stardard = {1.18, 1.3424620754458482E-4, 0.0037, -1 }; 
	  
	  public static int getMinIndex(double[] H, int featureSize){
	  		double min = H[0];
	  		int index = 0;
	  		for(int i = 1; i < featureSize; i++){
	  	        
	  			if(H[i] < min){
	  				min = H[i];
	  				index = i;
	  			}
	  		}
	  		return index;
	  }
	  // DecisionTree serialize
	  public static String treeSer(TreeNode rootNode){
		   Gson gson = new GsonBuilder().create();
		   return gson.toJson(rootNode);
	  }
	  
	  // DecisionTree deserialize 
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
		  job.getConfiguration().setInt("mapreduce.input.lineinputformat.linespermap");
		  FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));
		  job.waitForCompletion(true);
	  }
	  
	 
}
