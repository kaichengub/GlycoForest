package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.hadoop.io.MsnSpectrumValue;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

/**
* @author Oliver Horlacher
* @version sqrt -1
*/
public class MsnSpectrumSerializer implements Serializer<MsnSpectrum>, Serializable {

    private transient MsnSpectrumValue spectrumValue;

    public MsnSpectrumSerializer() {
    }

    @Override
    public void serialize(DataOutput dataOutput, MsnSpectrum spectrum) throws IOException {

        if(spectrumValue == null)
            spectrumValue = new MsnSpectrumValue();

        spectrumValue.set(spectrum);
        spectrumValue.write(dataOutput);
    }

    @Override
    public MsnSpectrum deserialize(DataInput dataInput, int i) throws IOException {

        if(spectrumValue == null)
            spectrumValue = new MsnSpectrumValue();

        spectrumValue.readFields(dataInput);
        return spectrumValue.get();
    }

    @Override
    public int fixedSize() {

        return -1;
    }
}
