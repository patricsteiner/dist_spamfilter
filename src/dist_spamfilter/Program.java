package dist_spamfilter;

import java.io.IOException;

public class Program {

	public static void main(String[] args) {
		try {
			new SpamFilter().learn();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
