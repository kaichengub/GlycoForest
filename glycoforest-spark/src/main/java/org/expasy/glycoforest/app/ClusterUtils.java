package org.expasy.glycoforest.app;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.expasy.glycoforest.app.factories.ConsensusPeakMergerFactory;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraphBuilder;
import org.expasy.mzjava.core.ms.peaklist.*;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Abstract class that contains utility methods for clustering spectra and creating consensus spectra.
 *
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public final class ClusterUtils {

    private ClusterUtils() {

    }

    /**
     * Create consensus peaks and add them to the consensus spectrum.
     *
     * @param spectra   the spectra from which to create the consensus
     * @param consensus the consensus to which the peaks are added
     * @param <A>       the peak annotation
     * @param <PL>      the peak list type
     */
    public static <A extends PeakAnnotation, PL extends PeakList<A>, C extends PeakList<LibPeakAnnotation>> void addPeaksToConsensus(List<PL> spectra, C consensus, ConsensusPeakMergerFactory<A> mergePeakFilterFactory) {

        PeakListMerger<A> peakListMerger = new PeakListMerger<>();
        peakListMerger
                .setSink(mergePeakFilterFactory.mergeFilter(spectra.size()))
                .setSink(new PeakCollectorSink<>(consensus));

        peakListMerger.merge(spectra);

        consensus.trimToSize();
    }

    /**
     * Calculate the score statistics for the edges for the sub-graph that contains all nodes in cluster.
     *
     * @param cluster         the spectra
     * @param similarityGraph the similarity graph
     * @return the score statistics
     */
    public static <S extends PeakList> SummaryStatistics calcScoreStats(Set<S> cluster, SimilarityGraph<S> similarityGraph) {

        SummaryStatistics stats = new SummaryStatistics();
        for (SimEdge<S> edge : similarityGraph.getEdges()) {

            if (cluster.contains(edge.getVertex1()) && cluster.contains(edge.getVertex2()))
                stats.addValue(edge.getScore());
        }
        return stats;
    }

    static <A extends PeakAnnotation, S extends PeakList<A>, G extends SimilarityGraph<S>> void addSpectraToBuilder(final List<S> rawSpectra,
                                                                                                                    final List<S> processedSpectra,
                                                                                                                    final SimFunc<A, A> simFunc,
                                                                                                                    final Tolerance precursorTolerance,
                                                                                                                    final SimilarityGraphBuilder<S, G> builder) {

        checkArgument(rawSpectra.size() == processedSpectra.size());

        for (int i = 0; i < processedSpectra.size(); i++) {

            S spectrum1 = processedSpectra.get(i);
            builder.add(rawSpectra.get(i));
            Peak precursor1 = spectrum1.getPrecursor();

            for (int j = i + 1; j < processedSpectra.size(); j++) {

                S spectrum2 = processedSpectra.get(j);
                Peak precursor2 = spectrum2.getPrecursor();

                if (precursor2.compareTo(precursor1) < 0)
                    throw new IllegalStateException("spectra not sorted");

                if (precursor2.getCharge() > precursor1.getCharge() || precursorTolerance.check(precursor1.getMz(), precursor2.getMz()) == Tolerance.Location.LARGER)
                    break;

                checkState(precursorTolerance.withinTolerance(precursor1.getMz(), precursor2.getMz()));

                double score = simFunc.calcSimilarity(spectrum1, spectrum2);
                if (!Double.isNaN(score))
                    builder.add(rawSpectra.get(i), rawSpectra.get(j), score);
            }
        }
    }
}
