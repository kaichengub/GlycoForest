package org.expasy.glycoforest.app;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.expasy.glycoforest.chargestate.FrequencyChargeEstimator;
import org.expasy.glycoforest.chargestate.MemberChargeEstimator;
import org.expasy.glycoforest.data.MsnSimGraph;
import org.expasy.glycoforest.app.factories.ConsensusPeakMergerFactory;
import org.expasy.mzjava.core.ms.cluster.ClusterBuilder;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.peakfilter.LibraryMergePeakFilter;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.core.ms.spectrum.RetentionTime;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeDiscrete;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;
import org.expasy.mzjava.spark.Cached;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static org.expasy.mzjava.core.ms.peaklist.peakfilter.AbstractMergePeakFilter.IntensityMode.SUM_INTENSITY;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WithinRunClusterFmfTest {

    @Test
    public void testCall() throws Exception {

        //noinspection unchecked
        ConsensusPeakMergerFactory<PeakAnnotation> mergePeakFilterFactory = mock(ConsensusPeakMergerFactory.class);
        when(mergePeakFilterFactory.mergeFilter(2)).thenReturn(new LibraryMergePeakFilter<>(0.3, 0.6, SUM_INTENSITY, 2));
        when(mergePeakFilterFactory.mergeFilter(1)).thenReturn(new LibraryMergePeakFilter<>(0.3, 0.6, SUM_INTENSITY, 1));

        MsnSpectrum msnA1 = mockSpectrum(733.2, new double[]{457.2, 733.2}, new double[]{10, 20}, new RetentionTimeDiscrete(302, TimeUnit.SECOND), 1);
        MsnSpectrum msnA2 = mockSpectrum(733.4, new double[]{457.2, 733.2}, new double[]{5, 10}, new RetentionTimeDiscrete(304, TimeUnit.SECOND), 2);
        MsnSpectrum msnB1 = mockSpectrum(733.25, new double[]{321.2, 733.2}, new double[]{30, 1}, new RetentionTimeDiscrete(745, TimeUnit.SECOND), 3);

        final UUID runId = UUID.randomUUID();
        MsnSimGraph simGraph = new MsnSimGraph.Builder(runId)
                .add(new SimEdge<>(msnA1, msnA2, 0.9))
                .add(new SimEdge<>(msnA1, msnB1, 0.1))
                .add(new SimEdge<>(msnA2, msnB1, 0.1))
                .build();

        Set<MsnSpectrum> clusterA = Sets.newLinkedHashSet(Lists.newArrayList(msnA1, msnA2));
        Set<MsnSpectrum> clusterB = Sets.newHashSet(msnB1);

        //noinspection unchecked
        ClusterBuilder<MsnSpectrum> clusterBuilder = mock(ClusterBuilder.class);
        //noinspection unchecked
        when(clusterBuilder.cluster(simGraph)).thenReturn(Lists.newArrayList(clusterA, clusterB));

        //noinspection unchecked
        Cached<ClusterBuilder<MsnSpectrum>> cachedClusterBuilder = mock(Cached.class);
        when(cachedClusterBuilder.get()).thenReturn(clusterBuilder);

        //noinspection unchecked
        Cached<Predicate<WithinRunConsensus>> consensusPredicate = mock(Cached.class);
        when(consensusPredicate.get()).thenReturn(peakList -> true);

        //noinspection unchecked
        Cached<MemberChargeEstimator> chargeEstimator = mock(Cached.class);
        when(chargeEstimator.get()).thenReturn(new FrequencyChargeEstimator());

        final WithinRunClusterFmf fmFunction = new WithinRunClusterFmf(mergePeakFilterFactory, cachedClusterBuilder, consensusPredicate, chargeEstimator, PeakList.Precision.DOUBLE);

        List<Tuple2<WithinRunConsensus, List<MsnSpectrum>>> consensusList = Lists.newArrayList(fmFunction.call(simGraph));

        Assert.assertEquals(2, consensusList.size());

        WithinRunConsensus consensus1 = consensusList.get(0)._1();
        Assert.assertEquals(2, consensus1.size());
        Assert.assertEquals(2, consensus1.getMemberCount());
        Assert.assertEquals(runId, consensus1.getRunId());
        Assert.assertEquals(303, consensus1.getRetentionTime(), 0.0000001);
        Assert.assertEquals(733.3, consensus1.getPrecursor().getMz(), 0.0000001);
        Assert.assertEquals(Lists.newArrayList(msnA1, msnA2), consensusList.get(0)._2());

        WithinRunConsensus consensus2 = consensusList.get(1)._1();
        Assert.assertEquals(2, consensus2.size());
        Assert.assertEquals(1, consensus2.getMemberCount());
        Assert.assertEquals(runId, consensus2.getRunId());
        Assert.assertEquals(745, consensus2.getRetentionTime(), 0.0000001);
        Assert.assertEquals(733.25, consensus2.getPrecursor().getMz(), 0.0000001);
        Assert.assertEquals(Lists.newArrayList(msnB1), consensusList.get(1)._2());
    }

    @Test
    public void testCallWithFilter() throws Exception {

        //noinspection unchecked
        ConsensusPeakMergerFactory<PeakAnnotation> mergePeakFilterFactory = mock(ConsensusPeakMergerFactory.class);
        when(mergePeakFilterFactory.mergeFilter(2)).thenReturn(new LibraryMergePeakFilter<>(0.3, 0.6, SUM_INTENSITY, 2));
        when(mergePeakFilterFactory.mergeFilter(1)).thenReturn(new LibraryMergePeakFilter<>(0.3, 0.6, SUM_INTENSITY, 1));

        MsnSpectrum msnA1 = mockSpectrum(733.2, new double[]{457.2, 733.2}, new double[]{10, 20}, new RetentionTimeDiscrete(302, TimeUnit.SECOND), 1);
        MsnSpectrum msnA2 = mockSpectrum(733.4, new double[]{457.2, 733.2}, new double[]{5, 10}, new RetentionTimeDiscrete(304, TimeUnit.SECOND), 2);
        MsnSpectrum msnB1 = mockSpectrum(733.25, new double[]{321.2, 733.2}, new double[]{30, 1}, new RetentionTimeDiscrete(745, TimeUnit.SECOND), 3);

        final UUID runId = UUID.randomUUID();
        MsnSimGraph simGraph = new MsnSimGraph.Builder(runId)
                .add(new SimEdge<>(msnA1, msnA2, 0.9))
                .add(new SimEdge<>(msnA1, msnB1, 0.1))
                .add(new SimEdge<>(msnA2, msnB1, 0.1))
                .build();

        Set<MsnSpectrum> clusterA = Sets.newLinkedHashSet(Lists.newArrayList(msnA1, msnA2));
        Set<MsnSpectrum> clusterB = Sets.newHashSet(msnB1);

        //noinspection unchecked
        ClusterBuilder<MsnSpectrum> clusterBuilder = mock(ClusterBuilder.class);
        //noinspection unchecked
        when(clusterBuilder.cluster(simGraph)).thenReturn(Lists.newArrayList(clusterA, clusterB));

        //noinspection unchecked
        Cached<ClusterBuilder<MsnSpectrum>> cachedClusterBuilder = mock(Cached.class);
        when(cachedClusterBuilder.get()).thenReturn(clusterBuilder);

        //noinspection unchecked
        Cached<Predicate<WithinRunConsensus>> consensusPredicate = mock(Cached.class);
        when(consensusPredicate.get()).thenReturn(peakList -> peakList.getMemberCount() > 1);

        //noinspection unchecked
        Cached<MemberChargeEstimator> chargeEstimator = mock(Cached.class);
        when(chargeEstimator.get()).thenReturn(new FrequencyChargeEstimator());

        final WithinRunClusterFmf fmFunction = new WithinRunClusterFmf(mergePeakFilterFactory, cachedClusterBuilder, consensusPredicate, chargeEstimator, PeakList.Precision.DOUBLE);

        List<Tuple2<WithinRunConsensus, List<MsnSpectrum>>> consensusList = Lists.newArrayList(fmFunction.call(simGraph));

        Assert.assertEquals(1, consensusList.size());

        WithinRunConsensus consensus1 = consensusList.get(0)._1();
        Assert.assertEquals(2, consensus1.size());
        Assert.assertEquals(2, consensus1.getMemberCount());
        Assert.assertEquals(runId, consensus1.getRunId());
        Assert.assertEquals(303, consensus1.getRetentionTime(), 0.0000001);
        Assert.assertEquals(733.3, consensus1.getPrecursor().getMz(), 0.0000001);
        Assert.assertEquals(Lists.newArrayList(msnA1, msnA2), consensusList.get(0)._2());
    }

    private MsnSpectrum mockSpectrum(double precursorMz, double[] mzs, double[] intensities, RetentionTime retentionTime, int scanNumber) {

        MsnSpectrum consensus = new MsnSpectrum(PeakList.Precision.DOUBLE);
        consensus.setPrecursor(new Peak(precursorMz, 100, 1));

        for (int i = 0; i < mzs.length; i++) {
            consensus.add(mzs[i], intensities[i]);
        }
        consensus.setSpectrumSource(new File("/dev/spectra/experiment3").toURI());
        consensus.getRetentionTimes().add(retentionTime);
        consensus.getScanNumbers().add(scanNumber);

        return consensus;
    }
}