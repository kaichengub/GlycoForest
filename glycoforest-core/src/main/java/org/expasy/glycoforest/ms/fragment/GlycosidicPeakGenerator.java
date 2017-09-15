package org.expasy.glycoforest.ms.fragment;

import com.google.common.base.Preconditions;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructureFragment;
import org.expasy.mzjava.core.ms.spectrum.FragmentType;
import org.expasy.mzjava.core.ms.spectrum.IonType;

import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycosidicPeakGenerator implements GlycanPeakGenerator {

    private final IonType ionType;
    private final GlycanMassCalculator massCalculator;

    public GlycosidicPeakGenerator(IonType ionType, GlycanMassCalculator massCalculator) {

        Preconditions.checkArgument(ionType.equals(IonType.y) || ionType.equals(IonType.z) || ionType.equals(IonType.b) || ionType.equals(IonType.c), "Not valid IonType, for Glycosidic fragment only b,c,z,y Ion are allowed");

        this.ionType = ionType;
        this.massCalculator = massCalculator;
    }

    @Override
    public FragmentType getFragmentType() {

        return ionType.getFragmentType();
    }

    @Override
    public Stream<SugarStructurePeak> apply(Integer charge, SugarStructureFragment fragment) {

        if (ionType.getFragmentType() == fragment.getFragmentType()) {

            return Stream.of(new SugarStructurePeak(fragment, ionType, massCalculator.calcComposition(fragment.getComposition(), ionType, charge)));
        } else {

            throw new IllegalStateException("Cannot generate " + ionType + " ions from fragment that is " + fragment.getFragmentType());
        }
    }
}
