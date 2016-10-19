package dist_spamfilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SpamFilter {
	final double ALPHA = 0.5;
	
	MailData ham; //not private for easier testing
	MailData spam;
	
	public void learn() throws IOException {
		ham = readMails(new ZipFile("ham-anlern.zip"));
		spam = readMails(new ZipFile("spam-anlern.zip"));
		correlate();
	}
	
	public void correlate() {
		for (String hamWord : ham.getWords()) {
			if (spam.getWordCount(hamWord) < 1) {
				spam.addDummyWord(hamWord, ALPHA);
			}
		}
		for (String spamWord : spam.getWords()) {
			if (ham.getWordCount(spamWord) < 1) {
				ham.addDummyWord(spamWord, ALPHA);
			}
		}
	}
	
	private MailData readMails(ZipFile zipFile) throws IOException {
	    MailData mailData = new MailData();
	    Enumeration<? extends ZipEntry> entries;
	    entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            mailData.add(Util.getWords(zipFile.getInputStream(entry)));
        }
        zipFile.close();
        return mailData;
	}
	
	public double calculateProbability(InputStream instream, boolean hamOrSpam) {
		Collection<String> words = Util.getWords(instream);
		double numerator = 1;
		double denominator = 1;
		for (String word : words) {
			if (hamOrSpam) { 
				numerator *= ham.calculateProbability(word); //TODO double underflow! change formula somehow
			    denominator *= spam.calculateProbability(word);
			} 
			else {
				numerator *= spam.calculateProbability(word);
			    denominator *= ham.calculateProbability(word);
			}
		}
		denominator += numerator;
	    return numerator / denominator;
	}
}
