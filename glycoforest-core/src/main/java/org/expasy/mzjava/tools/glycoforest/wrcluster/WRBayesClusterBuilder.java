package org.expasy.mzjava.tools.glycoforest.wrcluster;

import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.tools.glycoforest.bayescluster.AbstractBayesClusterBuilder;
import org.expasy.mzjava.tools.glycoforest.bayescluster.Cluster;
import org.expasy.mzjava.tools.glycoforest.bayescluster.UpdatableDistribution;
import org.expasy.mzjava.tools.glycoforest.bayescluster.UpdatableNormal;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

import java.util.Collection;
import java.util.Set;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class WRBayesClusterBuilder extends AbstractBayesClusterBuilder<WithinRunConsensus, Cluster<WithinRunConsensus>, WRClusterTopology> {

        public WRBayesClusterBuilder(UpdatableNormal clusterPrior, UpdatableNormal edgePrior, double edgeJoinThreshold) {

            super(clusterPrior, edgePrior, edgeJoinThreshold);
        }

        @Override
        protected WRClusterTopology newClusterTopology(Collection<Set<WithinRunConsensus>> startingClusters, SimilarityGraph<WithinRunConsensus> graph,
                UpdatableDistribution clusterPrior, UpdatableDistribution edgePrior) {

            return new WRClusterTopology(startingClusters, graph, clusterPrior, edgePrior);
        }

        @Override
        protected WRClusterTopology newClusterTopology(SimilarityGraph<WithinRunConsensus> graph,
                UpdatableDistribution clusterPrior, UpdatableDistribution edgePrior) {

            return new WRClusterTopology(graph, clusterPrior, edgePrior);
        }
}
