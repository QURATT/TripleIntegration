package de.di.schema_matching;

import de.di.schema_matching.structures.CorrespondenceMatrix;
import de.di.schema_matching.structures.SimilarityMatrix;

public class SecondLineSchemaMatcher {

    /**
     * Translates the provided similarity matrix into a binary correspondence matrix by selecting possibly optimal
     * attribute correspondences from the similarities.
     * @param similarityMatrix A matrix of pair-wise attribute similarities.
     * @return A CorrespondenceMatrix of pair-wise attribute correspondences.
     */
    public CorrespondenceMatrix match(SimilarityMatrix similarityMatrix) {
        double[][] simMatrix = similarityMatrix.getMatrix();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Translate the similarity matrix into a binary correlation matrix by implementing either the StableMarriage //
        // algorithm or the Hungarian method. Here, the Hungarian algorithm is implemented for maximum matching.     //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        int[] sourceAssignments = hungarianAlgorithm(simMatrix);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        int[][] corrMatrix = assignmentArray2correlationMatrix(sourceAssignments, simMatrix);

        return new CorrespondenceMatrix(corrMatrix, similarityMatrix.getSourceRelation(), similarityMatrix.getTargetRelation());
    }

    /**
     * Translate an array of source assignments into a correlation matrix. For example, [0,3,2] maps 0->1, 1->3, 2->2
     * and, therefore, translates into [[1,0,0,0][0,0,0,1][0,0,1,0]].
     * @param sourceAssignments The list of source assignments.
     * @param simMatrix The original similarity matrix; just used to determine the number of source and target attributes.
     * @return The correlation matrix extracted from the source assignments.
     */
    private int[][] assignmentArray2correlationMatrix(int[] sourceAssignments, double[][] simMatrix) {
        int[][] corrMatrix = new int[simMatrix.length][];
        for (int i = 0; i < simMatrix.length; i++) {
            corrMatrix[i] = new int[simMatrix[i].length];
            for (int j = 0; j < simMatrix[i].length; j++) {
                corrMatrix[i][j] = 0;
            }
        }
        for (int i = 0; i < sourceAssignments.length; i++) {
            if (sourceAssignments[i] >= 0) {
                corrMatrix[i][sourceAssignments[i]] = 1;
            }
        }
        return corrMatrix;
    }

    /**
     * Hungarian algorithm for maximum weight bipartite matching.
     * @param weightMatrix double[][] with similarity weights [source][target]
     * @return int[] sourceAssignments where sourceAssignments[i] = matched target index or -1 if none
     */
    private int[] hungarianAlgorithm(double[][] weightMatrix) {
        int n = weightMatrix.length;       // number of source attributes
        int m = weightMatrix[0].length;    // number of target attributes
        int dim = Math.max(n, m);

        // Create square cost matrix (we want max weight, but Hungarian solves min cost, so negate weights)
        double[][] cost = new double[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (i < n && j < m) {
                    cost[i][j] = -weightMatrix[i][j]; // negate because Hungarian solves min cost
                } else {
                    cost[i][j] = 0; // pad with zeros if matrix not square
                }
            }
        }

        // Arrays for the Hungarian algorithm
        double[] u = new double[dim + 1];
        double[] v = new double[dim + 1];
        int[] p = new int[dim + 1];
        int[] way = new int[dim + 1];

        for (int i = 1; i <= dim; i++) {
            p[0] = i;
            int j0 = 0;
            double[] minv = new double[dim + 1];
            boolean[] used = new boolean[dim + 1];
            for (int j = 0; j <= dim; j++) minv[j] = Double.POSITIVE_INFINITY;
            do {
                used[j0] = true;
                int i0 = p[j0], j1 = 0;
                double delta = Double.POSITIVE_INFINITY;
                for (int j = 1; j <= dim; j++) {
                    if (!used[j]) {
                        double cur = cost[i0 - 1][j - 1] - u[i0] - v[j];
                        if (cur < minv[j]) {
                            minv[j] = cur;
                            way[j] = j0;
                        }
                        if (minv[j] < delta) {
                            delta = minv[j];
                            j1 = j;
                        }
                    }
                }
                for (int j = 0; j <= dim; j++) {
                    if (used[j]) {
                        u[p[j]] += delta;
                        v[j] -= delta;
                    } else {
                        minv[j] -= delta;
                    }
                }
                j0 = j1;
            } while (p[j0] != 0);

            do {
                int j1 = way[j0];
                p[j0] = p[j1];
                j0 = j1;
            } while (j0 != 0);
        }

        // p[j]: source matched to target j
        int[] assignment = new int[n];
        for (int i = 0; i < n; i++) assignment[i] = -1; // initialize with -1

        for (int j = 1; j <= dim; j++) {
            if (p[j] <= n && j <= m) {
                assignment[p[j] - 1] = j - 1;
            }
        }
        return assignment;
    }
}
