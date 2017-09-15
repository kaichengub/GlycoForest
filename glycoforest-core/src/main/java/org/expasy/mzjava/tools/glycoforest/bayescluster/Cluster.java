package org.expasy.mzjava.tools.glycoforest.bayescluster;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class Cluster<V> extends AbstractClusterObject<V> {

    protected final Set<V> vertices;

    public Cluster(Set<V> vertices, SimilarityGraph<V> simGraph, UpdatableDistribution prior) {

        super(extractSimEdges(vertices, simGraph), prior, 1.0);

        checkArgument(!vertices.isEmpty());

        this.vertices = vertices;
    }

    protected boolean isEmpty() {

        return vertices.isEmpty();
    }

    private static <V> List<SimEdge<V>> extractSimEdges(Set<V> vertices, SimilarityGraph<V> graph) {

        checkNotNull(vertices);
        checkNotNull(graph);

        int size = vertices.size();
        List<SimEdge<V>> edges = new ArrayList<>(size * (size - 1) /2 + 1);

        if (vertices.isEmpty() || vertices.size() == 1)
            return edges;

        List<V> vertexList = new ArrayList<>(vertices);
        for (int i = 0; i < vertexList.size(); i++) {

            V vertex1 = vertexList.get(i);
            for (int j = i + 1; j < vertexList.size(); j++) {

                V vertex2 = vertexList.get(j);

                Optional<SimEdge<V>> edgeOpt = graph.findEdge(vertex1, vertex2);
                final SimEdge<V> edge = edgeOpt.isPresent() ? edgeOpt.get() : new SimEdge<>(vertex1, vertex2, 0.0);
                edges.add(edge);
            }
        }
        return edges;
    }

    public void forEachVertex(Consumer<V> consumer) {

        vertices.forEach(consumer::accept);
    }

    public Iterable<? extends V> vertexIterable() {

        return Iterables.unmodifiableIterable(vertices);
    }

    @Override
    public String toString() {

        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(3);
        return format.format(getEstimatedMean()) + ", " +
                format.format(getEstimatedStandardDeviation()) + ", "  +
                format.format(getSampleMean())  + ", " +
                format.format(getSampleStandardDeviation()) + "; " + vertices.size();
    }

    public Set<V> getVertices() {

        return Collections.unmodifiableSet(vertices);
    }

    public Stream<V> getVerticeStream() {

        return vertices.stream();
    }

    public int getVertexCount() {

        return vertices.size();
    }
}

