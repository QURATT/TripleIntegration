package de.di.schema_matching;

import de.di.Relation;
import de.di.schema_matching.structures.SimilarityMatrix;
import de.di.similarity_measures.Jaccard;
import de.di.similarity_measures.helper.Tokenizer;

public class FirstLineSchemaMatcher {

    public SimilarityMatrix match(Relation sourceRelation, Relation targetRelation) {
        String[][] sourceColumns = sourceRelation.getColumns();
        String[][] targetColumns = targetRelation.getColumns();

        int sourceAttrCount = sourceColumns.length;
        int targetAttrCount = targetColumns.length;

        double[][] matrix = new double[sourceAttrCount][targetAttrCount];

        // Tokenizer trigram with padding
        Tokenizer tokenizer = new Tokenizer(3, true);

        // Jaccard with bag semantics
        Jaccard jaccardBag = new Jaccard(tokenizer, true);

        // Jaccard with set semantics
        Jaccard jaccardSet = new Jaccard(tokenizer, false);

        for (int i = 0; i < sourceAttrCount; i++) {
            StringBuilder sourceConcat = new StringBuilder();
            for (String val : sourceColumns[i]) {
                if (val != null) {
                    sourceConcat.append(val.toLowerCase()).append(" ");
                }
            }
            String sourceString = sourceConcat.toString().trim();

            for (int j = 0; j < targetAttrCount; j++) {
                StringBuilder targetConcat = new StringBuilder();
                for (String val : targetColumns[j]) {
                    if (val != null) {
                        targetConcat.append(val.toLowerCase()).append(" ");
                    }
                }
                String targetString = targetConcat.toString().trim();

                double simBag = jaccardBag.calculate(sourceString, targetString);
                double simSet = jaccardSet.calculate(sourceString, targetString);

                // Combine similarities - weighted average (equal weight here)
                double combinedSim = (simBag + simSet) / 2.0;

                matrix[i][j] = combinedSim;
            }
        }

        return new SimilarityMatrix(matrix, sourceRelation, targetRelation);
    }
}






//package de.di.schema_matching;
//
//import de.di.Relation;
//import de.di.schema_matching.structures.SimilarityMatrix;
//import de.di.similarity_measures.Jaccard;
//import de.di.similarity_measures.helper.Tokenizer;
//
//public class FirstLineSchemaMatcher {
//
//    /**
//     * Matches the attributes of the source and target table and produces a #source_attributes x #target_attributes
//     * sized similarity matrix that represents the attribute-to-attribute similarities of the two relations.
//     * @param sourceRelation The first relation for the matching that determines the first (= y) dimension of the
//     *                       similarity matrix, i.e., double[*][].
//     * @param targetRelation The second relation for the matching that determines the second (= x) dimension of the
//     *                       similarity matrix, i.e., double[][*].
//     * @return The similarity matrix that describes the attribute-to-attribute similarities of the two relations.
//     */
//    public SimilarityMatrix match(Relation sourceRelation, Relation targetRelation) {
//        String[][] sourceColumns = sourceRelation.getColumns();
//        String[][] targetColumns = targetRelation.getColumns();
//
//        // Initialize the similarity matrix
//        double[][] matrix = new double[sourceColumns.length][];
//        for (int i = 0; i < sourceColumns.length; i++)
//            matrix[i] = new double[targetColumns.length];
//
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //                                      DATA INTEGRATION ASSIGNMENT                                           //
//        // Calculate all pair-wise attribute similarities of the two relations and store the result in a similarity   //
//        // matrix. A naive Jaccard-based implementation will complete the task, but with the already implemented      //
//        // further similarity measures, the data profiling algorithms and a clever matching strategy, much better     //
//        // matching results are possible!                                                                             //
//
//        Tokenizer tokenizer = new Tokenizer(3, true); // Using trigram with padding
//        Jaccard jaccard = new Jaccard(tokenizer, true); // Bag semantics
//
//        for (int i = 0; i < sourceColumns.length; i++) {
//            for (int j = 0; j < targetColumns.length; j++) {
//                matrix[i][j] = jaccard.calculate(sourceColumns[i], targetColumns[j]);
//            }
//        }
//
//        //                                                                                                            //
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//        return new SimilarityMatrix(matrix, sourceRelation, targetRelation);
//    }
//}



//package de.di.schema_matching;
//
//import de.di.Relation;
//import de.di.schema_matching.structures.SimilarityMatrix;
//import de.di.similarity_measures.Jaccard;
//import de.di.similarity_measures.helper.Tokenizer;
//
///**
// * The FirstLineSchemaMatcher matches two relations by comparing
// * the first non-empty value of each column using trigram tokenization
// * and Jaccard similarity with bag semantics.
// */
//public class FirstLineSchemaMatcher {
//
//    // Tokenizer for trigrams with padding
//    private final Tokenizer tokenizer;
//
//    // Jaccard similarity measure using bag semantics
//    private final Jaccard jaccard;
//
//    /**
//     * Constructor: Initializes tokenizer and similarity measure.
//     */
//    public FirstLineSchemaMatcher() {
//        this.tokenizer = new Tokenizer(3, true); // n=3, pad=true
//        this.jaccard = new Jaccard(tokenizer, true); // bag semantics enabled
//    }
//
//    /**
//     * Matches two relations by computing similarity between
//     * the first non-empty value of each column in source and target.
//     *
//     * @param source The source relation
//     * @param target The target relation
//     * @return SimilarityMatrix containing similarity scores
//     */
//    public SimilarityMatrix match(Relation source, Relation target) {
//        int sourceCols = source.getAttributes().length;
//        int targetCols = target.getAttributes().length;
//
//        double[][] matrix = new double[sourceCols][targetCols];
//
//        // Extract first non-empty value of each source column
//        String[] sourceFirstValues = new String[sourceCols];
//        for (int i = 0; i < sourceCols; i++) {
//            sourceFirstValues[i] = getFirstNonEmptyValue(source, i);
//        }
//
//        // Extract first non-empty value of each target column
//        String[] targetFirstValues = new String[targetCols];
//        for (int j = 0; j < targetCols; j++) {
//            targetFirstValues[j] = getFirstNonEmptyValue(target, j);
//        }
//
//        // Calculate similarity scores between all pairs of columns
//        for (int i = 0; i < sourceCols; i++) {
//            String sourceVal = sourceFirstValues[i];
//            String[] sourceTokens = tokenizer.tokenize(sourceVal == null ? "" : sourceVal);
//
//            for (int j = 0; j < targetCols; j++) {
//                String targetVal = targetFirstValues[j];
//                String[] targetTokens = tokenizer.tokenize(targetVal == null ? "" : targetVal);
//
//                double similarity = jaccard.calculate(sourceTokens, targetTokens);
//                matrix[i][j] = similarity;
//            }
//        }
//
//        return new SimilarityMatrix(matrix, source, target);
//    }
//
//
//    /**
//     * Finds the first non-null, non-empty value in the specified column.
//     *
//     * @param relation The relation to search
//     * @param colIndex The column index
//     * @return The first non-empty value or empty string if none found
//     */
//    private String getFirstNonEmptyValue(Relation relation, int colIndex) {
//        for (int row = 0; row < relation.getRecords().length; row++) {
//            String val = relation.getRecords()[row][colIndex];
//            if (val != null && !val.trim().isEmpty()) {
//                return val.trim();
//            }
//        }
//        // Return empty string if no non-empty value found
//        return "";
//    }
//}



//package de.di.schema_matching;
//
//import de.di.Relation;
//import de.di.schema_matching.structures.SimilarityMatrix;
//import de.di.similarity_measures.Jaccard;
//import de.di.similarity_measures.helper.Tokenizer;
//
//public class FirstLineSchemaMatcher {
//
//    public SimilarityMatrix match(Relation sourceRelation, Relation targetRelation) {
//        String[][] sourceColumns = sourceRelation.getColumns();  // each column = array of attribute values (strings)
//        String[][] targetColumns = targetRelation.getColumns();
//
//        int sourceAttrCount = sourceColumns.length;
//        int targetAttrCount = targetColumns.length;
//
//        double[][] matrix = new double[sourceAttrCount][targetAttrCount];
//
//        // Initialize tokenizer for trigrams with padding
//        Tokenizer tokenizer = new Tokenizer(3, true);
//
//        // Use Jaccard similarity with bag semantics
//        Jaccard jaccard = new Jaccard(tokenizer, true);
//
//
//        for (int i = 0; i < sourceAttrCount; i++) {
//            // Tokenize all values of source attribute i and accumulate tokens
//            // Actually, tokenizing each value separately and merging tokens for the attribute is better:
//            // Build multiset of tokens from all source attribute values concatenated
//            // Join all strings with a delimiter, lowercase them, then tokenize as one string.
//
//            StringBuilder sourceConcat = new StringBuilder();
//            for (String val : sourceColumns[i]) {
//                if (val != null) {
//                    sourceConcat.append(val.toLowerCase()).append(" "); // lowercase and separate values by space
//                }
//            }
//            // Tokenize concatenated string (bag semantics)
//            String[] sourceTokens = tokenizer.tokenize(sourceConcat.toString().trim());
//
//            for (int j = 0; j < targetAttrCount; j++) {
//                StringBuilder targetConcat = new StringBuilder();
//                for (String val : targetColumns[j]) {
//                    if (val != null) {
//                        targetConcat.append(val.toLowerCase()).append(" ");
//                    }
//                }
//                String[] targetTokens = tokenizer.tokenize(targetConcat.toString().trim());
//
//                // Calculate Jaccard similarity with bag semantics on token arrays
//                double similarity = jaccard.calculate(sourceTokens, targetTokens);
//
//                matrix[i][j] = similarity;
//            }
//        }
//
//        return new SimilarityMatrix(matrix, sourceRelation, targetRelation);
//    }
//}

