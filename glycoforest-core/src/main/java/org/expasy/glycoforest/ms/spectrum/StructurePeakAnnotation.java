package org.expasy.glycoforest.ms.spectrum;

import org.expasy.glycoforest.mol.SugarStructureFragment;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.IonType;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructurePeakAnnotation implements PeakAnnotation {

    private final int charge;
    private final IonType ionType;
    private final SugarStructureFragment fragment;

    public StructurePeakAnnotation(int charge, IonType ionType, SugarStructureFragment fragment) {

        this.charge = charge;
        this.ionType = ionType;
        this.fragment = fragment;
    }

    public StructurePeakAnnotation(StructurePeakAnnotation src) {

        this.charge = src.charge;
        this.ionType = src.ionType;
        this.fragment = src.fragment;
    }

    @Override
    public int getCharge() {

        return charge;
    }

    public SugarStructureFragment getFragment() {

        return fragment;
    }

    public IonType getIonType() {

        return ionType;
    }

    @Override
    public PeakAnnotation copy() {

        return new StructurePeakAnnotation(this);
    }
}
