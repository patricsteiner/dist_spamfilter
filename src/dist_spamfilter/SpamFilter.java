package dist_spamfilter;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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
	final double ALPHA = 0.01; //counter for dummy values (see MailData)
	
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
	 * to avoid any of the probabilities becoming 0 (@see MailData).
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
	 * @param useLogFormula use different approach for calculation (@see https://en.wikipedia.org/wiki/Naive_Bayes_spam_filtering)
	 * @return P(Ham/Spam|words)
	 */
	public double calculateProbability(InputStream instream, boolean hamOrSpam, boolean useLogFormula) {
		Collection<String> words = Util.getWords(instream);
		return calculateProbability(words, hamOrSpam, useLogFormula);
	}
	
	/**
     * Uses Bayes Formula and gathered data to calculate the probability of a mail being spam or ham.
     * @param words The email
     * @param hamOrSpam If true: return P(Ham|words), else P(Spam|words)
     * @param useLogFormula use different approach for calculation, but it doesn't work as well as 
     * the first method. Just set it to false (@see https://en.wikipedia.org/wiki/Naive_Bayes_spam_filtering).
     * @return P(Ham/Spam|words)
     */
	public double calculateProbability(Collection<String> words, boolean hamOrSpam, boolean useLogFormula) {
		MathContext mc = new MathContext(500, RoundingMode.HALF_UP);
		if (!useLogFormula) {
		    BigDecimal numerator = new BigDecimal(1, mc);
	        BigDecimal denominator = new BigDecimal(1, mc);
	        for (String word : words) {
	        	double ph = ham.calculateProbability(word);
	        	double ps = spam.calculateProbability(word);
	            if (( ph > 0 || ps > 0 ) && ph < 1 && ps < 1 ) {
	                if (hamOrSpam) { 
	                    numerator = numerator.multiply(BigDecimal.valueOf(ph), mc);
	                    denominator = denominator.multiply(BigDecimal.valueOf(ps), mc);
	                }
	                else {
	                    numerator = numerator.multiply(BigDecimal.valueOf(ps), mc);
	                    denominator = denominator.multiply(BigDecimal.valueOf(ph), mc);
	                }
	            }
	        }
	        denominator = denominator.add(numerator, mc);
	        return numerator.divide(denominator, mc).doubleValue();
		}
		else {
			//doesn't work that well
		    BigDecimal sum = new BigDecimal(0, mc);
	        for (String word : words) {
	        	//if (ham.calculateProbability(word) > .1 || spam.calculateProbability(word) > .1)
	        	if (hamOrSpam) {
	        		sum.add(BigDecimal.valueOf(Math.log(1 - ham.calculateProbability(word)) - Math.log(ham.calculateProbability(word))));
	        	} else {
	        		double p = spam.calculateProbability(word);
	        		if (p < 1 && p > 0) { //there are words like "from" that are in every mail, ignore these
		        		sum = sum.add(BigDecimal.valueOf(Math.log(1 - p) - Math.log(p)));
	        		}
	        	}
	        }
	        return 1 /(1 + BigDecimal.valueOf(Math.E).pow(sum.intValue()).doubleValue());
		}
	}
	
	/**
	 * Classifies an email (=Colleciton<String>) as either ham or spam.
	 * @param words
	 * @return true if mail is classified as spam, false if ham.
	 */
	public boolean classify(Collection<String> words) {
		double ph = calculateProbability(words, true, false);
		double ps = calculateProbability(words, false, false);
		return ps > ph;
	}
	
	/**
	 * Classifies an email (=InputStream) as either ham or spam.
	 * @param words
	 * @return true if mail is classified as spam, false if ham.
	 */
	public boolean classify(InputStream instream) {
		Collection<String> words = Util.getWords(instream);
		return classify(words);
	}
}
