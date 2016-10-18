package dist_spamfilter;

public class Counter {
	private int count = 0;
	
	public void inc() {
		count++;
	}
	
	public int get() {
		return count;
	}
	
	public String toString() {
		return Integer.toString(count);
	}
}
