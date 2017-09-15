package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FEdgeKeyWritable implements WritableComparable<FEdgeKeyWritable> {

    private float mz;
    private EdgeType edgeType;
    private Optional<UUID> runIdOpt;

    public FEdgeKeyWritable() {}

    public FEdgeKeyWritable(float mz, EdgeType edgeType, Optional<UUID> runIdOpt) {

        setValues(mz, edgeType, runIdOpt);
    }

    public float getMz() {

        return mz;
    }

    public EdgeType getEdgeType() {

        return edgeType;
    }

    public Optional<UUID> getRunId() {

        return runIdOpt;
    }

    public void setValues(float mz, EdgeType edgeType, Optional<UUID> runId) {

        this.mz = mz;
        this.edgeType = edgeType;
        this.runIdOpt = runId;
    }

    @Override
    public int compareTo(FEdgeKeyWritable other) {

        return Float.compare(mz, other.mz);
    }

    @Override
    public void write(DataOutput out) throws IOException {

        out.writeFloat(mz);
        out.writeByte(edgeType.ordinal());
        final boolean present = runIdOpt.isPresent();
        out.writeBoolean(present);
        if (present) {

            UUID runId = runIdOpt.get();
            out.writeLong(runId.getMostSignificantBits());
            out.writeLong(runId.getLeastSignificantBits());
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {

        mz = in.readFloat();
        edgeType = EdgeType.values()[in.readByte()];
        if (in.readBoolean()) {
            runIdOpt = Optional.of(new UUID(in.readLong(), in.readLong()));
        } else {
            runIdOpt = Optional.empty();
        }
    }
}
