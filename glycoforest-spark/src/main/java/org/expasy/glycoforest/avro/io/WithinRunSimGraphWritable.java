package org.expasy.glycoforest.avro.io;

import org.expasy.glycoforest.data.WithinRunSimGraph;
import org.expasy.mzjava.hadoop.io.AbstractAvroWritable;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class WithinRunSimGraphWritable extends AbstractAvroWritable<WithinRunSimGraph> {

    public WithinRunSimGraphWritable() {

        super(new WithinRunSimGraphWriter(), new WithinRunSimGraphReader());
    }

    public WithinRunSimGraphWritable(WithinRunSimGraph value) {

        super(value, new WithinRunSimGraphWriter(), new WithinRunSimGraphReader());
    }
}
