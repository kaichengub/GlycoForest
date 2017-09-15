package org.expasy.mzjava.tools.glycoforest.wrcluster;

import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

import java.util.function.Predicate;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class EdgeMergePredicate implements Predicate<SimEdge<WithinRunConsensus>> {

    private final double rtThreshold;

    public EdgeMergePredicate(double rtThreshold) {

        this.rtThreshold = rtThreshold;
    }

    @Override
    public boolean test(SimEdge<WithinRunConsensus> edge) {

        final WithinRunConsensus vertex1 = edge.getVertex1();
        final WithinRunConsensus vertex2 = edge.getVertex2();

        if (vertex1.getRunId().equals(vertex2.getRunId())) {

            final double rtDelta;
            if (vertex1.getRetentionTime() < vertex2.getRetentionTime())
                rtDelta = vertex2.getMinRetentionTime() - vertex1.getMaxRetentionTime();
            else
                rtDelta = vertex1.getMinRetentionTime() - vertex2.getMaxRetentionTime();

            return rtDelta <= rtThreshold;
        } else {

            return true;
        }
    }
}
