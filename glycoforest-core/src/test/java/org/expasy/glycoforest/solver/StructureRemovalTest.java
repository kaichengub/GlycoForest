package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureRemovalTest {

    @Test
    public void testGenerateCandidates() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex)
                .build();

        StructureRemoval removal = new StructureRemoval(Hex);
        Assert.assertEquals("-Hex", removal.getName());
        Assert.assertEquals(-162.05282343, removal.massShift(), 0.0);

        List<SugarStructure> candidates = removal.generateCandidates(structure, false);
        Assert.assertEquals(1, candidates.size());
        Assert.assertEquals(true, candidates.get(0).isIsomorphic(new SugarStructure.Builder("expected", HexNAc).build(), IsomorphismType.TOPOLOGY));
    }

    @Test
    public void testGenerateCandidates2() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex)
                .build();

        StructureRemoval removal = new StructureRemoval(HexNAc);

        List<SugarStructure> candidates = removal.generateCandidates(structure, false);
        Assert.assertEquals(0, candidates.size());
    }

    @Test
    public void testGenerateCandidates3() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(HexNAc)
                .build();

        StructureRemoval removal = new StructureRemoval(HexNAc);

        List<SugarStructure> candidates = removal.generateCandidates(structure, false);
        Assert.assertEquals(1, candidates.size());
        Assert.assertEquals(true, candidates.get(0).isIsomorphic(new SugarStructure.Builder("expected", HexNAc).build(), IsomorphismType.TOPOLOGY));
    }

    @Test
    public void testGenerateCandidates4() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex).branch().add(Neu5Ac).pop().add(Fuc)
                .build();

        StructureRemoval removal = new StructureRemoval(Hex);

        List<SugarStructure> candidates = removal.generateCandidates(structure, false);
        Assert.assertEquals(1, candidates.size());
        Assert.assertEquals(true, candidates.get(0).isIsomorphic(new SugarStructure.Builder("expected", HexNAc).branch().add(Neu5Ac).pop().add(Fuc).build(), IsomorphismType.TOPOLOGY));
    }

    @Test
    public void testGenerateCandidates5() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex).branch().add(Neu5Ac).pop().add(Fuc)
                .build();

        StructureRemoval removal = new StructureRemoval(Hex, (v, g) -> g.outDegreeOf(v) <= 1);

        List<SugarStructure> candidates = removal.generateCandidates(structure, false);
        Assert.assertEquals(0, candidates.size());
    }
}