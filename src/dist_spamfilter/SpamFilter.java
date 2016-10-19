package dist_spamfilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SpamFilter {
	final double P_H = 0.5;
	final double P_S = 0.5;
	
	MailData ham;
	MailData spam;
	
	//TODO: refactor, better method names, more OO
	//TODO: make sure every word is in ham as well as spam! (set counter to verylow? or solve it with defualtvalue...)
	
	public void learn() throws IOException {
		ham = readMails(new ZipFile("ham-anlern.zip"));
		spam = readMails(new ZipFile("spam-anlern.zip"));
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

	public double calculateSpamProbability(InputStream instream) {
		Set<String> words = Util.getWords(instream);
		double numerator = P_S; //TODO is it right to use these values? or should it just be 1?
		double denominator = P_H;
		for (String word : words) {
		    numerator *= spam.calculateProbability(word);
		    denominator *= ham.calculateProbability(word);
		}
		denominator += numerator;
	    return numerator / denominator;
	}
}
