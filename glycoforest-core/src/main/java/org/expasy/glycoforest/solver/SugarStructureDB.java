package org.expasy.glycoforest.solver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.mzjava.glycomics.io.mol.glycoct.GlycoCTReader;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarStructureDB {

    private final Map<SugarComposition, List<SugarStructure>> compositionMap;
    private final Map<String, SugarStructure> labelMap;
    private int size = 0;

    private SugarStructureDB() {

        compositionMap = new HashMap<>();
        labelMap = new HashMap<>();
    }

    private SugarStructureDB(SugarStructureDB src) {

        compositionMap = new HashMap<>(src.compositionMap);
        labelMap = new HashMap<>(src.labelMap);
        size = src.size;
    }

    private void add(SugarStructure structure) {

        SugarStructure oldValue = labelMap.put(structure.getLabel(), structure);
        if(oldValue != null)
            throw new IllegalStateException(structure.getLabel() + " cannot have two structures associated with it");

        SugarComposition composition = structure.getComposition();
        List<SugarStructure> list = compositionMap.get(composition);
        if(list == null) {

            list = new ArrayList<>();
            compositionMap.put(composition, list);
        }
        list.add(structure);
        size += 1;
    }

    public int getSize() {

        return size;
    }

    public Optional<SugarStructure> get(String label) {

        return Optional.ofNullable(labelMap.get(label));
    }

    public Stream<SugarStructure> streamFor(SugarComposition composition) {

        final List<SugarStructure> sugarStructures = compositionMap.get(composition);
        return sugarStructures == null ? Stream.empty() : sugarStructures.stream();
    }

    public Stream<Map.Entry<SugarComposition, List<SugarStructure>>> stream() {

        return compositionMap.entrySet().stream();
    }

    public static class Builder {

        private SugarStructureDB sugarStructureDB;

        public Builder() {

            sugarStructureDB = new SugarStructureDB();
        }

        public Builder(SugarStructureDB src) {

            sugarStructureDB = new SugarStructureDB(src);
        }

        public Builder add(SugarStructure structure) {

            checkState();

            sugarStructureDB.add(structure);
            return this;
        }

        public Builder addGlycoCT(Reader jsonReader) throws IOException {

            checkState();

            final GlycoCTReader reader = new GlycoCTReader();
            readAndProcess(jsonReader, (dbId, glycoCt) -> sugarStructureDB.add(SugarStructure.fromGlycan(reader.read(glycoCt, dbId))));
            return this;
        }

        public Builder addNonIsomorphicGlycoCT(Reader jsonReader) throws IOException {

            checkState();

            final GlycoCTReader reader = new GlycoCTReader();
            readAndProcess(jsonReader, (dbId, glycoCt) -> {

                final SugarStructure graph;
                try {
                    graph = SugarStructure.fromGlycan(reader.read(glycoCt, dbId));
                } catch (Exception e) {
                    System.out.println("Could not read " + dbId + " for db\n" + e);                      //sout
                    return;
                }
                if (sugarStructureDB.stream().flatMap(entry -> entry.getValue().stream()).noneMatch(candidate -> graph.isIsomorphic(candidate, IsomorphismType.TOPOLOGY)))
                    sugarStructureDB.add(graph);
            });
            return this;
        }

        private void checkState() {

            if(sugarStructureDB == null)
                throw new IllegalStateException("Cannot reuse this builder");
        }

        private void readAndProcess(Reader jsonReader, BiConsumer<String, String> biConsumer) throws IOException {

            new ObjectMapper().reader(Map.class).<Map<String, String>>readValue(jsonReader).forEach(biConsumer);
        }

        public SugarStructureDB build(){

            final SugarStructureDB tmp = sugarStructureDB;
            sugarStructureDB = null;
            return tmp;
        }
    }
}
