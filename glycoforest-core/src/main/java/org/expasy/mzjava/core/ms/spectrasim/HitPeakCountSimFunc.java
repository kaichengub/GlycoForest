package org.expasy.mzjava.core.ms.spectrasim;

import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.glycomics.ms.spectrum.GlycanFragAnnotation;

import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class HitPeakCountSimFunc extends AbstractSimFunc<GlycanFragAnnotation, LibPeakAnnotation> {

    private int theoreticalPeakCount = 0;
    private int hitPeakCount = 0;

    public HitPeakCountSimFunc(Tolerance tolerance) {

        super(tolerance);
    }

    @Override
    public void processPeakPair(double centroid, double xIntensity, double yIntensity, List<GlycanFragAnnotation> xAnnotations, List<LibPeakAnnotation> yAnnotations) {

        if (xIntensity > 0)
            theoreticalPeakCount += 1;

        if (xIntensity > 0 && yIntensity > 0)
            hitPeakCount += 1;
    }

    @Override
    public double calcSimilarity(PeakList<GlycanFragAnnotation> plX, PeakList<LibPeakAnnotation> plY) {

        theoreticalPeakCount = 0;
        hitPeakCount = 0;

        vectorize(plX, plY);

        return hitPeakCount / (double) theoreticalPeakCount;
    }

    @Override
    public int getTotalPeakCount() {

        return theoreticalPeakCount + hitPeakCount;
    }

    @Override
    public double getBestScore() {

        return 1;
    }

    @Override
    public double getWorstScore() {

        return 0;
    }
}
