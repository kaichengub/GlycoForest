package org.expasy.glycoforest.app.data;

import com.google.common.collect.Lists;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeInterval;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ElutionAnnotationStoreTest {

    @Test
    public void test() throws Exception {

        ElutionAnnotationStore store = new ElutionAnnotationStore.Builder()
                .add(Lists.newArrayList(
                        new StructureQuantEntry.Builder("513", "SA1N1", "NeuAca2-6GalNAcol")
                                .addQuantEntry("JC_131209FMS1", new QuantEntry(16.14, 836953.2))
                                .build()))
                .buildEsiNegativeReduced(new AbsoluteTolerance(0.3), 2);

        final Set<SugarStructure> structureMap = store.getStructure("JC_131209FMS1", 513.1, new RetentionTimeInterval(16.04, 16.32, TimeUnit.MINUTE));

        Assert.assertEquals(1, structureMap.size());
    }

    @Test
    public void testDuplicateRetentionTime() throws Exception {

        ElutionAnnotationStore store = new ElutionAnnotationStore.Builder()
                .add(Lists.newArrayList(
                                new StructureQuantEntry.Builder("513", "SA1N1", "NeuAca2-6GalNAcol")
                                        .addQuantEntry("JC_131209FMS1", new QuantEntry(16.14, 836953.2))
                                        .build(),
                                new StructureQuantEntry.Builder("1016-1", "Hex1HexNAc3dHex1Sul1", "GalNAca1-3(Fuca1-2)Galb1-3(SGlcNAcb1-6)GalNAcol")
                                        .addQuantEntry("JC_131209FMS1", new QuantEntry(16.14, 12.2))
                                        .build())
                )
                .buildEsiNegativeReduced(new AbsoluteTolerance(0.3), 2);

        final Set<SugarStructure> structureMap = store.getStructure("JC_131209FMS1", 513.1, new RetentionTimeInterval(16.04, 16.32, TimeUnit.MINUTE));

        Assert.assertEquals(1, structureMap.size());
    }

    @Test
    public void testGetAllStructures() throws Exception {

        ElutionAnnotationStore store = new ElutionAnnotationStore.Builder()
                .add("a ", Lists.newArrayList(
                        new StructureQuantEntry.Builder("513", "SA1N1", "NeuAca2-6GalNAcol")
                                .addQuantEntry("JC_131209FMS1", new QuantEntry(16.14, 836953.2))
                                .build()))
                .add("a ", Lists.newArrayList(
                        new StructureQuantEntry.Builder("513-2", "SA1N1", "NeuAca2-6GalNAcol")
                                .addQuantEntry("JC_131209FMS2", new QuantEntry(17.14, 65928.3))
                                .build()))
                .buildEsiNegativeReduced(new AbsoluteTolerance(0.3), 2);

        List<SugarStructure> allStructures = store.getAllStructures();
        Assert.assertEquals(2, allStructures.size());
    }

    @Test
    public void testGetUniqueStructures() throws Exception {

        ElutionAnnotationStore store = new ElutionAnnotationStore.Builder()
                .add("a ", Lists.newArrayList(
                        new StructureQuantEntry.Builder("513-1", "SA1N1", "NeuAca2-6GalNAcol")
                                .addQuantEntry("JC_131209FMS1", new QuantEntry(16.14, 836953.2))
                                .build()))
                .add("b ", Lists.newArrayList(
                        new StructureQuantEntry.Builder("513-2", "SA1N1", "NeuAca2-6GalNAcol")
                                .addQuantEntry("JC_131209FMS2", new QuantEntry(17.14, 65928.3))
                                .build()))
                .buildEsiNegativeReduced(new AbsoluteTolerance(0.3), 2);

        Map<String, String> unique = store.getUniqueStructures().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getLabel(), entry -> entry.getValue().getLabel()));

        Map<String, String> expected = Stream.of(
                new String[]{"a 513-1", "a 513-1"},
                new String[]{"b 513-2", "a 513-1"}
        ).collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));

        Assert.assertEquals(expected, unique);
    }
}