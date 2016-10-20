package dist_spamfilter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Used to store data gathered from emails. Class uses a Map<String, Counter> to
 * keep track on how often a word has occured in a set of emails.
 */
public class MailData {
    
    private int mailCount = 0;
    private Map<String, Counter> wordCount = new TreeMap<>();
    
    /**
     * Dummy words are needed because if there is a word missing, the calculated 
     * probability will we 0, which results in wrong behaviour of the Bayes formula
     * @param word
     * @param dummyCount should be set somewhere below 1 (find good value by calibrating)
     */
    public void addDummyWord(String word, double dummyCount) {
    	wordCount.put(word, new Counter(dummyCount));
    	//mailCount++; //XXX needed?
    }
    
    /**
     * Adds an email (=collection of words) to this container and updates the 
     * corresponding Counters for each word.
     * @param mail
     */
    public void add(Collection<String> mail) {
        for (String word : mail) {
            if (!wordCount.containsKey(word)) {
                wordCount.put(word, new Counter()); //initialize counter with 0
            }
            wordCount.get(word).inc(); //increase counter by 1
        }
        mailCount++;
    }
    
    public int getMailCount() {
        return mailCount;
    }
    
    public double getWordCount(String word) {
        return wordCount.getOrDefault(word, new Counter()).getValue();
    }
    
    public Set<String> getWords() {
    	return wordCount.keySet();
    }
    
    /**
     * Calculates the probability of a word occuring in an email according to collected data.
     * @param word
     * @return probability
     */
    public double calculateProbability(String word) {
    	double prob = getWordCount(word)/getMailCount();
        return prob;
    }
}
