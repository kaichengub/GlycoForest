package org.expasy.glycoforest.ms.fragment;

import org.expasy.glycoforest.mol.SugarStructureFragment;
import org.expasy.glycoforest.ms.spectrum.StructurePeakAnnotation;
import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.core.ms.spectrum.IonType;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarStructurePeak implements Comparable<SugarStructurePeak>{

    private final SugarStructureFragment fragment;
    private final IonType ionType;
    private final Composition composition;
    private final double intensity;

    public SugarStructurePeak(SugarStructureFragment fragment, IonType ionType, Composition composition) {

        this(fragment, ionType, composition, 1);
    }

    public SugarStructurePeak(SugarStructureFragment fragment, IonType ionType, Composition composition, double intensity) {

        this.fragment = fragment;
        this.ionType = ionType;
        this.composition = composition;
        this.intensity = intensity;
    }

    public double getMz() {

        return composition.getMolecularMass() / Math.abs(composition.getCharge());
    }

    public double getIntensity() {

        return intensity;
    }

    public IonType getIonType() {

        return ionType;
    }

    public Composition getComposition() {

        return composition;
    }

    public StructurePeakAnnotation newAnnotation() {

        return new StructurePeakAnnotation(composition.getCharge(), ionType, fragment);
    }

    @Override
    public int compareTo(SugarStructurePeak other) {

        return Double.compare(this.getMz(), other.getMz());
    }
}
