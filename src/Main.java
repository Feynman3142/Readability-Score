import java.util.Scanner;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Math;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No path to input text file found!");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
            Scanner scanner = new Scanner(System.in);
            int numSentences = 0;
            int numWords = 0;
            int numChars = 0;
            int numSyllables = 0;
            int numPolySyllables = 0;

            System.out.println("The text is:");
            while (reader.ready()) {
                String text = reader.readLine();
                System.out.println(text);
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
                    String[] words = sentence.split("\\s+");
                    numWords += words.length;
                    for (String word : words) {
                        if ("".equals(word)) {
                            --numWords;
                        } else {
                            numChars += word.length();
                            int numWordSyllables = countSyllables(word);
                            numSyllables += numWordSyllables;
                            if (numWordSyllables > 2) {
                                ++numPolySyllables;
                            }
                            /*if (words[word].matches("\\W+")) {
                                --numWords;
                            }*/
                        }
                    }
                }
            }
            System.out.printf("\nWords: %d\nSentences: %d\nCharacters: %d\nSyllables: %d\nPolysyllables: %d\n\n\n", numWords, numSentences, numChars, numSyllables, numPolySyllables);
            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
            String choice = scanner.nextLine();
            System.out.println();
            displayScore(choice, numSentences, numWords, numChars, numSyllables, numPolySyllables);
        } catch (FileNotFoundException e) {
            System.out.printf("File (%s) not found!", args[0]);
        } catch (IOException e) {
            System.out.printf("Error reading from file (%s)!\n[DETAILS: %s]", args[0], e.getMessage());
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

    private static int displayScore(String scoreType, int numSentences, int numWords, int numChars, int numSyllables, int numPolySyllables) {
        double score;
        switch(scoreType.toLowerCase()) {
            case "ari":
                System.out.print("Automated Readability Index: ");
                score = calculateARIScore(numSentences, numWords, numChars);
                break;
            case "fk":
                System.out.print("Flesch-Kincaid readability tests: ");
                score = calculateFKScore(numSentences, numWords, numSyllables);
                break;
            case "smog":
                System.out.print("Simple Measure of Gobbledygook: ");
                score = calculateSMOGScore(numSentences, numPolySyllables);
                break;
            case "cl":
                System.out.print("Coleman-Liau index: ");
                score = calculateCLScore(numSentences, numWords, numChars);
                break;
            case "all":
                int ageARI = displayScore("ari", numSentences, numWords, numChars, numSyllables, numPolySyllables);
                int ageFK = displayScore("fk", numSentences, numWords, numChars, numSyllables, numPolySyllables);
                int ageSMOG = displayScore("smog", numSentences, numWords, numChars, numSyllables, numPolySyllables);
                int ageCL = displayScore("cl", numSentences, numWords, numChars, numSyllables, numPolySyllables);
                double avgAge = (ageARI + ageFK + ageSMOG + ageCL) / 4.0;
                System.out.printf("\nThis text should be understood in average by %.2f year olds.\n", avgAge);
                return (int) Math.ceil(avgAge);
            default:
                System.out.printf("%s score not supported/existent!", scoreType);
                return -1;
        }
        int age = mapScore(score);
        System.out.printf("%.2f (about %d year olds).\n", score, age);
        return age;
    }

    private static double calculateARIScore(int numSentences, int numWords, int numChars) {
        return (((4.71 * numChars) / numWords) + ((0.5 * numWords) / numSentences) - 21.43);
    }

    private static double calculateFKScore(int numSentences, int numWords, int numSyllables) {
        return ((0.39 * numWords) / numSentences) + ((11.8 * numSyllables) / numWords) - 15.59;
    }

    private static double calculateSMOGScore(int numSentences, int numPolySyllables) {
        return (1.043 * Math.sqrt((numPolySyllables * 30.0) / numSentences)) + 3.1291;
    }

    private static double calculateCLScore(int numSentences, int numWords, int numChars) {
        double L = (numChars * 100.0) / numWords;
        double S = (numSentences * 100.0) / numWords;
        return (0.0588 * L) - (0.296 * S) - 15.8;
    }

    private static int mapScore(double score) {
        int mappedScore = (int) Math.round(score);
        if (mappedScore > 12) {
            return 24;
        } else if (mappedScore > 2) {
            return (mappedScore + 6);
        } else if (mappedScore > 0) {
            return (mappedScore + 5);
        } else {
            return 6;
        }
    }
}
