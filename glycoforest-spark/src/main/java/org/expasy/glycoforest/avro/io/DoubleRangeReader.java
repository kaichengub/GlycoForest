package org.expasy.glycoforest.avro.io;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
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
public class DoubleRangeReader extends AbstractAvroReader<Range<Double>> {

    private enum BoundType2 {OPEN, CLOSED, ABSENT}

    private final BoundType2[] enums = {BoundType2.OPEN, BoundType2.CLOSED, BoundType2.ABSENT};

    @Override
    protected String rewriteNameSpace(Class clazz) {

        return "org.expasy.glycoforest.avro.io";
    }

    @Override
    public Class getObjectClass() {

        return Range.class;
    }

    @Override
    protected void createRecordFields(List<Schema.Field> fields) {

        fields.add(createSchemaField("lowerType", createEnumSchema(BoundType2.class, enums)));
        fields.add(createSchemaField("lowerValue", Schema.create(Schema.Type.DOUBLE)));
        fields.add(createSchemaField("upperType", createEnumSchema(BoundType2.class, enums)));
        fields.add(createSchemaField("upperValue", Schema.create(Schema.Type.DOUBLE)));
    }

    @Override
    public Range<Double> read(Decoder in) throws IOException {

        final BoundType2 lowerType = enums[in.readEnum()];
        final double lowerValue = in.readDouble();
        final BoundType2 upperType = enums[in.readEnum()];
        final double upperValue = in.readDouble();

        final Range<Double> range;
        if(lowerType == BoundType2.ABSENT && upperType == BoundType2.ABSENT) {

            range = Range.all();
        } else if(lowerType == BoundType2.ABSENT) {

            range = Range.upTo(upperValue, convert(upperType));
        } else if(upperType == BoundType2.ABSENT) {

            range = Range.downTo(lowerValue, convert(lowerType));
        } else {

            range = Range.range(lowerValue, convert(lowerType), upperValue, convert(upperType));
        }

        return range;
    }

    private BoundType convert(BoundType2 type) {

        switch (type) {

            case OPEN:

                return BoundType.OPEN;
            case CLOSED:

                return BoundType.CLOSED;
            case ABSENT:
            default:

                throw new IllegalStateException("Cannot convert " + type);
        }
    }
}
