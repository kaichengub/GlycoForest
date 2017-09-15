package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.BetweenRunConsensusValue;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

/**
* @author Oliver Horlacher
* @version sqrt -1
*/
public class BetweenRunConsensusSerializer implements Serializer<BetweenRunConsensus>, Serializable {

    private transient BetweenRunConsensusValue spectrumValue;

    public BetweenRunConsensusSerializer() {
    }

    @Override
    public void serialize(DataOutput dataOutput, BetweenRunConsensus spectrum) throws IOException {

        if(spectrumValue == null)
            spectrumValue = new BetweenRunConsensusValue();

        spectrumValue.set(spectrum);
        spectrumValue.write(dataOutput);
    }

    @Override
    public BetweenRunConsensus deserialize(DataInput dataInput, int available) throws IOException {

        if(spectrumValue == null)
            spectrumValue = new BetweenRunConsensusValue();

        spectrumValue.readFields(dataInput);
        return spectrumValue.get();
    }

    @Override
    public int fixedSize() {

        return -1;
    }
}
