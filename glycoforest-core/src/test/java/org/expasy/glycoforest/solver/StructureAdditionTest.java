package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.glycoforest.mol.SugarExtension;
import org.expasy.glycoforest.mol.SugarStructure;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureAdditionTest {

    @Test
    public void testGenerateCandidatesNoDuplicates() throws Exception {

        StructureAddition addition = new StructureAddition("+Hex", Collections.singleton(new SugarExtension.Builder("+Hex", SugarUnit.Hex).setOut().build()),
                (v, g) -> !g.getRoot().equals(v), (p, c, g) -> false
        );
        Assert.assertEquals(162.05, addition.massShift(), 0.01);

        SugarStructure sugar = new SugarStructure.
                Builder("", SugarUnit.HexNAc)
                .branch()
                .add(SugarUnit.HexNAc)
                .pop()
                .add(SugarUnit.HexNAc)
                .build();

        List<SugarStructure> extensions = addition.generateCandidates(sugar, false);

        Assert.assertEquals(1, extensions.size());
        Assert.assertEquals(true, extensions.get(0).isIsomorphic(new SugarStructure.
                        Builder("", SugarUnit.HexNAc)
                        .branch()
                        .add(SugarUnit.HexNAc).add(SugarUnit.Hex)
                        .pop()
                        .add(SugarUnit.HexNAc)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
    }

    @Test
    public void testGenerateCandidatesAllowDuplicates() throws Exception {

        StructureAddition addition = new StructureAddition("+Hex", Collections.singleton(new SugarExtension.Builder("+Hex", SugarUnit.Hex).setOut().build()),
                (v, g) -> !g.getRoot().equals(v), (p, c, g) -> false
        );

        SugarStructure sugar = new SugarStructure.
                Builder("", SugarUnit.HexNAc)
                .branch()
                .add(SugarUnit.HexNAc)
                .pop()
                .add(SugarUnit.HexNAc)
                .build();

        List<SugarStructure> extensions = addition.generateCandidates(sugar, true);

        Assert.assertEquals(2, extensions.size());
        Assert.assertEquals(true, extensions.get(0).isIsomorphic(new SugarStructure.
                        Builder("", SugarUnit.HexNAc)
                        .branch()
                        .add(SugarUnit.HexNAc).add(SugarUnit.Hex)
                        .pop()
                        .add(SugarUnit.HexNAc)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
        Assert.assertEquals(true, extensions.get(1).isIsomorphic(new SugarStructure.
                        Builder("", SugarUnit.HexNAc)
                        .branch()
                        .add(SugarUnit.HexNAc)
                        .pop()
                        .add(SugarUnit.HexNAc).add(SugarUnit.Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
    }

    @Test
    public void testGenerateCandidatesNoFuc() throws Exception {

        StructureAddition addition = StructureAddition.noTerminal(SugarUnit.Hex);

        SugarStructure sugar = new SugarStructure.
                Builder("", HexNAc)
                .branch()
                .add(Hex).add(HexNAc).add(Hex)
                .pop()
                .add(HexNAc)
                .build();

        List<SugarStructure> extensions = addition.generateCandidates(sugar, false);

        Assert.assertEquals(6, extensions.size());
    }

    @Test
    public void testNoInsertExtendSameTerminal() throws Exception {

        StructureAddition addition = StructureAddition.noInsertExtendSameTerminal(Neu5Ac);

        SugarStructure structure = new SugarStructure.Builder("", HexNAc).add(Neu5Ac).build();

        List<SugarStructure> candidates = addition.generateCandidates(structure, false);

        Assert.assertEquals(2, candidates.size());
        Assert.assertEquals(true, candidates.get(0).isIsomorphic(new SugarStructure.Builder("expected", HexNAc)
                        .branch().add(Neu5Ac).pop()
                        .add(Neu5Ac)
                        .build(),
                IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, candidates.get(1).isIsomorphic(new SugarStructure.Builder("expected", HexNAc).add(Neu5Ac).add(Neu5Ac)
                        .build(),
                IsomorphismType.TOPOLOGY));
    }

    @Test
    public void testNoInsertNoTerminal() throws Exception {

        StructureAddition addition = StructureAddition.noInsertNoTerminal(Neu5Ac);

        SugarStructure structure = new SugarStructure.Builder("", HexNAc).add(Hex).add(Neu5Ac).build();

        List<SugarStructure> candidates = addition.generateCandidates(structure, false);

        Assert.assertEquals(2, candidates.size());
        Assert.assertEquals(true, candidates.get(0).isIsomorphic(new SugarStructure.Builder("expected", HexNAc)
                        .branch().add(Neu5Ac).pop()
                        .add(Hex).add(Neu5Ac)
                        .build(),
                IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, candidates.get(1).isIsomorphic(new SugarStructure.Builder("expected", HexNAc).add(Hex)
                        .branch().add(Neu5Ac).pop()
                        .add(Neu5Ac)
                        .build(),
                IsomorphismType.TOPOLOGY));
    }

    @Test
    public void testNoTerminal() throws Exception {

        StructureAddition addition = StructureAddition.noTerminal(Hex);

        SugarStructure structure = new SugarStructure.Builder("", HexNAc).add(Fuc).build();

        List<SugarStructure> candidates = addition.generateCandidates(structure, false);

        Assert.assertEquals(2, candidates.size());
        Assert.assertEquals(true, candidates.get(0).isIsomorphic(new SugarStructure.Builder("", HexNAc)
                        .branch().add(Hex)
                        .pop().add(Fuc)
                        .build(),
                IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, candidates.get(1).isIsomorphic(new SugarStructure.Builder("", HexNAc).add(Hex).add(Fuc).build(), IsomorphismType.TOPOLOGY));
    }

    @Test
    public void testInsert() throws Exception {

        StructureAddition addition = StructureAddition.noTerminal(Hex);

        SugarStructure structure = new SugarStructure.Builder("f 1169b", HexNAc)
                .branch().add(Neu5Ac)
                .pop().add(Fuc)
                .build();

        final List<SugarStructure> candidates = addition.generateCandidates(structure, false);

        Assert.assertEquals(2, candidates.size());

        Assert.assertEquals(true, candidates.get(0).isIsomorphic(new SugarStructure.Builder("0", HexNAc)
                        .branch().add(Hex).add(Neu5Ac)
                        .pop().add(Fuc)
                        .build(),
                IsomorphismType.TOPOLOGY));

        Assert.assertEquals(true, candidates.get(1).isIsomorphic(new SugarStructure.Builder("1", HexNAc)
                        .branch().add(Neu5Ac)
                        .pop().add(Hex).add(Fuc)
                        .build(),
                IsomorphismType.TOPOLOGY));
    }
}