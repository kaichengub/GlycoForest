package org.expasy.glycoforest.cluster;

import org.expasy.mzjava.core.io.IterativeReader;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.TraversalListenerAdapter;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Class for splitting an MS run into portions that have spectra where the m/z and retention time
 * are within tolerance.
 *
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class MsRunSplitter {

    private final Tolerance mzTolerance;
    //Retention time tolerance in seconds
    private final double rtTolerance;
    private final Predicate<MsnSpectrum> spectrumPredicate;

    /**
     * Construct new MzRtSplitter
     *
     * @param mzTolerance the m/z tolerance
     * @param rtTolerance the retention time tolerance in seconds
     */
    public MsRunSplitter(Tolerance mzTolerance, double rtTolerance, Predicate<MsnSpectrum> spectrumPredicate) {

        this.mzTolerance = mzTolerance;
        this.rtTolerance = rtTolerance;
        this.spectrumPredicate = spectrumPredicate;
    }

    /**
     * Split the spectra provided by the reader
     *
     * @param reader the reader
     * @return list containing lists of spectra that are within m/z and rt tolerance
     */
    public List<List<MsnSpectrum>> split(IterativeReader<MsnSpectrum> reader) {

        final UndirectedGraph<EqualsHashWrapper, DefaultEdge> graph = new SimpleGraph<>((sourceVertex, targetVertex) -> new DefaultEdge());
        final List<EqualsHashWrapper> spectra = new ArrayList<>();
        while (reader.hasNext()) {

            try {

                final MsnSpectrum spectrum = reader.next();
                if (spectrumPredicate.test(spectrum)) {

                    final EqualsHashWrapper wrapper = new EqualsHashWrapper(spectrum);
                    spectra.add(wrapper);
                    graph.addVertex(wrapper);
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        final int size = spectra.size();
        for (int i = 0; i < size; i++) {

            EqualsHashWrapper spectrum1 = spectra.get(i);
            for (int j = i + 1; j < size; j++) {

                EqualsHashWrapper spectrum2 = spectra.get(j);
                if (mzTolerance.withinTolerance(spectrum1.getMz(), spectrum2.getMz()) && withinRtTolerance(spectrum1.getRetentionTime(), spectrum2.getRetentionTime()))
                    graph.addEdge(spectrum1, spectrum2);
            }
        }

        BreadthFirstIterator<EqualsHashWrapper, DefaultEdge> it = new BreadthFirstIterator<>(graph, null);
        final ConnectedComponentTraversalListener traversalListener = new ConnectedComponentTraversalListener();
        it.addTraversalListener(traversalListener);

        while (it.hasNext()) {
            it.next();
        }

        return traversalListener.connectedLists;
    }

    private boolean withinRtTolerance(double retentionTime1, double retentionTime2) {

        return Math.abs(retentionTime1 - retentionTime2) < rtTolerance;
    }

    /**
     * A traversal listener that groups all vertices according to their
     * containing connected set.
     */
    private static class ConnectedComponentTraversalListener extends TraversalListenerAdapter<EqualsHashWrapper, DefaultEdge> {

        private List<List<MsnSpectrum>> connectedLists = new ArrayList<>();
        private List<MsnSpectrum> currentConnectedSet;

        /**
         * @see TraversalListenerAdapter#connectedComponentFinished(ConnectedComponentTraversalEvent)
         */
        @Override
        public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {

            connectedLists.add(currentConnectedSet);
        }

        /**
         * @see TraversalListenerAdapter#connectedComponentStarted(ConnectedComponentTraversalEvent)
         */
        @Override
        public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {

            currentConnectedSet = new ArrayList<>();
        }

        /**
         * @see TraversalListenerAdapter#vertexTraversed(VertexTraversalEvent)
         */
        @Override
        public void vertexTraversed(VertexTraversalEvent<EqualsHashWrapper> e) {

            MsnSpectrum v = e.getVertex().spectrum;
            currentConnectedSet.add(v);
        }
    }

    //Using this class because MzJava-1.1.0 checks every peak whenever equals or hashcode is called.
    private static class EqualsHashWrapper {

        private final MsnSpectrum spectrum;

        public EqualsHashWrapper(MsnSpectrum spectrum) {

            this.spectrum = spectrum;
        }

        @Override
        public int hashCode() {

            return spectrum.getId().hashCode();
        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final EqualsHashWrapper other = (EqualsHashWrapper) obj;
            return Objects.equals(this.spectrum.getId(), other.spectrum.getId());
        }

        public double getRetentionTime() {

            return spectrum.getRetentionTimes().getFirst().getTime();
        }

        public double getMz() {

            return spectrum.getPrecursor().getMz();
        }
    }
}
