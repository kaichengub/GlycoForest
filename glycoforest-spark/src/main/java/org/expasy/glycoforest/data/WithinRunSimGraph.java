package org.expasy.glycoforest.data;

import gnu.trove.map.TObjectIntMap;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class WithinRunSimGraph extends DenseSimilarityGraph<WithinRunConsensus> {

    private WithinRunSimGraph(TObjectIntMap<WithinRunConsensus> vertexIndexMap, SimEdge<WithinRunConsensus>[][] adjacencyList, int edgeCount) {

        super(vertexIndexMap, adjacencyList, edgeCount);
    }

    public static class Builder extends AbstractBuilder<WithinRunConsensus, WithinRunSimGraph> {

        public Builder() {

            super(true);
        }

        @Override
        protected WithinRunSimGraph doBuild(TObjectIntMap<WithinRunConsensus> vertexIndexMap, int edgeCount, SimEdge<WithinRunConsensus>[][] adjacencyList) {

            return new WithinRunSimGraph(vertexIndexMap, adjacencyList, edgeCount);
        }

        @Override
        public boolean isReusable() {

            return false;
        }
    }
}
