//package de.di.similarity_measures;
//
//import de.di.similarity_measures.helper.Tokenizer;
//import lombok.AllArgsConstructor;
//
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@AllArgsConstructor
//public class Jaccard implements SimilarityMeasure {
//
//    // The tokenizer that is used to transform string inputs into token lists.
//    private final Tokenizer tokenizer;
//
//    // A flag indicating whether the Jaccard algorithm should use set or bag semantics for the similarity calculation.
//    private final boolean bagSemantics;
//
//    /**
//     * Calculates the Jaccard similarity of the two input strings. Note that the Jaccard similarity may use set or
//     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
//     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
//     * @param string1 The first string argument for the similarity calculation.
//     * @param string2 The second string argument for the similarity calculation.
//     * @return The multiset Jaccard similarity of the two arguments.
//     */
//    @Override
//    public double calculate(String string1, String string2) {
//        string1 = (string1 == null) ? "" : string1;
//        string2 = (string2 == null) ? "" : string2;
//
//        String[] strings1 = this.tokenizer.tokenize(string1);
//        String[] strings2 = this.tokenizer.tokenize(string2);
//        return this.calculate(strings1, strings2);
//    }
//
//    /**
//     * Calculates the Jaccard similarity of the two string lists. Note that the Jaccard similarity may use set or
//     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
//     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
//     * @param strings1 The first string list argument for the similarity calculation.
//     * @param strings2 The second string list argument for the similarity calculation.
//     * @return The multiset Jaccard similarity of the two arguments.
//     */
//    @Override
//    public double calculate(String[] strings1, String[] strings2) {
//        double jaccardSimilarity = 0;
//
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //                                      DATA INTEGRATION ASSIGNMENT                                           //
//        // Calculate the Jaccard similarity of the two String arrays. Note that the Jaccard similarity needs to be    //
//        // calculated differently depending on the token semantics: set semantics remove duplicates while bag         //
//        // semantics consider them during the calculation. The solution should be able to calculate the Jaccard       //
//        // similarity either of the two semantics by respecting the inner bagSemantics flag.                          //
//
//
//
//        //                                                                                                            //
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//        if (bagSemantics) {
//            // Multiset (bag) semantics
//            Map<String, Long> freq1 = Arrays.stream(strings1)
//                    .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
//            Map<String, Long> freq2 = Arrays.stream(strings2)
//                    .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
//
//            Set<String> allTokens = new HashSet<>();
//            allTokens.addAll(freq1.keySet());
//            allTokens.addAll(freq2.keySet());
//
//            long intersection = 0;
//            long union = 0;
//
//            for (String token : allTokens) {
//                long count1 = freq1.getOrDefault(token, 0L);
//                long count2 = freq2.getOrDefault(token, 0L);
//                intersection += Math.min(count1, count2);
//                union += Math.max(count1, count2);
//            }
//
//            jaccardSimilarity = (union == 0) ? 0 : (double) intersection / union;
//
//        } else {
//            // Set semantics
//            Set<String> set1 = new HashSet<>(Arrays.asList(strings1));
//            Set<String> set2 = new HashSet<>(Arrays.asList(strings2));
//
//            Set<String> intersection = new HashSet<>(set1);
//            intersection.retainAll(set2);
//
//            Set<String> union = new HashSet<>(set1);
//            union.addAll(set2);
//
//            jaccardSimilarity = (union.size() == 0) ? 0 : (double) intersection.size() / union.size();
//        }
//
//        return jaccardSimilarity;
//    }
//}
package de.di.similarity_measures;

import de.di.similarity_measures.helper.Tokenizer;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Jaccard implements SimilarityMeasure {

    // The tokenizer that is used to transform string inputs into token lists.
    private final Tokenizer tokenizer;

    // A flag indicating whether the Jaccard algorithm should use set or bag semantics for the similarity calculation.
    private final boolean bagSemantics;

    /**
     * Calculates the Jaccard similarity of the two input strings. Note that the Jaccard similarity may use set or
     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The multiset Jaccard similarity of the two arguments.
     */
    @Override
    public double calculate(String string1, String string2) {
        string1 = (string1 == null) ? "" : string1;
        string2 = (string2 == null) ? "" : string2;

        String[] strings1 = this.tokenizer.tokenize(string1);
        String[] strings2 = this.tokenizer.tokenize(string2);
        return this.calculate(strings1, strings2);
    }

    /**
     * Calculates the Jaccard similarity of the two string lists. Note that the Jaccard similarity may use set or
     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
     * @param strings1 The first string list argument for the similarity calculation.
     * @param strings2 The second string list argument for the similarity calculation.
     * @return The multiset Jaccard similarity of the two arguments.
     */
    @Override
    public double calculate(String[] strings1, String[] strings2) {
        double jaccardSimilarity = 0;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Calculate the Jaccard similarity of the two String arrays. Note that the Jaccard similarity needs to be    //
        // calculated differently depending on the token semantics: set semantics remove duplicates while bag         //
        // semantics consider them during the calculation. The solution should be able to calculate the Jaccard       //
        // similarity either of the two semantics by respecting the inner bagSemantics flag.                          //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        if (strings1 == null) strings1 = new String[0];
        if (strings2 == null) strings2 = new String[0];

        if (bagSemantics) {
            // Multiset (bag) semantics
            Map<String, Integer> freq1 = new HashMap<>();
            Map<String, Integer> freq2 = new HashMap<>();

            for (String s : strings1)
                freq1.put(s, freq1.getOrDefault(s, 0) + 1);
            for (String s : strings2)
                freq2.put(s, freq2.getOrDefault(s, 0) + 1);

            int intersection = 0;
            for (String token : freq1.keySet()) {
                if (freq2.containsKey(token)) {
                    intersection += Math.min(freq1.get(token), freq2.get(token));
                }
            }

            int totalTokens = strings1.length + strings2.length;
            jaccardSimilarity = totalTokens == 0 ? 0 : (double) intersection / totalTokens;
//            Map<String, Integer> freq1 = new HashMap<>();
//            for (String s : strings1) freq1.put(s, freq1.getOrDefault(s, 0) + 1);
//
//            Map<String, Integer> freq2 = new HashMap<>();
//            for (String s : strings2) freq2.put(s, freq2.getOrDefault(s, 0) + 1);
//
//            Set<String> allTokens = new HashSet<>();
//            allTokens.addAll(freq1.keySet());
//            allTokens.addAll(freq2.keySet());
//
//            int intersection = 0;
//            int union = 0;
//
//            for (String token : allTokens) {
//                int count1 = freq1.getOrDefault(token, 0);
//                int count2 = freq2.getOrDefault(token, 0);
//                intersection += Math.min(count1, count2);
//                union += Math.max(count1, count2);
//            }
//
//            jaccardSimilarity = (union == 0) ? 0 : (double) intersection / union;
        } else {
            // Set semantics
            Set<String> set1 = new HashSet<>(Arrays.asList(strings1));
            Set<String> set2 = new HashSet<>(Arrays.asList(strings2));

            Set<String> intersection = new HashSet<>(set1);
            intersection.retainAll(set2);

            Set<String> union = new HashSet<>(set1);
            union.addAll(set2);

            jaccardSimilarity = (union.size() == 0) ? 0 : (double) intersection.size() / union.size();
        }

        return jaccardSimilarity;
    }
}

