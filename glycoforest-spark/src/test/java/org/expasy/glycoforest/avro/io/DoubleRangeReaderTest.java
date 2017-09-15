package org.expasy.glycoforest.avro.io;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class DoubleRangeReaderTest {

    @Test
    public void testReadOpen() throws Exception {

        DoubleRangeReader reader = new DoubleRangeReader();
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\"lowerType\":\"OPEN\",\"lowerValue\":732.9,\"upperType\":\"OPEN\",\"upperValue\":733.6}");

        Range<Double> range = reader.read(in);

        Assert.assertEquals(Range.open(732.9, 733.6), range);
    }

    @Test
    public void testReadClosed() throws Exception {

        DoubleRangeReader reader = new DoubleRangeReader();
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\"lowerType\":\"CLOSED\",\"lowerValue\":732.9,\"upperType\":\"CLOSED\",\"upperValue\":733.6}");

        Range<Double> range = reader.read(in);

        Assert.assertEquals(Range.closed(732.9, 733.6), range);
    }

    @Test
    public void testReadOpenClosed() throws Exception {

        DoubleRangeReader reader = new DoubleRangeReader();
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\"lowerType\":\"OPEN\",\"lowerValue\":732.9,\"upperType\":\"CLOSED\",\"upperValue\":733.6}");

        Range<Double> range = reader.read(in);

        Assert.assertEquals(Range.openClosed(732.9, 733.6), range);
    }

    @Test
    public void testReadClosedOpen() throws Exception {

        DoubleRangeReader reader = new DoubleRangeReader();
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\"lowerType\":\"CLOSED\",\"lowerValue\":732.9,\"upperType\":\"OPEN\",\"upperValue\":733.6}");

        Range<Double> range = reader.read(in);

        Assert.assertEquals(Range.closedOpen(732.9, 733.6), range);
    }

    @Test
    public void testReadGreaterThan() throws Exception {

        DoubleRangeReader reader = new DoubleRangeReader();
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\"lowerType\":\"OPEN\",\"lowerValue\":732.9,\"upperType\":\"ABSENT\",\"upperValue\":0.0}");

        Range<Double> range = reader.read(in);

        Assert.assertEquals(Range.greaterThan(732.9), range);
    }

    @Test
    public void testReadLessThan() throws Exception {

        DoubleRangeReader reader = new DoubleRangeReader();
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\"lowerType\":\"ABSENT\",\"lowerValue\":0.0,\"upperType\":\"OPEN\",\"upperValue\":732.9}");

        Range<Double> range = reader.read(in);

        Assert.assertEquals(Range.lessThan(732.9), range);
    }

    @Test
    public void testReadUpTo() throws Exception {

        DoubleRangeReader reader = new DoubleRangeReader();
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\"lowerType\":\"ABSENT\",\"lowerValue\":0.0,\"upperType\":\"CLOSED\",\"upperValue\":732.9}");

        Range<Double> range = reader.read(in);

        Assert.assertEquals(Range.upTo(732.9, BoundType.CLOSED), range);
    }

    @Test
    public void testReadDownTo() throws Exception {

        DoubleRangeReader reader = new DoubleRangeReader();
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\"lowerType\":\"CLOSED\",\"lowerValue\":732.9,\"upperType\":\"ABSENT\",\"upperValue\":0.0}");

        Range<Double> range = reader.read(in);

        Assert.assertEquals(Range.downTo(732.9, BoundType.CLOSED), range);
    }

    @Test
    public void testCreateRecordFields() throws Exception {

        Schema expected = new Schema.Parser().parse("{\n" +
                "  \"type\" : \"record\",\n" +
                "  \"name\" : \"Range\",\n" +
                "  \"namespace\" : \"org.expasy.glycoforest.avro.io\",\n" +
                "  \"fields\" : [ {\n" +
                "    \"name\" : \"lowerType\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"enum\",\n" +
                "      \"name\" : \"BoundType2\",\n" +
                "      \"symbols\" : [ \"OPEN\", \"CLOSED\", \"ABSENT\" ]\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"name\" : \"lowerValue\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"upperType\",\n" +
                "    \"type\" : \"BoundType2\"\n" +
                "  }, {\n" +
                "    \"name\" : \"upperValue\",\n" +
                "    \"type\" : \"double\"\n" +
                "  } ]\n" +
                "}");

        Assert.assertEquals(expected.toString(true), new DoubleRangeReader().createSchema().toString(true));
    }
}