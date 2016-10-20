package dist_spamfilter;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
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
	final double ALPHA = 0.1; //counter for dummy values (see MailData)
	
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
		return calculateProbability(words, hamOrSpam);
	}
	
	/**
     * Uses Bayes Formula and gathered data to calculate the probability of a mail being spam or ham.
     * @param words The email
     * @param hamOrSpam If true: return P(Ham|words), else P(Spam|words)
     * @return P(Ham/Spam|words)
     */
	public double calculateProbability(Collection<String> words, boolean hamOrSpam) {
	    BigDecimal numerator = new BigDecimal(1, new MathContext(100));
        BigDecimal denominator = new BigDecimal(1, new MathContext(100));
        for (String word : words) {
            //only take big vals to avoid underflow
            if (ham.calculateProbability(word) > .001 || spam.calculateProbability(word) > .001) {
                if (hamOrSpam) { 
                    numerator = numerator.multiply(new BigDecimal(ham.calculateProbability(word)), new MathContext(100)); //TODO double underflow! change formula somehow
                    denominator = denominator.multiply(new BigDecimal(spam.calculateProbability(word)), new MathContext(100));
                }
                else {
                    numerator = numerator.multiply(new BigDecimal(spam.calculateProbability(word)), new MathContext(20));
                    denominator = denominator.multiply(new BigDecimal(ham.calculateProbability(word)), new MathContext(20));
                }
            }
        }
        denominator = denominator.add(numerator);
        return numerator.divide(denominator, 100, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
