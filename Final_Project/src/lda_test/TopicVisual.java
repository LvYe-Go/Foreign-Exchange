package lda_test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import lda.Corpus;
import lda.LdaGibbsSampler;
import lda.LdaUtil;

import javax.script.*;

/** 
 * Showing the topics and 2-level 
 * @Author Jing Yu 
 */
public class TopicVisual 
{
	public static final int TOPICNUM = 10; // Topic Number
	public static String folderpath = "data/mini";  // Path to data
	public static final int MUL = (int) 10E5; 
	public static Set<String> set = null;
	
	public static void main(String[] args) throws IOException
	{
		 set = new HashSet<String>(); // The set keep the empty words to filter out 
	     String[] emptyWords = {"the", "and", "for","has", "is", "was", "be",
	    		          "of", "are", "or", "it", "they", "he", "she"};
	     for(String str : emptyWords ){
	    	 set.add(str);
	     } 
	     
	     String[] preEx = {
	    		 "fermentation", "Bap", "viability", "Disease", "Cd", "genetic","dye", "NPK",
	    		 "small", "frequ", "those"
	     };
	     System.out.println("Please input word you choose\n based on pre-experience please select from word");
	     
	     Scanner scanner = new Scanner(System.in);
	     String inputWord = scanner.next();
		 getTopic(inputWord); 
	}
	
	/**
	 * Train the data and get the theta to get documents with high coeffeciency to certain topic
	 * Get object list to json, then to flare.json , and show on the html
	 * @throws IOException
	 */
	 public static void getTopic(String inputWord) throws IOException{		
		
		// 1. Load corpus from disk
	    Corpus corpus = Corpus.load(folderpath);
	    
	    // 2. Create a LDA sampler
	    LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
	    
	    // 3. Train it
	    ldaGibbsSampler.gibbs(TOPICNUM);
	    
	    // 4. The phi matrix is a LDA model, you can use LdaUtil to explain it.
	    double[][] phi = ldaGibbsSampler.getPhi();
	    Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), TOPICNUM);
	    LdaUtil.explain(topicMap); 
	    
	    // 5. Get the theta 
	    double[][] theta = ldaGibbsSampler.getTheta();
	    boolean[][] docThetaCoeff = LdaUtil.convertTheta(theta);
	    LdaUtil.explain(docThetaCoeff);
	    
	    // 6. Get the 1nd-level sub topics
	    List<FLNode>  topicLevelList = new ArrayList<FLNode>();
	    generateFirstLevelTopicObject(topicMap , topicLevelList, inputWord);
	    
	    // 7.  Get the 2nd level topics 
	    genNextLevel(docThetaCoeff, topicLevelList);
	    
	    // 8. Convert the result to json -> flare.json -> html 
	    convertJsontoFlare(topicLevelList);
	}

	 /**
	  * Get the first level topic list
	  * @param topicMap
	  * @param topicLevelList
	  */
	public static void generateFirstLevelTopicObject(Map<String, Double>[] topicMap , List<FLNode> topicLevelList, String inputWord){
		 System.out.println("\n\n\nIn translate the fisrt level of topic"); 
		
		 for(Map<String, Double> map : topicMap){
			// Ge the first with most probability as the topic 
            Map.Entry<String, Double> entry = map.entrySet().iterator().next();
            String name = entry.getKey();
            // filer out dirty and empty words 
            if(!isDirty(name) || set.contains(name)) continue;
            FLNode flnode = new FLNode(name, new ArrayList<SLNode>());
            topicLevelList.add(flnode);
		 }
	}
	 
	/**
	 * Get the next level topic subtopic around certain topic
	 * @param docThetaCoeff
	 * @param topicLevelList
	 * @throws IOException
	 */
	public static void genNextLevel(boolean[][] docThetaCoeff, List<FLNode> topicLevelList) throws IOException{
       
		for(int topicIndex = 0; topicIndex < topicLevelList.size(); topicIndex++){
			 
			// For every firstLevel optic has a list of secodn level topic 
			List<SLNode> curLevelList = new ArrayList<SLNode>();
			
			boolean[] topicThetaCoeff = docThetaCoeff[topicIndex];
			
			// Use overridden load corpus with theta coefficiency
		    Corpus corpus = Corpus.loadTopicCoeff(folderpath, topicThetaCoeff);
		    
		    LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
		    ldaGibbsSampler.gibbs(TOPICNUM);
		    
		    double[][] phi = ldaGibbsSampler.getPhi();
		    
		    Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), TOPICNUM);
		    
		    for(Map<String, Double> map : topicMap){
	            Map.Entry<String, Double> entry = map.entrySet().iterator().next();
	            String name = entry.getKey();
	            // Fliter the dirty data and empty words
	            if(!isDirty(name) || set.contains(name)) continue;
	            
	            // Add the current level node to the list 
	            SLNode slnode = new SLNode(name, (int)(entry.getValue() * MUL));
	            curLevelList.add(slnode);
			 } 
		     // Attach the list to corresponding upper level topic
		     topicLevelList.get(topicIndex).setChildren(curLevelList);
		}
	}
	
	/**
	 * Just keep alphabet and number only
	 * @param name
	 * @return
	 */
	public static boolean isDirty(String name){
		int len  = name.length();
		for(int i = 0; i < len; i++){
			char c = name.charAt(i);
			if(!((c <= 'z' && c >= 'a') || (c <= 'Z' && c >= 'A') || (c <= '9' && c >= '0'))){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Convert the object list to flat json, then convert it to flare json to be used in js 
	 * @param topicLevelList
	 * @throws IOException
	 */
	public static void convertJsontoFlare(List<FLNode> topicLevelList) throws IOException {

		System.out.println("Create the flare json : \n");
		
		Gson gson = new Gson(); 
		String json = gson.toJson(topicLevelList); 
		
		String data = "{\"name\" : \"topicV_root\", \"children\":" + json + "}";
		 
		File file = new File("flare.json");

		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(data);
		bw.close();
	}
}
