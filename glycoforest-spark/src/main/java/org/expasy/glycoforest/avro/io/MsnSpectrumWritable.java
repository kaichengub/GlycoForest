package org.expasy.glycoforest.avro.io;

import org.expasy.mzjava.avro.io.MsnSpectrumReader;
import org.expasy.mzjava.avro.io.MsnSpectrumWriter;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.hadoop.io.AbstractAvroWritable;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class MsnSpectrumWritable extends AbstractAvroWritable<MsnSpectrum>{

    public MsnSpectrumWritable() {

        super(new MsnSpectrumWriter(), new MsnSpectrumReader());
    }

    public MsnSpectrumWritable(MsnSpectrum value) {

        super(value, new MsnSpectrumWriter(), new MsnSpectrumReader());
    }
}
