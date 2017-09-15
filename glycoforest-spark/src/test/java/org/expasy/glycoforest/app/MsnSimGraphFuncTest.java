package org.expasy.glycoforest.app;

import com.google.common.collect.Lists;
import org.expasy.glycoforest.data.MsnSimGraph;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.spark.Cached;
import org.expasy.mzjava.spark.CachedAbsoluteTolerance;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class MsnSimGraphFuncTest {

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
    public void testCallWithProcChain() throws Exception {

        //noinspection unchecked
        final PeakProcessorChain<PeakAnnotation> processorChain = mock(PeakProcessorChain.class);
        when(processorChain.isEmpty()).thenReturn(false);

        MsnSpectrum rawA1 = newSpectrum(733.1, 1, "raw A1");
        MsnSpectrum procA1 = newSpectrum(733.1, 1, "proc A1");
        when(rawA1.copy(processorChain)).thenReturn(procA1);

        MsnSpectrum rawA2 = newSpectrum(733.2, 1, "raw A2");
        MsnSpectrum procA2 = newSpectrum(733.2, 1, "proc A2");
        when(rawA2.copy(processorChain)).thenReturn(procA2);

        MsnSpectrum rawB1 = newSpectrum(733.3, 1, "raw B1");
        MsnSpectrum procB1 = newSpectrum(733.3, 1, "proc B1");
        when(rawB1.copy(processorChain)).thenReturn(procB1);

        //noinspection unchecked
        final SimFunc<PeakAnnotation, PeakAnnotation> simFunc = mock(SimFunc.class);
        when(simFunc.calcSimilarity(procA1, procA2)).thenReturn(0.9);
        when(simFunc.calcSimilarity(procA1, procB1)).thenReturn(0.1);
        when(simFunc.calcSimilarity(procA2, procB1)).thenReturn(0.1);

        Cached<SimFunc<PeakAnnotation, PeakAnnotation>> cachedSimFunc = new Cached<SimFunc<PeakAnnotation, PeakAnnotation>>() {
            @Override
            protected SimFunc<PeakAnnotation, PeakAnnotation> build() {

                return simFunc;
            }
        };

        Cached<PeakProcessorChain<PeakAnnotation>> cachedProcessorChain = new Cached<PeakProcessorChain<PeakAnnotation>>() {
            @Override
            protected PeakProcessorChain<PeakAnnotation> build() {

                return processorChain;
            }
        };

        MsnSimGraphFunc function = new MsnSimGraphFunc(cachedSimFunc, new CachedAbsoluteTolerance(0.3), cachedProcessorChain);

        final UUID runId = UUID.randomUUID();
        MsnSimGraph graph = function.call(new Tuple2<>(runId, Lists.newArrayList(rawA1, rawA2, rawB1)));

        Assert.assertEquals(runId, graph.getRunId());
        Assert.assertEquals(3, graph.getVertexCount());
        Assert.assertEquals(3, graph.getEdgeCount());
        Assert.assertEquals(0.9, graph.findEdge(rawA1, rawA2).get().getScore(), 0.00000001);
        Assert.assertEquals(0.1, graph.findEdge(rawA1, rawB1).get().getScore(), 0.00000001);
        Assert.assertEquals(0.1, graph.findEdge(rawA2, rawB1).get().getScore(), 0.00000001);

        //Check that processed spectra are used to calculate the similarities
        verify(simFunc).calcSimilarity(procA1, procA2);
        verify(simFunc).calcSimilarity(procA1, procB1);
        verify(simFunc).calcSimilarity(procA2, procB1);
        verifyNoMoreInteractions(simFunc);
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

        //noinspection unchecked
        final PeakProcessorChain<PeakAnnotation> processorChain = mock(PeakProcessorChain.class);

        MsnSpectrum rawA1 = newSpectrum(733.1, 1, "raw A1");
        MsnSpectrum procA1 = newSpectrum(733.1, 1, "proc A1");
        when(rawA1.copy(processorChain)).thenReturn(procA1);

        MsnSpectrum rawA2 = newSpectrum(733.2, 1, "raw A2");
        MsnSpectrum procA2 = newSpectrum(733.2, 1, "proc A2");
        when(rawA2.copy(processorChain)).thenReturn(procA2);

        MsnSpectrum rawB1 = newSpectrum(895.3, 1, "raw B1");
        MsnSpectrum procB1 = newSpectrum(895.3, 1, "proc B1");
        when(rawB1.copy(processorChain)).thenReturn(procB1);

        //noinspection unchecked
        SimFunc<PeakAnnotation, PeakAnnotation> simFunc = mock(SimFunc.class);
        when(simFunc.calcSimilarity(procA1, procA2)).thenReturn(0.9);

        Cached<SimFunc<PeakAnnotation, PeakAnnotation>> cachedSimFunc = new Cached<SimFunc<PeakAnnotation, PeakAnnotation>>() {
            @Override
            protected SimFunc<PeakAnnotation, PeakAnnotation> build() {

                return simFunc;
            }
        };

        when(processorChain.isEmpty()).thenReturn(false);
        Cached<PeakProcessorChain<PeakAnnotation>> cachedProcessorChain = new Cached<PeakProcessorChain<PeakAnnotation>>() {
            @Override
            protected PeakProcessorChain<PeakAnnotation> build() {

                return processorChain;
            }
        };

        MsnSimGraphFunc function = new MsnSimGraphFunc(cachedSimFunc, new CachedAbsoluteTolerance(0.3), cachedProcessorChain);

        MsnSimGraph graph = function.call(new Tuple2<>(UUID.randomUUID(), Lists.newArrayList(rawA1, rawA2, rawB1)));

        verify(simFunc).calcSimilarity(procA1, procA2);
        verifyNoMoreInteractions(simFunc);

        Assert.assertEquals(3, graph.getVertexCount());
        Assert.assertEquals(1, graph.getEdgeCount());
        Assert.assertEquals(0.9, graph.findEdge(rawA1, rawA2).get().getScore(), 0.01);
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
    public void testCallWithoutProcChain() throws Exception {

        //noinspection unchecked
        final PeakProcessorChain<PeakAnnotation> processorChain = mock(PeakProcessorChain.class);
        when(processorChain.isEmpty()).thenReturn(true);

        MsnSpectrum rawA1 = newSpectrum(733.1, 1, "raw A1");

        MsnSpectrum rawA2 = newSpectrum(733.2, 1, "raw A2");

        MsnSpectrum rawB1 = newSpectrum(733.3, 1, "raw B1");

        //noinspection unchecked
        final SimFunc<PeakAnnotation, PeakAnnotation> simFunc = mock(SimFunc.class);
        when(simFunc.calcSimilarity(rawA1, rawA2)).thenReturn(0.9);
        when(simFunc.calcSimilarity(rawA1, rawB1)).thenReturn(0.1);
        when(simFunc.calcSimilarity(rawA2, rawB1)).thenReturn(0.1);

        Cached<SimFunc<PeakAnnotation, PeakAnnotation>> cachedSimFunc = new Cached<SimFunc<PeakAnnotation, PeakAnnotation>>() {
            @Override
            protected SimFunc<PeakAnnotation, PeakAnnotation> build() {

                return simFunc;
            }
        };

        Cached<PeakProcessorChain<PeakAnnotation>> cachedProcessorChain = new Cached<PeakProcessorChain<PeakAnnotation>>() {
            @Override
            protected PeakProcessorChain<PeakAnnotation> build() {

                return processorChain;
            }
        };

        MsnSimGraphFunc function = new MsnSimGraphFunc(cachedSimFunc, new CachedAbsoluteTolerance(0.3), cachedProcessorChain);

        final UUID runId = UUID.randomUUID();
        MsnSimGraph graph = function.call(new Tuple2<>(runId, Lists.newArrayList(rawA1, rawA2, rawB1)));

        Assert.assertEquals(runId, graph.getRunId());
        Assert.assertEquals(3, graph.getVertexCount());
        Assert.assertEquals(3, graph.getEdgeCount());
        Assert.assertEquals(0.9, graph.findEdge(rawA1, rawA2).get().getScore(), 0.00000001);
        Assert.assertEquals(0.1, graph.findEdge(rawA1, rawB1).get().getScore(), 0.00000001);
        Assert.assertEquals(0.1, graph.findEdge(rawA2, rawB1).get().getScore(), 0.00000001);

        //Check that processed spectra are used to calculate the similarities
        verify(simFunc).calcSimilarity(rawA1, rawA2);
        verify(simFunc).calcSimilarity(rawA1, rawB1);
        verify(simFunc).calcSimilarity(rawA2, rawB1);
        verifyNoMoreInteractions(simFunc);
    }

    private MsnSpectrum newSpectrum(double mz, int charge, String id) {

        MsnSpectrum spectrum = mock(MsnSpectrum.class);
        when(spectrum.getPrecursor()).thenReturn(new Peak(mz, 100.0, charge));
        when(spectrum.toString()).thenReturn(id);
        return spectrum;
    }
}