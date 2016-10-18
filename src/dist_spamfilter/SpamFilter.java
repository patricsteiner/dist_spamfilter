package dist_spamfilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SpamFilter {
	final double P_H = 0.5;
	final double P_S = 0.5;
	
	Map<String, Counter> ham = new HashMap<>();
	Map<String, Counter> spam = new HashMap<>();
	
	int hamMailsAnalyzed = 0;
	int spamMailsAnalyzed = 0;
	
	//TODO: make sure every word is in ham as well as spam! (set counter to verylow)
	
	/**
	 * 
	 * @throws IOException
	 */
	public void learn() throws IOException {
		ZipFile zipFile = new ZipFile("ham-anlern.zip");
		hamMailsAnalyzed += analyzeMails(zipFile, ham);
		zipFile = new ZipFile("spam-anlern.zip");
		spamMailsAnalyzed += analyzeMails(zipFile, spam);
	    
		
		//testing stuff...
		String wordToTest = "money";
		System.out.println();
	    System.out.println(hamMailsAnalyzed + " ham mails analyzed, found '" + wordToTest + "' " + ham.get(wordToTest) + " times");
	    System.out.println(spamMailsAnalyzed + " spam mails analyzed, found '" + wordToTest + "' " + spam.get(wordToTest) + " times");
	    System.out.println();
	    System.out.println("Probability of '" + wordToTest + "' being ham: " + getProbability(wordToTest, ham, hamMailsAnalyzed));
	    System.out.println("Probability of '" + wordToTest + "' being spam: " + getProbability(wordToTest, spam, spamMailsAnalyzed));
	    
	}
	
	/**
	 * 
	 * @param zipFile
	 * @param map the map where the (word,counter) pairs will be stored
	 * @return the amount of mails analyzed
	 * @throws IOException
	 */
	public int analyzeMails(ZipFile zipFile, Map<String, Counter> map) throws IOException {
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
	        ZipEntry entry = entries.nextElement();
	        analyzeMail(zipFile.getInputStream(entry), map);
	    }
		return zipFile.size();
	}
	
	/**
	 * 
	 * @param instream
	 * @param map
	 * @throws IOException
	 */
	public void analyzeMail(InputStream instream, Map<String, Counter> map) throws IOException {
		Scanner scanner = new Scanner(instream);
        scanner.useDelimiter(" ");
        while (scanner.hasNext()) {
        	try {
        		String word = scanner.next("[a-zA-Z0-9]+").toLowerCase();
        		//TODO: max increase counter once per mail? (-> Mehrfachzählung?)
	        	if (!map.containsKey(word)) {
	        		map.put(word, new Counter());
	        		System.out.println(word);
	        	}
	        	map.get(word).inc();
        	}
        	catch (InputMismatchException e) {
        		scanner.next();
        	}
        }
        scanner.close();
	}
	
	/**
	 * 
	 * @param instream
	 * @return
	 */
	public double calculateSpamProbability(InputStream instream) {
		return 0;
		//TODO
	}
	
	
	/**
	 * 
	 * @param word
	 * @param map
	 * @param amountOfMailAnalyzed
	 * @return
	 */
	public double getProbability(String word, Map<String, Counter> map, int amountOfMailAnalyzed) {
		return map.get(word).get() / amountOfMailAnalyzed;
	}
}
