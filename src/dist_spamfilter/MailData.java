package dist_spamfilter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MailData {
    
    private int mailCount = 0;
    Map<String, Counter> wordCount = new HashMap<>();
    
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
    
    public double calculateProbability(String word) {
        return getWordCount(word)/getMailCount();
    }
}
