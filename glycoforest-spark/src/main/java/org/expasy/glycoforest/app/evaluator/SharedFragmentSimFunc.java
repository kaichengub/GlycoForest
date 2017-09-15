package org.expasy.glycoforest.app.evaluator;

import org.expasy.glycoforest.ms.spectrum.StructurePeakAnnotation;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrasim.AbstractSimFunc;

import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SharedFragmentSimFunc extends AbstractSimFunc<StructurePeakAnnotation, StructurePeakAnnotation> {

    private double sum;
    private int peakCount;

    /**
     * Construct a SharedPeakSimFunc that uses the default peak list aligner.
     *
     * @param tolerance the tolerance to use when aligning the two PeakLists
     */
    public SharedFragmentSimFunc(Tolerance tolerance) {

        super(tolerance);
    }


    @Override
    public double calcSimilarity(PeakList<StructurePeakAnnotation> plX, PeakList<StructurePeakAnnotation> plY) {

        peakCount = 0;
        sum = 0;

        vectorize(plX, plY);

        return sum/2;
    }

    @Override
    public int getTotalPeakCount() {

        return peakCount;
    }

    @Override
    public void processPeakPair(double centroid, double xIntensity, double yIntensity, List<StructurePeakAnnotation> xAnnotations, List<StructurePeakAnnotation> yAnnotations) {

        if(xIntensity > 0) peakCount++;
        if(yIntensity > 0) peakCount++;

        sum += Math.abs(xIntensity - yIntensity);
    }

    @Override
    public double getBestScore() {

        return 0;
    }

    @Override
    public double getWorstScore() {

        return Integer.MAX_VALUE;
    }
}
