package dist_spamfilter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MailData {
    
    private int mailCount = 0;
    private Map<String, Counter> wordCount = new TreeMap<>();
    
    public void addDummyWord(String word, double dummyCount) {
    	wordCount.put(word, new Counter(dummyCount));
    	//mailCount++; //XXX needed?
    }
    
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
    
    public double calculateProbability(String word) {
        return getWordCount(word)/getMailCount();
    }
}
