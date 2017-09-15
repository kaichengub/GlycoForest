package org.expasy.glycoforest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.glycoforest.mol.SugarVertex;
import org.expasy.mzjava.glycomics.io.mol.glycoct.GlycoCTReader;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycanFeatureExtractor {

    public static void main(String[] args) throws IOException {

        Map<String, String> glycoctMap = new ObjectMapper()
                .reader(Map.class)
                .readValue(new FileReader("C:\\Users\\ohorlach\\Documents\\IdeaProjects\\glycoforest-spark\\glycoforest-scratch\\src\\main\\resources\\org\\expasy\\glycoforest\\scratch\\jin_structures.json"));

        GlycoCTReader reader = new GlycoCTReader();
        final List<SugarStructure> graphs = glycoctMap.entrySet().stream()
                .flatMap(entry -> {
                    try {
                        return Stream.of(reader.read(entry.getValue(), entry.getKey()));
                    } catch (IllegalStateException e) {
                        System.out.println("Could not read " + entry.getKey());                      //sout
                        return Stream.empty();
                    }
                })
                .map(SugarStructure::fromGlycan)
                .collect(Collectors.toList());
        Collections.sort(graphs, (g1, g2) -> Double.compare(Double.parseDouble(g1.getLabel().replace('-', '.')), Double.parseDouble(g2.getLabel().replace('-', '.'))));

        BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Users\\ohorlach\\Documents\\tmp\\glycoforest\\glycan_features.tsv", false));
        out.write("Id\tlinear");
        for(SugarUnit sugarUnit : SugarUnit.values()) {
            out.write("\t" + sugarUnit);
        }
        out.newLine();

        for (SugarStructure graph : graphs) {

            boolean linear = graph.outDegreeOf(graph.getRoot()) == 1;

            Map<SugarUnit, Integer> counts = graph.vertexSet().stream()
                    .filter(unit -> graph.outDegreeOf(unit) == 0)
                    .collect(Collectors.groupingBy(SugarVertex::getUnit, Collectors.summingInt(u -> 1)));

            out.write(graph.getLabel() + "\t" + linear);
            for(SugarUnit sugarUnit : SugarUnit.values()) {
                out.write("\t" + counts.getOrDefault(sugarUnit, 0));
            }
            out.newLine();
        }
        out.close();
    }
}