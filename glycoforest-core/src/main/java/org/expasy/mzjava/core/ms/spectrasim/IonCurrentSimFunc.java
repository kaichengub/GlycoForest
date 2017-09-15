package org.expasy.mzjava.core.ms.spectrasim;

import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrasim.peakpairprocessor.PeakListAligner;
import org.expasy.mzjava.core.ms.spectrasim.peakpairprocessor.PeakPairProcessor;

import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class IonCurrentSimFunc<X extends PeakAnnotation, Y extends PeakAnnotation> extends AbstractSimFunc<X, Y> {

    private int peakCount = 0;
    private double ionCurrent = 0;
    private int matchedPeakCount;

    public IonCurrentSimFunc(Tolerance tolerance) {

        super(tolerance);
    }

    public IonCurrentSimFunc(PeakListAligner<X, Y> peakListAligner, PeakPairProcessor<X, Y>... chain) {

        super(peakListAligner, chain);
    }

    @Override
    public double calcSimilarity(PeakList<X> plX, PeakList<Y> plY) {

        peakCount = 0;
        ionCurrent = 0;
        matchedPeakCount = 0;

        vectorize(plX, plY);

        return ionCurrent;
    }

    @Override
    public void processPeakPair(double centroid, double xIntensity, double yIntensity, List<X> xAnnotations, List<Y> yAnnotations) {

        if(xIntensity > 0)
            peakCount += 1;
        if(yIntensity > 0)
            peakCount += 1;

        if(xIntensity > 0) {

            ionCurrent += yIntensity;
            if (yIntensity > 0) {

                matchedPeakCount += 1;
            }
        }
    }

    @Override
    public int getTotalPeakCount() {

        return peakCount;
    }

    @Override
    public double getBestScore() {

        return Double.MAX_VALUE;
    }

    @Override
    public double getWorstScore() {

        return 0;
    }

    public int getMatchedPeakCount() {

        return matchedPeakCount;
    }
}
