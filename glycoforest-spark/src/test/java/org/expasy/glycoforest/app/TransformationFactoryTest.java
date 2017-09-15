package org.expasy.glycoforest.app;

import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.solver.StructureTransformation;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class TransformationFactoryTest {

    @Test
    public void newRemoveAddAndSubstitution() throws Exception {

        final SugarStructure srcStructure = new SugarStructure.Builder("g 813-1", HexNAc)
                .branch().add(HexNAc).add(S)
                .pop().add(Hex).add(Fuc)
                .build();

        final StructureTransformation transformation = TransformationFactory.newRemoveAddAndSubstitution(Fuc, Hex);

        List<SugarStructure> candidates = transformation.generateCandidates(srcStructure, false);

        Assert.assertEquals(2, candidates.size());

        Assert.assertEquals(true, candidates.get(0).isIsomorphic(
                new SugarStructure.Builder("0", HexNAc)
                        .branch().add(HexNAc).add(S)
                        .pop().add(Hex).add(Hex)
                        .build()
                , IsomorphismType.TOPOLOGY
        ));

        Assert.assertEquals(true, candidates.get(1).isIsomorphic(
                new SugarStructure.Builder("1", HexNAc)
                        .branch().add(HexNAc)
                        .branch().add(S)
                        .pop()
                        .add(Hex)
                        .pop().add(Hex)
                        .build()
                , IsomorphismType.TOPOLOGY
        ));
    }

    @Test
    public void newRemoveAddAndSubstitution2() throws Exception {

        final SugarStructure srcStructure = new SugarStructure.Builder("g 829-1", HexNAc)
                .branch().add(HexNAc)
                .branch().add(S)
                .pop().add(Hex)
                .pop().add(Hex)
                .build();

        final StructureTransformation transformation = TransformationFactory.newRemoveAddAndSubstitution(Hex, Fuc);
        final List<SugarStructure> candidates = transformation.generateCandidates(srcStructure, false);

        Assert.assertEquals(4, candidates.size());

        Assert.assertEquals(true, candidates.get(0).isIsomorphic(
                new SugarStructure.Builder("g 813-1", HexNAc)
                        .branch().add(HexNAc)
                        .branch().add(S)
                        .pop().add(Fuc)
                        .pop().add(Hex)
                        .build()
                , IsomorphismType.TOPOLOGY
        ));

        Assert.assertEquals(true, candidates.get(1).isIsomorphic(
                new SugarStructure.Builder("g 813-1", HexNAc)
                        .branch().add(HexNAc)
                        .branch().add(S)
                        .pop().add(Hex)
                        .pop().add(Fuc)
                        .build()
                , IsomorphismType.TOPOLOGY
        ));

        Assert.assertEquals(true, candidates.get(2).isIsomorphic(
                new SugarStructure.Builder("g 813-1", HexNAc)
                        .branch().add(HexNAc).add(S)
                        .pop().add(Hex).add(Fuc)
                        .build()
                , IsomorphismType.TOPOLOGY
        ));

        Assert.assertEquals(true, candidates.get(3).isIsomorphic(
                new SugarStructure.Builder("g 813-1", HexNAc).add(HexNAc)
                        .branch().add(S)
                        .pop().add(Hex).add(Fuc)
                        .build()
                , IsomorphismType.TOPOLOGY
        ));
    }

    @Test
    public void testRemove() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex).add(Fuc).build();

        List<SugarStructure> candidates = TransformationFactory.newRemove(HexNAc).generateCandidates(structure, false);

        Assert.assertEquals(0, candidates.size());
    }

    @Test
    public void testRemoveAddAndSubstitution() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex).add(Fuc).build();

        List<SugarStructure> candidates = TransformationFactory.newRemoveAddAndSubstitution(HexNAc, Hex).generateCandidates(structure, false);

        Assert.assertEquals(0, candidates.size());
    }

    @Test
    public void testLeafSubstitution() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex).add(Fuc).build();

        List<SugarStructure> candidates = TransformationFactory.newLeafSubstitution(HexNAc, Hex).generateCandidates(structure, false);

        Assert.assertEquals(0, candidates.size());
    }

    @Test
    public void testLeafSubstitution2() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex).add(Fuc).build();

        List<SugarStructure> candidates = TransformationFactory.newLeafSubstitution(Hex, Kdn).generateCandidates(structure, false);

        Assert.assertEquals(0, candidates.size());
    }

    @Test
    public void testLeafSubstitution3() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex).add(Fuc).build();

        List<SugarStructure> candidates = TransformationFactory.newLeafSubstitution(Fuc, Kdn).generateCandidates(structure, false);

        Assert.assertEquals(1, candidates.size());
    }
}