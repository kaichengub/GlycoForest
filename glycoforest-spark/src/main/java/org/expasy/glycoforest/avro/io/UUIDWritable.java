package org.expasy.glycoforest.avro.io;

import org.expasy.mzjava.avro.io.UUIDReader;
import org.expasy.mzjava.avro.io.UUIDWriter;
import org.expasy.mzjava.hadoop.io.AbstractAvroWritable;

import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class UUIDWritable extends AbstractAvroWritable<UUID> {

    public UUIDWritable() {

        super(new UUIDWriter(), new UUIDReader());
    }

    public UUIDWritable(UUID value) {

        super(value, new UUIDWriter(), new UUIDReader());
    }
}
