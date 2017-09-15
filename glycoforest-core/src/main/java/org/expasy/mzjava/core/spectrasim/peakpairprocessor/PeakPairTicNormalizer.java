package org.expasy.mzjava.core.spectrasim.peakpairprocessor;

import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrasim.peakpairprocessor.AbstractPeakPairProcessor;

import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class PeakPairTicNormalizer<X extends PeakAnnotation, Y extends PeakAnnotation> extends AbstractPeakPairProcessor<X, Y> {

    private double scale;
    private double threshold = 0;
    private final double noisePercentage;

    private enum ScaleCandidate {
        PEAK_LIST_X, PEAK_LIST_Y
    }

    private ScaleCandidate scaleCandidate;

    public PeakPairTicNormalizer(double noisePercentage) {

        this.noisePercentage = noisePercentage;
    }

    @Override
    public void begin(PeakList<X> xPeakList, PeakList<Y> yPeakList) {

        if (xPeakList.getTotalIonCurrent() < yPeakList.getTotalIonCurrent()) {

            scaleCandidate = ScaleCandidate.PEAK_LIST_X;
            scale = yPeakList.getTotalIonCurrent() / xPeakList.getTotalIonCurrent();
            setThreshold(xPeakList);
        } else {

            scaleCandidate = ScaleCandidate.PEAK_LIST_Y;
            scale = xPeakList.getTotalIonCurrent() / yPeakList.getTotalIonCurrent();
            setThreshold(yPeakList);
        }

        sink.begin(xPeakList, yPeakList);
    }

    private void setThreshold(PeakList<?> peakList) {

        double min = Double.MAX_VALUE;
        for(int i = 0; i < peakList.size(); i++) {

            final double intensity = peakList.getIntensity(i);
            if (intensity > 0)
                min = Math.min(min, intensity);
        }
        threshold = (min * scale) + (min * scale * noisePercentage);
    }

    @Override
    public void processPeakPair(final double centroid, final double xIntensity, final double yIntensity, final List<X> xAnnotations, final List<Y> yAnnotations) {

        double xScaled = xIntensity;
        double yScaled = yIntensity;
        if (scaleCandidate == ScaleCandidate.PEAK_LIST_X) {

            xScaled = xIntensity * scale;
        } else {

            yScaled = yIntensity * scale;
        }

        if (xScaled >= threshold || yScaled >= threshold) {

            sink.processPeakPair(centroid, xScaled, yScaled, xAnnotations, yAnnotations);
        }
    }

    @Override
    public void end() {

        sink.end();
    }
}
