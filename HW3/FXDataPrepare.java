import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.StringTokenizer;

public class FXDataPrepare {
	static SimpleDateFormat FORMART = new java.text.SimpleDateFormat("yyyyMMdd HH:mm:ss");    // Time alignment prepare 
	DecimalFormat df = new DecimalFormat("###.00000");
    
	final static int INTERVAL = 60  * 60 * 1000;  // Window Size 
	final static int TIME_SPAN = 1; // range one hour 

	public static void main(String[] args) throws IOException {
	   
	    File root = new File("data");
	    File[] subFile = root.listFiles();
	    System.out.println(subFile.length);
	    int line = 0;
	   // String outputFileName="DecTreeData.txt";  
	    String outputFileName="TestData.txt";  
	    BufferedReader reader = null;
	    BufferedWriter out = null;
	    
	    for(int i = 0; i < subFile.length; i++) {  // 多个文件
			// File file = new File("EURUSD-2015-01.csv");
	    	String filename = subFile[i].getName();
	    	if(!filename.endsWith(".csv")) continue;
	    	File file = new File(filename);
			
		    System.out.println(subFile[i].getName());
		    
			Date timestamp = null;
			String[] parts = null;
			
			try {
				reader = new BufferedReader(new FileReader(file));  // Read Write Stream 
				out = new BufferedWriter(new FileWriter(outputFileName, true));
				
				// Initalize prepare variables 
				String str = null;
				double max_ask_in_hour = 0, min_ask_in_hour = Double.MAX_VALUE, avg_ask_in_hour = 0; // Attributes
				double max_bid_in_hour = 0, min_bid_in_hour = Double.MAX_VALUE, avg_bid_in_hour = 0; // Attributes
				double max_spread_in_hour = 0, min_spread_in_hour = Double.MAX_VALUE, avg_spread_in_hour = 0; // Attributes
				int bid_direction = 0;
				int ask_direction = 0;
				
				int lastDay = -1;
			    long lastHour = 0;
			    double last_avg_bid = 0; 
			    double last_avg_ask = 0; 
			    int curHour = 0;
			    
			    double totalAsk = 0;
			    double totalBid = 0;
			    double totalSpread = 0;
			    
			    double count = 0; 
			    String dateStr = null;
			    String lastputLine = null;
			    
			    // To predict the furture , I records the data of the last line but not write in 
			    // When get the information of this line , I start to write  the data of the last line 
			    // So the direction is furture to the last line 
			     
			    
			    // Good format 
			   /* String titleStr =   String.format("%-9s","Type") +   // Title 
						    		String.format("%-9s","Date") + 
						    		String.format("%-9s", "Hour")+
						            String.format("%-20s", "avg_bid") +
						            String.format("%-20s", "bid_diff") + 
						            String.format("%-20s", "avg_spread") +
						            String.format("%-20s", "pre-bid-direction")+
			                        String.format("%-20s", "bid-direction")+"\n"; */
						    
			   // System.out.println(titleStr);
			   
			   // out.write(titleStr); 
				
				while((str = reader.readLine()) != null){
					parts = str.split(",");   // split the line with ','
					timestamp = FORMART.parse(parts[1]);  // The second column is the time 
					
					int curDay = timestamp.getDay();
					curHour = timestamp.getHours();
				    dateStr = parts[1].substring(0, 8);
					
					double curBid = Double.valueOf(parts[2]);  // Parse the input 
					double curAsk = Double.valueOf(parts[3]);
					double curSpread = 0;
					
					if(lastDay == -1 ) {
						// Not the same day, data not continuous 
						lastHour= curHour;
						lastDay = curDay;
						bid_direction = 0;
						// Reset our status 
						count = 1;
						totalAsk = curAsk;
						totalBid = curBid;
						
						// If the first line data, keep as it is ,
						// also can be outut in the file 
						last_avg_bid = avg_bid_in_hour;
						last_avg_ask = avg_ask_in_hour;
						
						max_ask_in_hour = curAsk;  min_ask_in_hour = curAsk;   // First hour, get the info
						max_bid_in_hour = curBid;  min_bid_in_hour = curBid;
						max_spread_in_hour = curSpread;
						min_spread_in_hour = curSpread;
					} 
					else if(curHour != lastHour){
					// If the  the days are not continuous, we predict does not stop 
					// If the hour number change, we continue predict future ! 
						avg_ask_in_hour = totalAsk/count; 
						avg_bid_in_hour = totalBid/count;
						avg_spread_in_hour = totalSpread/count;
						
				 	// Calculate the direction for both bid and ask  	
						bid_direction = (avg_bid_in_hour - last_avg_bid) > 0? 1: -1;
						ask_direction = (avg_ask_in_hour - last_avg_ask) > 0? 1: -1;
						
						count++;
						line++;
						
						String outputLine = "\n" + parts[0] + " " +
							 	dateStr  + " " + 
							 	lastHour + " " +
					            avg_bid_in_hour+ " " +
					            (max_spread_in_hour - min_spread_in_hour)+ " " +
					            avg_spread_in_hour + " " +
					            ask_direction + " " + bid_direction;
		
						//System.out.println(outputLine);
						//lastputLine = outputLine;
						
						out.write(outputLine);
						
						// Reset data
						// Updated the last data
						lastHour= curHour;
						last_avg_bid = avg_bid_in_hour;
						last_avg_ask = avg_ask_in_hour;
						
						// Reset all the variables 
						count = 0;
						totalAsk = 0;
						totalBid = 0;
						max_ask_in_hour = 0;  min_ask_in_hour = Double.MAX_VALUE;
						max_bid_in_hour = 0;  min_bid_in_hour = Double.MAX_VALUE;
						max_spread_in_hour = 0;  min_spread_in_hour = Double.MAX_VALUE;
					} else{
						// If the time span in one hour
						// Update the max, min , value
						max_ask_in_hour  = Math.max(max_ask_in_hour , curAsk);
						min_ask_in_hour  = Math.min(min_ask_in_hour , curAsk);
						
						max_bid_in_hour  = Math.max(max_bid_in_hour , curBid);
						min_bid_in_hour  = Math.min(min_bid_in_hour , curBid);
						
					   // Spread e= (Ask - Bid) / Bid , always is positive 
						curSpread = (curAsk - curBid)/curBid;
						
						max_spread_in_hour  = Math.max(max_spread_in_hour , curSpread);
						min_spread_in_hour  = Math.min(min_spread_in_hour , curSpread);
						
						// Gather the data  and updated the count and total status 
						totalAsk += curAsk;
						totalBid += curBid;
						totalSpread += curSpread;
						count++;
					}
				}
				System.out.println("line  " + line);
				System.out.println("The process has finished");
			} catch (ParseException e) {
				e.printStackTrace();
			}  catch(IOException e) {
				e.printStackTrace();
			}
		}
		reader.close();
		out.close();
	}
	
}
