package org.expasy.glycoforest.app.gig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.parser.GigCondensedReader;
import org.expasy.glycoforest.app.data.StructureLabel;
import org.expasy.glycoforest.app.data.StructureLabelComparator;
import org.expasy.glycoforest.app.data.StructureQuantEntry;
import org.expasy.glycoforest.app.data.StructureStore;
import scala.Tuple2;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureStoreBuilder {

    public static void main(String[] args) throws IOException {

        final File root = new File("C:/Users/Oliver/Documents/IdeaProjects/glycoforest-spark/glycoforest-scratch/src/main/resources/org/expasy/glycoforest/scratch/gig");

        final Map<String, String> labelStructureMap = Stream.concat(
                readStructureQuantEntries("g ", new File(root, "human_gastric/human_gastric_structures.json")),
                readStructureQuantEntries("f ", new File(root, "fish_mucin/fish_mucin_structures.json"))
        )
                .filter(tuple -> !tuple._2().contains("+"))
                .collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));

        final GigCondensedReader reader = new GigCondensedReader();
        final StructureStore structureStore = new StructureStore.Builder().add(labelStructureMap.entrySet().stream().map(entry -> reader.readStructure(entry.getKey(), entry.getValue()))).build(GlycanMassCalculator.newEsiNegativeReduced());

        List<StructureEntry> entryList = structureStore.entryStream()
                .map(Map.Entry::getKey)
                .map(label -> new StructureEntry(label.getLabel(), label.getSynonyms().stream().collect(Collectors.toMap(Function.identity(), labelStructureMap::get))))
                .sorted(new StructureEntryComparator())
                .collect(Collectors.toList());

        final OutputStream out = new BufferedOutputStream(new FileOutputStream("C:/Users/Oliver/Documents/tmp/glycoforest/structure_store.json"));
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(out, entryList);
        out.close();
    }

    private static Stream<Tuple2<String, String>> readStructureQuantEntries(String prefix, File file) throws IOException {

        final List<StructureQuantEntry> list = new ObjectMapper().readValue(file, new TypeReference<List<StructureQuantEntry>>() {
        });
        return list.stream().map(entry -> new Tuple2<>(prefix + entry.getName(), entry.getStructure()));
    }

    public static StructureStore build(File file){

        try {

            final GigCondensedReader reader = new GigCondensedReader();

            final Map<StructureLabel, SugarStructure> structureMap = new ObjectMapper().<List<StructureEntry>>readValue(file, new TypeReference<List<StructureEntry>>() {
            }).stream().collect(Collectors.toMap(StructureEntry::toLabel, entry -> entry.getStructure(reader)));

            return StructureStore.fromStream(structureMap.entrySet().stream());
        } catch (IOException e) {

            throw new IllegalStateException(e);
        }
    }

    private static class StructureEntry {

        private final String label;
        private final Map<String, String> structureMap;

        public StructureEntry(@JsonProperty("label") String label, @JsonProperty("structureMap") Map<String, String> structureMap) {

            this.label = label;
            this.structureMap = structureMap;
        }

        public String getLabel() {

            return label;
        }

        public Map<String, String> getStructureMap() {

            return structureMap;
        }

        public StructureLabel toLabel() {

            return new StructureLabel(label, structureMap.keySet().stream());
        }

        public SugarStructure getStructure(GigCondensedReader reader) {

            return reader.readStructure(label, structureMap.values().stream().findAny().get());
        }
    }

    private static class StructureEntryComparator implements Comparator<StructureEntry> {

        private StructureLabelComparator labelComparator = new StructureLabelComparator();

        @Override
        public int compare(StructureEntry e1, StructureEntry e2) {

            return labelComparator.compare(e1.getLabel(), e2.getLabel());
        }
    }
}
