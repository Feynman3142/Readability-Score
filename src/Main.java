import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String inPath;
        if (args.length == 0) {
            System.out.println("Enter the path to the text file: ");
            inPath = scanner.nextLine();
        } else {
            inPath = args[0];
        }
        Map<String, Integer> features = TextParser.parseText(inPath);
        if (features == null) {
            System.out.println("Exiting...");
            return;
        }
        for (String feature : features.keySet()) {
            System.out.printf("%c%s: %d\n", (char) (feature.charAt(0) - 32), feature.substring(1), features.get(feature));
        }
        int numSentences = features.get("sentences");
        int numWords = features.get("words");
        int numChars = features.get("characters");
        int numSyllables = features.get("syllables");
        int numPolySyllables = features.get("polysyllables");
        System.out.print("Enter the metric you want to use (ARI, FK, SMOG, CL, all): ");
        String choice = scanner.nextLine();
        System.out.println();
        displayScore(choice, numSentences, numWords, numChars, numSyllables, numPolySyllables);
    }

    private static int displayScore(String metric, int numSentences, int numWords, int numChars, int numSyllables, int numPolySyllables) {
        double score;
        switch(metric.toLowerCase()) {
            case "ari":
                System.out.print("Automated Readability Index: ");
                score = Metric.calculateARIScore(numSentences, numWords, numChars);
                break;
            case "fk":
                System.out.print("Flesch-Kincaid readability tests: ");
                score = Metric.calculateFKScore(numSentences, numWords, numSyllables);
                break;
            case "smog":
                System.out.print("Simple Measure of Gobbledygook: ");
                score = Metric.calculateSMOGScore(numSentences, numPolySyllables);
                break;
            case "cl":
                System.out.print("Coleman-Liau index: ");
                score = Metric.calculateCLScore(numSentences, numWords, numChars);
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
                System.out.printf("'%s' metric not supported/existent!", metric);
                return -1;
        }
        int age = Metric.mapScore(score);
        System.out.printf("%.2f (about %d year olds).\n", score, age);
        return age;
    }
}
