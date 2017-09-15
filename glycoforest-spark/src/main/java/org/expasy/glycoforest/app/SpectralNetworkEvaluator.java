package org.expasy.glycoforest.app;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.app.evaluator.GsmResultList;
import org.expasy.glycoforest.app.evaluator.SolveTask;
import org.expasy.glycoforest.solver.StructureTransformation;
import org.expasy.glycoforest.solver.WithinRunStructureVertex;
import org.expasy.glycoforest.solver2.StructureMultimap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SpectralNetworkEvaluator {

    private final IsomorphismType isomorphismType;
    private final ReferenceDbQueryBuilder queryBuilder;
    private final List<SpectrumEntry> allReferences;
    private final SolveFunction solveFunction;

    private final List<Vertex> solvedVertices = new ArrayList<>();
    private final List<Vertex> unSolvedVertices = new ArrayList<>();

    public SpectralNetworkEvaluator(final IsomorphismType isomorphismType, final ReferenceDbQueryBuilder queryBuilder, final List<SpectrumEntry> allReferences, final SolveFunction solveFunction) {

        this.isomorphismType = isomorphismType;
        this.queryBuilder = queryBuilder;
        this.allReferences = allReferences;
        this.solveFunction = solveFunction;
    }

    public void add(final List<SpectrumEntry> queries, final List<SpectrumEntry> runReferences, final Predicate<SpectrumEntry> referenceSelectionPredicate) {

        for (SpectrumEntry query : queries) {

            final List<SpectrumEntry> referencesSpectra = query.isAnnotated() ? runReferences : allReferences;
            final SolveTask solveTask = queryBuilder.buildQuery(query, referencesSpectra, score -> score >= 0.1);
            final Optional<? extends GsmResultList> optResults = solveFunction.apply(solveTask).findAny();

            if (optResults.isPresent()) {

                solvedVertices.add(new Vertex(query, optResults.get(), referenceSelectionPredicate, isomorphismType));
            } else {

                unSolvedVertices.add(new Vertex(query, new GsmResultList(solveTask, Stream.empty()), referenceSelectionPredicate, isomorphismType));
            }
        }
    }

    void export(final File file) {

        final DirectedGraph<Vertex, Edge> graph = new DirectedSparseGraph<>();
        final StructureMultimap.Builder<Vertex> builder = new StructureMultimap.Builder<>(isomorphismType);
        solvedVertices.forEach(v -> builder.add(v.getResultList().getBest().getHit().getKey(), v));
        final List<Vertex> bestStructures = builder.build().stream()
                .map(e -> e.valuesStream().max(Comparator.comparing(v -> v.getSpectrumEntry().getRawSpectrum().getTotalIonCurrent())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        final List<Vertex> vertices = new ArrayList<>(unSolvedVertices);
        vertices.addAll(bestStructures);

        vertices.forEach(graph::addVertex);

        System.out.println("Have " + vertices.size() + " results");                      //sout
        final int size = vertices.size();
        final double maxMassShift = queryBuilder.getAbsMaxMassShift() + 3;
        for(int i = 0; i < size; i++) {

            final Vertex query = vertices.get(i);
            final WithinRunStructureVertex queryVertex = query.getSpectrumEntry().makeWithinRunStructureVertex();
            for(int j = i + 1; j < size; j++) {

                final Vertex reference = vertices.get(j);
                final double massShift = Math.abs(reference.getSpectrumEntry().getMz() - query.getSpectrumEntry().getMz());
                if (massShift > 7 && massShift < maxMassShift) {
                    final Optional<ReferenceDbQueryBuilder.OpenSearchTask> optSearchTask = queryBuilder.findSearchTask(query.getSpectrumEntry(), queryVertex, reference.getSpectrumEntry());
                    if(optSearchTask.isPresent()){

                        final ReferenceDbQueryBuilder.OpenSearchTask searchTask = optSearchTask.get();
                        final double score = searchTask.execute();
                        if(score > 0.1) {

                            final StructureTransformation structureTransformation = searchTask.getStructureTransformation();
                            final StructureTransformation reverseTransformation = queryBuilder.findTransformation(massShift, query.getSpectrumEntry().getCharge()).orElseThrow(IllegalStateException::new);


                            if (isOk(reference, structureTransformation) && isOk(query, reverseTransformation) && goodPairing(reference, query)) {

                                graph.addEdge(new Edge(score, structureTransformation), reference, query);
                                graph.addEdge(new Edge(score, reverseTransformation), query, reference);
                            }
                        }
                    }
                }
            }
        }

        //Only keep best edge for each transformation
        final DirectedGraph<Vertex, Edge> cleanGraph = new DirectedSparseGraph<>();
        vertices.forEach(cleanGraph::addVertex);
        for(Vertex vertex : vertices) {

            final Map<StructureTransformation, List<Edge>> edgesMap = graph.getInEdges(vertex).stream().collect(Collectors.groupingBy(Edge::getTransformation));
            for(List<Edge> edges : edgesMap.values()) {

                final Edge edge = edges.stream().max(Comparator.comparing(Edge::getScore)).orElseThrow(IllegalStateException::new);

                final Pair<Vertex> endpoints = graph.getEndpoints(edge);
                cleanGraph.addEdge(edge, endpoints.getFirst(), endpoints.getSecond());
            }
        }

        try (FileWriter fileWriter = new FileWriter(file)) {

            new SpectralNetworkGraphMLWriter().save(cleanGraph, fileWriter);
        } catch (IOException e) {

            throw new IllegalStateException(e);
        }
    }

    private boolean goodPairing(final Vertex v1, final Vertex v2) {

        if(v1.getType() == Vertex.Type.REFERENCE){

            return v2.getType() != Vertex.Type.NEW_UN_ANNOTATED;
        } else if(v2.getType() == Vertex.Type.REFERENCE){

            return v1.getType() != Vertex.Type.NEW_UN_ANNOTATED;
        } else {

            return true;
        }
    }

    private boolean isOk(final Vertex vertex, final StructureTransformation structureTransformation) {

        return !vertex.getSpectrumEntry().isAnnotated() || vertex.getSpectrumEntry().structureStream().filter(s -> !structureTransformation.generateCandidates(s, true).isEmpty()).count() > 0;
    }

    public static class Vertex {

        public enum Validation{CORRECT, WRONG, NO_RESULT, REFERENCE, NEW}
        public enum Type{REFERENCE, ANNOTATED, NEW_ANNOTATED, NEW_UN_ANNOTATED}

        private final SpectrumEntry spectrumEntry;
        private final GsmResultList resultList;
        private final Validation validation;
        private final Type type;

        public Vertex(final SpectrumEntry spectrumEntry, final GsmResultList resultList, final Predicate<SpectrumEntry> referenceSelectionPredicate, final IsomorphismType isomorphismType) {

            checkNotNull(spectrumEntry);
            checkNotNull(resultList);

            this.spectrumEntry = spectrumEntry;
            this.resultList = resultList;

            if(resultList.isEmpty()){

                validation = Validation.NO_RESULT;
            } else if(spectrumEntry.isAnnotated()) {

                validation = resultList.isBestCorrect(isomorphismType) ? Validation.CORRECT : Validation.WRONG;
            } else {

                validation = Validation.NEW;
            }

            if(referenceSelectionPredicate.test(spectrumEntry)) {

                type = Type.REFERENCE;
            } else if(spectrumEntry.isAnnotated()) {

                type = Type.ANNOTATED;
            } else {

                type = validation == Validation.NO_RESULT ? Type.NEW_UN_ANNOTATED : Type.NEW_ANNOTATED;
            }
        }

        public GsmResultList getResultList() {

            return resultList;
        }

        public SpectrumEntry getSpectrumEntry() {

            return spectrumEntry;
        }

        public Validation getValidation() {

            return validation;
        }

        public Type getType() {

            return type;
        }
    }

    public static class Edge {

        private final double score;
        private final StructureTransformation transformation;

        public Edge(final double score, final StructureTransformation transformation) {

            this.score = score;
            this.transformation = transformation;
        }

        public double getScore() {

            return score;
        }

        public StructureTransformation getTransformation() {

            return transformation;
        }
    }
}
