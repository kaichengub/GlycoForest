package org.expasy.glycoforest.solver;

import com.google.common.collect.Lists;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.mol.SugarUnit;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class CompoundStructureTransformationTest {

    @Test
    public void testName() throws Exception {

        StructureTransformation transformation = new CompoundStructureTransformation(
                Lists.newArrayList(
                        StructureSubstitution.singleSubstitution(SugarUnit.Fuc, SugarUnit.Hex),
                        StructureSubstitution.singleSubstitution(SugarUnit.Neu5Ac, SugarUnit.Neu5Gc)
                )
        );

        Assert.assertEquals("Fuc > Hex; Neu5Ac > Neu5Gc", transformation.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentException() throws Exception {

        new CompoundStructureTransformation(
                Lists.newArrayList(
                        StructureSubstitution.singleSubstitution(SugarUnit.Fuc, SugarUnit.Hex),
                        StructureSubstitution.singleSubstitution(SugarUnit.Fuc, SugarUnit.HexNAc)
                )
        );
    }

    @Test
    public void testGenerateCandidates() throws Exception {

        StructureTransformation transformation = new CompoundStructureTransformation(
                Lists.newArrayList(
                        StructureSubstitution.singleSubstitution(SugarUnit.Fuc, SugarUnit.Hex),
                        StructureSubstitution.singleSubstitution(SugarUnit.Neu5Ac, SugarUnit.Neu5Gc)
                )
        );

        List<SugarStructure> candidates = transformation.generateCandidates(new SugarStructure.Builder("Src", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Fuc)
                .build(), false);

        Assert.assertEquals(1, candidates.size());
        Assert.assertEquals(true, candidates.get(0).isIsomorphic(
                new SugarStructure.Builder("0", HexNAc)
                        .branch().add(HexNAc)
                        .pop().add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
    }

    @Test
    public void testGenerateCandidates2() throws Exception {

        StructureTransformation transformation = new CompoundStructureTransformation(
                Lists.newArrayList(
                        StructureSubstitution.singleSubstitution(SugarUnit.Fuc, SugarUnit.Hex),
                        StructureSubstitution.singleSubstitution(SugarUnit.Neu5Ac, SugarUnit.Neu5Gc)
                )
        );

        List<SugarStructure> candidates = transformation.generateCandidates(new SugarStructure.Builder("Src", HexNAc)
                .branch().add(HexNAc).add(Neu5Ac)
                .pop().add(Fuc)
                .build(), false);

        Assert.assertEquals(2, candidates.size());
        Assert.assertEquals(true, candidates.get(0).isIsomorphic(
                new SugarStructure.Builder("0", HexNAc)
                        .branch().add(HexNAc).add(Neu5Ac)
                        .pop().add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));

        Assert.assertEquals(true, candidates.get(1).isIsomorphic(
                new SugarStructure.Builder("1", HexNAc)
                        .branch().add(HexNAc).add(Neu5Gc)
                        .pop().add(Fuc)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
    }
}