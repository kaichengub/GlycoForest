/**
 * Copyright (c) 2010, SIB. All rights reserved.
 *
 * SIB (Swiss Institute of Bioinformatics) - http://www.isb-sib.ch Host -
 * http://mzjava.expasy.org
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the SIB/GENEBIO nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL SIB/GENEBIO BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.expasy.mzjava.tools.glycoforest.bayescluster;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;
import com.google.common.base.Preconditions;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraphBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class EdgeListGenerator {

    private EdgeListGenerator() {
    }

    public static <V, G extends SimilarityGraph<V>> SummaryStatistics addBetweenEdges(List<V> nodes1, List<V> nodes2, SimilarityGraphBuilder<V, G> builder, double mean, double stdev) {

        return addBetweenEdges(nodes1, nodes2, builder, mean, stdev, 0, 1.0);
    }

    public static <V, G extends SimilarityGraph<V>> SummaryStatistics addBetweenEdges(List<V> nodes1, List<V> nodes2, SimilarityGraphBuilder<V, G> builder, double mean, double stdev, double minScore, double maxScore) {

        checkArgument(minScore >= 0);
        checkArgument(maxScore <= 1);

        SummaryStatistics stats = new SummaryStatistics();
        Normal dist = new Normal(mean, stdev, new MersenneTwister(new Date()));
        for (V node1 : nodes1) {

            for (V node2 : nodes2) {

                if (node1 != node2) {

                    double score = nextScore(dist, minScore, maxScore);
                    stats.addValue(score);
                    builder.add(node1, node2, score);
                }
            }
        }
        return stats;
    }

    public static <V, G extends SimilarityGraph<V>> int addBetweenEdges(List<V> nodes1, List<V> nodes2, SimilarityGraphBuilder<V, G> builder, double score) {

        int sizeBefore = builder.edgeCount();
        for (V node1 : nodes1) {

            for (V node2 : nodes2) {

                if (node1 != node2) {

                    builder.add(node1, node2, score);
                }
            }
        }
        return builder.edgeCount() - sizeBefore;
    }

    public static <V, G extends SimilarityGraph<V>> SummaryStatistics addWithinEdges(List<V> nodes, SimilarityGraphBuilder<V, G> builder, double mean, double stdev) {

        return addWithinEdges(nodes, builder, mean, stdev, 0, 1.0);
    }

    public static <V, G extends SimilarityGraph<V>> SummaryStatistics addWithinEdges(List<V> nodes, SimilarityGraphBuilder<V, G> builder, double mean, double stdev, double minScore, double maxScore) {

        checkArgument(minScore >= 0);
        checkArgument(maxScore <= 1);

        Normal dist = new Normal(mean, stdev, new MersenneTwister(new Date()));
        int size = nodes.size();
        SummaryStatistics stats = new SummaryStatistics();
        for (int i = 0; i < size; i++) {

            V node1 = nodes.get(i);
            for (int j = i; j < size; j++) {

                V node2 = nodes.get(j);
                if (node1 != node2) {

                    double score = nextScore(dist, minScore, maxScore);
                    stats.addValue(score);
                    builder.add(node1, node2, score);
                }
            }
        }

        return stats;
    }

    public static <V, G extends SimilarityGraph<V>> int addWithinEdges(List<V> nodes, SimilarityGraphBuilder<V, G> builder, double score) {

        int sizeBefore = builder.edgeCount();
        int size = nodes.size();
        for (int i = 0; i < size; i++) {

            V node1 = nodes.get(i);
            for (int j = i; j < size; j++) {

                V node2 = nodes.get(j);
                if (node1 != node2) {

                    builder.add(node1, node2, score);
                }
            }
        }

        return builder.edgeCount() - sizeBefore;
    }

    private static double nextScore(Normal dist, double minScore, double maxScore) {

        return Math.min(maxScore, Math.max(minScore, dist.nextDouble()));
    }

    public static List<UUID> makeNodes(int count) {

        List<UUID> nodes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {

            nodes.add(UUID.randomUUID());
        }

        return nodes;
    }

    public static List<UUID> makeNodes(int count, String prefix) {

        Preconditions.checkArgument(prefix.length() == 8);

        List<UUID> nodes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {

            String id = prefix + UUID.randomUUID().toString().substring(8, 36);
            nodes.add(UUID.fromString(id));
        }

        return nodes;
    }

    public static <V> List<V> makeNodes(int count, VertexFunction<V> function) {

        List<V> nodes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {

            nodes.add(function.apply(i));
        }

        return nodes;
    }

    public static PrefixVertexFunction newStringVertexFunction(String prefix) {

        return new PrefixVertexFunction(prefix);
    }

    public interface VertexFunction<V> {

        V apply(int value);
    }
    
    public static class PrefixVertexFunction  implements VertexFunction<String> {

        private final String prefix;

        private PrefixVertexFunction(String prefix) {

            this.prefix = prefix;
        }

        public String apply(int value) {

            return prefix + value;
        }
    }
}
