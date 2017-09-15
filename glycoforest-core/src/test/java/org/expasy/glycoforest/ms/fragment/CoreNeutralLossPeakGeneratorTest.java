package org.expasy.glycoforest.ms.fragment;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructureFragment;
import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.core.ms.spectrum.FragmentType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class CoreNeutralLossPeakGeneratorTest {

    @Test
    public void testApplyLinear() throws Exception {

        final CoreNeutralLossPeakGenerator peakGenerator = new CoreNeutralLossPeakGenerator(1, GlycanMassCalculator.newEsiNegativeReduced());

        final SugarStructureFragment fragment = new SugarStructureFragment.Builder(FragmentType.INTACT, HexNAc).add(Hex).build();

        final List<SugarStructurePeak> peaks = peakGenerator.apply(1, fragment).collect(Collectors.toList());

        Assert.assertEquals(1, peaks.size());

        final Composition composition = peaks.get(0).getComposition();
        Assert.assertEquals(Composition.parseComposition("C11H18NO7(-)"), composition);
    }

    @Test
    public void testApplyBranched() throws Exception {

        final CoreNeutralLossPeakGenerator peakGenerator = new CoreNeutralLossPeakGenerator(1, GlycanMassCalculator.newEsiNegativeReduced());

        final SugarStructureFragment fragment = new SugarStructureFragment.Builder(FragmentType.INTACT, HexNAc)
                .branch().add(Fuc)
                .pop().add(Hex)
                .build();

        final List<SugarStructurePeak> peaks = peakGenerator.apply(1, fragment).collect(Collectors.toList());

        Assert.assertEquals(2, peaks.size());
        Assert.assertEquals(Composition.parseComposition("C18H34NO14(-)"), peaks.get(0).getComposition());
        Assert.assertEquals(Composition.parseComposition("C18H32NO13(-)"), peaks.get(1).getComposition());
    }
}