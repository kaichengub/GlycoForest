package org.expasy.mzjava.core.spectrasim.peakpairprocessor;

import org.expasy.mzjava.core.ms.peaklist.DoublePeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrasim.peakpairprocessor.PeakPairSink;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class PeakPairTicNormalizerTest {

    @Test
    public void testSmallerX() throws Exception {

        PeakPairSink<PeakAnnotation, PeakAnnotation> sink = mock(PeakPairSink.class);

        PeakPairTicNormalizer<PeakAnnotation, PeakAnnotation> ticNormalizer = new PeakPairTicNormalizer<>(0);
        ticNormalizer.setSink(sink);


        final PeakList<PeakAnnotation> plX = newPeakList(new double[]{9.959904671, 14.24168682, 0, 3.619037628});
        final PeakList<PeakAnnotation> plY = newPeakList(new double[]{44.32028198, 0, 10.91252041, 30.69290352});
        final List<PeakAnnotation> emptyAnnotations = Collections.<PeakAnnotation>emptyList();

        ticNormalizer.begin(plX, plY);
        ticNormalizer.processPeakPair(1, plX.getIntensity(0), plY.getIntensity(0), emptyAnnotations, emptyAnnotations);
        ticNormalizer.processPeakPair(2, plX.getIntensity(1), plY.getIntensity(1), emptyAnnotations, emptyAnnotations);
        ticNormalizer.processPeakPair(3, plX.getIntensity(2), plY.getIntensity(2), emptyAnnotations, emptyAnnotations);
        ticNormalizer.processPeakPair(4, plX.getIntensity(3), plY.getIntensity(3), emptyAnnotations, emptyAnnotations);
        ticNormalizer.end();

        verify(sink).begin(plX, plY);
        verify(sink).processPeakPair(1, 30.761771633248497, 44.32028198, emptyAnnotations, emptyAnnotations);
        verify(sink).processPeakPair(2, 43.98631634544537, 0, emptyAnnotations, emptyAnnotations);
        verify(sink).processPeakPair(4, 11.177617931306134, 30.69290352, emptyAnnotations, emptyAnnotations);
        verify(sink).end();
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void testSmallerY() throws Exception {

        PeakPairSink<PeakAnnotation, PeakAnnotation> sink = mock(PeakPairSink.class);

        PeakPairTicNormalizer<PeakAnnotation, PeakAnnotation> ticNormalizer = new PeakPairTicNormalizer<>(0);
        ticNormalizer.setSink(sink);


        final PeakList<PeakAnnotation> plX = newPeakList(new double[]{44.32028198, 0, 10.91252041, 30.69290352});
        final PeakList<PeakAnnotation> plY = newPeakList(new double[]{9.959904671, 14.24168682, 0, 3.619037628});
        final List<PeakAnnotation> emptyAnnotations = Collections.<PeakAnnotation>emptyList();

        ticNormalizer.begin(plX, plY);
        ticNormalizer.processPeakPair(1, plX.getIntensity(0), plY.getIntensity(0), emptyAnnotations, emptyAnnotations);
        ticNormalizer.processPeakPair(2, plX.getIntensity(1), plY.getIntensity(1), emptyAnnotations, emptyAnnotations);
        ticNormalizer.processPeakPair(3, plX.getIntensity(2), plY.getIntensity(2), emptyAnnotations, emptyAnnotations);
        ticNormalizer.processPeakPair(4, plX.getIntensity(3), plY.getIntensity(3), emptyAnnotations, emptyAnnotations);
        ticNormalizer.end();

        verify(sink).begin(plX, plY);
        verify(sink).processPeakPair(1, 44.32028198, 30.761771633248497, emptyAnnotations, emptyAnnotations);
        verify(sink).processPeakPair(2, 0, 43.98631634544537, emptyAnnotations, emptyAnnotations);
        verify(sink).processPeakPair(4, 30.69290352, 11.177617931306134, emptyAnnotations, emptyAnnotations);
        verify(sink).end();
        verifyNoMoreInteractions(sink);
    }

    private PeakList<PeakAnnotation> newPeakList(double[] intensities) {

        final PeakList<PeakAnnotation> peakList = new DoublePeakList<>(intensities.length);

        for (int i = 0; i < intensities.length; i++) {

            peakList.add(i + 1, intensities[i]);
        }

        return peakList;
    }
}