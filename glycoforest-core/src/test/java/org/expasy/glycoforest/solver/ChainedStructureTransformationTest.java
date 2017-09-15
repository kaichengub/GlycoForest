package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.mol.SugarUnit;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ChainedStructureTransformationTest {

    @Test
    public void testGenerateCandidates() throws Exception {

        ChainedStructureTransformation transformation = new ChainedStructureTransformation(
                StructureAddition.noInsertNoTerminal(SugarUnit.Fuc),
                StructureAddition.noTerminal(HexNAc)
        );

        final List<SugarStructure> candidates = transformation.generateCandidates(
                new SugarStructure.Builder("Src", HexNAc)
                        .branch().add(HexNAc)
                        .pop().add(Hex)
                        .build(), false)
        ;

        Assert.assertEquals(6, candidates.size());
        Collections.sort(candidates, (c1, c2) -> c1.toString().compareTo(c2.toString())); //Make sure order is always the same
        Assert.assertEquals(true, candidates.get(0).isIsomorphic(
                new SugarStructure.Builder("0", HexNAc)
                        .branch().add(HexNAc).add(HexNAc).add(Fuc)
                        .pop().add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
        Assert.assertEquals(true, candidates.get(1).isIsomorphic(
                new SugarStructure.Builder("0", HexNAc)
                        .branch().add(HexNAc)
                        .pop().add(Hex)
                            .branch().add(HexNAc)
                            .pop().add(Fuc)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
        Assert.assertEquals(true, candidates.get(2).isIsomorphic(
                new SugarStructure.Builder("0", HexNAc)
                        .branch().add(HexNAc).add(HexNAc)
                        .pop().add(Hex).add(Fuc)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
        Assert.assertEquals(true, candidates.get(3).isIsomorphic(
                new SugarStructure.Builder("0", HexNAc)
                        .branch().add(HexNAc)
                        .pop().add(Hex).add(HexNAc).add(Fuc)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
        Assert.assertEquals(true, candidates.get(4).isIsomorphic(
                new SugarStructure.Builder("0", HexNAc)
                        .branch().add(HexNAc).add(Fuc)
                        .pop().add(Hex).add(HexNAc)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
        Assert.assertEquals(true, candidates.get(5).isIsomorphic(
                new SugarStructure.Builder("0", HexNAc)
                        .branch().add(HexNAc)
                            .branch().add(HexNAc)
                            .pop().add(Fuc)
                        .pop().add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
    }
}