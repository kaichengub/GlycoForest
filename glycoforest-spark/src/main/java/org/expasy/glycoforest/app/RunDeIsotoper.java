package org.expasy.glycoforest.app;

import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrasim.NdpSimFunc;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.tools.glycoforest.graphdb.GraphRepository;
import org.expasy.mzjava.tools.glycoforest.graphdb.hstore.HWithinRunNode;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class RunDeIsotoper {

    private final GraphRepository graphRepository;
    private final double rtCutOff;
    private final Tolerance precursorTolerance;
    private final SimFunc<LibPeakAnnotation, LibPeakAnnotation> simFunc;

    public RunDeIsotoper(final GraphRepository graphRepository, final double rtCutOff, final Tolerance precursorTolerance) {

        this.graphRepository = graphRepository;
        this.rtCutOff = rtCutOff;
        this.precursorTolerance = precursorTolerance;
        simFunc = new NdpSimFunc<>(0, new AbsoluteTolerance(0.3));
    }

    public RunDeIsotoper(final GraphRepository graphRepository, final double rtCutOff, final Tolerance precursorTolerance, final SimFunc<LibPeakAnnotation, LibPeakAnnotation> simFunc) {

        this.graphRepository = graphRepository;
        this.rtCutOff = rtCutOff;
        this.precursorTolerance = precursorTolerance;
        this.simFunc = simFunc;
    }

    public Stream<SpectrumEntry> removeIsotopes(final Stream<SpectrumEntry> spectrumEntryStream) {

        final List<IsotopeNode> isotopeNodes = spectrumEntryStream
                .map(se -> new IsotopeNode(se, graphRepository))
                .collect(Collectors.toList());

        //Build a graph that connects all structure vertexes that are isotopes
        final UndirectedGraph<IsotopeNode, Object> isotopeGraph = new SimpleGraph<>((sourceVertex, targetVertex) -> new Object());
        final int size = isotopeNodes.size();
        for (int i = 0; i < size; i++) {

            final IsotopeNode vertex1 = isotopeNodes.get(i);
            isotopeGraph.addVertex(vertex1);
            for (int j = i + 1; j < size; j++) {

                final IsotopeNode vertex2 = isotopeNodes.get(j);

                isotopeGraph.addVertex(vertex2);
                if (isIsotope(vertex1, vertex2)) {

                    isotopeGraph.addEdge(vertex1, vertex2);
                }
            }
        }

        final ConnectivityInspector<IsotopeNode, Object> connectivityInspector = new ConnectivityInspector<>(isotopeGraph);
        final Stream<SpectrumEntry> resultStream = connectivityInspector.connectedSets().stream()
                .flatMap(connectedSet -> {

                    //Build a graph of where all nodes that are within tolerance are connected.
                    final UndirectedGraph<IsotopeNode, Object> mzGraph = new SimpleGraph<>((sourceVertex, targetVertex) -> new Object());
                    final List<IsotopeNode> connectedList = new ArrayList<>(connectedSet);
                    for (int i = 0; i < connectedList.size(); i++) {

                        final IsotopeNode vertex1 = connectedList.get(i);
                        mzGraph.addVertex(vertex1);
                        for (int j = i + 1; j < connectedList.size(); j++) {

                            final IsotopeNode vertex2 = connectedList.get(j);
                            mzGraph.addVertex(vertex2);
                            if (precursorTolerance.withinTolerance(vertex1.getProcessedPeakList().getPrecursor().getMz(), vertex2.getProcessedPeakList().getPrecursor().getMz())) {
                                mzGraph.addEdge(vertex1, vertex2);
                            }
                        }
                    }

                    //Find set of all spectra that have the "same" m/z
                    final List<Set<IsotopeNode>> mzSets = new ConnectivityInspector<>(mzGraph).connectedSets();
                    final List<Set<IsotopeNode>> isotopeClusters = mzSets.stream()
                            .map(set -> new Tuple2<>(set.stream().mapToDouble(result -> result.getProcessedPeakList().getPrecursor().getMz()).average().orElse(0), set))
                            .sorted((t1, t2) -> t1._1().compareTo(t2._1()))
                            .map(Tuple2::_2)
                            .collect(Collectors.toList());

                    //If there are three masses, for example 715, 716 and 717 then only the first gets retained. Not sure if this is the desired behaviour
                    if (isotopeClusters.size() >= 1) {

                        final List<IsotopeNode> removedAnnotations = new ArrayList<>();
                        for (int i = 1; i < isotopeClusters.size(); i++) {

                            isotopeClusters.get(i).stream()
                                    .filter(in -> in.spectrumEntry.isAnnotated())
                                    .forEach(removedAnnotations::add);
                        }

                        if (removedAnnotations.isEmpty()) {

                            return isotopeClusters.get(0).stream();
                        } else {

                            return Stream.concat(isotopeClusters.get(0).stream(), removedAnnotations.stream());
                        }
                    } else {

                        throw new IllegalStateException("A isotope cluster that is empty makes no sense");
                    }
                })
                .map(n -> n.spectrumEntry);

        return resultStream;
    }

    private boolean isIsotope(final IsotopeNode vertex1, final IsotopeNode vertex2) {

        if (vertex1.getPrecursorCharge() != vertex2.getPrecursorCharge())
            return false;

        final double isotopeDelta = 1 / vertex1.getPrecursorCharge();
        if (precursorTolerance.withinTolerance(vertex1.getPrecursorMz(), vertex2.getPrecursorMz() - isotopeDelta)) {

            if (simFunc.calcSimilarity(vertex1.getProcessedPeakList(), vertex2.getProcessedPeakList()) < 0.5)
                return false;

            for (double rt1 : vertex1.getRetentionTimes()) {

                for (double rt2 : vertex2.getRetentionTimes()) {

                    double delta = rt2 - rt1;
                    if (delta > 0 && delta < rtCutOff)
                        return true;
                }
            }
        }

        return false;
    }

    private static class IsotopeNode {

        private final SpectrumEntry spectrumEntry;
        private final List<MsnSpectrum> msnSpectra;

        public IsotopeNode(final SpectrumEntry spectrumEntry, final GraphRepository graphRepository) {

            this.spectrumEntry = spectrumEntry;

            msnSpectra = graphRepository.loadChildren(new HWithinRunNode(spectrumEntry.getRawSpectrum()), new ArrayList<>()).stream()
                    .map(graphRepository::getSpectrum)
                    .collect(Collectors.toList());
        }

        public int getPrecursorCharge() {

            return spectrumEntry.getCharge();
        }

        public double getPrecursorMz() {

            return spectrumEntry.getMz();
        }

        public double[] getRetentionTimes() {

            return msnSpectra.stream().mapToDouble(msnSpectrum -> msnSpectrum.getRetentionTimes().getFirst().getTime()).toArray();
        }

        public PeakList<LibPeakAnnotation> getProcessedPeakList() {

            return spectrumEntry.getProcessedSpectrum();
        }
    }
}
