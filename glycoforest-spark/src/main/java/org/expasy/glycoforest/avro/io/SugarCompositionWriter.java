package org.expasy.glycoforest.avro.io;

import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.mzjava.avro.io.AbstractAvroWriter;
import org.expasy.mzjava.avro.io.AvroWriter;
import org.expasy.mzjava.utils.Counter;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service=AvroWriter.class)
public class SugarCompositionWriter extends AbstractAvroWriter<SugarComposition> {

    @Override
    public Class getObjectClass() {

        return SugarComposition.class;
    }

    @Override
    protected void createRecordFields(List<Schema.Field> fields) {

        fields.add(createSchemaField("composition", Schema.createMap(Schema.create(Schema.Type.INT))));
    }

    @Override
    public void write(SugarComposition value, Encoder out) throws IOException {

        final Counter counter = new Counter();
        value.forEachEntry((sugar, count) -> {

            counter.increment();
            return true;
        });

        out.writeMapStart();
        out.setItemCount(counter.getCount());
        value.forEachEntry((sugar, count) -> {

            try {
                out.startItem();
                out.writeString(sugar.name());
                out.writeInt(count);
            } catch (IOException e) {

                throw new IllegalStateException(e);
            }
            return true;
        });
        out.writeMapEnd();
    }
}
