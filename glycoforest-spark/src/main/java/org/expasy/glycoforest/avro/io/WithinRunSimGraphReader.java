package org.expasy.glycoforest.avro.io;

import com.google.common.base.Optional;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.expasy.glycoforest.data.WithinRunSimGraph;
import org.expasy.mzjava.avro.io.AvroReader;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.WithinRunConsensusReader;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service=AvroReader.class)
public class WithinRunSimGraphReader extends DenseSimilarityGraphReader<WithinRunConsensus, WithinRunSimGraph> {

    public WithinRunSimGraphReader() {

        super(new WithinRunConsensusReader(Optional.<PeakList.Precision>absent(), Collections.emptyList()));
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
    public WithinRunSimGraph read(Decoder in) throws IOException {

        WithinRunSimGraph.Builder builder = new WithinRunSimGraph.Builder();
        readGraph(in, builder);
        return builder.build();
    }
}
