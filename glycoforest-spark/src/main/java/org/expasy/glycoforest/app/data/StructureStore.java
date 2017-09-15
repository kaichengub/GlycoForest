package org.expasy.glycoforest.app.data;

import com.google.common.collect.Sets;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.mol.SugarStructure;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureStore {

    private final Map<String, StructureLabel> synonymMap;
    private final Map<StructureLabel, SugarStructure> structureMap;

    private StructureStore(Map<String, StructureLabel> synonymMap, Map<StructureLabel, SugarStructure> structureMap) {

        this.synonymMap = synonymMap;
        this.structureMap = structureMap;
    }

    public Optional<StructureLabel> getLabel(String label) {

        return Optional.ofNullable(synonymMap.get(label));
    }

    public Optional<SugarStructure> getStructure(String label) {

        Optional<StructureLabel> structureLabel = getLabel(label);

        return structureLabel.isPresent() ? Optional.of(structureMap.get(structureLabel.get())) : Optional.empty();
    }

    public Optional<SugarStructure> getStructure(StructureLabel label) {

        return Optional.ofNullable(structureMap.get(label));
    }

    public Stream<SugarStructure> allStructures() {

        return structureMap.values().stream();
    }

    public Stream<Map.Entry<StructureLabel, SugarStructure>> entryStream(){

        return structureMap.entrySet().stream();
    }

    public static StructureStore fromStream(Stream<Map.Entry<StructureLabel, SugarStructure>> stream) {

        final Map<StructureLabel, SugarStructure> structureMap = stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        final Map<String, StructureLabel> synonymMap = new HashMap<>();
        for(StructureLabel structureLabel : structureMap.keySet()) {

            synonymMap.put(structureLabel.getLabel(), structureLabel);
            for(String synonym : structureLabel.getSynonyms()) {

                synonymMap.put(synonym, structureLabel);
            }
        }
        return new StructureStore(synonymMap, structureMap);
    }

    public static class Builder {

        final Map<SugarComposition, Set<SugarStructure>> sugarMap = new HashMap<>();

        public Builder add(Stream<SugarStructure> structureStream) {

            structureStream.forEach(structure -> sugarMap.computeIfAbsent(structure.getComposition(), key -> new HashSet<>()).add(structure));

            return this;
        }

        public StructureStore build(GlycanMassCalculator glycanMassCalculator) {

            final Map<String, StructureLabel> synonymMap = new HashMap<>();
            final Map<StructureLabel, SugarStructure> structureMap = new HashMap<>();

            for (Map.Entry<SugarComposition, Set<SugarStructure>> entry : sugarMap.entrySet()) {

                final List<SugarStructure> structureList = new ArrayList<>(entry.getValue());
                structureList.sort((s1, s2) -> s2.getLabel().compareTo(s1.getLabel()));

                final int nominalMass = glycanMassCalculator.calcNominalMass(entry.getKey(), 1);
                int count = 1;
                while (!structureList.isEmpty()) {

                    final SugarStructure seed = structureList.remove(structureList.size() - 1);
                    final Set<String> synonyms = Sets.newHashSet(seed.getLabel());
                    for (int i = structureList.size() - 1; i >= 0; i--) {

                        if (seed.isIsomorphic(structureList.get(i), IsomorphismType.ROOTED_LINKAGE)) {

                            synonyms.add(structureList.remove(i).getLabel());
                        }
                    }

                    final StructureLabel label = new StructureLabel(nominalMass + "-" + (count++), synonyms.stream());
                    synonyms.stream().forEach(synonym -> synonymMap.put(synonym, label));
                    synonymMap.put(label.getLabel(), label);

                    structureMap.put(label, seed.setLabel(label.getLabel()));
                }
            }

            return new StructureStore(synonymMap, structureMap);
        }
    }
}
