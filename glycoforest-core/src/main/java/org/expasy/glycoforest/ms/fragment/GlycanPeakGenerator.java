package org.expasy.glycoforest.ms.fragment;

import org.expasy.glycoforest.mol.SugarStructureFragment;
import org.expasy.mzjava.core.ms.spectrum.FragmentType;

import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public interface GlycanPeakGenerator extends BiFunction<Integer, SugarStructureFragment, Stream<SugarStructurePeak>> {

    FragmentType getFragmentType();
}
