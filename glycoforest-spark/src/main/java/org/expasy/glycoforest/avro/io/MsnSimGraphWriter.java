package org.expasy.glycoforest.avro.io;

import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.expasy.glycoforest.data.MsnSimGraph;
import org.expasy.mzjava.avro.io.AvroWriter;
import org.expasy.mzjava.avro.io.MsnSpectrumWriter;
import org.expasy.mzjava.avro.io.UUIDWriter;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service=AvroWriter.class)
public class MsnSimGraphWriter extends DenseSimilarityGraphWriter<MsnSpectrum, MsnSimGraph> {

    private final UUIDWriter uuidWriter = new UUIDWriter();

    public MsnSimGraphWriter() {

        super(new MsnSpectrumWriter());
    }

    @Override
    public Class getObjectClass() {

        return MsnSimGraph.class;
    }

    @Override
    protected void createRecordFields(List<Schema.Field> fields) {

        fields.add(createSchemaField("runId", uuidWriter.createSchema()));
        createGraphRecordFields(fields);
    }

    @Override
    public void write(MsnSimGraph graph, Encoder out) throws IOException {

        uuidWriter.write(graph.getRunId(), out);
        writeGraph(graph, out);
    }
}
