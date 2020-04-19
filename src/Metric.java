class Metric {
    /*
        Class that houses the formulae for the different metrics used to calculate the readability of a text
     */

    /**
     * Calculates the readability score based on the Automated Readability Index (ARI)
     * @param numSentences the # of sentences in the text
     * @param numWords the # of words in the text
     * @param numChars the # of characters in the text
     * @return the score according to the ARI metric
     */
    static double calculateARIScore(int numSentences, int numWords, int numChars) {
        return (((4.71 * numChars) / numWords) + ((0.5 * numWords) / numSentences) - 21.43);
    }

    /**
     * Calculates the readability score based on the Flesch-Kincaid (FK) metric
     * @param numSentences the # of sentences in the text
     * @param numWords the # of words in the text
     * @param numSyllables the # of syllables in the text
     * @return the score according to the FK metric
     */
    static double calculateFKScore(int numSentences, int numWords, int numSyllables) {
        return ((0.39 * numWords) / numSentences) + ((11.8 * numSyllables) / numWords) - 15.59;
    }

    /**
     * Calculates the readability score based on the Simple Measure of Gobbledygook (SMOG) metric
     * @param numSentences the # of sentences in the text
     * @param numPolySyllables the # of polysyllables in the text
     * @return the score according to the SMOG metric
     */
    static double calculateSMOGScore(int numSentences, int numPolySyllables) {
        return (1.043 * Math.sqrt((numPolySyllables * 30.0) / numSentences)) + 3.1291;
    }

    /**
     * Calculates the readability score based on the Coleman-Liau (CL) metric
     * @param numSentences the # of sentences in the text
     * @param numWords the # of words in the text
     * @param numChars the # of characters in the text
     * @return the score according to the CL metric
     */
    static double calculateCLScore(int numSentences, int numWords, int numChars) {
        double L = (numChars * 100.0) / numWords;
        double S = (numSentences * 100.0) / numWords;
        return (0.0588 * L) - (0.296 * S) - 15.8;
    }

    /**
     * Maps the readability score from a metric to an age range of readers that will understand the text
     * @param score the readability score from a metric
     * @return the upper age bound of readers that will understand the text
     */
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


