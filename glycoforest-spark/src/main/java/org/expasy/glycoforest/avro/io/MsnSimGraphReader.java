package org.expasy.glycoforest.avro.io;

import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.expasy.glycoforest.data.MsnSimGraph;
import org.expasy.mzjava.avro.io.AvroReader;
import org.expasy.mzjava.avro.io.MsnSpectrumReader;
import org.expasy.mzjava.avro.io.UUIDReader;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service=AvroReader.class)
public class MsnSimGraphReader extends DenseSimilarityGraphReader<MsnSpectrum, MsnSimGraph> {

    private final UUIDReader uuidReader = new UUIDReader();

    public MsnSimGraphReader() {

        super(new MsnSpectrumReader());
    }

    @Override
    public Class getObjectClass() {

        return MsnSimGraph.class;
    }

    @Override
    protected void createRecordFields(List<Schema.Field> fields) {

        fields.add(createSchemaField("runId", uuidReader.createSchema()));
        createGraphRecordFields(fields);
    }

    @Override
    public MsnSimGraph read(Decoder in) throws IOException {

        UUID runId = uuidReader.read(in);

        MsnSimGraph.Builder builder = new MsnSimGraph.Builder(runId);
        readGraph(in, builder);
        return builder.build();
    }
}
