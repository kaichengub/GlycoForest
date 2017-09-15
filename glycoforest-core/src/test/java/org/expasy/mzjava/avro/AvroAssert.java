package org.expasy.mzjava.avro;

import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.internal.Diff;
import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.expasy.mzjava.avro.io.AvroExternalizable;
import org.expasy.mzjava.avro.io.AvroReader;
import org.expasy.mzjava.avro.io.AvroWriter;
import org.junit.Assert;
import org.openide.util.Lookup;

import java.io.IOException;

import static net.javacrumbs.jsonunit.core.internal.Diff.create;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class AvroAssert {

    private AvroAssert() {

    }

    static public void assertSchema(String expected, AvroExternalizable schemaBuilder) {

        String actual = schemaBuilder.createSchema().toString(true);

        Schema expectedSchema;
        try {
            expectedSchema = new Schema.Parser().parse(expected);
        } catch (Exception e) {

            Assert.assertEquals(expected, actual);
            return;
        }

        Assert.assertEquals(expectedSchema.toString(true), new Schema.Parser().parse(actual).toString(true));
    }

    private static <O> String serialize(AvroWriter<O> writer, O object) throws IOException {

        java.io.StringWriter out = new java.io.StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        gen.setPrettyPrinter(new DefaultPrettyPrinter());
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);
        writer.write(object, encoder);
        encoder.flush();

        return out.toString().replace("\r", "");
    }

    static public <O> O deSerialize(String json, AvroReader<O> reader) throws IOException {

        return reader.read(DecoderFactory.get().jsonDecoder(reader.createSchema(), json));
    }

    static public <O> void assertRoundTrip(String expectedJson, O object) throws IOException {

        assertRoundTrip(expectedJson, object, Configuration.empty(), (o1, o2) -> EqualsBuilder.reflectionEquals(o1, o2));
    }

    static public <O> void assertRoundTrip(String expectedJson, O object, Configuration configuration, EqualsTester<O> equalsTester) throws IOException {

        Lookup lookup = Lookup.getDefault();

        AvroReader<O> reader = null;
        Class<?> objectClass = object.getClass();
        for (AvroReader objectReader : lookup.lookupAll(AvroReader.class)) {

            if (objectClass.equals(objectReader.getObjectClass())) {
                //noinspection unchecked
                reader = objectReader;
            }
        }

        AvroWriter<O> writer = null;
        for (AvroWriter objectWriter : lookup.lookupAll(AvroWriter.class)) {

            if (objectClass.equals(objectWriter.getObjectClass())) {

                //noinspection unchecked
                writer = objectWriter;
            }
        }

        Assert.assertNotNull("Could not find a reader for " + objectClass, reader);
        Assert.assertNotNull("Could not find a writer for " + objectClass, writer);

        assertRoundTrip(writer, expectedJson, reader, object, configuration, equalsTester);
    }

    static public <O> void assertRoundTrip(AvroWriter<O> writer, String expectedJson, AvroReader<O> reader, O object) throws IOException {

        assertRoundTrip(writer, expectedJson, reader, object, Configuration.empty(), (o1, o2) -> EqualsBuilder.reflectionEquals(o1, o2));
    }

    static public <O> void assertRoundTrip(AvroWriter<O> writer, String expectedJson, AvroReader<O> reader, O object, Configuration configuration, EqualsTester<O> equalsTester) throws IOException {

        String json = serialize(writer, object);
        Diff diff = create(expectedJson, json, "fullJson", "", configuration);
        if (!diff.similar()) {

            Assert.assertEquals(diff.toString(), expectedJson, json);
        }

        O readObject = deSerialize(json, reader);

        Assert.assertTrue("written and read objects are not the same", equalsTester.isEquals(object, readObject));
    }

}
