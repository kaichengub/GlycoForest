package org.expasy.glycoforest.mol;

import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.core.ms.spectrum.IonType;
import org.junit.Assert;
import org.junit.Test;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycanMassCalculatorTest {

    //Expected masses obtained from GlycoMod for singly charged
    @Test
    public void testCalcMz() throws Exception {

        final GlycanMassCalculator massCalc = GlycanMassCalculator.newEsiNegativeReduced();

        Assert.assertEquals(384.1509246, massCalc.calcMz(new SugarComposition(Hex, HexNAc), 1), 0.001);
        Assert.assertEquals(503.1806203, massCalc.calcMz(new SugarComposition(HexNAc, HexNAc, Neu5Ac, Neu5Ac), 2), 0.001);
    }

    @Test
    public void testCompositionMassMinus1() throws Exception {

        double mz = 513.1937; //NeuAc-HexNAcol (-)
        int charge = 1;
        GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiNegativeReduced();

        Assert.assertEquals(494.1747, massCalculator.calcCompositionMass(mz, charge), 0.001);
    }

    @Test
    public void testCompositionMassMinus2() throws Exception {

        double mz = 256.0932; //NeuAc-HexNAcol (--)
        int charge = 2;
        GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiNegativeReduced();
        Assert.assertEquals(494.1747, massCalculator.calcCompositionMass(mz, charge), 0.001);
    }

    @Test
    public void testCalcBIonMz() throws Exception {

        final GlycanMassCalculator massCalc = GlycanMassCalculator.newEsiNegativeReduced();

        Assert.assertEquals(161.0455, massCalc.calcMz(new SugarComposition(Hex), IonType.b, 1), 0.001);
        Assert.assertEquals(80.0191, massCalc.calcMz(new SugarComposition(Hex), IonType.b, 2), 0.001);
    }

    @Test
    public void testCalcCIonMz() throws Exception {

        final GlycanMassCalculator massCalc = GlycanMassCalculator.newEsiNegativeReduced();

        Assert.assertEquals(179.0561, massCalc.calcMz(new SugarComposition(Hex), IonType.c, 1), 0.001);
        Assert.assertEquals(89.0244, massCalc.calcMz(new SugarComposition(Hex), IonType.c, 2), 0.001);
    }

    @Test
    public void testCalcYIonMz() throws Exception {

        final GlycanMassCalculator massCalc = GlycanMassCalculator.newEsiNegativeReduced();

        Assert.assertEquals(222.0983, massCalc.calcMz(new SugarComposition(HexNAc), IonType.y, 1), 0.001);
        Assert.assertEquals(110.5455, massCalc.calcMz(new SugarComposition(HexNAc), IonType.y, 2), 0.001);
    }

    @Test
    public void testCalcZIonMz() throws Exception {

        final GlycanMassCalculator massCalc = GlycanMassCalculator.newEsiNegativeReduced();

        Assert.assertEquals(204.0877, massCalc.calcMz(new SugarComposition(HexNAc), IonType.z, 1), 0.001);
        Assert.assertEquals(101.5402, massCalc.calcMz(new SugarComposition(HexNAc), IonType.z, 2), 0.001);
    }

    @Test
    public void testNominalMass() throws Exception {

        final GlycanMassCalculator massCalc = GlycanMassCalculator.newEsiNegativeReduced();

        Assert.assertEquals(733, massCalc.calcNominalMass(new SugarComposition(HexNAc, HexNAc, Hex, Fuc), 1));
    }

    @Test
    public void testCalcComposition() throws Exception {

        final GlycanMassCalculator massCalc = GlycanMassCalculator.newEsiNegativeReduced();

        Assert.assertEquals(Composition.parseComposition("C8H16NO6(-)"), massCalc.calcComposition(new SugarComposition(HexNAc), IonType.y, 1));
        Assert.assertEquals(Composition.parseComposition("C8H15NO6(2-)"), massCalc.calcComposition(new SugarComposition(HexNAc), IonType.y, 2));
        Assert.assertEquals(Composition.parseComposition("C6H11O6(-)"), massCalc.calcComposition(new SugarComposition(Hex), IonType.c, 1));
        Assert.assertEquals(Composition.parseComposition("C6H10O6(2-)"), massCalc.calcComposition(new SugarComposition(Hex), IonType.c, 2));
    }

    @Test
    public void testCalcMass() throws Exception {

        final GlycanMassCalculator massCalc = GlycanMassCalculator.newEsiNegativeReduced();

        Assert.assertEquals(385.1582007077, massCalc.calcReducedMass(new SugarComposition(Hex, HexNAc)), 0.001);
    }

    @Test
    public void testCompositionPrecursor() throws Exception {

        final GlycanMassCalculator massCalc = GlycanMassCalculator.newEsiNegativeReduced();
        Assert.assertEquals(384.15113460329997, massCalc.calcComposition(new SugarComposition(Hex, HexNAc), IonType.p, 1).getMolecularMass(), 0.001);
    }

    @Test
    public void testCompositionPrecursorPositive() throws Exception {

        final GlycanMassCalculator massCalc = GlycanMassCalculator.newEsiPositiveReduced();
        Assert.assertEquals(408.1476314867, massCalc.calcComposition(new SugarComposition(Hex, HexNAc), IonType.p, 1).getMolecularMass(), 0.001);
        Assert.assertEquals(Composition.parseComposition("C14H27NNaO11(+)"), massCalc.calcComposition(new SugarComposition(Hex, HexNAc), IonType.p, 1));
    }
}