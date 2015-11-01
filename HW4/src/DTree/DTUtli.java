package DTree;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
}
