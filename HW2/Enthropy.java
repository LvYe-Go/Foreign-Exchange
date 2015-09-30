import java.util.ArrayList;

public class Entropy {	
	public static double calculateEntropy(ArrayList<Feature> data, int count) {
		double entropy = 0;
		
		if(data.size() == 0) {
			// nothing to do
			return 0;
		}
		
		for(int i = 0; i < count; i++) {
			for(int j = 0; j < data.size(); j++) {
				Feature feature = data.get(j);
			}
				
			double probability = count / (double)data.size();
			if(count > 0) {
				entropy += -probability * (Math.log(probability) / Math.log(2));
			}
		}
		
		return entropy;
	}
	
	public static double calculateGain(double rootEntropy, ArrayList<Double> subEntropies, ArrayList<Integer> setSizes, int data) {
		double gain = rootEntropy; 
		
		for(int i = 0; i < subEntropies.size(); i++) {
			gain += -((setSizes.get(i) / (double)data) * subEntropies.get(i));
		}
		
		return gain;
	}
}