package de.di.data_profiling;

import de.di.Relation;
import de.di.data_profiling.structures.AttributeList;
import de.di.data_profiling.structures.IND;


import java.util.*;
import java.util.stream.Collectors;

public class INDProfiler {

    /**
     * Discovers all non-trivial unary (and n-ary) inclusion dependencies in the provided relations.
     * @param relations The relations that should be profiled for inclusion dependencies.
     * @return The list of all non-trivial unary (and n-ary) inclusion dependencies in the provided relations.
     */
    public List<IND> profile(List<Relation> relations, boolean discoverNary) {
        List<IND> inclusionDependencies = new ArrayList<>();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Discover all inclusion dependencies and return them in inclusion dependencies list. The boolean flag       //
        // discoverNary indicates, whether only unary or both unary and n-ary INDs should be discovered. To solve     //
        // this assignment, only unary INDs need to be discovered. Discovering also n-ary INDs is optional.           //
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        if (discoverNary)
            // Here, the lattice search would start if n-ary IND discovery would be supported.
            throw new RuntimeException("Sorry, n-ary IND discovery is not supported by this solution.");

        // Map to hold column data
        Map<String, Set<String>> columnValueSets = new HashMap<>();
        Map<String, Relation> columnToRelation = new HashMap<>();
        Map<String, Integer> columnToIndex = new HashMap<>();

        for (Relation rel : relations) {
            String[][] records = rel.getRecords(); // CORRECT method from Relation
            int numCols = rel.getAttributes().length;

            for (int col = 0; col < numCols; col++) {
                Set<String> values = new HashSet<>();
                for (String[] row : records) {
                    if (col < row.length) {
                        values.add(row[col]);
                    }
                }
                String columnId = rel.getName() + "#" + col;
                columnValueSets.put(columnId, values);
                columnToRelation.put(columnId, rel);
                columnToIndex.put(columnId, col);
            }
        }

        for (String depColumnId : columnValueSets.keySet()) {
            for (String refColumnId : columnValueSets.keySet()) {
                if (depColumnId.equals(refColumnId)) continue;

                Set<String> depValues = columnValueSets.get(depColumnId);
                Set<String> refValues = columnValueSets.get(refColumnId);

                if (refValues.containsAll(depValues)) {
                    Relation depRel = columnToRelation.get(depColumnId);
                    Relation refRel = columnToRelation.get(refColumnId);
                    int depColIndex = columnToIndex.get(depColumnId);
                    int refColIndex = columnToIndex.get(refColumnId);

                    inclusionDependencies.add(new IND(
                            refRel,
                            new AttributeList(refColIndex),
                            depRel,
                            new AttributeList(depColIndex)
                    ));
                }
            }
        }

        return inclusionDependencies;
    }

    private List<Set<String>> toColumnSets(String[][] columns) {
        return Arrays.stream(columns)
                .map(column -> new HashSet<>(new ArrayList<>(List.of(column))))
                .collect(Collectors.toList());
    }
}
