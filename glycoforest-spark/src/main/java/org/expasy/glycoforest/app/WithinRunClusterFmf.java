package org.expasy.glycoforest.app;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.expasy.glycoforest.chargestate.MemberChargeEstimator;
import org.expasy.glycoforest.data.MsnSimGraph;
import org.expasy.glycoforest.app.factories.ConsensusPeakMergerFactory;
import org.expasy.mzjava.core.ms.cluster.ClusterBuilder;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeInterval;
import org.expasy.mzjava.core.ms.spectrum.ScanNumberInterval;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;
import org.expasy.mzjava.spark.Cached;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import scala.Tuple2;

import java.util.*;
import java.util.function.Predicate;

/**
 * Flat map function for creating within run consensus by clustering the MsnSpectrum similarity graph
 *
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class WithinRunClusterFmf extends ClusterFmf implements PairFlatMapFunction<MsnSimGraph, WithinRunConsensus, List<MsnSpectrum>> {

    private static final long serialVersionUID = 78921079509937443L;

    private final ConsensusPeakMergerFactory<PeakAnnotation> mergePeakFilterFactory;
    private final Cached<ClusterBuilder<MsnSpectrum>> clusterBuilder;
    private final Cached<Predicate<WithinRunConsensus>> consensusPredicate;
    private final Cached<MemberChargeEstimator> chargeEstimator;
    private final PeakList.Precision precision;

    public WithinRunClusterFmf(ConsensusPeakMergerFactory<PeakAnnotation> mergePeakFilterFactory, Cached<ClusterBuilder<MsnSpectrum>> clusterBuilder, Cached<Predicate<WithinRunConsensus>> consensusPredicate, Cached<MemberChargeEstimator> chargeEstimator, PeakList.Precision precision) {

        this.mergePeakFilterFactory = mergePeakFilterFactory;
        this.clusterBuilder = clusterBuilder;
        this.consensusPredicate = consensusPredicate;
        this.chargeEstimator = chargeEstimator;
        this.precision = precision;
    }

    @Override
    public Iterable<Tuple2<WithinRunConsensus, List<MsnSpectrum>>> call(MsnSimGraph simGraph) throws Exception {

        Collection<Set<MsnSpectrum>> clusters = clusterBuilder.get().cluster(simGraph);

        List<Tuple2<WithinRunConsensus, List<MsnSpectrum>>> consensusList = new ArrayList<>(clusters.size());
        for (Set<MsnSpectrum> cluster : clusters) {

            final List<MsnSpectrum> rawSpectrumList = new ArrayList<>(cluster.size());
            final SummaryStatistics mzStats = new SummaryStatistics();
            final IntSummaryStatistics scanNumberStats = new IntSummaryStatistics();
            final DoubleSummaryStatistics rtStats = new DoubleSummaryStatistics();
            double totalIonCurrent = 0;

            for (MsnSpectrum rawSpectrum : cluster) {

                final Peak precursor = rawSpectrum.getPrecursor();
                mzStats.addValue(precursor.getMz());
                rawSpectrumList.add(rawSpectrum);
                scanNumberStats.accept(rawSpectrum.getScanNumbers().getFirst().getValue());
                rtStats.accept(rawSpectrum.getRetentionTimes().getFirst().getTime());
            }

            StatisticalSummary scoreStats = ClusterUtils.calcScoreStats(cluster, simGraph);
            WithinRunConsensus consensus = new WithinRunConsensus(UUID.randomUUID(), simGraph.getRunId(),
                    scoreStats,
                    totalIonCurrent, mzStats, chargeEstimator.get().estimateChargeState(cluster),
                    new ScanNumberInterval(scanNumberStats.getMin(), scanNumberStats.getMax()),
                    new RetentionTimeInterval(rtStats.getMin(), rtStats.getMax(), TimeUnit.SECOND),
                    precision);
            ClusterUtils.addPeaksToConsensus(rawSpectrumList, consensus, mergePeakFilterFactory);

            if (consensusPredicate.get().test(consensus)) {
                consensusList.add(new Tuple2<>(consensus, rawSpectrumList));
            }
        }

        return consensusList;
    }
}
