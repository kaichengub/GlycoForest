package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.mol.SugarUnit;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.expasy.glycoforest.mol.SugarUnit.Hex;
import static org.expasy.glycoforest.mol.SugarUnit.HexNAc;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureSubstitutionTest {

    @Test
    public void testGenerateCandidates() throws Exception {

        StructureSubstitution substitution = StructureSubstitution.singleSubstitution(Hex, HexNAc);

        SugarStructure sugar = new SugarStructure
                .Builder("", SugarUnit.HexNAc)
                .branch()
                .add(Hex)
                .pop()
                .add(Hex)
                .build();

        List<SugarStructure> candidates = substitution.generateCandidates(sugar, true);
        Assert.assertEquals(2, candidates.size());
        Assert.assertEquals(true, candidates.get(0).isIsomorphic(new SugarStructure
                        .Builder("", HexNAc)
                        .branch()
                        .add(HexNAc)
                        .pop()
                        .add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
        Assert.assertEquals(true, candidates.get(1).isIsomorphic(new SugarStructure
                        .Builder("", HexNAc)
                        .branch()
                        .add(Hex)
                        .pop()
                        .add(HexNAc)
                        .build(),
                IsomorphismType.TOPOLOGY
        ));
    }
}