package org.expasy.glycoforest.glycomod;

import com.google.common.collect.Range;
import org.expasy.glycoforest.avro.io.DoubleRangeReader;
import org.expasy.glycoforest.avro.io.DoubleRangeWriter;
import org.expasy.mzjava.hadoop.io.AbstractAvroWritable;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class DoubleRangeWritable extends AbstractAvroWritable<Range<Double>> {

    public DoubleRangeWritable() {

        super(new DoubleRangeWriter(), new DoubleRangeReader());
    }

    public DoubleRangeWritable(Range<Double> value) {

        super(value, new DoubleRangeWriter(), new DoubleRangeReader());
    }
}
