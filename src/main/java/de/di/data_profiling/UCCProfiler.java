package de.di.data_profiling;

import de.di.Relation;
import de.di.data_profiling.structures.AttributeList;
import de.di.data_profiling.structures.PositionListIndex;
import de.di.data_profiling.structures.UCC;

import java.util.*;

public class UCCProfiler {

    /**
     * Discovers all minimal, non-trivial unique column combinations in the provided relation.
     * @param relation The relation that should be profiled for unique column combinations.
     * @return The list of all minimal, non-trivial unique column combinations in ths provided relation.
     */
    public List<UCC> profile(Relation relation) {
        int numAttributes = relation.getAttributes().length;
        List<UCC> uniques = new ArrayList<>();
        List<PositionListIndex> currentNonUniques = new ArrayList<>();

        // Calculate all unary UCCs and unary non-UCCs
        for (int attribute = 0; attribute < numAttributes; attribute++) {
            AttributeList attributes = new AttributeList(attribute);
            PositionListIndex pli = new PositionListIndex(attributes, relation.getColumns()[attribute]);
            if (pli.isUnique())
                uniques.add(new UCC(relation, attributes));
            else
                currentNonUniques.add(pli);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Discover all unique column combinations of size n>1 by traversing the lattice level-wise. Make sure to     //
        // generate only minimal candidates while moving upwards and to prune non-minimal ones. Hint: The class       //
        // AttributeList offers some helpful functions to test for sub- and superset relationships. Use PLI           //
        // intersection to validate the candidates in every lattice level. Advances techniques, such as random walks, //
        // hybrid search strategies, or hitting set reasoning can be used, but are optional to pass the assignment.   //
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Set<String> knownUniqueSets = new HashSet<>();
        for (UCC ucc : uniques) {
            knownUniqueSets.add(Arrays.toString(ucc.getAttributeList().getAttributes()));
        }

        List<PositionListIndex> currentLevel = currentNonUniques;

        while (!currentLevel.isEmpty()) {
            List<PositionListIndex> nextLevel = new ArrayList<>();

            for (int i = 0; i < currentLevel.size(); i++) {
                for (int j = i + 1; j < currentLevel.size(); j++) {
                    PositionListIndex pli1 = currentLevel.get(i);
                    PositionListIndex pli2 = currentLevel.get(j);

                    AttributeList attrs1 = pli1.getAttributes();
                    AttributeList attrs2 = pli2.getAttributes();

                    if (attrs1.samePrefixAs(attrs2)) {
                        AttributeList combinedAttrs = attrs1.union(attrs2);

                        // Minimality check: skip if combinedAttrs is a superset of any known unique
                        boolean isMinimal = true;
                        for (String known : knownUniqueSets) {
                            AttributeList knownAttrs = new AttributeList(
                                    Arrays.stream(known.replaceAll("[\\[\\]\\s]", "").split(","))
                                            .mapToInt(Integer::parseInt)
                                            .toArray()
                            );
                            if (combinedAttrs.supersetOf(knownAttrs)) {
                                isMinimal = false;
                                break;
                            }
                        }

                        if (!isMinimal)
                            continue;

                        PositionListIndex combinedPLI = pli1.intersect(pli2);

                        if (combinedPLI.isUnique()) {
                            uniques.add(new UCC(relation, combinedAttrs));
                            knownUniqueSets.add(Arrays.toString(combinedAttrs.getAttributes()));
                        } else {
                            nextLevel.add(combinedPLI);
                        }
                    }
                }
            }

            currentLevel = nextLevel;
        }

        return uniques;
    }
}
