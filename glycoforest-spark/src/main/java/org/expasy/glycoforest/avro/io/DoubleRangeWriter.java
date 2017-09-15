package org.expasy.glycoforest.avro.io;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.expasy.mzjava.avro.io.AbstractAvroWriter;
import org.expasy.mzjava.avro.io.AvroWriter;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service=AvroWriter.class)
public class DoubleRangeWriter extends AbstractAvroWriter<Range<Double>> {

    private enum BoundType2 {OPEN, CLOSED, ABSENT}

    @Override
    public Class getObjectClass() {

        return Range.class;
    }

    @Override
    protected String rewriteNameSpace(Class clazz) {

        return "org.expasy.glycoforest.avro.io";
    }

    @Override
    protected void createRecordFields(List<Schema.Field> fields) {

        fields.add(createSchemaField("lowerType", createEnumSchema(BoundType2.class, BoundType2.OPEN, BoundType2.CLOSED, BoundType2.ABSENT)));
        fields.add(createSchemaField("lowerValue", Schema.create(Schema.Type.DOUBLE)));
        fields.add(createSchemaField("upperType", createEnumSchema(BoundType2.class, BoundType2.OPEN, BoundType2.CLOSED, BoundType2.ABSENT)));
        fields.add(createSchemaField("upperValue", Schema.create(Schema.Type.DOUBLE)));
    }

    @Override
    public void write(Range<Double> value, Encoder out) throws IOException {

        if(value.hasLowerBound()) {

            out.writeEnum(value.lowerBoundType() == BoundType.OPEN ? 0 : 1);
            out.writeDouble(value.lowerEndpoint());
        } else {

            out.writeEnum(2);
            out.writeDouble(0);
        }

        if(value.hasUpperBound()) {

            out.writeEnum(value.upperBoundType() == BoundType.OPEN ? 0 : 1);
            out.writeDouble(value.upperEndpoint());
        } else {

            out.writeEnum(2);
            out.writeDouble(0);
        }
    }
}
