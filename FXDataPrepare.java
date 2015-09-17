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
import java.util.StringTokenizer;

public class FXDataPrepare {
	static SimpleDateFormat FORMART = new java.text.SimpleDateFormat("yyyyMMdd HH:mm:ss");    // Time alignment prepare 
	DecimalFormat df = new DecimalFormat("###.00000");
	final static int INTERVAL = 60  * 60 * 1000;
	final static int TIME_SPAN = 1; // range one hour 

	public static void main(String[] args) {
		//File file = new File("Sample.csv");
		File file = new File("EURUSD-2015-01.csv");
		String outputFileName="FXSampleRes.txt";
		
		BufferedReader reader = null;
		Date timestamp = null;
		String[] parts = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			BufferedWriter out=new BufferedWriter(new FileWriter(outputFileName));
			
			String str = null;
			double max_ask_in_hour = 0, min_ask_in_hour = Double.MAX_VALUE, avg_ask_in_hour = 0; // Attributes
			double max_bid_in_hour = 0, min_bid_in_hour = Double.MAX_VALUE, avg_bid_in_hour = 0; // Attributes
			double spread = 0 ; // (ask - bid) / bid
			double diff_before = 0; // bid diff to the last hour , use the last hour data to predict now 
			int direction = 0;
			
			int lastDay = -1;
		    long lastHour = 0;
		    double last_avg_bid = 0; 
		    int curHour = 0;
		    
		    double totalAsk = 0;
		    double totalBid = 0;
		    double count = 0; 
		    String dateStr = null;

		  /*  System.out.println("Type" + "\t\t" + "Date" + "    " + "Hour" +
		              "\t" + "min_ask" + 
		              "\t  " + "max_ask" + 
		              "\t" + "avg_ask" +
		              "      " + "min_bid" +
		              "\t" + "max_bid" +
		              "    " + "avg_bid" +
		              "\t   " + "spread" +
		              "\t" + "direction(label)");  */
		    
		    String titleStr = "Type" + "\t\t" + "Date" + "    " + "Hour" +
		    					"\t" + "min_ask" + 
					            "\t  " + "max_ask" + 
					            "\t" + "avg_ask" +
					            "      " + "min_bid" +
					            "\t" + "max_bid" +
					            "    " + "avg_bid" +
					            "\t" + "direction(label)\n"; 
		    out.write(titleStr);
			
			while((str = reader.readLine()) != null){
				parts = str.split(",");   // split the line with ','
				timestamp = FORMART.parse(parts[1]);  // The second column is the time 
				
				int curDay = timestamp.getDay();
				curHour = timestamp.getHours();
			    dateStr = parts[1].substring(0, 8);
				
				
				double curBid = Double.valueOf(parts[2]);
				double curAsk = Double.valueOf(parts[3]);
				
				if(lastDay == -1 ) {
					// Not the same day, data not continuous 
					lastHour= curHour;
					lastDay = curDay;
					direction = 0;
					
					count = 1;
					totalAsk = curAsk;
					totalBid = curBid;
					last_avg_bid = avg_bid_in_hour;
					max_ask_in_hour = curAsk;  min_ask_in_hour = curAsk;
					max_bid_in_hour = curBid;  min_bid_in_hour = curBid;
					
				} 
				else if(lastDay != -1 && curDay != lastDay) {
					
					// Get data 
					avg_ask_in_hour = totalAsk/count; 
					avg_bid_in_hour = totalBid/count;
					
					
					direction = (avg_bid_in_hour - last_avg_bid) > 0? 1: -1;
					spread = (avg_ask_in_hour - avg_bid_in_hour)/avg_bid_in_hour;
					
				/*	System.out.println(parts[0] + "\t\t" + dateStr+ " "+ lastHour+
									              "\t" + min_ask_in_hour + 
									              "\t  " + max_ask_in_hour + 
									              "    " + String.format("%10.5f", avg_ask_in_hour) +
									              "      " + min_bid_in_hour +
									              "\t" + max_bid_in_hour +
									              "\t" + String.format("%10.5f", avg_bid_in_hour) +
									              "\t" + String.format("%10.5f", spread) +
									              "\t   " + direction);  */
					
					String outputLine = parts[0] + "\t\t" + dateStr+ " "+ lastHour+
				               "\t" + min_ask_in_hour + 
				               "\t  " + max_ask_in_hour + 
				               "    " + String.format("%10.5f", avg_ask_in_hour) +
				               "      " + min_bid_in_hour +
				               "\t" + max_bid_in_hour +
				               "\t" + String.format("%10.5f", avg_bid_in_hour) +
				               "\t   " + direction + "\n";  
			
				    out.write(outputLine);
					
					lastHour= curHour;
					lastDay = curDay;
					direction = 0;
					
					count = 1;
					totalAsk = curAsk;
					totalBid = curBid;
					last_avg_bid = avg_bid_in_hour;
					max_ask_in_hour = curAsk;  min_ask_in_hour = curAsk;
					max_bid_in_hour = curBid;  min_bid_in_hour = curBid;
				}
				else if(curDay == lastDay  && curHour != lastHour){
					// Get data 
					avg_ask_in_hour = totalAsk/count; 
					avg_bid_in_hour = totalBid/count;
					
					
					direction = (avg_bid_in_hour - last_avg_bid) > 0? 1: -1;
					spread = (avg_ask_in_hour - avg_bid_in_hour)/avg_bid_in_hour;
					
				/*	System.out.println(parts[0] + "\t\t" + dateStr+ " "+ lastHour+
									              "\t" + min_ask_in_hour + 
									              "\t  " + max_ask_in_hour + 
									              "    " + String.format("%10.5f", avg_ask_in_hour) +
									              "      " + min_bid_in_hour +
									              "\t" + max_bid_in_hour +
									              "\t" + String.format("%10.5f", avg_bid_in_hour) +
									              "\t" + String.format("%10.5f", spread) +
									              "\t   " + direction); */
					
					String outputLine = parts[0] + "\t\t" + dateStr+ " "+ lastHour+
						               "\t" + min_ask_in_hour + 
						               "\t  " + max_ask_in_hour + 
						               "    " + String.format("%10.5f", avg_ask_in_hour) +
						               "      " + min_bid_in_hour +
						               "\t" + max_bid_in_hour +
						               "\t" + String.format("%10.5f", avg_bid_in_hour) +
						               "\t   " + direction + "\n";  
					
					out.write(outputLine);
					
					// reset data
					lastHour= curHour;
					last_avg_bid = avg_bid_in_hour;
					count = 0;
					totalAsk = 0;
					totalBid = 0;
					last_avg_bid = avg_bid_in_hour;
					max_ask_in_hour = 0;  min_ask_in_hour = Double.MAX_VALUE;
					max_bid_in_hour = 0;  min_bid_in_hour = Double.MAX_VALUE;
							
				}else{
					// If the time span in one hour
					max_ask_in_hour  = Math.max(max_ask_in_hour , curAsk);
					min_ask_in_hour  = Math.min(min_ask_in_hour , curAsk);
					
					max_bid_in_hour  = Math.max(max_bid_in_hour , curBid);
					min_bid_in_hour  = Math.min(min_bid_in_hour , curBid);
					
					totalAsk += curAsk;
					totalBid += curBid;
					count++;
				}
			}
			
			// Get data 
			avg_ask_in_hour = totalAsk/count; 
			avg_bid_in_hour = totalBid/count;
			
			direction = (avg_bid_in_hour - last_avg_bid) > 0? 1: -1;
			spread = (avg_ask_in_hour - avg_bid_in_hour)/avg_bid_in_hour;
			
			String outputLine = parts[0] + "\t\t" + dateStr+ " "+ lastHour+
		               "\t" + min_ask_in_hour + 
		               "\t  " + max_ask_in_hour + 
		               "    " + String.format("%10.5f", avg_ask_in_hour) +
		               "      " + min_bid_in_hour +
		               "\t" + max_bid_in_hour +
		               "\t" + String.format("%10.5f", avg_bid_in_hour) +
		               "\t   " + direction + "\n";  
	
	        out.write(outputLine);
			
			/*System.out.println(parts[0] + "\t\t" + dateStr+ " "+ curHour+
		              "\t" + min_ask_in_hour + 
		              "\t  " + max_ask_in_hour + 
		              "    " + String.format("%10.5f", avg_ask_in_hour) +
		              "      " + min_bid_in_hour +
		              "\t" + max_bid_in_hour +
		              "" + String.format("%10.5f", avg_bid_in_hour) +
		              "\t" + String.format("%10.5f", spread) +
		              "\t   " + direction);  */
			
			reader.close();
			out.close();
			System.out.println("The process has finished");
		} catch (ParseException e) {
			e.printStackTrace();
		}  catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
