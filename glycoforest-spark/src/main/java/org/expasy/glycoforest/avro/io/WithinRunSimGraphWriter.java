package org.expasy.glycoforest.avro.io;

import com.google.common.base.Optional;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.expasy.glycoforest.data.WithinRunSimGraph;
import org.expasy.mzjava.avro.io.AvroWriter;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.WithinRunConsensusWriter;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service=AvroWriter.class)
public class WithinRunSimGraphWriter extends DenseSimilarityGraphWriter<WithinRunConsensus, WithinRunSimGraph> {

    public WithinRunSimGraphWriter() {

        super(new WithinRunConsensusWriter(Optional.<PeakList.Precision>absent()));
    }

    @Override
    public Class getObjectClass() {

        return WithinRunSimGraph.class;
    }

    @Override
    protected void createRecordFields(List<Schema.Field> fields) {

        createGraphRecordFields(fields);
    }

    @Override
    public void write(WithinRunSimGraph value, Encoder out) throws IOException {

        writeGraph(value, out);
    }
}
