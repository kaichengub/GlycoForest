package org.expasy.glycoforest.glycomod;

import org.expasy.glycoforest.avro.io.SugarCompositionReader;
import org.expasy.glycoforest.avro.io.SugarCompositionWriter;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.mzjava.hadoop.io.AbstractAvroWritable;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarCompositionWritable extends AbstractAvroWritable<SugarComposition> {

    public SugarCompositionWritable() {

        super(new SugarCompositionWriter(), new SugarCompositionReader());
    }

    public SugarCompositionWritable(SugarComposition value) {

        super(value, new SugarCompositionWriter(), new SugarCompositionReader());
    }
}
