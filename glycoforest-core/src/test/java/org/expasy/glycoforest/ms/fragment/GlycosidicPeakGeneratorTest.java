package org.expasy.glycoforest.ms.fragment;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.mol.SugarStructureFragment;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.mzjava.core.ms.spectrum.FragmentType;
import org.expasy.mzjava.core.ms.spectrum.IonType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycosidicPeakGeneratorTest {

    @Test
    public void testApply() throws Exception {

        final GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiNegativeReduced();

        final SugarComposition fragComposition = new SugarComposition(SugarUnit.Hex);
        final SugarStructureFragment fragment = mock(SugarStructureFragment.class);
        when(fragment.getFragmentType()).thenReturn(FragmentType.FORWARD);
        when(fragment.getComposition()).thenReturn(fragComposition);

        final GlycosidicPeakGenerator peakGenerator = new GlycosidicPeakGenerator(IonType.b, massCalculator);

        final List<SugarStructurePeak> generated = peakGenerator.apply(1, fragment).collect(Collectors.toList());

        Assert.assertEquals(1, generated.size());

        final SugarStructurePeak sugarStructurePeak = generated.get(0);
        Assert.assertEquals(IonType.b, sugarStructurePeak.getIonType());
        Assert.assertEquals(1, sugarStructurePeak.getIntensity(), 0.0);
        Assert.assertEquals(161.0455, sugarStructurePeak.getMz(), 0.0001);
    }
}