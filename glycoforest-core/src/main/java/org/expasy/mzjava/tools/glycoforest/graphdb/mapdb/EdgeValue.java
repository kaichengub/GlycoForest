package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class EdgeValue {

    private UUID node1Id;
    private UUID node2Id;
    private float score;

    public EdgeValue(UUID node1Id, UUID node2Id, float score) {

        setValues(node1Id, node2Id, score);
    }

    public void setValues(UUID node1Id, UUID node2Id, float score) {

        this.node1Id = node1Id;
        this.node2Id = node2Id;
        this.score = score;
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
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeValue edgeValue = (EdgeValue) o;
        return Objects.equals(score, edgeValue.score) &&
                Objects.equals(node1Id, edgeValue.node1Id) &&
                Objects.equals(node2Id, edgeValue.node2Id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(node1Id, node2Id, score);
    }

    @Override
    public String toString() {

        return "EdgeValue{" +
                "node1Id=" + node1Id +
                ", node2Id=" + node2Id +
                ", score=" + score +
                '}';
    }
}
