package org.expasy.glycoforest.avro.io;

import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.mzjava.avro.io.AbstractAvroReader;
import org.expasy.mzjava.avro.io.AvroReader;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service=AvroReader.class)
public class SugarCompositionReader extends AbstractAvroReader<SugarComposition> {

    @Override
    public Class getObjectClass() {

        return SugarComposition.class;
    }

    @Override
    public SugarComposition read(Decoder in) throws IOException {

        SugarComposition.Builder builder = new SugarComposition.Builder();

        for(long i = in.readMapStart(); i != 0; i = in.mapNext()) {
            for (long j = 0; j < i; j++) {

                builder.put(SugarUnit.valueOf(in.readString()), in.readInt());
            }
        }

        return builder.build();
    }

    @Override
    protected void createRecordFields(List<Schema.Field> fields) {

        fields.add(createSchemaField("composition", Schema.createMap(Schema.create(Schema.Type.INT))));
    }
}
