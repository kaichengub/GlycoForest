package org.expasy.glycoforest.app.evaluator;

import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrasim.NdpSimFunc;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.tools.glycoforest.graphdb.BetweenRunNode;
import org.expasy.mzjava.tools.glycoforest.graphdb.GraphRepository;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UndirectedSubgraph;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ResultsDeisotoper {

    private final GraphRepository graphRepository;
    private final double rtCutOff;
    private final Tolerance precursorTolerance;
    private final SimFunc<LibPeakAnnotation, LibPeakAnnotation> simFunc;

    public ResultsDeisotoper(final GraphRepository graphRepository, final double rtCutOff, final Tolerance precursorTolerance) {

        this.graphRepository = graphRepository;
        this.rtCutOff = rtCutOff;
        this.precursorTolerance = precursorTolerance;
        simFunc = new NdpSimFunc<>(0, new AbsoluteTolerance(0.3));
    }

    public ResultsDeisotoper(final GraphRepository graphRepository, final double rtCutOff, final Tolerance precursorTolerance, final SimFunc<LibPeakAnnotation, LibPeakAnnotation> simFunc) {

        this.graphRepository = graphRepository;
        this.rtCutOff = rtCutOff;
        this.precursorTolerance = precursorTolerance;
        this.simFunc = simFunc;
    }

    public List<GsmResultList> removeIsotopes(final List<GsmResultList> resultLists) {

        final UndirectedGraph<GsmResultList, Object> isotopeGraph = new SimpleGraph<>((sourceVertex, targetVertex) -> new Object());

        resultLists.sort((rl1, rl2) -> Double.compare(rl1.getProcessedSpectrum().getPrecursor().getMz(), rl2.getProcessedSpectrum().getPrecursor().getMz()));

        final int size = resultLists.size();
        for (int i = 0; i < size; i++) {

            final GsmResultList vertex1 = resultLists.get(i);
            isotopeGraph.addVertex(vertex1);
            for (int j = i + 1; j < size; j++) {

                final GsmResultList vertex2 = resultLists.get(j);

                isotopeGraph.addVertex(vertex2);
                if(isIsotope(vertex1, vertex2)) {

                    isotopeGraph.addEdge(vertex1, vertex2);
                }
            }
        }

        final ConnectivityInspector<GsmResultList, Object> connectivityInspector = new ConnectivityInspector<>(isotopeGraph);
        return connectivityInspector.connectedSets().stream()
                .flatMap(connectedSet -> {

                    final UndirectedSubgraph<GsmResultList, Object> subgraph = new UndirectedSubgraph<>(isotopeGraph, connectedSet, null);

                    System.out.println();                      //sout
                    subgraph.edgeSet().forEach(edge -> {

                                final PeakList<LibPeakAnnotation> processedSpectrum1 = subgraph.getEdgeSource(edge).getProcessedSpectrum();
                                final PeakList<LibPeakAnnotation> processedSpectrum2 = subgraph.getEdgeTarget(edge).getProcessedSpectrum();
                                System.out.println(simFunc.calcSimilarity(processedSpectrum1, processedSpectrum2) + "\t" + processedSpectrum1.getPrecursor().getMz() + "\t" + processedSpectrum2.getPrecursor().getMz());                      //sout
                            });
                    System.out.println();                      //sout

                    final UndirectedGraph<GsmResultList, Object> mzGraph = new SimpleGraph<>((sourceVertex, targetVertex) -> new Object());
                    final List<GsmResultList> connectedList = new ArrayList<>(connectedSet);
                    for(int i = 0; i < connectedList.size(); i++) {

                        final GsmResultList vertex1 = connectedList.get(i);
                        mzGraph.addVertex(vertex1);
                        for(int j = i + 1; j <connectedList.size(); j++) {

                            final GsmResultList vertex2 = connectedList.get(j);
                            mzGraph.addVertex(vertex2);
                            if (precursorTolerance.withinTolerance(vertex1.getProcessedSpectrum().getPrecursor().getMz(), vertex2.getProcessedSpectrum().getPrecursor().getMz())) {
                                mzGraph.addEdge(vertex1, vertex2);
                            }
                        }
                    }

                    final List<Set<GsmResultList>> mzSets = new ConnectivityInspector<>(mzGraph).connectedSets();
                    final Optional<Tuple2<Double, Set<GsmResultList>>> nonIsotope = mzSets.stream()
                            .map(set -> new Tuple2<>(set.stream().mapToDouble(result -> result.getProcessedSpectrum().getPrecursor().getMz()).average().orElse(0), set))
                            .min((t1, t2) -> t1._1().compareTo(t2._1()));
                    //If there are three masses, for example 715, 716 and 717 then only the first gets retained. Not sure if this is the desired behaviour
                    System.out.println("Base isotope " + nonIsotope.get()._2().stream().mapToDouble(result -> result.getProcessedSpectrum().getPrecursor().getMz()).average().orElse(0) + "\t" + nonIsotope.get()._2().size());                      //sout

                    if (nonIsotope.isPresent()) {
                        return nonIsotope.get()._2().stream();
                    } else {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());
    }

    private boolean isIsotope(final GsmResultList vertex1, final GsmResultList vertex2) {

        if (precursorTolerance.withinTolerance(vertex1.getProcessedSpectrum().getPrecursor().getMz(), vertex2.getProcessedSpectrum().getPrecursor().getMz() - 1)) {

            if(simFunc.calcSimilarity(vertex1.getProcessedSpectrum(), vertex2.getProcessedSpectrum()) < 0.5)
                return false;

            final List<MsnSpectrum> spectra1 = getMsnSpectra((BetweenRunNode)vertex1.getSpectrumNode(), graphRepository);
            final List<MsnSpectrum> spectra2 = getMsnSpectra((BetweenRunNode)vertex2.getSpectrumNode(), graphRepository);

            for (MsnSpectrum spectrum1 : spectra1) {

                for (MsnSpectrum spectrum2 : spectra2) {

                    if (spectrum2.getRetentionTimes().getFirst().getTime() - spectrum1.getRetentionTimes().getFirst().getTime() < rtCutOff){

                        return true;
                    }
                }
            }
        }

        return false;
    }

    private List<MsnSpectrum> getMsnSpectra(final BetweenRunNode betweenRunNode, final GraphRepository graphRepository) {

        return graphRepository.loadChildren(betweenRunNode, new ArrayList<>()).stream()
                .flatMap(withinRunNode -> graphRepository.loadChildren(withinRunNode, new ArrayList<>()).stream())
                .map(graphRepository::getSpectrum)
                .collect(Collectors.toList());
    }
}
