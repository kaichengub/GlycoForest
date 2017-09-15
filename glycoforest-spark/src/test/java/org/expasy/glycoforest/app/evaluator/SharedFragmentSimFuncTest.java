package org.expasy.glycoforest.app.evaluator;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.ms.spectrum.SugarStructureSpectrum;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.IonType;
import org.junit.Assert;
import org.junit.Test;

import java.util.EnumSet;

import static org.expasy.glycoforest.mol.SugarUnit.Fuc;
import static org.expasy.glycoforest.mol.SugarUnit.Hex;
import static org.expasy.glycoforest.mol.SugarUnit.HexNAc;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SharedFragmentSimFuncTest {

    private final GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiNegativeReduced();
    private final SugarStructureSpectrum.Builder spectrumBuilder = new SugarStructureSpectrum.Builder(EnumSet.of(IonType.b), massCalculator, PeakList.Precision.DOUBLE);

    @Test
    public void testCalcSimilarity1() throws Exception {

        final SugarStructureSpectrum spectrum1 = spectrumBuilder.build(new SugarStructure.Builder("1", HexNAc).add(HexNAc).add(Hex)
                        .build(),
                1,
                d -> d
        );
        final SugarStructureSpectrum spectrum2 = spectrumBuilder.build(new SugarStructure.Builder("2", HexNAc).add(Hex).add(HexNAc)
                        .build(),
                1,
                d -> d
        );

        Assert.assertEquals(1.0, new SharedFragmentSimFunc(new AbsoluteTolerance(0.3)).calcSimilarity(spectrum1, spectrum2), 0.0);
    }

    @Test
    public void testCalcSimilarity2() throws Exception {

        final SugarStructureSpectrum spectrum1 = spectrumBuilder.build(new SugarStructure.Builder("1", HexNAc)
                        .branch().add(HexNAc)
                        .pop().add(Hex)
                        .build(),
                1,
                d -> d
        );
        final SugarStructureSpectrum spectrum2 = spectrumBuilder.build(new SugarStructure.Builder("2", HexNAc).add(Hex).add(HexNAc)
                        .build(),
                1,
                d -> d
        );

        Assert.assertEquals(1.0, new SharedFragmentSimFunc(new AbsoluteTolerance(0.3)).calcSimilarity(spectrum1, spectrum2), 0.0);
    }

    @Test
    public void testCalcSimilarity3() throws Exception {

        final SugarStructureSpectrum spectrum1 = spectrumBuilder.build(new SugarStructure.Builder("1", HexNAc).add(HexNAc).branch().add(Fuc).pop().add(Hex)
                        .build(),
                1,
                d -> d
        );
        final SugarStructureSpectrum spectrum2 = spectrumBuilder.build(new SugarStructure.Builder("2", HexNAc).add(Hex).add(HexNAc).add(Fuc)
                        .build(),
                1,
                d -> d
        );

        Assert.assertEquals(1.0, new SharedFragmentSimFunc(new AbsoluteTolerance(0.3)).calcSimilarity(spectrum1, spectrum2), 0.0);
    }

    @Test
    public void testCalcSimilarity4() throws Exception {

        final SugarStructureSpectrum spectrum1 = spectrumBuilder.build(new SugarStructure.Builder("1", HexNAc).branch()
                        .add(Hex).add(HexNAc).add(Hex)
                        .pop()
                        .add(HexNAc).add(Hex).add(HexNAc).add(Hex)
                        .build(),
                1,
                d -> d
        );
        final SugarStructureSpectrum spectrum2 = spectrumBuilder.build(new SugarStructure.Builder("2", HexNAc).branch()
                        .add(Hex).add(HexNAc).add(Hex).branch().add(HexNAc).pop().add(Hex)
                        .pop()
                        .add(HexNAc).add(Hex)
                        .build(),
                1,
                d -> d
        );

        Assert.assertEquals(2.0, new SharedFragmentSimFunc(new AbsoluteTolerance(0.3)).calcSimilarity(spectrum1, spectrum2), 0.0);
    }
}