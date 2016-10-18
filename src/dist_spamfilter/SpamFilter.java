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
	Map<String, Counter> ham = new HashMap<>();
	Map<String, Counter> spam = new HashMap<>();
	
	public void learn() throws IOException {
		ZipFile zipFile = new ZipFile("ham-anlern.zip");
	    analyzeMails(zipFile, ham);
		zipFile = new ZipFile("spam-anlern.zip");
	    analyzeMails(zipFile, spam);
	    System.out.println(ham.get("Thank"));
	    System.out.println(spam.get("Thank"));
	}
	
	public void analyzeMails(ZipFile zipFile, Map<String, Counter> map) throws IOException {
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
	        ZipEntry entry = entries.nextElement();
	        Scanner scanner = new Scanner(zipFile.getInputStream(entry));
	        scanner.useDelimiter(" ");
	        while (scanner.hasNext()) {
	        	try {
	        		String word = scanner.next("[a-zA-Z0-9]+");
	        		//TODO: max increase counter once per mail?
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
	}
}
