package dist_spamfilter;

/**
 * Used to count stuff. Value is double because on special occasions 
 * (for dummy objects, @see MailData) the counter needs to be > 0 and < 1
 * @author Patric
 *
 */
public class Counter {
	private double value = 0;
	
	public Counter() {
	    
	}
	
	public Counter(double init) {
	    value = init;
	}
	
	public void inc() {
		value++;
	}
	
	public double getValue() {
		return value;
	}
	
	public String toString() {
		return Double.toString(value);
	}
}
