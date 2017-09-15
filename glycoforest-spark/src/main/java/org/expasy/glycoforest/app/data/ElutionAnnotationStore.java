package org.expasy.glycoforest.app.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.RootedSugarEquivalenceComparator;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.parser.GigCondensedReader;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeInterval;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ElutionAnnotationStore {

    private final Map<String, NavigableMap<Double, Set<SugarStructure>>> runMap;
    private Map<String, SugarStructure> structureMap;
    private final GlycanMassCalculator massCalculator;
    private final int maxCharge;
    private final Tolerance tolerance;

    private ElutionAnnotationStore(Map<String, NavigableMap<Double, Set<SugarStructure>>> runMap, Map<String, SugarStructure> structureMap, GlycanMassCalculator massCalculator, int maxCharge, Tolerance tolerance) {

        this.runMap = runMap;
        this.structureMap = structureMap;
        this.massCalculator = massCalculator;
        this.maxCharge = maxCharge;
        this.tolerance = tolerance;
    }

    public Set<SugarStructure> getStructure(String run, double mz, RetentionTimeInterval retentionTimeInterval) {

        final NavigableMap<Double, Set<SugarStructure>> rtNavMap = runMap.get(run);
        if (rtNavMap == null)
            return Collections.emptySet();

        final NavigableMap<Double, Set<SugarStructure>> subMap = rtNavMap.subMap(retentionTimeInterval.getMinRetentionTime(), true, retentionTimeInterval.getMaxRetentionTime(), true);

        return subMap.values().stream()
                .flatMap(Collection::stream)
                .filter(structure -> {

                    for (int z = 1; z <= maxCharge; z++) {

                        if (tolerance.withinTolerance(massCalculator.calcMz(structure.getComposition(), z), mz))
                            return true;
                    }
                    return false;
                }).collect(Collectors.toSet());
    }

    public List<SugarStructure> getAllStructures() {

        return runMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().values().stream())
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public Optional<SugarStructure> getStructure(String structureLabel) {

        return Optional.ofNullable(structureMap.get(structureLabel));
    }

    public Set<String> getRuns() {

        return Collections.unmodifiableSet(runMap.keySet());
    }

    public static class Builder {

        final GigCondensedReader reader = new GigCondensedReader();
        private Map<String, NavigableMap<Double, Set<SugarStructure>>> runMap = new HashMap<>();

        public Builder add(Collection<StructureQuantEntry> entries) {

            return add("", entries);
        }

        public Builder add(String prefix, Collection<StructureQuantEntry> entries) {

            for (StructureQuantEntry structureQuantEntry : entries) {

                final SugarStructure structure = reader.readStructure(prefix + structureQuantEntry.getName(), structureQuantEntry.getStructure());
                for (Map.Entry<String, QuantEntry> entry : structureQuantEntry.getQuantMap().entrySet()) {

                    final QuantEntry quantEntry = entry.getValue();
                    if (quantEntry.getRetentionTime() > 0.0) {
                        final NavigableMap<Double, Set<SugarStructure>> currentRun = this.runMap.computeIfAbsent(entry.getKey(), (key) -> new TreeMap<>());
                        currentRun.computeIfAbsent(TimeUnit.MINUTE.convert(quantEntry.getRetentionTime(), TimeUnit.SECOND), rt -> new HashSet<>()).add(structure);
                    }
                }
            }
            return this;
        }

        public Builder add(String prefix, File file) {

            try {

                final List<StructureQuantEntry> structureEntries = new ObjectMapper().readValue(file, new TypeReference<List<StructureQuantEntry>>() {
                });
                final List<StructureQuantEntry> entries = structureEntries.stream()
                        .filter(structureEntry -> structureEntry.getStructure().indexOf('+') == -1)
                        .collect(Collectors.toList());
                add(prefix, entries);
            } catch (IOException e) {

                throw new IllegalStateException(e);
            }

            return this;
        }

        public ElutionAnnotationStore buildEsiNegativeReduced(Tolerance tolerance, int maxCharge) {

            Preconditions.checkNotNull(runMap, "The ElutionAnnotationStore builder can only be used once");

            final Map<String, NavigableMap<Double, Set<SugarStructure>>> tmp = runMap;
            runMap = null;

            final Map<String, SugarStructure> structureMap = tmp.values().stream()
                    .flatMap(map -> map.values().stream())
                    .flatMap(Collection::stream)
                    .distinct()
                    .collect(Collectors.toMap(SugarStructure::getLabel, Function.identity()));

            return new ElutionAnnotationStore(tmp, structureMap, GlycanMassCalculator.newEsiNegativeReduced(), maxCharge, tolerance);
        }
    }

    public Map<SugarStructure, SugarStructure> getUniqueStructures() {

        Map<SugarComposition, List<SugarStructure>> compositionMap = new HashMap<>();
        for (SugarStructure structure : getAllStructures()) {

            compositionMap.computeIfAbsent(structure.getComposition(), key -> new ArrayList<>()).add(structure);
        }

        final Map<SugarStructure, SugarStructure> structureMap = new HashMap<>();
        for (List<SugarStructure> structures : compositionMap.values()) {

            if (structures.size() == 1) {

                structureMap.put(structures.get(0), structures.get(0));
                continue;
            }

            while (!structures.isEmpty()) {

                final SugarStructure current = structures.remove(structures.size() - 1);
                structureMap.put(current, current);
                Iterator<SugarStructure> it = structures.iterator();
                while (it.hasNext()) {

                    final SugarStructure next = it.next();
                    if (new VF2GraphIsomorphismInspector<>(current, next, new RootedSugarEquivalenceComparator(current.getRoot(), next.getRoot()), null, false).isomorphismExists()) {

                        structureMap.put(next, current);
                        it.remove();
                    }
                }
            }
        }

        return structureMap;
    }
}
