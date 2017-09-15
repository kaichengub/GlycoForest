package org.expasy.glycoforest.ms.fragment;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.ms.spectrum.StructurePeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.DoublePeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.IonType;
import org.junit.Assert;
import org.junit.Test;

import java.util.EnumSet;
import java.util.stream.Stream;

import static org.expasy.glycoforest.mol.SugarUnit.Hex;
import static org.expasy.glycoforest.mol.SugarUnit.HexNAc;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class PeakListCollectorTest {

    @Test
    public void test() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("587-2", HexNAc).add(Hex).add(HexNAc)
                .build();

        final PeakList<StructurePeakAnnotation> peakList = structure.fragmentStream()
                .flatMap(new FragmentToPeakFMF.Builder()
                        .addGlycosidicGenerators(EnumSet.of(IonType.b, IonType.y), GlycanMassCalculator.newEsiNegativeReduced())
                        .build())
                .sorted()
                .collect(new PeakListCollector<>(DoublePeakList::new, 0));

        Assert.assertEquals(4, peakList.size());
        double delta = 0.0001;
        Assert.assertEquals(202.0720961, peakList.getMz(0), delta);
        Assert.assertEquals(222.0983108, peakList.getMz(1), delta);
        Assert.assertEquals(364.1249195, peakList.getMz(2), delta);
        Assert.assertEquals(384.1511343, peakList.getMz(3), delta);
    }

    @Test
    public void test2() throws Exception {

        final SugarStructurePeak peak1 = mock(SugarStructurePeak.class);
        when(peak1.getMz()).thenReturn(641.7386963067999);
        when(peak1.getIntensity()).thenReturn(1.0);
        when(peak1.newAnnotation()).thenReturn(mock(StructurePeakAnnotation.class));
        final SugarStructurePeak peak2 = mock(SugarStructurePeak.class);
        when(peak2.getMz()).thenReturn(641.7386963068);
        when(peak2.getIntensity()).thenReturn(2.0);
        when(peak2.newAnnotation()).thenReturn(mock(StructurePeakAnnotation.class));

        final PeakList<StructurePeakAnnotation> peakList = Stream.of(peak1, peak2)
                .collect(new PeakListCollector<>(DoublePeakList::new, 0.00000001));

        Assert.assertEquals(1, peakList.size());
        Assert.assertEquals(641.7386963067999, peakList.getMz(0), 0.0);
    }
}