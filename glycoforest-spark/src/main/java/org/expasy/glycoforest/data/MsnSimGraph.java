package org.expasy.glycoforest.data;

import com.google.common.base.Preconditions;
import gnu.trove.map.TObjectIntMap;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;

import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class MsnSimGraph extends DenseSimilarityGraph<MsnSpectrum> {

    private final UUID runId;

    private MsnSimGraph(UUID runId, TObjectIntMap<MsnSpectrum> vertexIndexMap, SimEdge<MsnSpectrum>[][] adjacencyList, int edgeCount) {

        super(vertexIndexMap, adjacencyList, edgeCount);

        this.runId = runId;
    }

    public UUID getRunId() {

        return runId;
    }

    public static class Builder extends AbstractBuilder<MsnSpectrum, MsnSimGraph> {

        private final UUID runId;

        public Builder(UUID runId) {

            super(true);

            Preconditions.checkNotNull(runId);
            this.runId = runId;
        }

        @Override
        protected MsnSimGraph doBuild(TObjectIntMap<MsnSpectrum> vertexIndexMap, int edgeCount, SimEdge<MsnSpectrum>[][] adjacencyList) {

            return new MsnSimGraph(runId, vertexIndexMap, adjacencyList, edgeCount);
        }

        @Override
        public boolean isReusable() {

            return false;
        }
    }
}
