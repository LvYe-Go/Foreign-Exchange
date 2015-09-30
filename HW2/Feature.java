
public class Feature {
	public double avg_bid = 0;
	public double avg_bid_diff = 0;
	public double avg_spread = 0;
	public double avg_ask_bid_direction = 0; 
	public Feature(double avg_bid, double avg_bid_diff, double avg_spread, double avg_ask_bid_direction){
		this.avg_bid = avg_bid;
		this.avg_bid_diff = avg_bid_diff;
		this.avg_spread = avg_spread;
		this.avg_ask_bid_direction = avg_ask_bid_direction;
	}
}
