import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DecisonTreeBuild {
	  static  double avg_bid = 0, avg_bid_diff = 0, avg_spread = 0, avg_ask_bid_direction = 0; 
	  static  double total_avg_bid = 0, total_bid_diff = 0, total_avg_spread = 0, total_avg_ask_bid_direction = 0; 
	  
	  final static double eps = 1e-8;
	  
	  public static double count = 0;
	  
	  static int ab_eq_avg_bid = 0, below_avg_bid = 0; 
	  
	  static int ab_avg_bid_diff = 0, below_ab_avg_bid_diff = 0; 
	  
	  static int ab_eq_avg_spread = 0, below_avg_spread = 0; 
	  
	  static int ab_eq_avg_ask_bid_direction = 0, below_avg_ask_bid_direction = 0; 
	  
	  static List<Double> list_Eth = new ArrayList<Double>();
	  
      public static void main(String[] args) throws IOException{
          String InputFileName = "Test.txt";
          readData(InputFileName);
          System.out.println("avg_bid   " + avg_bid);
          System.out.println("avg_bid_diff  " + avg_bid_diff);
          System.out.println("avg_spread  " + avg_spread);
          System.out.println("avg_pre_bid_direction  " + avg_ask_bid_direction);
      }
		  
      public static void readData(String filename) throws IOException {
    	  BufferedReader reader = null;
    	  File file = new File(filename);
		  reader = new BufferedReader(new FileReader(file));  // Read Write Stream 
		  String str = "";
		  String[] parts = null;
		  
		  while((str = reader.readLine()) != null){
			if(str.contains("Infinity") || str.isEmpty() || str.startsWith(" ")) continue; // rid of dirt data.
			parts = str.split(" ");   // split the line with ','
			if(parts.length < 8) continue;
			
			double tmp_avg_bid = Double.parseDouble(parts[4]);
			double tmp_avg_bid_diff = Double.parseDouble(parts[5]);
			double tmp_avg_spread = Double.parseDouble(parts[6]);
			double tmp_avg_ask_bid_direction = Double.parseDouble(parts[7]);
			 
			Feature feature = new Feature(tmp_avg_bid, tmp_avg_bid_diff, tmp_avg_spread, tmp_avg_ask_bid_direction);
			
			total_avg_bid += Double.parseDouble(parts[4]);
		    total_bid_diff += Double.parseDouble(parts[5]);
		    total_avg_spread += Double.parseDouble(parts[6]);
		    total_avg_ask_bid_direction += Double.parseDouble(parts[7]);
		    count++;
          }
		  avg_bid = total_avg_bid/count;
		  avg_bid_diff = total_bid_diff/count;
		  avg_spread = total_avg_spread/count;
		  avg_ask_bid_direction =  total_avg_ask_bid_direction/count;
     }
}
