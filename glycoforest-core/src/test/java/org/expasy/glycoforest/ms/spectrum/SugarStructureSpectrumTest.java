package org.expasy.glycoforest.ms.spectrum;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.ms.fragment.CoreNeutralLossPeakGenerator;
import org.expasy.glycoforest.ms.fragment.GlycanPeakGenerator;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.IonType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static org.expasy.glycoforest.mol.SugarUnit.Fuc;
import static org.expasy.glycoforest.mol.SugarUnit.Hex;
import static org.expasy.glycoforest.mol.SugarUnit.HexNAc;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarStructureSpectrumTest {

    @Test
    public void test384_minus1() throws Exception {

        //Hex-HexNAc~
        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex)
                .build();

        final SugarStructureSpectrum.Builder builder = new SugarStructureSpectrum.Builder(EnumSet.of(IonType.b, IonType.y), GlycanMassCalculator.newEsiNegativeReduced(), PeakList.Precision.DOUBLE);

        SugarStructureSpectrum spectrum = builder.build(structure, 1, d -> d);

        Assert.assertEquals(2, spectrum.size());
        final double delta = 0.0001;
        Assert.assertEquals(161.0455, spectrum.getMz(0), delta);    // Hex-] -1
        Assert.assertEquals(222.0983, spectrum.getMz(1), delta);    //   [-HexNac~ -1
    }

    @Test
    public void test384_minus2() throws Exception {

        //Hex-HexNAc~
        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex)
                .build();

        final SugarStructureSpectrum.Builder builder = new SugarStructureSpectrum.Builder(EnumSet.of(IonType.b, IonType.y), GlycanMassCalculator.newEsiNegativeReduced(), PeakList.Precision.DOUBLE);

        SugarStructureSpectrum spectrum = builder.build(structure, 2, d -> d);

        Assert.assertEquals(4, spectrum.size());
        final double delta = 0.0001;
        Assert.assertEquals(80.0191, spectrum.getMz(0), delta);     // Hex-] -2
        Assert.assertEquals(110.5455, spectrum.getMz(1), delta);    //   [-HexNac~ -2
        Assert.assertEquals(161.0455, spectrum.getMz(2), delta);    // Hex-] -1
        Assert.assertEquals(222.0983, spectrum.getMz(3), delta);    //   [-HexNac~ -1
    }

    @Test
    public void test587Branched() throws Exception {

        //Hex(HexNAc)HexNAc~
        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Hex)
                .build();

        final SugarStructureSpectrum.Builder builder = new SugarStructureSpectrum.Builder(EnumSet.of(IonType.b, IonType.y), GlycanMassCalculator.newEsiNegativeReduced(), PeakList.Precision.DOUBLE);

        SugarStructureSpectrum spectrum = builder.build(structure, 1, d -> d);

        Assert.assertEquals(4, spectrum.size());
        final double delta = 0.0001;
        Assert.assertEquals(161.0455473223, spectrum.getMz(0), delta);    //   Hex-] b -1
        Assert.assertEquals(202.0720964233, spectrum.getMz(1), delta);    //   HexNAc-] b -1
        Assert.assertEquals(384.15113460329997, spectrum.getMz(2), delta);    //   Hex-([-)HexNAc~ y -1
        Assert.assertEquals(425.17768370429997, spectrum.getMz(3), delta);    //   [-(HexNAc-)HexNAc~ y -1
    }

    @Test
    public void test587Linear() throws Exception {

        //Hex-HexNAc-HexNAc~
        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(HexNAc).add(Hex)
                .build();

        final SugarStructureSpectrum.Builder builder = new SugarStructureSpectrum.Builder(EnumSet.of(IonType.b, IonType.y), GlycanMassCalculator.newEsiNegativeReduced(), PeakList.Precision.DOUBLE);

        SugarStructureSpectrum spectrum = builder.build(structure, 1, d -> d);

        Assert.assertEquals(4, spectrum.size());
        final double delta = 0.0001;
        Assert.assertEquals(161.0455473223, spectrum.getMz(0), delta);    //   Hex-] b -1
        Assert.assertEquals(222.0983111733, spectrum.getMz(1), delta);    //   [-HexNAc~ y -1
        Assert.assertEquals(364.12491985329996, spectrum.getMz(2), delta);    //   Hex-HexNAc-] b -1
        Assert.assertEquals(425.17768370429997, spectrum.getMz(3), delta);    //   [-HexNAc-HexNAc~ y -1
    }

    @Test
    public void test733() throws Exception {

        //Hex-(Fuc-)HexNAc-HexNAc~
        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(HexNAc)
                .branch().add(Fuc)
                .pop().add(Hex)
                .build();

        final SugarStructureSpectrum.Builder builder = new SugarStructureSpectrum.Builder(EnumSet.of(IonType.b, IonType.y), GlycanMassCalculator.newEsiNegativeReduced(), PeakList.Precision.DOUBLE);

        SugarStructureSpectrum spectrum = builder.build(structure, 1, d -> d);

        Assert.assertEquals(6, spectrum.size());
        final double delta = 0.0001;
        Assert.assertEquals(145.05063270029999, spectrum.getMz(0), delta);    //   Fuc- b -1
        Assert.assertEquals(161.0455473223, spectrum.getMz(1), delta);    //   Hex- b -1
        Assert.assertEquals(222.0983111733, spectrum.getMz(2), delta);    //   -HexNAc~ y -1
        Assert.assertEquals(510.18282866129994, spectrum.getMz(3), delta);    //   Fuc(Hex)HexNAc- b -1
        Assert.assertEquals(571.2355925123, spectrum.getMz(4), delta);    //   -Fuc-HexNAc-HexNAc~ y -1
        Assert.assertEquals(587.2305071343, spectrum.getMz(5), delta);    //   -Hex-HexNAc-HexNAc~ y -1
    }

    @Test
    public void test587BranchedAndCore() throws Exception {

        //Hex(HexNAc)HexNAc~
        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Hex)
                .build();

        final GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiNegativeReduced();
        final List<GlycanPeakGenerator> peakGenerators = Collections.singletonList(new CoreNeutralLossPeakGenerator(1, massCalculator));
        final SugarStructureSpectrum.Builder builder = new SugarStructureSpectrum.Builder(EnumSet.of(IonType.b, IonType.y), massCalculator, PeakList.Precision.DOUBLE, peakGenerators);

        SugarStructureSpectrum spectrum = builder.build(structure, 1, d -> d);

        Assert.assertEquals(6, spectrum.size());
        final double delta = 0.0001;
        Assert.assertEquals(161.0455473223, spectrum.getMz(0), delta);    //   Hex- b -1
        Assert.assertEquals(202.0720964233, spectrum.getMz(1), delta);    //   HexNAc- b -1
        Assert.assertEquals(384.15113460329997, spectrum.getMz(2), delta);    //   -Hex-HexNAc~ y -1
        Assert.assertEquals(425.17768370429997, spectrum.getMz(3), delta);    //   -HexNAc-HexNAc~ y -1
        Assert.assertEquals(527.2093777622998, spectrum.getMz(4), delta);    //   Hex(HexNAc)HexNAc~ p -1
        Assert.assertEquals(545.2199424482998, spectrum.getMz(5), delta);    //   Hex(HexNAc)HexNAc~ p -1
    }

    @Test
    public void test587LinearAndCore() throws Exception {

        //Hex-HexNAc-HexNAc~
        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(HexNAc).add(Hex)
                .build();

        final GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiNegativeReduced();
        final List<GlycanPeakGenerator> peakGenerators = Collections.singletonList(new CoreNeutralLossPeakGenerator(1, massCalculator));
        final SugarStructureSpectrum.Builder builder = new SugarStructureSpectrum.Builder(EnumSet.of(IonType.b, IonType.y), massCalculator, PeakList.Precision.DOUBLE, peakGenerators);

        SugarStructureSpectrum spectrum = builder.build(structure, 1, d -> d);

        Assert.assertEquals(5, spectrum.size());
        final double delta = 0.0001;
        Assert.assertEquals(161.0455473223, spectrum.getMz(0), delta);    //   Hex- b -1
        Assert.assertEquals(222.0983111733, spectrum.getMz(1), delta);    //   -HexNAc~ y -1
        Assert.assertEquals(364.12491985329996, spectrum.getMz(2), delta);    //   Hex-HexNAc- b -1
        Assert.assertEquals(425.17768370429997, spectrum.getMz(3), delta);    //   -HexNAc-HexNAc~ y -1
        Assert.assertEquals(479.18824839029986, spectrum.getMz(4), delta);    //   Hex-HexNAc-HexNAc~ p -1
    }

    @Test
    public void testHexHexNAcNegative() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex)
                .build();

        final GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiNegativeReduced();
        final SugarStructureSpectrum.Builder builder = new SugarStructureSpectrum.Builder(EnumSet.of(IonType.b, IonType.y), massCalculator, PeakList.Precision.DOUBLE);

        SugarStructureSpectrum spectrum = builder.build(structure, 1, d -> d);

        Assert.assertEquals(2, spectrum.size());
        final double delta = 0.0001;
        Assert.assertEquals(161.045546998, spectrum.getMz(0), delta);    //   Hex- b -1
        Assert.assertEquals(222.098310849, spectrum.getMz(1), delta);    //   -HexNAc~ y -1
    }

    @Test
    public void testHexHexNAcPositive() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex)
                .build();

        final GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiPositiveReduced();
        final SugarStructureSpectrum.Builder builder = new SugarStructureSpectrum.Builder(EnumSet.of(IonType.b, IonType.y), massCalculator, PeakList.Precision.DOUBLE);

        SugarStructureSpectrum spectrum = builder.build(structure, 1, d -> d);

        Assert.assertEquals(2, spectrum.size());
        final double delta = 0.0001;
        Assert.assertEquals(185.0420445, spectrum.getMz(0), delta);    //   Hex- b -1
        Assert.assertEquals(246.094808351, spectrum.getMz(1), delta);    //   -HexNAc~ y -1
    }
}