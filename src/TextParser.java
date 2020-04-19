import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TextParser {
    /* Class to determine the important features of the text.
        Currently supported features: # of sentences, words, characters, syllables, polysyllables.
     */

    /**
     * Determines important features of text
     * @param filePath Path to the input text file as a string
     * @return Map of calculated features (sentences, words, characters, syllables, polysyllables)
     */
    static Map<String,Integer> parseText(String filePath) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            int numSentences = 0;
            int numWords = 0;
            int numChars = 0;
            int numSyllables = 0;
            int numPolySyllables = 0;

            while (reader.ready()) {
                String text = reader.readLine();
                // If you find an empty line (only consisting of spaces, tabs, etc.), skip it
                if (text.matches("\\s*")) {
                    continue;
                }
                // English sentences end with periods (.), exclamation points (!), or question marks (?)
                // Also account for the fact that these may be accompanied with quotes
                // Use that to split text into sentences
                String[] sentences = text.split("[.?!]+[”'\"]*\\s*");
                numSentences += sentences.length;
                final Pattern eosPattern = Pattern.compile("[.?!]+[”'\"]*");
                Matcher eosPatternMatcher = eosPattern.matcher(text);
                // Since we split on the above pattern, those characters need to be accounted for
                // This is done in the following lines
                while (eosPatternMatcher.find()) {
                    // This formula is used to account for consecutive . or ! or ? (eg: '!?') along with quotes
                    numChars += eosPatternMatcher.end() - eosPatternMatcher.start();
                }
                for (String sentence : sentences) {
                    // Counts every group of characters that doesn't contain any 'space' characters as a word
                    // May be subject to change
                    String[] words = sentence.trim().split("\\s+");
                    numWords += words.length;
                    for (String word : words) {
                        numChars += word.length();
                        int numWordSyllables = countSyllables(word);
                        numSyllables += numWordSyllables;
                        // A polysyllable is a word that has more than 2 syllables
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

    /**
     * Counts the syllables in a given word
     * @param origWord the word in which to count syllables
     * @return the # of syllables in the word
     */
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
                    //System.out.println(word);
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

    /**
     * Check if a character belongs to the English alphabet
     * @param ch the character to check
     * @return true if it belongs, false if it doesn't
     */
    private static boolean isLetter(char ch) {
        return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'));
    }

    /**
     * Check if a character is a vowel
     * @param ch the character to check
     * @return true if it is a vowel, false otherwise (note: does not mean it's a consonant; could be a non-English letter)
     */
    private static boolean isVowel(char ch) {
        char actCh = (ch >= 'A' && ch <= 'Z') ? (char) (ch + 32) : ch;
        switch(actCh) {
            case 'a': case 'e' : case 'i' : case 'o' : case 'u' : case 'y' : return true;
            default: return false;
        }
    }
}