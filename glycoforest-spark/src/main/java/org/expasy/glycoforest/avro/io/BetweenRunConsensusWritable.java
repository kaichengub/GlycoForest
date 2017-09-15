package org.expasy.glycoforest.avro.io;

import com.google.common.base.Optional;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.hadoop.io.AbstractAvroWritable;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.BetweenRunConsensusReader;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.BetweenRunConsensusWriter;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;

import java.util.Collections;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class BetweenRunConsensusWritable extends AbstractAvroWritable<BetweenRunConsensus> {

    public BetweenRunConsensusWritable() {

        super(new BetweenRunConsensusWriter(Optional.<PeakList.Precision>absent()), new BetweenRunConsensusReader(Optional.<PeakList.Precision>absent(), Collections.emptyList()));
    }

    public BetweenRunConsensusWritable(BetweenRunConsensus value) {

        super(value, new BetweenRunConsensusWriter(Optional.<PeakList.Precision>absent()), new BetweenRunConsensusReader(Optional.<PeakList.Precision>absent(), Collections.emptyList()));
    }
}
