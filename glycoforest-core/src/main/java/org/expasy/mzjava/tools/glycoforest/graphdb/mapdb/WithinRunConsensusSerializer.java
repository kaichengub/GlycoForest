package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.WithinRunConsensusValue;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

/**
* @author Oliver Horlacher
* @version sqrt -1
*/
public class WithinRunConsensusSerializer implements Serializer<WithinRunConsensus>, Serializable {

    private transient WithinRunConsensusValue spectrumValue;

    public WithinRunConsensusSerializer() {
    }

    @Override
    public void serialize(DataOutput dataOutput, WithinRunConsensus spectrum) throws IOException {

        if(spectrumValue == null)
            spectrumValue = new WithinRunConsensusValue();

        spectrumValue.set(spectrum);
        spectrumValue.write(dataOutput);
    }

    @Override
    public WithinRunConsensus deserialize(DataInput dataInput, int i) throws IOException {

        if(spectrumValue == null)
            spectrumValue = new WithinRunConsensusValue();

        spectrumValue.readFields(dataInput);
        return spectrumValue.get();
    }

    @Override
    public int fixedSize() {

        return -1;
    }
}
