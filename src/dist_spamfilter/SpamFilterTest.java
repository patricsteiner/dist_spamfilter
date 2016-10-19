package dist_spamfilter;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SpamFilterTest {

    SpamFilter spamFilter = new SpamFilter();
    
    @Before
    public void setup() throws IOException {
        spamFilter.learn();
    }
    
    @Test
    @Ignore
    public void testCalculateSpamProbability() throws IOException {
        ZipFile zipFile = new ZipFile("spam-test.zip");
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            System.out.println(spamFilter.calculateSpamProbability(zipFile.getInputStream(entry)));
        }
        zipFile.close();
    }
    
    @Test
    public void testCalculateProbability() {
        String[] wordsToTest = {"money", "cash", "have", "hello", "pill", "viagra", "prince", "and", "casino" };
        System.out.println("Word :: P(Word|Spam) :: P(Word|Spam)");
        for (String word : wordsToTest) {
            System.out.printf("%s  ::  %.3f  ::  %.3f%n" , word, spamFilter.ham.calculateProbability(word), spamFilter.spam.calculateProbability(word));            
        }
    }

}
