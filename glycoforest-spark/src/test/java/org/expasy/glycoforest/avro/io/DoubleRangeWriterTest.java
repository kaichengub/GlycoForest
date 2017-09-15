package org.expasy.glycoforest.avro.io;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringWriter;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class DoubleRangeWriterTest {

    @Test
    public void testWriteOpen() throws Exception {

        DoubleRangeWriter writer = new DoubleRangeWriter();

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        writer.write(Range.open(732.9, 733.6), encoder);
        encoder.flush();

        Assert.assertEquals("{\"lowerType\":\"OPEN\",\"lowerValue\":732.9,\"upperType\":\"OPEN\",\"upperValue\":733.6}", out.toString());
    }

    @Test
    public void testWriteClosed() throws Exception {

        DoubleRangeWriter writer = new DoubleRangeWriter();

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        writer.write(Range.closed(732.9, 733.6), encoder);
        encoder.flush();

        Assert.assertEquals("{\"lowerType\":\"CLOSED\",\"lowerValue\":732.9,\"upperType\":\"CLOSED\",\"upperValue\":733.6}", out.toString());
    }

    @Test
    public void testWriteOpenClosed() throws Exception {

        DoubleRangeWriter writer = new DoubleRangeWriter();

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        writer.write(Range.openClosed(732.9, 733.6), encoder);
        encoder.flush();

        Assert.assertEquals("{\"lowerType\":\"OPEN\",\"lowerValue\":732.9,\"upperType\":\"CLOSED\",\"upperValue\":733.6}", out.toString());
    }

    @Test
    public void testWriteClosedOpen() throws Exception {

        DoubleRangeWriter writer = new DoubleRangeWriter();

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        writer.write(Range.closedOpen(732.9, 733.6), encoder);
        encoder.flush();

        Assert.assertEquals("{\"lowerType\":\"CLOSED\",\"lowerValue\":732.9,\"upperType\":\"OPEN\",\"upperValue\":733.6}", out.toString());
    }

    @Test
    public void testWriteGreaterThan() throws Exception {

        DoubleRangeWriter writer = new DoubleRangeWriter();

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        writer.write(Range.greaterThan(732.9), encoder);
        encoder.flush();

        Assert.assertEquals("{\"lowerType\":\"OPEN\",\"lowerValue\":732.9,\"upperType\":\"ABSENT\",\"upperValue\":0.0}", out.toString());
    }

    @Test
    public void testWriteLessThan() throws Exception {

        DoubleRangeWriter writer = new DoubleRangeWriter();

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        writer.write(Range.lessThan(732.9), encoder);
        encoder.flush();

        Assert.assertEquals("{\"lowerType\":\"ABSENT\",\"lowerValue\":0.0,\"upperType\":\"OPEN\",\"upperValue\":732.9}", out.toString());
    }

    @Test
    public void testWriteUpTo() throws Exception {

        DoubleRangeWriter writer = new DoubleRangeWriter();

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        writer.write(Range.upTo(732.9, BoundType.CLOSED), encoder);
        encoder.flush();

        Assert.assertEquals("{\"lowerType\":\"ABSENT\",\"lowerValue\":0.0,\"upperType\":\"CLOSED\",\"upperValue\":732.9}", out.toString());
    }

    @Test
    public void testWriteDownTo() throws Exception {

        DoubleRangeWriter writer = new DoubleRangeWriter();

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        writer.write(Range.downTo(732.9, BoundType.CLOSED), encoder);
        encoder.flush();

        Assert.assertEquals("{\"lowerType\":\"CLOSED\",\"lowerValue\":732.9,\"upperType\":\"ABSENT\",\"upperValue\":0.0}", out.toString());
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

        Assert.assertEquals(expected.toString(true), new DoubleRangeWriter().createSchema().toString(true));
    }
}