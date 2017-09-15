package org.expasy.glycoforest.app.data;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Stream;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureStoreTest {

    @Test
    public void test() throws Exception {

        final SugarStructure s733_1 = new SugarStructure.Builder("g 733-1", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Hex).add(Fuc)
                .build();
        final SugarStructure s733_a = new SugarStructure.Builder("f 733a", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Hex).add(Fuc)
                .build();
        final SugarStructure s733_2 = new SugarStructure.Builder("g 733-2", HexNAc).add(HexNAc)
                .branch().add(Hex)
                .pop().add(Fuc)
                .build();

        StructureStore structureStore = new StructureStore.Builder()
                .add(Stream.of(s733_1, s733_a, s733_2))
                .build(GlycanMassCalculator.newEsiNegativeReduced());

        Assert.assertEquals("733-1", structureStore.getLabel("g 733-1").get().getLabel());
        Assert.assertEquals("733-1", structureStore.getLabel("f 733a").get().getLabel());
        Assert.assertEquals("733-1", structureStore.getLabel("733-1").get().getLabel());

        Assert.assertEquals("733-2", structureStore.getLabel("g 733-2").get().getLabel());
        Assert.assertEquals("733-2", structureStore.getLabel("733-2").get().getLabel());

        Assert.assertEquals(true, structureStore.getStructure("733-1").get().isIsomorphic(s733_1, IsomorphismType.ROOTED_LINKAGE));
        Assert.assertEquals(true, structureStore.getStructure("733-1").get().isIsomorphic(s733_a, IsomorphismType.ROOTED_LINKAGE));
        Assert.assertEquals(true, structureStore.getStructure("733-2").get().isIsomorphic(s733_2, IsomorphismType.ROOTED_LINKAGE));

        final StructureLabel l773_1 = structureStore.getLabel("733-1").get();
        final StructureLabel l773_2 = structureStore.getLabel("733-2").get();
        Assert.assertEquals(true, structureStore.getStructure(l773_1).get().isIsomorphic(s733_1, IsomorphismType.ROOTED_LINKAGE));
        Assert.assertEquals(true, structureStore.getStructure(l773_1).get().isIsomorphic(s733_a, IsomorphismType.ROOTED_LINKAGE));
        Assert.assertEquals(true, structureStore.getStructure(l773_2).get().isIsomorphic(s733_2, IsomorphismType.ROOTED_LINKAGE));
    }
}