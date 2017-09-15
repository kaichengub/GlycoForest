package org.expasy.glycoforest.avro.io;

import org.expasy.glycoforest.data.MsnSimGraph;
import org.expasy.mzjava.hadoop.io.AbstractAvroWritable;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class MsnSimGraphWritable extends AbstractAvroWritable<MsnSimGraph> {

    public MsnSimGraphWritable() {

        super(new MsnSimGraphWriter(), new MsnSimGraphReader());
    }

    public MsnSimGraphWritable(MsnSimGraph value) {

        super(value, new MsnSimGraphWriter(), new MsnSimGraphReader());
    }
}
