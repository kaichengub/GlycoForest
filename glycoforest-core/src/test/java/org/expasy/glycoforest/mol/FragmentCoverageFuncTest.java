package org.expasy.glycoforest.mol;

import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;

import static org.expasy.glycoforest.mol.SugarUnit.Fuc;
import static org.expasy.glycoforest.mol.SugarUnit.Hex;
import static org.expasy.glycoforest.mol.SugarUnit.HexNAc;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FragmentCoverageFuncTest {

    @Test
    public void testMissingLinkEvidence() throws Exception {

        FragmentCoverageFunc func = new FragmentCoverageFunc(GlycanMassCalculator.newEsiNegativeReduced(), new AbsoluteTolerance(0.3));

        final BetweenRunConsensus peakList = new BetweenRunConsensus(0.5, 0.01, 12, 24, 211.9, 212.2, Collections.singleton(UUID.randomUUID()), PeakList.Precision.FLOAT);
        peakList.getPrecursor().setMzAndCharge(212.08, 2);
        peakList.add(100.5324, 10);
        peakList.add(110.5455, 12);

        final SugarStructure structure = new SugarStructure.Builder("", HexNAc).add(HexNAc).build();

        Assert.assertEquals(0, func.missingLinkEvidence(structure, peakList));
    }

    @Test
    public void testCalcCoverage() throws Exception {

        FragmentCoverageFunc func = new FragmentCoverageFunc(GlycanMassCalculator.newEsiNegativeReduced(), new AbsoluteTolerance(0.3));

        final BetweenRunConsensus peakList = new BetweenRunConsensus(0.5, 0.01, 12, 24, 211.9, 212.2, Collections.singleton(UUID.randomUUID()), PeakList.Precision.FLOAT);
        peakList.getPrecursor().setMzAndCharge(212.08, 2);
        peakList.add(100.5324, 10);
        peakList.add(110.5455, 12);

        final SugarStructure structure = new SugarStructure.Builder("", HexNAc).add(HexNAc).build();

        Assert.assertEquals(1.0, func.calcCoverage(structure, peakList), 0.0);
    }

    @Test
    public void testMissingLinkEvidence2() throws Exception {

        FragmentCoverageFunc func = new FragmentCoverageFunc(GlycanMassCalculator.newEsiNegativeReduced(), new AbsoluteTolerance(0.3));

        final BetweenRunConsensus peakList = new BetweenRunConsensus(0.5, 0.01, 12, 24, 292.9, 293.2, Collections.singleton(UUID.randomUUID()), PeakList.Precision.FLOAT);
        peakList.getPrecursor().setMzAndCharge(293.08, 2);
        peakList.add(100.5324, 10);

        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .branch().add(Hex)
                .pop().add(HexNAc)
                .build();

        Assert.assertEquals(1, func.missingLinkEvidence(structure, peakList));
    }

    @Test
    public void testCalcCoverage2() throws Exception {

        FragmentCoverageFunc func = new FragmentCoverageFunc(GlycanMassCalculator.newEsiNegativeReduced(), new AbsoluteTolerance(0.3));

        final BetweenRunConsensus peakList = new BetweenRunConsensus(0.5, 0.01, 12, 24, 292.9, 293.2, Collections.singleton(UUID.randomUUID()), PeakList.Precision.FLOAT);
        peakList.getPrecursor().setMzAndCharge(293.08, 2);
        peakList.add(100.5324, 10);

        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .branch().add(Hex)
                .pop().add(HexNAc)
                .build();

        Assert.assertEquals(0.5, func.calcCoverage(structure, peakList), 0.0);
    }

    @Test
    public void testCalcCoverage3() throws Exception {

        FragmentCoverageFunc func = new FragmentCoverageFunc(GlycanMassCalculator.newEsiNegativeReduced(), new AbsoluteTolerance(0.3));

        final BetweenRunConsensus peakList = new BetweenRunConsensus(0.5, 0.01, 12, 24, 292.9, 293.2, Collections.singleton(UUID.randomUUID()), PeakList.Precision.FLOAT);
        peakList.getPrecursor().setMzAndCharge(293.08, 2);
        peakList.add(100.5324, 10);

        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .branch().add(Hex).add(Fuc)
                .pop().add(HexNAc)
                .build();

        Assert.assertEquals(1/3.0, func.calcCoverage(structure, peakList), 0.0);
    }
}