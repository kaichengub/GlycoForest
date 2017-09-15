package org.expasy.mzjava.tools.glycoforest.bayescluster;

import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public abstract class AbstractClusterTopology<V, C extends Cluster<V>, CT extends AbstractClusterTopology<V, C, CT>> {

    private final SimilarityGraph<V> simGraph;
    private final UpdatableDistribution nodePrior;
    private final UpdatableDistribution edgePrior;

    protected final List<C> clusters;
    protected final List<ClusterEdge<V, C>> edgeList;
    private double sumOfLogLikelihoods;

    public AbstractClusterTopology(final SimilarityGraph<V> simGraph, final UpdatableDistribution nodePrior, final UpdatableDistribution edgePrior) {

        this.nodePrior = nodePrior;
        this.edgePrior = edgePrior;
        this.simGraph = simGraph;
        clusters = new ArrayList<>(simGraph.getVertexCount());
        edgeList = new ArrayList<>(calcNumEdges(simGraph.getVertexCount()));
    }

    public AbstractClusterTopology(final List<C> clusters, final List<ClusterEdge<V, C>> edgeList, final SimilarityGraph<V> simGraph, final UpdatableDistribution nodePrior, final UpdatableDistribution edgePrior) {

        this.nodePrior = nodePrior;
        this.edgePrior = edgePrior;
        this.simGraph = simGraph;
        this.clusters = clusters;
        this.edgeList = edgeList;

        sumOfLogLikelihoods = calcLogLikelihood();
    }

    private double calcLogLikelihood() {

        double sumOfLogs = 0;
        for (C cluster : clusters)
            sumOfLogs += cluster.getSumOfLogLikelihoods();
        for (ClusterEdge<V, C> edge : edgeList)
            sumOfLogs += edge.getSumOfLogLikelihoods();
        return sumOfLogs;
    }

    private static int calcNumEdges(int vertexCount) {

        return (vertexCount * (vertexCount - 1) / 2) + 1;
    }

    protected void buildGraph(final SimilarityGraph<V> graph) {

        graph.forEachVertex(v -> clusters.add(newCluster(Collections.singleton(v))));
        buildEdges();
        sumOfLogLikelihoods = calcLogLikelihood();
    }

    protected void buildGraph(Collection<Set<V>> initialClusters) {

        clusters.addAll(initialClusters.stream().map(this::newCluster).collect(Collectors.toList()));

        buildEdges();
        sumOfLogLikelihoods = calcLogLikelihood();
    }

    protected abstract C newCluster(Set<V> vertices);

    protected void buildEdges() {

        for (int i = 0; i < clusters.size(); i++) {

            C vertex1 = clusters.get(i);
            for (int j = i + 1; j < clusters.size(); j++) {

                final ClusterEdge<V, C> clusterEdge = new ClusterEdge<>(vertex1, clusters.get(j), getSimGraph(), edgePrior);
                edgeList.add(clusterEdge);
            }
        }
    }

    /**
     * Creates a copy of this topology where the Clusters that are connected by clusterEdge are combined.
     *
     * @param clusterEdge the edge specifying the clusters to join
     * @return the new topology
     */
    public CT combine(ClusterEdge<V, C> clusterEdge) {

        final List<C> newClusters = new ArrayList<>(clusters.size() - 1);
        final List<ClusterEdge<V, C>> newEdgeList = new ArrayList<>(calcNumEdges(newClusters.size()));

        final C cluster1 = clusterEdge.getCluster1();
        final C cluster2 = clusterEdge.getCluster2();
        //Copy the clusters that are not being combined
        for (C cluster : clusters) {

            if (!cluster.equals(cluster1) && !cluster.equals(cluster2))
                newClusters.add(cluster);
        }

        //Copy all edges that are not incident to a cluster that is being combined
        for (ClusterEdge<V, C> edge : edgeList) {

            if (!edge.contains(cluster1) && !edge.contains(cluster2))
                newEdgeList.add(edge);
        }

        //Create the new combined cluster
        Set<V> vertices = new HashSet<>(cluster1.getVertexCount() + cluster2.getVertexCount());
        vertices.addAll(cluster1.getVertices());
        vertices.addAll(cluster2.getVertices());
        C newCluster = newCluster(vertices);

        //Create the new edges
        for (C cluster : newClusters) {

            final ClusterEdge<V, C> newClusterEdge = new ClusterEdge<>(newCluster, cluster, simGraph, edgePrior);
            newEdgeList.add(newClusterEdge);
        }
        newClusters.add(newCluster);

        return newClusterTopology(newClusters, newEdgeList);
    }

    protected abstract CT newClusterTopology(List<C> clusters, List<ClusterEdge<V, C>> edgeList);

    public Collection<Set<V>> createVertexSets() {

        List<Set<V>> clusterList = new ArrayList<>(clusters.size());
        for (C current : clusters) {

            final Set<V> vertices = new HashSet<>();
            current.forEachVertex(vertices::add);
            clusterList.add(vertices);
        }
        return clusterList;
    }

    public List<ClusterEdge<V, C>> getMergeableEdges(double minEdgeMean) {

        return edgeList.stream().filter(clusterEdge -> clusterEdge.getSampleMean() >= minEdgeMean).collect(Collectors.toList());
    }

    @Override
    public String toString() {

        final StringBuilder buff = new StringBuilder();

        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(3);
        buff.append("id ,  mean, stdev,e mean,estdev, v, e");
        buff.append('\n');
        for (int i = 0; i < clusters.size(); i++) {

            final C cluster = clusters.get(i);
//            if (cluster.getVertexCount() > 1) {

            buff.append(i);
            buff.append("->");
            buff.append(", ");
            buff.append(format.format(cluster.getSampleMean()));
            buff.append(", ");
            buff.append(format.format(cluster.getSampleStandardDeviation()));
            buff.append(", ");
            buff.append(format.format(cluster.getEstimatedMean()));
            buff.append(", ");
            buff.append(format.format(cluster.getEstimatedStandardDeviation()));
            buff.append(", [");
            buff.append(cluster.getVertexCount());
            buff.append(", ");
            buff.append(cluster.getEdgeCount());
            buff.append("]");
//            buff.append(", ");
//            buff.append(cluster.getVertices());
            buff.append('\n');
//            }
        }

        if (clusters.size() > 1) {
            buff.append("id ,  mean, stdev,e mean,estdev\n");
            for(int i = 0; i < edgeList.size(); i++){

                final ClusterEdge<V, C> edge = edgeList.get(i);

                buff.append(i);
                buff.append("->");
                buff.append(", ");
                buff.append(format.format(edge.getSampleMean()));
                buff.append(", ");
                buff.append(format.format(edge.getSampleStandardDeviation()));
                buff.append(", ");
                buff.append(format.format(edge.getEstimatedMean()));
                buff.append(", ");
                buff.append(format.format(edge.getEstimatedStandardDeviation()));
                buff.append(", [");
                buff.append(edge.getEdgeCount());
                buff.append("]\n");
            }
        }

        return buff.toString();
    }

    public double getLogLikelihood() {

        return sumOfLogLikelihoods;
    }

    public int getClusterCount() {

        return clusters.size();
    }

    public SimilarityGraph<V> getSimGraph() {

        return simGraph;
    }

    public int getEdgeCount() {

        return edgeList.size();
    }

    public UpdatableDistribution getNodePrior() {

        return nodePrior;
    }

    public UpdatableDistribution getEdgePrior() {

        return edgePrior;
    }

    public double calcBIC() {

        final double logLikelihood = getLogLikelihood();

        double bic = logLikelihood - 0.5*(getClusterCount() + getEdgeCount())*Math.log(getSimGraph().getEdgeCount());

        if(Double.isInfinite(bic)){

            throw new IllegalStateException("bic is " + bic + "\n logLikelihood = " + logLikelihood + " cluster graph cluster count = " + getClusterCount() + " edge count = " + getEdgeCount() + " sim graph edge count = " + getSimGraph().getEdgeCount());
        }

        return bic;
    }
}
