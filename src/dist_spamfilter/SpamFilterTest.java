package dist_spamfilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class SpamFilterTest {

    static SpamFilter spamFilter;
    
    @BeforeClass
    public static void setup() throws IOException {
        spamFilter = new SpamFilter();
        spamFilter.learn();
        //Un-comment the following line if you want to input your own email:
        //readEmailFromFile();
    }
    
    
    public static void readEmailFromFile() throws IOException {
    	System.out.println("Please input path of email you want to add:");
    	Scanner scanner = new Scanner(System.in);
    	String fileName = scanner.nextLine();
    	InputStream instream = new FileInputStream(new File(fileName));
    	System.out.println(fileName);
    	System.out.println("Probability of being ham: " + spamFilter.calculateProbability(instream, true, false));
    	System.out.println("Probability of being spam: " + spamFilter.calculateProbability(instream, false, false));
    	System.out.println(fileName + " was classified as " + (spamFilter.classify(instream) ? "spam" : "ham"));
    	System.out.println("Is this mail spam or ham? (type either 'spam' or 'ham')");
    	String s = "";
    	while (!s.equals("spam") && !s.equals("ham")) {
    		s = scanner.nextLine();
    	}
    	if (s.equals("spam")) {
    		spamFilter.spam.add(Util.getWords(instream));
    		System.out.println("Added to spam");
    	}
    	else {
    		spamFilter.ham.add(Util.getWords(instream));
    		System.out.println("Added to ham");
    	}
    	spamFilter.correlate();
    	scanner.close();
    	System.out.println("continuing with tests...");
    }
    
    
    @Test
    /**
     * ~93% are correctly classified as ham!
     */
    public void testHamClassification() throws IOException {
    	int ham = 0;
    	int spam = 0;
    	 ZipFile zipFile = new ZipFile("ham-test.zip");
         Enumeration<? extends ZipEntry> entries = zipFile.entries();
         while (entries.hasMoreElements()) {
             ZipEntry entry = entries.nextElement();
             if (spamFilter.classify(zipFile.getInputStream(entry))) {
            	 spam++;
             }
             else {
            	 ham++;
             };
         }
         zipFile.close();
         System.out.println("Ham correctly classified: "+ ((double)ham / (ham + spam) * 100) + "%");
    }
    
    @Test
    /**
     * ~87% are correctly classified as spam!
     */
    public void testSpamClassification() throws IOException {
    	int ham = 0;
    	int spam = 0;
    	 ZipFile zipFile = new ZipFile("spam-test.zip");
         Enumeration<? extends ZipEntry> entries = zipFile.entries();
         while (entries.hasMoreElements()) {
             ZipEntry entry = entries.nextElement();
             if (spamFilter.classify(zipFile.getInputStream(entry))) {
            	 spam++;
             }
             else {
            	 ham++;
             };
         }
         zipFile.close();
         System.out.println("Spam correctly classified: "+ ((double)spam / (ham + spam) * 100) + "%");
    }
    
    @Test
    @Ignore
    public void testCalculateProbabilityOfSomeRandomWords() {
        String[] wordsToTest = { "money", "cash", "have", "hello", "pill", "viagra", "prince", 
        		"and", "casino", "sex", "online", "month", "week", "day", "popular", "mail",
        		"technology", "insurance", "it", "your", "greetings", "regards", "cheap", "win", 
        		"prize", "big", "kenia", "large", "microsoft", "software", "support", "phone" };
        System.out.println("Word         P(Word|Ham)   P(Word|Spam)");
        System.out.println("---------------------------------------");
        for (String word : wordsToTest) {
            System.out.printf("%-10s   %.3f         %.3f%n" , word, spamFilter.ham.calculateProbability(word), spamFilter.spam.calculateProbability(word));            
        }
        System.out.println("---------------------------------------");
    }
    
    @Test
    @Ignore
    public void testCalculateProbabilityOfMails() throws IOException {
    	System.out.println("Ham probablities:");
    	calculateProbabilities("ham-kallibrierung.zip", true, false);
    	System.out.println("Spam probabilities:");
    	calculateProbabilities("spam-kallibrierung.zip", false, false);
    }
    
    void calculateProbabilities(String zipName, boolean hamOrSpam, boolean useLogFormula) throws IOException {
        ZipFile zipFile = new ZipFile(zipName);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            System.out.printf("%.10f%n",spamFilter.calculateProbability(zipFile.getInputStream(entry), hamOrSpam, useLogFormula));
        }
        zipFile.close();
    }

}
