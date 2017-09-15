package org.expasy.glycoforest.avro.io;

import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.writer.GigCondensedWriter;
import org.expasy.mzjava.avro.io.AbstractAvroWriter;
import org.expasy.mzjava.avro.io.AvroWriter;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service = AvroWriter.class)
public class SugarStructureWriter extends AbstractAvroWriter<SugarStructure> {

    private final GigCondensedWriter structureWriter = new GigCondensedWriter();

    @Override
    public Class getObjectClass() {

        return SugarStructure.class;
    }

    @Override
    public void write(final SugarStructure value, final Encoder out) throws IOException {

        out.writeString(value.getLabel());
        out.writeString(structureWriter.write(value));
    }

    @Override
    protected void createRecordFields(final List<Schema.Field> fields) {

        fields.add(createSchemaField("label", Schema.create(Schema.Type.STRING)));
        fields.add(createSchemaField("structure", Schema.create(Schema.Type.STRING)));
    }
}