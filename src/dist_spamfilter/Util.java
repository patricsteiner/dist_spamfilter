package dist_spamfilter;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Util {
    public static Collection<String> getWords(InputStream instream) {
        Collection<String> words = new HashSet<>(); //use either Set or List, depending on if you want duplicates or not
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
