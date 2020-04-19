class Metric {

    static double calculateARIScore(int numSentences, int numWords, int numChars) {
        return (((4.71 * numChars) / numWords) + ((0.5 * numWords) / numSentences) - 21.43);
    }

    static double calculateFKScore(int numSentences, int numWords, int numSyllables) {
        return ((0.39 * numWords) / numSentences) + ((11.8 * numSyllables) / numWords) - 15.59;
    }

    static double calculateSMOGScore(int numSentences, int numPolySyllables) {
        return (1.043 * Math.sqrt((numPolySyllables * 30.0) / numSentences)) + 3.1291;
    }

    static double calculateCLScore(int numSentences, int numWords, int numChars) {
        double L = (numChars * 100.0) / numWords;
        double S = (numSentences * 100.0) / numWords;
        return (0.0588 * L) - (0.296 * S) - 15.8;
    }

    static int mapScore(double score) {
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


