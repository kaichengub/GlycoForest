package org.expasy.glycoforest.data;

import gnu.trove.map.TObjectIntMap;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class BetweenRunSimGraph extends DenseSimilarityGraph<BetweenRunConsensus> {

    private BetweenRunSimGraph(TObjectIntMap<BetweenRunConsensus> vertexIndexMap, SimEdge<BetweenRunConsensus>[][] adjacencyList, int edgeCount) {

        super(vertexIndexMap, adjacencyList, edgeCount);
    }

    public static class Builder extends AbstractBuilder<BetweenRunConsensus, BetweenRunSimGraph> {

        public Builder() {

            super(true);
        }

        @Override
        protected BetweenRunSimGraph doBuild(TObjectIntMap<BetweenRunConsensus> vertexIndexMap, int edgeCount, SimEdge<BetweenRunConsensus>[][] adjacencyList) {

            return new BetweenRunSimGraph(vertexIndexMap, adjacencyList, edgeCount);
        }
    }
}
