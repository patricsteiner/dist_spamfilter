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
    public void test() throws IOException {
    	testCalculateProbability("ham-kallibrierung.zip", true);
    }
    
    @Test
    public void calibrateHam() throws IOException {
    	
    }
    
    @Test
    public void calibrateSpam() throws IOException {

    }
    
    @Test
    public void testCalculateSpamProbability() throws IOException {

    }
    
    @Test
    public void testCalculateProbability() {
        String[] wordsToTest = { "money", "cash", "have", "hello", "pill", "viagra", "prince", 
        		"and", "casino", "sex", "online", "month", "week", "day", "popular", "mail",
        		"technology", "insurance", "it", "your", "greetings", "regards", "cheap", "win", "prize" };
        System.out.println("Word         P(Word|Ham)   P(Word|Spam)");
        System.out.println("---------------------------------------");
        for (String word : wordsToTest) {
            System.out.printf("%-10s   %.3f         %.3f%n" , word, spamFilter.ham.calculateProbability(word), spamFilter.spam.calculateProbability(word));            
        }
        System.out.println("---------------------------------------");
    }
    
    void testCalculateProbability(String zipName, boolean hamOrSpam) throws IOException {
        ZipFile zipFile = new ZipFile(zipName);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            System.out.println(spamFilter.calculateProbability(zipFile.getInputStream(entry), hamOrSpam));
        }
        zipFile.close();
    }

}
