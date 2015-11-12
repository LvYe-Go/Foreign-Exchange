package Cassandra;

import DTree.*;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;



public class cassandra {
	public static Cluster cluster = null;
	public static Session session = null;
	public static final int CONNETCT_TIMEOUT =  5 * 10000;
	public static final int READ_TIMEOUT = 100000;
	
	/**
	 * Init the cassandra connect
	 * @param sessionName
	 */
	public static  void init(String sessionName){
		 Builder builder = Cluster.builder();
	     builder.addContactPoint("127.0.0.1");
	
	     // socket link  and set timeout
	     SocketOptions socketOptions = new SocketOptions().setKeepAlive(true)
	    		                           .setConnectTimeoutMillis(CONNETCT_TIMEOUT)
	    		                           .setReadTimeoutMillis(READ_TIMEOUT);
	     
	     builder.withSocketOptions(socketOptions);
	     Cluster cluster = builder.build();
	     
	     Metadata metadata = cluster.getMetadata();	     
	     System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
	     
	     Session session = cluster.connect(sessionName);
	}
	
	/**
	 *  Write into the rows: create table and insert 
	 * @param filename
	 * @param node
	 */
    public static void output(String filename, TreeNode node){
    	String file_increase = node.num+ DTUtli.IS_INCREASE;
  		String file_decrease = node.num+ DTUtli.IS_DECREASE;
  		
    	 String outputQuery = "SELECT * FROM " + filename;
    	 ResultSet res = session.execute(outputQuery);
    	 String insertQuery = "(id, avg_bid, bid_diff, avg_spread, aid_direction, label)";
    	 for(Row row : res){
    		 String cur = row.getInt("id")+", "+ row.getInt("avg_bid")+", "+ row.getInt("bid_diff")+", "+ row.getInt("avg_spread")+", "
						+ row.getInt("aid_direction")+", "+ row.getInt("label");
				if(row.getInt(node.feature) == Integer.parseInt(DTUtli.IS_INCREASE)){
					session.execute("INSERT INTO "+file_increase+ " " + insertQuery+ " VALUES ( " + cur +" )");
				}else{
					session.execute("INSERT INTO "+file_increase+ " " + insertQuery + "VALUES ( " + cur +" )");
				}
    	 }
    }
}
