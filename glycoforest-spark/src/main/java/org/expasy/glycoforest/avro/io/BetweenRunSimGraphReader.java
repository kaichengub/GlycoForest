package org.expasy.glycoforest.avro.io;

import com.google.common.base.Optional;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.expasy.glycoforest.data.BetweenRunSimGraph;
import org.expasy.mzjava.avro.io.AvroReader;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.BetweenRunConsensusReader;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service=AvroReader.class)
public class BetweenRunSimGraphReader extends DenseSimilarityGraphReader<BetweenRunConsensus, BetweenRunSimGraph> {

    public BetweenRunSimGraphReader() {

        super(new BetweenRunConsensusReader(Optional.absent(), Collections.emptyList()));
    }

    @Override
    public Class getObjectClass() {

        return BetweenRunSimGraph.class;
    }

    @Override
    protected void createRecordFields(List<Schema.Field> fields) {

        createGraphRecordFields(fields);
    }

    @Override
    public BetweenRunSimGraph read(Decoder in) throws IOException {

        final BetweenRunSimGraph.Builder builder = new BetweenRunSimGraph.Builder();
        readGraph(in, builder);
        return builder.build();
    }
}
