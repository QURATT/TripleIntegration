package de.di.similarity_measures;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class Levenshtein implements SimilarityMeasure {

    public static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

    // The choice of whether Levenshtein or DamerauLevenshtein should be calculated.
    private final boolean withDamerau;

    /**
     * Calculates the Levenshtein similarity of the two input strings.
     * The Levenshtein similarity is defined as "1 - normalized Levenshtein distance".
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The (Damerau) Levenshtein similarity of the two arguments.
     */
    @Override
    public double calculate(final String string1, final String string2) {
        if (string1 == null || string2 == null || string1.isEmpty() || string2.isEmpty())
            return 0;

        int len1 = string1.length();
        int len2 = string2.length();

        int[] upperupperLine = new int[len1 + 1]; // i-2
        int[] upperLine = new int[len1 + 1];      // i-1
        int[] lowerLine = new int[len1 + 1];      // i

        // Fill the first line with the initial positions (= edits to generate string1 from nothing)
        for (int i = 0; i <= len1; i++)
            upperLine[i] = i;

        for (int i = 1; i <= len2; i++) {
            lowerLine[0] = i;
            for (int j = 1; j <= len1; j++) {
                int cost = (string2.charAt(i - 1) == string1.charAt(j - 1)) ? 0 : 1;

                int deletion = upperLine[j] + 1;
                int insertion = lowerLine[j - 1] + 1;
                int substitution = upperLine[j - 1] + cost;

                int distance = min(deletion, insertion, substitution);

                // Damerau-Levenshtein
                if (withDamerau && i > 1 && j > 1 &&
                        string2.charAt(i - 1) == string1.charAt(j - 2) &&
                        string2.charAt(i - 2) == string1.charAt(j - 1)) {
                    distance = Math.min(distance, upperupperLine[j - 2] + 1);
                }

                lowerLine[j] = distance;
            }

            // shift lines
            int[] temp = upperupperLine;
            upperupperLine = upperLine;
            upperLine = lowerLine;
            lowerLine = temp;
        }

        int distance = upperLine[len1];
        return 1 - (double) distance / Math.max(len1, len2);
    }

    /**
     * Calculates the Levenshtein similarity of the two input string lists.
     * The Levenshtein similarity is defined as "1 - normalized Levenshtein distance".
     * For string lists, we consider each list as an ordered list of tokens and calculate the distance as the number of
     * token insertions, deletions, replacements (and swaps) that transform one list into the other.
     * @param strings1 The first string list argument for the similarity calculation.
     * @param strings2 The second string list argument for the similarity calculation.
     * @return The (multiset) Levenshtein similarity of the two arguments.
     */
    @Override
    public double calculate(final String[] strings1, final String[] strings2) {
        if (strings1 == null || strings2 == null || strings1.length == 0 || strings2.length == 0)
            return 0;

        int len1 = strings1.length;
        int len2 = strings2.length;

        int[] upperupperLine = new int[len1 + 1];
        int[] upperLine = new int[len1 + 1];
        int[] lowerLine = new int[len1 + 1];

        for (int i = 0; i <= len1; i++)
            upperLine[i] = i;

        for (int i = 1; i <= len2; i++) {
            lowerLine[0] = i;
            for (int j = 1; j <= len1; j++) {
                int cost = (strings2[i - 1].equals(strings1[j - 1])) ? 0 : 1;

                int deletion = upperLine[j] + 1;
                int insertion = lowerLine[j - 1] + 1;
                int substitution = upperLine[j - 1] + cost;

                int distance = min(deletion, insertion, substitution);

                if (withDamerau && i > 1 && j > 1 &&
                        strings2[i - 1].equals(strings1[j - 2]) &&
                        strings2[i - 2].equals(strings1[j - 1])) {
                    distance = Math.min(distance, upperupperLine[j - 2] + 1);
                }

                lowerLine[j] = distance;
            }

            int[] temp = upperupperLine;
            upperupperLine = upperLine;
            upperLine = lowerLine;
            lowerLine = temp;
        }

        int distance = upperLine[len1];
        return 1 - (double) distance / Math.max(len1, len2);
    }
}

