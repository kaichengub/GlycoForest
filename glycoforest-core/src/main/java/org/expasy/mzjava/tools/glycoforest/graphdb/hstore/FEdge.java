package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FEdge {

    private float mz;
    private EdgeType edgeType;
    private Optional<UUID> runIdOpt;
    private UUID node1Id;
    private UUID node2Id;
    private float score;

    public FEdge(FEdgeKeyWritable keyWritable, FEdgeValueWritable valueWritable) {

        mz = keyWritable.getMz();
        edgeType = keyWritable.getEdgeType();
        runIdOpt = keyWritable.getRunId();
        node1Id = valueWritable.getNode1Id();
        node2Id = valueWritable.getNode2Id();
        score = valueWritable.getScore();
    }

    public FEdge(float mz, EdgeType edgeType, Optional<UUID> runIdOpt, UUID node1Id, UUID node2Id, float score) {

        this.mz = mz;
        this.edgeType = edgeType;
        this.runIdOpt = runIdOpt;
        this.node1Id = node1Id;
        this.node2Id = node2Id;
        this.score = score;
    }

    public float getMz() {

        return mz;
    }

    public EdgeType getEdgeType() {

        return edgeType;
    }

    public Optional<UUID> getRunIdOpt() {

        return runIdOpt;
    }

    public UUID getNode1Id() {

        return node1Id;
    }

    public UUID getNode2Id() {

        return node2Id;
    }

    public float getScore() {

        return score;
    }

    @Override
    public String toString() {

        return "FEdge{" +
                "mz=" + mz +
                ", node1Id=" + node1Id +
                ", node2Id=" + node2Id +
                ", score=" + score +
                '}';
    }
}
