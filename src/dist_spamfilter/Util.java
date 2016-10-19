package dist_spamfilter;

import java.io.InputStream;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

public class Util {
    public static Set<String> getWords(InputStream instream) {
        Set<String> words = new HashSet<>();
        Scanner scanner = new Scanner(instream);
        scanner.useDelimiter(" ");
        while (scanner.hasNext()) {
            try {
                String word = scanner.next("[a-zA-Z0-9]+").toLowerCase();
                words.add(word);
            }
            catch (InputMismatchException e) {
                scanner.next();
            }
        }
        scanner.close();
        return words;
    }
}
