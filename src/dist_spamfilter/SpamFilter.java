package dist_spamfilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SpamFilter {
	final double P_H = 0.5;
	final double P_S = 0.5;
	
	Map<String, Counter> ham = new HashMap<>();
	Map<String, Counter> spam = new HashMap<>();
	
	int hamMailsAnalized = 0;
	int spamMailsAnalized = 0;
	
	public void learn() throws IOException {
		ZipFile zipFile = new ZipFile("ham-anlern.zip");
		hamMailsAnalized += analyzeMails(zipFile, ham);
		zipFile = new ZipFile("spam-anlern.zip");
		spamMailsAnalized += analyzeMails(zipFile, spam);
	    
	    System.out.println(hamMailsAnalized + " - " + ham.get("cash"));
	    System.out.println(spamMailsAnalized + " - " + spam.get("cash"));
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
	        analyzeMail(zipFile, entry, map);
	    }
		return zipFile.size();
	}
	
	public void analyzeMail(ZipFile zipFile, ZipEntry zipEntry, Map<String, Counter> map) throws IOException {
		Scanner scanner = new Scanner(zipFile.getInputStream(zipEntry));
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
	
	public double calculateSpamProbability(ZipEntry zipEntry) {
		return 0;
		
	}
}
