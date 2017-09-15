package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FEdgeValueWritable implements Writable {

    private UUID node1Id;
    private UUID node2Id;
    private float score;

    public FEdgeValueWritable() {}

    public FEdgeValueWritable(UUID node1Id, UUID node2Id, float score) {

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

    public void setValues(UUID node1Id, UUID node2Id, float score) {

        this.node1Id = node1Id;
        this.node2Id = node2Id;
        this.score = score;
    }

    @Override
    public void write(DataOutput out) throws IOException {

        out.writeLong(node1Id.getMostSignificantBits());
        out.writeLong(node1Id.getLeastSignificantBits());
        out.writeLong(node2Id.getMostSignificantBits());
        out.writeLong(node2Id.getLeastSignificantBits());
        out.writeFloat(score);
    }

    @Override
    public void readFields(DataInput in) throws IOException {

        node1Id = new UUID(in.readLong(), in.readLong());
        node2Id = new UUID(in.readLong(), in.readLong());
        score = in.readFloat();
    }
}
