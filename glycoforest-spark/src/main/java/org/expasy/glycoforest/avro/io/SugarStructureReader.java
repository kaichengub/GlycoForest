package org.expasy.glycoforest.avro.io;

import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.parser.GigCondensedReader;
import org.expasy.mzjava.avro.io.AbstractAvroReader;
import org.expasy.mzjava.avro.io.AvroReader;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service = AvroReader.class)
public class SugarStructureReader extends AbstractAvroReader<SugarStructure> {

    private final GigCondensedReader structureReader = new GigCondensedReader();

    @Override
    public Class getObjectClass() {

        return SugarStructure.class;
    }

    @Override
    public SugarStructure read(final Decoder in) throws IOException {

        return structureReader.readStructure(in.readString(), in.readString());
    }


    @Override
    protected void createRecordFields(final List<Schema.Field> fields) {

        fields.add(createSchemaField("label", Schema.create(Schema.Type.STRING)));
        fields.add(createSchemaField("structure", Schema.create(Schema.Type.STRING)));
    }
}
