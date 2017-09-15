package org.expasy.glycoforest.app;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.expasy.glycoforest.app.factories.ConsensusPeakMergerFactory;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraphBuilder;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.peakfilter.AbstractMergePeakFilter;
import org.expasy.mzjava.core.ms.peaklist.peakfilter.LibraryMergePeakFilter;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class ClusterUtilsTest {

    @Test
    public void testAddPeaksToConsensus() throws Exception {

        List<MsnSpectrum> spectra = Lists.newArrayList(

                newSpectrum(733.3, 1, new double[]{654.2, 1234.1}, new double[]{1, 3}),
                newSpectrum(733.3, 1, new double[]{654.3, 1234.1}, new double[]{1, 3}),
                newSpectrum(733.3, 1, new double[]{654.4, 1234.1}, new double[]{1, 3})
        );

        ConsensusPeakMergerFactory libMergePeakFilerFactory = mock(ConsensusPeakMergerFactory.class);
        when(libMergePeakFilerFactory.mergeFilter(anyInt())).thenReturn(new LibraryMergePeakFilter<>(0.3, 0.6, AbstractMergePeakFilter.IntensityMode.SUM_INTENSITY, spectra.size()));


        WithinRunConsensus consensus = new WithinRunConsensus(PeakList.Precision.DOUBLE);
        ClusterUtils.addPeaksToConsensus(spectra, consensus, libMergePeakFilerFactory);

        Assert.assertEquals(2, consensus.size());
        final double delta = 0.0000000001;
        Assert.assertEquals(654.3, consensus.getMz(0), delta);
        Assert.assertEquals(3, consensus.getIntensity(0), delta);
        Assert.assertEquals(1234.1, consensus.getMz(1), delta);
        Assert.assertEquals(9, consensus.getIntensity(1), delta);
        Assert.assertEquals(12, consensus.getTotalIonCurrent(), delta);
    }

    private MsnSpectrum newSpectrum(double precursorMz, int charge, double[] mzs, double[] intensities) {

        Preconditions.checkArgument(mzs.length == intensities.length);

        MsnSpectrum spectrum = new MsnSpectrum(mzs.length, PeakList.Precision.DOUBLE);
        spectrum.getPrecursor().setValues(precursorMz, 100, charge);
        spectrum.addSorted(mzs, intensities);
        return spectrum;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCalcScoreStats() throws Exception {

        MsnSpectrum spectrum1 = mock(MsnSpectrum.class);
        MsnSpectrum spectrum2 = mock(MsnSpectrum.class);
        MsnSpectrum spectrum3 = mock(MsnSpectrum.class);

        List<SimEdge<MsnSpectrum>> edges = Lists.newArrayList(
                new SimEdge<>(spectrum1, spectrum2, 0.9),
                new SimEdge<>(spectrum1, spectrum3, 0.1),
                new SimEdge<>(spectrum2, spectrum3, 0.1)
        );

        SimilarityGraph<MsnSpectrum> similarityGraph = mock(SimilarityGraph.class);
        when(similarityGraph.getEdges()).thenReturn(edges);

        Set<MsnSpectrum> cluster = Sets.newHashSet(
                spectrum1,
                spectrum2
        );

        SummaryStatistics stats = ClusterUtils.calcScoreStats(cluster, similarityGraph);

        Assert.assertEquals(0.9, stats.getMean(), 0);
        Assert.assertEquals(0.0, stats.getStandardDeviation(), 0);
        Assert.assertEquals(1, stats.getN());
    }

    /**
     *          A1
     *         /\
     *      0.9 0.1
     *      /     \
     *     A2-0.1- B1
     *
     * @throws Exception
     */
    @Test
    public void testBuildGraph() throws Exception {

        MsnSpectrum rawA1 = newMsnSpectrum(733.1, 1, "raw A1");
        MsnSpectrum procA1 = newMsnSpectrum(733.1, 1, "proc A1");

        MsnSpectrum rawA2 = newMsnSpectrum(733.2, 1, "raw A2");
        MsnSpectrum procA2 = newMsnSpectrum(733.2, 1, "proc A2");

        MsnSpectrum rawB1 = newMsnSpectrum(733.3, 1, "raw B1");
        MsnSpectrum procB1 = newMsnSpectrum(733.3, 1, "proc B1");

        //noinspection unchecked
        SimFunc<PeakAnnotation, PeakAnnotation> simFunc = mock(SimFunc.class);
        when(simFunc.calcSimilarity(procA1, procA2)).thenReturn(0.9);
        when(simFunc.calcSimilarity(procA1, procB1)).thenReturn(0.1);
        when(simFunc.calcSimilarity(procA2, procB1)).thenReturn(0.1);

        Tolerance precursorTolerance = new AbsoluteTolerance(0.3);

        //noinspection unchecked
        SimilarityGraphBuilder<MsnSpectrum, SimilarityGraph<MsnSpectrum>> builder = mock(SimilarityGraphBuilder.class);

        ClusterUtils.addSpectraToBuilder(Lists.newArrayList(rawA1, rawA2, rawB1), Lists.newArrayList(procA1, procA2, procB1), simFunc, precursorTolerance, builder);

        verify(builder).add(rawA1);
        verify(builder).add(rawA2);
        verify(builder).add(rawB1);
        verify(builder).add(rawA1, rawA2, 0.9);
        verify(builder).add(rawA1, rawB1, 0.1);
        verify(builder).add(rawA2, rawB1, 0.1);
        verifyNoMoreInteractions(builder);
    }

    /**
     *          A1
     *         /
     *      0.9
     *      /
     *     A2      B1
     *
     * @throws Exception
     */
    @Test
    public void testBuildDisconnectedGraph() throws Exception {

        MsnSpectrum rawA1 = newMsnSpectrum(733.1, 1, "raw A1");
        MsnSpectrum procA1 = newMsnSpectrum(733.1, 1, "proc A1");

        MsnSpectrum rawA2 = newMsnSpectrum(733.2, 1, "raw A2");
        MsnSpectrum procA2 = newMsnSpectrum(733.2, 1, "proc A2");

        MsnSpectrum rawB1 = newMsnSpectrum(895.3, 1, "raw B1");
        MsnSpectrum procB1 = newMsnSpectrum(895.3, 1, "proc B1");

        //noinspection unchecked
        SimFunc<PeakAnnotation, PeakAnnotation> simFunc = mock(SimFunc.class);
        when(simFunc.calcSimilarity(procA1, procA2)).thenReturn(0.9);

        Tolerance precursorTolerance = new AbsoluteTolerance(0.3);

        //noinspection unchecked
        SimilarityGraphBuilder<MsnSpectrum, SimilarityGraph<MsnSpectrum>> builder = mock(SimilarityGraphBuilder.class);

        ClusterUtils.addSpectraToBuilder(Lists.newArrayList(rawA1, rawA2, rawB1), Lists.newArrayList(procA1, procA2, procB1), simFunc, precursorTolerance, builder);

        verify(simFunc).calcSimilarity(procA1, procA2);
        verifyNoMoreInteractions(simFunc);

        verify(builder).add(rawA1);
        verify(builder).add(rawA2);
        verify(builder).add(rawB1);
        verify(builder).add(rawA1, rawA2, 0.9);
        verifyNoMoreInteractions(builder);
    }

    /**
     *          A1
     *         /\
     *      0.9 0.1
     *      /     \
     *     A2-0.1- B1
     *
     * @throws Exception
     */
    @Test(expected = IllegalStateException.class)
    public void testBuildGraphUnsorted() throws Exception {

        MsnSpectrum rawA1 = newMsnSpectrum(733.1, 1, "raw A1");
        MsnSpectrum procA1 = newMsnSpectrum(733.1, 1, "proc A1");

        MsnSpectrum rawA2 = newMsnSpectrum(733.2, 1, "raw A2");
        MsnSpectrum procA2 = newMsnSpectrum(733.2, 1, "proc A2");

        MsnSpectrum rawB1 = newMsnSpectrum(733.3, 1, "raw B1");
        MsnSpectrum procB1 = newMsnSpectrum(733.3, 1, "proc B1");

        //noinspection unchecked
        SimFunc<PeakAnnotation, PeakAnnotation> simFunc = mock(SimFunc.class);

        Tolerance precursorTolerance = new AbsoluteTolerance(0.3);

        //noinspection unchecked
        SimilarityGraphBuilder<MsnSpectrum, SimilarityGraph<MsnSpectrum>> builder = mock(SimilarityGraphBuilder.class);

        final ArrayList<MsnSpectrum> rawSpectra = Lists.newArrayList(rawA2, rawA1, rawB1);
        final ArrayList<MsnSpectrum> processedSpectra = Lists.newArrayList(procA2, procA1, procB1);
        ClusterUtils.addSpectraToBuilder(rawSpectra, processedSpectra, simFunc, precursorTolerance, builder);
    }

    /**
     *          A1
     *         /
     *      0.9
     *      /
     *     A2      B1
     *
     * @throws Exception
     */
    @Test
    public void testBuildGraphDifferentCharge() throws Exception {

        MsnSpectrum rawA1 = newMsnSpectrum(733.1, 1, "raw A1");
        MsnSpectrum procA1 = newMsnSpectrum(733.1, 1, "proc A1");

        MsnSpectrum rawA2 = newMsnSpectrum(733.2, 1, "raw A2");
        MsnSpectrum procA2 = newMsnSpectrum(733.2, 1, "proc A2");

        MsnSpectrum rawB1 = newMsnSpectrum(733.3, 2, "raw B1");
        MsnSpectrum procB1 = newMsnSpectrum(733.3, 2, "proc B1");

        //noinspection unchecked
        SimFunc<PeakAnnotation, PeakAnnotation> simFunc = mock(SimFunc.class);
        when(simFunc.calcSimilarity(procA1, procA2)).thenReturn(0.9);
        when(simFunc.calcSimilarity(procA1, procB1)).thenReturn(0.1);
        when(simFunc.calcSimilarity(procA2, procB1)).thenReturn(0.1);

        Tolerance precursorTolerance = mock(Tolerance.class);
        when(precursorTolerance.withinTolerance(anyDouble(), anyDouble())).thenReturn(true);

        //noinspection unchecked
        SimilarityGraphBuilder<MsnSpectrum, SimilarityGraph<MsnSpectrum>> builder = mock(SimilarityGraphBuilder.class);

        ClusterUtils.addSpectraToBuilder(Lists.newArrayList(rawA1, rawA2, rawB1), Lists.newArrayList(procA1, procA2, procB1), simFunc, precursorTolerance, builder);

        verify(simFunc).calcSimilarity(procA1, procA2);
        verifyNoMoreInteractions(simFunc);

        verify(builder).add(rawA1);
        verify(builder).add(rawA2);
        verify(builder).add(rawB1);
        verify(builder).add(rawA1, rawA2, 0.9);
        verifyNoMoreInteractions(builder);
    }

    /**
     *         A1
     *         /\
     *      0.9  NaN
     *      /     \
     *     A2-0.1-B1
     *
     * @throws Exception
     */
    @Test
    public void testBuildGraphNanSim() throws Exception {

        MsnSpectrum rawA1 = newMsnSpectrum(733.1, 1, "raw A1");
        MsnSpectrum procA1 = newMsnSpectrum(733.1, 1, "proc A1");

        MsnSpectrum rawA2 = newMsnSpectrum(733.2, 1, "raw A2");
        MsnSpectrum procA2 = newMsnSpectrum(733.2, 1, "proc A2");

        MsnSpectrum rawB1 = newMsnSpectrum(733.3, 1, "raw B1");
        MsnSpectrum procB1 = newMsnSpectrum(733.3, 1, "proc B1");

        //noinspection unchecked
        SimFunc<PeakAnnotation, PeakAnnotation> simFunc = mock(SimFunc.class);
        when(simFunc.calcSimilarity(procA1, procA2)).thenReturn(0.9);
        when(simFunc.calcSimilarity(procA1, procB1)).thenReturn(Double.NaN);
        when(simFunc.calcSimilarity(procA2, procB1)).thenReturn(0.1);

        Tolerance precursorTolerance = mock(Tolerance.class);
        when(precursorTolerance.withinTolerance(anyDouble(), anyDouble())).thenReturn(true);

        //noinspection unchecked
        SimilarityGraphBuilder<MsnSpectrum, SimilarityGraph<MsnSpectrum>> builder = mock(SimilarityGraphBuilder.class);

        ClusterUtils.addSpectraToBuilder(Lists.newArrayList(rawA1, rawA2, rawB1), Lists.newArrayList(procA1, procA2, procB1), simFunc, precursorTolerance, builder);

        verify(simFunc).calcSimilarity(procA1, procA2);
        verify(simFunc).calcSimilarity(procA1, procB1);
        verify(simFunc).calcSimilarity(procA2, procB1);
        verifyNoMoreInteractions(simFunc);

        verify(builder).add(rawA1);
        verify(builder).add(rawA2);
        verify(builder).add(rawB1);
        verify(builder).add(rawA1, rawA2, 0.9);
        verify(builder).add(rawA2, rawB1, 0.1);
        verifyNoMoreInteractions(builder);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingRawSpectrum() throws Exception {

        MsnSpectrum rawA1 = newMsnSpectrum(733.1, 1, "raw A1");
        MsnSpectrum procA1 = newMsnSpectrum(733.1, 1, "proc A1");

        MsnSpectrum rawA2 = newMsnSpectrum(733.2, 1, "raw A2");
        MsnSpectrum procA2 = newMsnSpectrum(733.2, 1, "proc A2");

        MsnSpectrum procB1 = newMsnSpectrum(733.3, 2, "proc B1");

        //noinspection unchecked
        SimFunc<PeakAnnotation, PeakAnnotation> simFunc = mock(SimFunc.class);

        Tolerance precursorTolerance = mock(Tolerance.class);

        //noinspection unchecked
        SimilarityGraphBuilder<MsnSpectrum, SimilarityGraph<MsnSpectrum>> builder = mock(SimilarityGraphBuilder.class);

        ClusterUtils.addSpectraToBuilder(
                Lists.newArrayList(rawA1, rawA2),
                Lists.newArrayList(procA1, procA2, procB1),
                simFunc, precursorTolerance, builder);
    }

    private MsnSpectrum newMsnSpectrum(double mz, int charge, String id) {

        MsnSpectrum spectrum = mock(MsnSpectrum.class);
        when(spectrum.getPrecursor()).thenReturn(new Peak(mz, 100.0, charge));
        when(spectrum.toString()).thenReturn(id);
        return spectrum;
    }
}