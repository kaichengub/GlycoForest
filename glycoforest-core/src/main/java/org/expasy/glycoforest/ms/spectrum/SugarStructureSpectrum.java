package org.expasy.glycoforest.ms.spectrum;

import cern.colt.function.DoubleFunction;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.mol.SugarStructureFragment;
import org.expasy.glycoforest.ms.fragment.FragmentToPeakFMF;
import org.expasy.glycoforest.ms.fragment.GlycanPeakGenerator;
import org.expasy.glycoforest.ms.fragment.PeakListCollector;
import org.expasy.glycoforest.ms.fragment.SugarStructurePeak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessor;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.spectrum.IonType;
import org.expasy.mzjava.core.ms.spectrum.Spectrum;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarStructureSpectrum extends Spectrum<StructurePeakAnnotation> {

    private final SugarStructure structure;

    public SugarStructureSpectrum(SugarStructure structure, int initialCapacity, Precision precision) {

        super(initialCapacity, precision);
        this.structure = structure;
    }

    public SugarStructure getStructure() {

        return structure;
    }

    @Override
    public PeakList<StructurePeakAnnotation> copy(PeakProcessor<StructurePeakAnnotation, StructurePeakAnnotation> peakProcessor) {

        throw new UnsupportedOperationException();
    }

    @Override
    public PeakList<StructurePeakAnnotation> copy(PeakProcessorChain<StructurePeakAnnotation> peakProcessorChain) {

        throw new UnsupportedOperationException();
    }

    public static class Builder {

        private final Set<IonType> ionTypes;
        private final GlycanMassCalculator massCalculator;
        private final Precision precision;
        private final List<? extends GlycanPeakGenerator> peakGenerators;

        public Builder(Set<IonType> ionTypes, GlycanMassCalculator massCalculator, Precision precision) {

            this(ionTypes, massCalculator, precision, Collections.emptyList());
        }

        public Builder(Set<IonType> ionTypes, GlycanMassCalculator massCalculator, Precision precision, final List<? extends GlycanPeakGenerator> peakGenerators) {

            this.ionTypes = ionTypes;
            this.massCalculator = massCalculator;
            this.precision = precision;
            this.peakGenerators = peakGenerators;
        }

        public SugarStructureSpectrum build(final SugarStructure structure, final int charge, final DoubleFunction intensityFunction) {

            final SugarStructureSpectrum spectrum = new SugarStructureSpectrum(structure, 100, precision);
            spectrum.getPrecursor().setValues(massCalculator.calcMz(structure.getComposition(), charge), 1.0, -charge);

            final Integer[] charges = new Integer[charge];
            for(int i = 1; i <= charge; i++) {
                charges[i - 1] = i;
            }

            final FragmentToPeakFMF.Builder builder = new FragmentToPeakFMF.Builder()
                    .addGlycosidicGenerators(ionTypes, massCalculator);

            for(GlycanPeakGenerator peakGenerator : peakGenerators){

                builder.addPeakGenerator(peakGenerator.getFragmentType(), peakGenerator);
            }

            final Function<SugarStructureFragment, Stream<SugarStructurePeak>> peakGeneratorFunction = builder
                    .build(charges);

            final SugarStructureSpectrum sugarStructureSpectrum = structure.fragmentStream()
                    .flatMap(peakGeneratorFunction)
                    .sorted()
                    .collect(new PeakListCollector<>(() -> spectrum, 0.00000001));

            sugarStructureSpectrum.trimToSize();
            for (int i = 0; i < sugarStructureSpectrum.size(); i++) {
                sugarStructureSpectrum.setIntensityAt(intensityFunction.apply(sugarStructureSpectrum.getIntensity(i)), i);
            }
            return sugarStructureSpectrum;
        }
    }
}
