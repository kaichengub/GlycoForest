package org.expasy.glycoforest.solver2;

import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureMultimapTest {

    @Test
    public void testAdd() throws Exception {

        final StructureMultimap<String> set = new StructureMultimap.Builder<String>(IsomorphismType.TOPOLOGY)
                .add(new SugarStructure.Builder("1", HexNAc).add(Hex).build(), "A")
                .add(new SugarStructure.Builder("2", HexNAc).add(Fuc).build(), "A")
                .add(new SugarStructure.Builder("1.1", HexNAc).add(Hex).build(), "B")
                .build();

        Assert.assertEquals(2, set.size());
        final List<StructureMultimap.Entry<String>> entries = set.stream().collect(Collectors.toList());
        Assert.assertEquals(2, entries.size());
        Assert.assertEquals(true, entries.get(0).getKey().isIsomorphic(new SugarStructure.Builder("a", HexNAc).add(Hex).build(), IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, entries.get(1).getKey().isIsomorphic(new SugarStructure.Builder("b", HexNAc).add(Fuc).build(), IsomorphismType.TOPOLOGY));

        Assert.assertArrayEquals(new String[]{"A", "B"}, entries.get(0).valuesStream().toArray(String[]::new));
        Assert.assertArrayEquals(new String[]{"A"}, entries.get(1).valuesStream().toArray(String[]::new));
    }

    @Test
    public void testCopy() throws Exception {

        final StructureMultimap<String> set = new StructureMultimap.Builder<String>(IsomorphismType.TOPOLOGY)
                .add(new SugarStructure.Builder("1", HexNAc).add(Hex).build(), "A", "B")
                .add(new SugarStructure.Builder("2", HexNAc).add(Fuc).build(), "A")
                .build();

        final StructureMultimap<String> copy = new StructureMultimap.Builder<>(set)
                .build();

        Assert.assertEquals(2, copy.size());
        final List<StructureMultimap.Entry<String>> entries = copy.stream().collect(Collectors.toList());
        Assert.assertEquals(2, entries.size());
        Assert.assertEquals(true, entries.get(0).getKey().isIsomorphic(new SugarStructure.Builder("a", HexNAc).add(Hex).build(), IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, entries.get(1).getKey().isIsomorphic(new SugarStructure.Builder("b", HexNAc).add(Fuc).build(), IsomorphismType.TOPOLOGY));

        Assert.assertArrayEquals(new String[]{"A", "B"}, entries.get(0).valuesStream().toArray(String[]::new));
        Assert.assertArrayEquals(new String[]{"A"}, entries.get(1).valuesStream().toArray(String[]::new));
    }

    @Test
    public void testUnion() throws Exception {

        final StructureMultimap<String> map1 = new StructureMultimap.Builder<String>(IsomorphismType.TOPOLOGY)
                .add(new SugarStructure.Builder("1", HexNAc).add(Hex).build(), "A")
                .add(new SugarStructure.Builder("2", HexNAc).add(Fuc).build(), "A")
                .build();

        final StructureMultimap<String> map2 = new StructureMultimap.Builder<String>(IsomorphismType.TOPOLOGY)
                .add(new SugarStructure.Builder("1", HexNAc).add(Hex).build(), "B")
                .add(new SugarStructure.Builder("2", HexNAc).add(Neu5Ac).build(), "B")
                .build();

        final StructureMultimap<String> union = StructureMultimap.union(map1, map2);

        Assert.assertEquals(3, union.size());
        final List<StructureMultimap.Entry<String>> entries = union.stream().collect(Collectors.toList());
        Assert.assertEquals(3, entries.size());
        Assert.assertEquals(true, entries.get(0).getKey().isIsomorphic(new SugarStructure.Builder("a", HexNAc).add(Hex).build(), IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, entries.get(1).getKey().isIsomorphic(new SugarStructure.Builder("b", HexNAc).add(Fuc).build(), IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, entries.get(2).getKey().isIsomorphic(new SugarStructure.Builder("c", HexNAc).add(Neu5Ac).build(), IsomorphismType.TOPOLOGY));

        Assert.assertArrayEquals(new String[]{"A", "B"}, entries.get(0).valuesStream().toArray(String[]::new));
        Assert.assertArrayEquals(new String[]{"A"}, entries.get(1).valuesStream().toArray(String[]::new));
        Assert.assertArrayEquals(new String[]{"B"}, entries.get(2).valuesStream().toArray(String[]::new));
    }

    @Test
    public void testIntersect() throws Exception {

        final StructureMultimap<String> map1 = new StructureMultimap.Builder<String>(IsomorphismType.TOPOLOGY)
                .add(new SugarStructure.Builder("1", HexNAc).add(Hex).build(), "A")
                .add(new SugarStructure.Builder("2", HexNAc).add(Fuc).build(), "A")
                .build();

        final StructureMultimap<String> map2 = new StructureMultimap.Builder<String>(IsomorphismType.TOPOLOGY)
                .add(new SugarStructure.Builder("1", HexNAc).add(Hex).build(), "B")
                .add(new SugarStructure.Builder("2", HexNAc).add(Neu5Ac).build(), "B")
                .build();

        final StructureMultimap<String> intersect = StructureMultimap.intersect(map1, map2);

        Assert.assertEquals(1, intersect.size());
        final List<StructureMultimap.Entry<String>> entries = intersect.stream().collect(Collectors.toList());
        Assert.assertEquals(1, entries.size());
        Assert.assertEquals(true, entries.get(0).getKey().isIsomorphic(new SugarStructure.Builder("a", HexNAc).add(Hex).build(), IsomorphismType.TOPOLOGY));

        Assert.assertArrayEquals(new String[]{"A", "B"}, entries.get(0).valuesStream().toArray(String[]::new));
    }

    @Test
    public void testIntersectBuilder() throws Exception {

        final StructureMultimap<String> map1 = new StructureMultimap.Builder<String>(IsomorphismType.TOPOLOGY)
                .add(new SugarStructure.Builder("1", HexNAc).add(Hex).build(), "A")
                .add(new SugarStructure.Builder("2", HexNAc).add(Fuc).build(), "A")
                .build();

        final StructureMultimap<String> map2 = new StructureMultimap.Builder<String>(IsomorphismType.TOPOLOGY)
                .add(new SugarStructure.Builder("1", HexNAc).add(Hex).build(), "B")
                .add(new SugarStructure.Builder("2", HexNAc).add(Neu5Ac).build(), "B")
                .build();

        final StructureMultimap<String> intersect = new StructureMultimap.Builder<String>(IsomorphismType.TOPOLOGY)
                .intersect(map1)
                .intersect(map2)
                .build();

        Assert.assertEquals(1, intersect.size());
        final List<StructureMultimap.Entry<String>> entries = intersect.stream().collect(Collectors.toList());
        Assert.assertEquals(1, entries.size());
        Assert.assertEquals(true, entries.get(0).getKey().isIsomorphic(new SugarStructure.Builder("a", HexNAc).add(Hex).build(), IsomorphismType.TOPOLOGY));

        Assert.assertArrayEquals(new String[]{"A", "B"}, entries.get(0).valuesStream().toArray(String[]::new));
    }
}