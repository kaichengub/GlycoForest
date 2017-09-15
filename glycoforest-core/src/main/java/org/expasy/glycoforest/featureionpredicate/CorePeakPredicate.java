package org.expasy.glycoforest.featureionpredicate;

import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakLists;

import java.util.function.Predicate;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class CorePeakPredicate<PL extends PeakList> implements Predicate<PL> {

    //Linear
    private final Composition neutralLoss1 = Composition.parseComposition("C3H8O4");
    //Branched
    private final Composition neutralLoss2 = Composition.parseComposition("C2H2O");
    private final Composition neutralLoss3 = Composition.parseComposition("C2H4O2");

    private final Tolerance tolerance;

    public CorePeakPredicate(Tolerance tolerance) {

        this.tolerance = tolerance;
    }

    @Override
    public boolean test(PL peakList) {

        final double precursorMz = peakList.getPrecursor().getMz();
        final double sum = calcSum(peakList, precursorMz, 1) /*+ calcSum(peakList, precursorMz, 2)*/;
        return sum > 0;
    }

    private double calcSum(PL peakList, double precursorMz, int charge) {

        final double intensitySum1 = PeakLists.getIntensitySum(precursorMz - neutralLoss1.getMolecularMass()/charge, peakList, tolerance);
        final double intensitySum2 = PeakLists.getIntensitySum(precursorMz - neutralLoss2.getMolecularMass()/charge, peakList, tolerance);
        final double intensitySum3 = PeakLists.getIntensitySum(precursorMz - neutralLoss3.getMolecularMass()/charge, peakList, tolerance);

        return intensitySum1 + intensitySum2 + intensitySum3;
    }
}
