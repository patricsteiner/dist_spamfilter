package dist_spamfilter;

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
	
	public void setVeryLow() {
	    value = 0.0001;
	}
}
