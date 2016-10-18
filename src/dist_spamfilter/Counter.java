package dist_spamfilter;

public class Counter {
	private double count = 0;
	
	public void inc() {
		count++;
	}
	
	public double get() {
		return count;
	}
	
	public String toString() {
		return Double.toString(count);
	}
	
	public void setVeryLow() {
		count = 0.0001;
	}
}
