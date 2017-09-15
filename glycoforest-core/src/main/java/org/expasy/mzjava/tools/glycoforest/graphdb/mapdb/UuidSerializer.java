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
public class UuidSerializer implements Serializer<UUID>, Serializable {

    @Override
    public void serialize(DataOutput out, UUID value) throws IOException {

        out.writeLong(value.getMostSignificantBits());
        out.writeLong(value.getLeastSignificantBits());
    }

    @Override
    public UUID deserialize(DataInput in, int available) throws IOException {

        return new UUID(in.readLong(), in.readLong());
    }

    @Override
    public int fixedSize() {

        return 128;
    }
}
