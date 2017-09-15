package org.expasy.glycoforest.avro.io;

import com.google.common.base.Optional;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.hadoop.io.AbstractAvroWritable;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.WithinRunConsensusReader;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.WithinRunConsensusWriter;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

import java.util.Collections;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class WithinRunConsensusWritable extends AbstractAvroWritable<WithinRunConsensus> {

    public WithinRunConsensusWritable() {

        super(new WithinRunConsensusWriter(Optional.<PeakList.Precision>absent()), new WithinRunConsensusReader(Optional.<PeakList.Precision>absent(), Collections.emptyList()));
    }

    public WithinRunConsensusWritable(WithinRunConsensus value) {

        super(value, new WithinRunConsensusWriter(Optional.<PeakList.Precision>absent()), new WithinRunConsensusReader(Optional.<PeakList.Precision>absent(), Collections.emptyList()));
    }
}
