package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
class EdgeValueSerializer implements Serializer<EdgeValue>, Serializable {

    @Override
    public void serialize(DataOutput out, EdgeValue value) throws IOException {

        final UUID node1Id = value.getNode1Id();
        final UUID node2Id = value.getNode2Id();

        out.writeLong(node1Id.getMostSignificantBits());
        out.writeLong(node1Id.getLeastSignificantBits());
        out.writeLong(node2Id.getMostSignificantBits());
        out.writeLong(node2Id.getLeastSignificantBits());
        out.writeFloat(value.getScore());
    }

    @Override
    public EdgeValue deserialize(DataInput in, int available) throws IOException {

        return new EdgeValue(new UUID(in.readLong(), in.readLong()), new UUID(in.readLong(), in.readLong()), in.readFloat());
    }

    @Override
    public int fixedSize() {

        return 36;
    }
}
