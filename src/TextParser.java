import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TextParser {

    static Map<String,Integer> parseText(String filePath) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            int numSentences = 0;
            int numWords = 0;
            int numChars = 0;
            int numSyllables = 0;
            int numPolySyllables = 0;

            while (reader.ready()) {
                String text = reader.readLine();
                if (text.matches("\\s+")) {
                    continue;
                }
                String[] sentences = text.split("[.?!]\\s*");
                numSentences += sentences.length;
                final Pattern eosPattern = Pattern.compile("[.?!]");
                Matcher eosPatternMatcher = eosPattern.matcher(text);
                while (eosPatternMatcher.find()) {
                    ++numChars;
                }

                for (String sentence : sentences) {
                    String[] words = sentence.trim().split("\\s+");
                    numWords += words.length;
                    for (String word : words) {
                        numChars += word.length();
                        int numWordSyllables = countSyllables(word);
                        numSyllables += numWordSyllables;
                        if (numWordSyllables > 2) {
                            ++numPolySyllables;
                        }
                    }
                }
            }
            return Map.of("sentences", numSentences,
                    "words", numWords,
                    "characters", numChars,
                    "syllables", numSyllables,
                    "polysyllables", numPolySyllables);

        } catch (FileNotFoundException e) {
            System.out.printf("Could not find file: %s\n", filePath);
            return null;
        } catch (IOException e) {
            System.out.printf("Error reading from file: %s\n[DETAILS: %s]\n", filePath, e.getMessage());
            return null;
        }
    }

    private static int countSyllables(String origWord) {

        boolean prevChIsVowel = false;
        int lastVowelInd = Integer.MAX_VALUE;
        int lastCharInd = Integer.MIN_VALUE;
        int syllableCount = 0;
        String word = origWord.toLowerCase();

        for (int ind = 0; ind < word.length(); ++ind) {
            char ch = word.charAt(ind);
            if (!isLetter(ch)) {
                if ((ind - 1 == lastVowelInd) && (word.charAt(ind - 1) == 'e')) {
                    System.out.println(word);
                    --syllableCount;
                }
                prevChIsVowel = false;
            } else {
                if (isVowel(ch)) {
                    if (!prevChIsVowel) {
                        ++syllableCount;
                        lastVowelInd = ind;
                    }
                    prevChIsVowel = true;
                } else {
                    prevChIsVowel = false;
                }
                lastCharInd = ind;
            }
        }

        if ((lastCharInd == lastVowelInd) && (word.charAt(lastCharInd) == 'e')) {
            --syllableCount;
        }

        return (syllableCount > 0) ? syllableCount : 1;
    }

    private static boolean isLetter(char ch) {
        return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'));
    }

    private static boolean isVowel(char ch) {
        char actCh = (ch >= 'A' && ch <= 'Z') ? (char) (ch + 32) : ch;
        switch(actCh) {
            case 'a': case 'e' : case 'i' : case 'o' : case 'u' : case 'y' : return true;
            default: return false;
        }
    }
}