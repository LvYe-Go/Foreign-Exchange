/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2015/1/29 17:03</create-date>
 *
 * <copyright file="Corpus.java" >
 * Copyright (c) 2003-2014, All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. 
 * </copyright>
 */
package lda;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.til.List;

/**
*  Reference : http://jsfiddle.net/9FqKS/
 * Modified by Jing Yu
 * Date: 12/11/201
 */
/**
 * a set of documents
 */
public class Corpus
{
    List<int[]> documentList;
    Vocabulary vocabulary;
    
    static final String charSetDecoder  = "UTF-8";  // charset decoder type

    public Corpus()
    {
        documentList = new LinkedList<int[]>();
        vocabulary = new Vocabulary();
    }

    public int[] addDocument(List<String> document)
    {
        int[] doc = new int[document.size()];
        int i = 0;
        for (String word : document)
        {
            doc[i++] = vocabulary.getId(word, true);
        }
        documentList.add(doc);
        return doc;
    }

    public int[][] toArray()
    {
        return documentList.toArray(new int[0][]);
    }

    public int getVocabularySize()
    {
        return vocabulary.size();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        for (int[] doc : documentList)
        {
            sb.append(Arrays.toString(doc)).append("\n");
        }
        sb.append(vocabulary);
        return sb.toString();
    }

    /**
     * Load documents from disk
     *
     * @param folderPath is a folder, which contains text documents.
     * @return a corpus
     * @throws IOException
     */
    public static Corpus load(String folderPath) throws IOException
    {
        Corpus corpus = new Corpus();
        File folder = new File(folderPath);
        
        for (File file : folder.listFiles())
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSetDecoder));
            String line;
            List<String> wordList = new LinkedList<String>();
            while ((line = br.readLine()) != null)
            {
                String[] words = line.split(" ");
                for (String word : words)
                {
                    if (word.trim().length() < 2) continue;
                    wordList.add(word);
                }
            }
            br.close();
            corpus.addDocument(wordList);
        }
        if (corpus.getVocabularySize() == 0) return null;

        return corpus;
    }

    public Vocabulary getVocabulary()
    {
        return vocabulary;
    }

    public int[][] getDocument()
    {
        return toArray();
    }

    public static int[] loadDocument(String path, Vocabulary vocabulary) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        List<Integer> wordList = new LinkedList<Integer>();
        while ((line = br.readLine()) != null)
        {	
            String[] words = line.split(" ");
            for (String word : words)
            {
                if (word.trim().length() < 2) continue;
                Integer id = vocabulary.getId(word);
                if (id != null)
                    wordList.add(id);
            }
        }
        br.close();
        int[] result = new int[wordList.size()];
        int i = 0;
        for (Integer integer : wordList)
        {
            result[i++] = integer;
        }
        return result;
    }
 
    /*============================= Implemented by Jing Yu ================================================================ */ 

    /**Jing Yu's code 
     * Override : used doc theta coefficiency to filer that files nor related to the topic 
     * The filter for dirty word and empty words are in the output
     * @param folderPath
     * @param topicThetaCoeff
     * @return
     * @throws IOException
     */
    public static Corpus loadTopicCoeff(String folderPath, boolean[] topicThetaCoeff) throws IOException
    {
        Corpus corpus = new Corpus();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        int folderLen =  folder.listFiles().length;
        
        for(int i = 0; i < folderLen; i++)
        {
        	if(!topicThetaCoeff[i]) continue; // filter out the unrelated files to the topic 
        	File file = files[i];
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSetDecoder));
            String line;
            List<String> wordList = new LinkedList<String>();
            while ((line = br.readLine()) != null)
            {
                String[] words = line.split(" ");
                for (String word : words)
                {
                    if (word.trim().length() < 2) continue;
                    wordList.add(word);
                }
            }
            br.close();
            corpus.addDocument(wordList);
        }
        if (corpus.getVocabularySize() == 0) return null;

        return corpus;
    }
}
