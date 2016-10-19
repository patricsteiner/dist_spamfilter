package dist_spamfilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Simple Spamfilter using the Bayes-formula.
 * @author Patric Steiner, Sandro Schwager, Roger Obrist
 *
 */
public class SpamFilter {
	final double ALPHA = 0.5;
	
	MailData ham; //not private for easier testing
	MailData spam;
	
	/**
	 * Gathers data to later be able to calculate the probability of a given mail being spam or not.
	 * @throws IOException
	 */
	public void learn() throws IOException {
		ham = readMails(new ZipFile("ham-anlern.zip"));
		spam = readMails(new ZipFile("spam-anlern.zip"));
		correlate();
	}
	
	/**
	 * Makes sure every word in ham is also in spam and vice versa. This is done 
	 * to avoid any of the probabilities becoming 0 (@see Maildata).
	 * Should be called after data has been added to spam or ham.
	 */
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
	
	/**
	 * Reads mails out of a zip file and stores data in MailData Object.
	 * @param zipFile containing emails
	 * @return the MailData gathered from the mails in zipFile
	 * @throws IOException
	 */
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
	
	/**
	 * Uses Bayes Formula and gathered data to calculate the probability of a mail being spam or ham.
	 * @param instream The email
	 * @param hamOrSpam If true: return P(Ham|words), else P(Spam|words)
	 * @return P(Ham/Spam|words)
	 */
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
