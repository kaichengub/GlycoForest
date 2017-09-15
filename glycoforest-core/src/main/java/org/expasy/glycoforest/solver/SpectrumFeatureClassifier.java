package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public abstract class SpectrumFeatureClassifier<F extends Enum> {

    protected final Tolerance tolerance;

    public SpectrumFeatureClassifier(Tolerance tolerance) {

        this.tolerance = tolerance;
    }

    public abstract SpectrumFeatureClassification<F> classify(PeakList peakList, SugarStructure sugarStructure);
}
