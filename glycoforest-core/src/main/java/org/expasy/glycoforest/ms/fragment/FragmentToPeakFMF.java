package org.expasy.glycoforest.ms.fragment;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructureFragment;
import org.expasy.mzjava.core.ms.spectrum.FragmentType;
import org.expasy.mzjava.core.ms.spectrum.IonType;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FragmentToPeakFMF implements Function<SugarStructureFragment, Stream<SugarStructurePeak>> {

    private final Integer[] charges;
    private final Map<FragmentType, Set<BiFunction<Integer, SugarStructureFragment, Stream<SugarStructurePeak>>>> generatorFunctionMap;

    private FragmentToPeakFMF(Integer[] charges, Map<FragmentType, Set<BiFunction<Integer, SugarStructureFragment, Stream<SugarStructurePeak>>>> generatorFunctionMap) {

        this.charges = charges;
        this.generatorFunctionMap = generatorFunctionMap;
    }

    @Override
    public Stream<SugarStructurePeak> apply(SugarStructureFragment sugarStructureFragment) {

        return generatorFunctionMap.getOrDefault(sugarStructureFragment.getFragmentType(), Collections.emptySet()).stream().flatMap(

                generatorFunction -> Stream.of(charges).flatMap(charge -> generatorFunction.apply(charge, sugarStructureFragment))
        );
    }

    public static class Builder {

        private final Map<FragmentType, Set<BiFunction<Integer, SugarStructureFragment, Stream<SugarStructurePeak>>>> generatorFunctionMap;

        public Builder() {

            generatorFunctionMap = new HashMap<>();
        }

        public Builder addGlycosidicGenerators(Set<IonType> ionTypes, GlycanMassCalculator massCalculator) {

            checkNotNull(ionTypes);
            checkNotNull(massCalculator);

            for(IonType ionType : ionTypes) {

                generatorFunctionMap.computeIfAbsent(ionType.getFragmentType(), fragmentType -> new HashSet<>())
                        .add(new GlycosidicPeakGenerator(ionType, massCalculator));
            }

            return this;
        }

        public Builder addPeakGenerator(FragmentType fragmentType, BiFunction<Integer, SugarStructureFragment, Stream<SugarStructurePeak>> peakGenerator) {

            generatorFunctionMap.computeIfAbsent(fragmentType, o -> new HashSet<>())
                    .add(peakGenerator);
            return this;
        }

        public FragmentToPeakFMF build(Integer... charges) {

            if(charges.length == 0)
                charges = new Integer[]{1};

            return new FragmentToPeakFMF(charges, generatorFunctionMap);
        }
    }
}
