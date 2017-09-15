package org.expasy.glycoforest.ms.fragment;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructureFragment;
import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.core.ms.spectrum.FragmentType;
import org.expasy.mzjava.core.ms.spectrum.IonType;

import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class CoreNeutralLossPeakGenerator implements GlycanPeakGenerator {

    //Linear
    private final Composition linearLoss = Composition.parseComposition("C-3H-8O-4");
    //Branched
    private final Composition branchedLoss1 = Composition.parseComposition("C-2H-2O-1");
    private final Composition branchedLoss2 = Composition.parseComposition("C-2H-4O-2");

    private final double intensity;
    private final GlycanMassCalculator massCalculator;

    public CoreNeutralLossPeakGenerator(final double intensity, final GlycanMassCalculator massCalculator) {

        this.intensity = intensity;
        this.massCalculator = massCalculator;
    }

    @Override
    public FragmentType getFragmentType() {

        return FragmentType.INTACT;
    }

    @Override
    public Stream<SugarStructurePeak> apply(final Integer charge, final SugarStructureFragment fragment) {

        if(!fragment.getFragmentType().equals(FragmentType.INTACT))
            return Stream.empty();

        final int rootOutDegree = fragment.outDegreeOf(fragment.getRoot());
        final Composition[] peakCompositions;
        final Composition fragmentComposition = massCalculator.calcComposition(fragment.getComposition(), IonType.p, charge);
        if(rootOutDegree == 1){ // linear

            peakCompositions = new Composition[]{new Composition(fragmentComposition, linearLoss)};
        } else {                // branched

            peakCompositions = new Composition[]{
                    new Composition(fragmentComposition, branchedLoss1),
                    new Composition(fragmentComposition, branchedLoss2)
            };
        }

        return Stream.of(peakCompositions).map(peakComposition -> new SugarStructurePeak(fragment, IonType.p, peakComposition, intensity));
    }
}
