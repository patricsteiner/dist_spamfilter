package dist_spamfilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SpamFilterTest {

    SpamFilter spamFilter;
    
    @Before
    public void setup() throws IOException {
        spamFilter = new SpamFilter();
        spamFilter.learn();
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
         System.out.println("Ham correctly classified: "+ (int)((double)ham / (ham + spam) * 100) + "%");
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
         System.out.println("Spam correctly classified: "+ (int)((double)spam / (ham + spam) * 100) + "%");
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
