/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2015/1/29 19:07</create-date>
 *
 * <copyright file="LdaUtil.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package lda;

import java.util.*;

/**
 * Modified Author: Jing Yu 
 */
public class LdaUtil
{
    /**
     * To translate a LDA matrix to readable result
     * @param phi the LDA model
     * @param vocabulary
     * @param limit limit of max words in a topic
     * @return a map array
     */
	
	final static double THETA_THRES_HOLD =  0.1;   // thed_hold to in the next filter
	
    public static Map<String, Double>[] translate(double[][] phi, Vocabulary vocabulary, int limit)
    {
        limit = Math.min(limit, phi[0].length);
        Map<String, Double>[] result = new Map[phi.length];
        for (int k = 0; k < phi.length; k++)
        {
            Map<Double, String> rankMap = new TreeMap<Double, String>(Collections.reverseOrder());
            for (int i = 0; i < phi[k].length; i++)
            {
                rankMap.put(phi[k][i], vocabulary.getWord(i));
            }
            Iterator<Map.Entry<Double, String>> iterator = rankMap.entrySet().iterator();
            result[k] = new LinkedHashMap<String, Double>();
            for (int i = 0; i < limit; ++i)
            {
            	if(iterator.hasNext()){
	                Map.Entry<Double, String> entry = iterator.next();
	                result[k].put(entry.getValue(), entry.getKey());
            	}
            }
        }
        return result;
    }

    public static Map<String, Double> translate(double[] tp, double[][] phi, Vocabulary vocabulary, int limit)
    {
        Map<String, Double>[] topicMapArray = translate(phi, vocabulary, limit);
        double p = -1.0;
        int t = -1;
        for (int k = 0; k < tp.length; k++)
        {
            if (tp[k] > p)
            {
                p = tp[k];
                t = k;
            }
        }
        return topicMapArray[t];
    }

    /**
     * To print the result in a well formatted form
     * @param result
     */
    public static void explain(Map<String, Double>[] result)
    {
        int i = 0;
        for (Map<String, Double> topicMap : result)
        {
            System.out.printf("topic %d :\n", i++);
            explain(topicMap);
            System.out.println();
        }
    }

    public static void explain(Map<String, Double> topicMap)
    {
        for (Map.Entry<String, Double> entry : topicMap.entrySet())
        {
            System.out.println(entry);
        }
    }
 /*============================= Implemented by Jing Yu ================================================================ */ 
    /**
     *  Obtain theta group and convert them to topic-> doc, to show the coefficiency on the document on that topic 
     *  Using boolean type and set threshold to determine use the document for the topic or not。 
     *  Can adjust the value of threshold to control how many docs to be involved in the next level topics
     */
    
    public static boolean[][] convertTheta(double[][] theta){
    	int docLen = theta.length; 
    	int topicNum = theta[0].length;
    	boolean[][] docThetaCoeff = new boolean[topicNum][docLen];
    	
    	for(int i = 0; i < docLen; i++){
    		for(int j = 0; j < topicNum; j++){
    			docThetaCoeff[j][i] = theta[i][j] > THETA_THRES_HOLD ? true: false;
    		}
    	 }
         return docThetaCoeff;
     }

    /**
     * Jing Yu's code 
     * Print the thate group
     * @param theta
     */
    public static void explain(double[][] theta){
    	
    	 System.out.println("\n");
         System.out.println("Document--Topic Associations, Theta[d][k]\n");
         
    	int docLen = theta.length; 
    	int topicNum = theta[0].length;
    	
    	for(int i = 0; i < docLen; i++){
    		System.out.print("No [" + i + "] doc theta is : ");
    		for(int j = 0; j < topicNum; j++){
    			System.out.print(theta[i][j] +  " ");
    		}
    		System.out.println();
    	}
    	
    	System.out.println();
    }
    
    /**
     * Jing Yu's code 
     * Print the topic-doc theta after converting
     * @param theta
     */
    
    public static void explain(boolean[][] docThetaCoeff){
    	
         System.out.println("\n\n Document--Topic Associations, Theta[k][d]\n\n");
         
    	int docLen = docThetaCoeff.length; 
    	int topicNum = docThetaCoeff[0].length;
    	
    	for(int i = 0; i < docLen; i++){
    		System.out.print("No [" + i + "] topic  high coefficiency docs are : ");
    		for(int j = 0; j < topicNum; j++){
    			System.out.print(docThetaCoeff[i][j] +  " ");
    		}
    		System.out.println();
    	}
    	
    	System.out.println();
    }
}
