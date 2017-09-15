package org.expasy.glycoforest.avro.io;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.utils.function.Procedure;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class DenseSimilarityGraphWriterTest {

    @Test
    public void testWriteGraph() throws Exception {

        MockSimGraphWriter writer = new MockSimGraphWriter();

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        gen.setPrettyPrinter(new DefaultPrettyPrinter());
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        final SugarComposition v1 = new SugarComposition(SugarUnit.Hex);
        final SugarComposition v2 = new SugarComposition(SugarUnit.HexNAc);
        final SugarComposition v3 = new SugarComposition(SugarUnit.Fuc);
        final SugarComposition v4 = new SugarComposition(SugarUnit.Kdn);

        //noinspection unchecked
        ArrayList<SimEdge<SugarComposition>> edgeList = Lists.newArrayList(
                new SimEdge<>(v1, v2, 0.8),
                new SimEdge<>(v2, v3, 0.7),
                new SimEdge<>(v3, v4, 0.6)
        );
        SimilarityGraph<SugarComposition> graph = new MockSimGraph<>(Lists.newArrayList(v1, v2, v3, v4), edgeList);

        writer.write(graph, encoder);
        encoder.flush();

        Assert.assertEquals("{\n" +
                "  \"members\" : [ {\n" +
                "    \"composition\" : {\n" +
                "      \"Hex\" : 1\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"composition\" : {\n" +
                "      \"HexNAc\" : 1\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"composition\" : {\n" +
                "      \"Fuc\" : 1\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"composition\" : {\n" +
                "      \"Kdn\" : 1\n" +
                "    }\n" +
                "  } ],\n" +
                "  \"scores\" : [ 0.8, 0.0, 0.0, 0.7, 0.0, 0.6 ]\n" +
                "}", out.toString().replace("\r", ""));
    }

    @Test
    public void testCreateGraphRecordFields() throws Exception {

        Schema expected = new Schema.Parser().parse("{\n" +
                "  \"type\" : \"record\",\n" +
                "  \"name\" : \"DenseSimilarityGraph\",\n" +
                "  \"namespace\" : \"org.expasy.mzjava_avro.core.ms.cluster\",\n" +
                "  \"fields\" : [ {\n" +
                "    \"name\" : \"members\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"array\",\n" +
                "      \"items\" : {\n" +
                "        \"type\" : \"record\",\n" +
                "        \"name\" : \"SugarComposition\",\n" +
                "        \"namespace\" : \"org.expasy.glycoforest.mol\",\n" +
                "        \"fields\" : [ {\n" +
                "          \"name\" : \"composition\",\n" +
                "          \"type\" : {\n" +
                "            \"type\" : \"map\",\n" +
                "            \"values\" : \"int\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"name\" : \"scores\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"array\",\n" +
                "      \"items\" : \"float\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}");

        Assert.assertEquals(expected.toString(true), new MockSimGraphWriter().createSchema().toString(true));
    }

    private static class MockSimGraphWriter extends DenseSimilarityGraphWriter<SugarComposition, SimilarityGraph<SugarComposition>> {

        public MockSimGraphWriter() {

            super(new SugarCompositionWriter());
        }

        @Override
        protected void createRecordFields(List<Schema.Field> fields) {

            super.createGraphRecordFields(fields);
        }

        @Override
        public void write(SimilarityGraph<SugarComposition> value, Encoder out) throws IOException {

            writeGraph(value, out);
        }

        @Override
        public Class getObjectClass() {

            return DenseSimilarityGraph.class;
        }
    }

    private static class MockSimGraph<V> implements SimilarityGraph<V> {

        private final List<V> vertices;
        private final List<SimEdge<V>> edges;

        public MockSimGraph(List<V> vertices, List<SimEdge<V>> edges) {

            this.vertices = vertices;
            this.edges = edges;
        }

        @Override
        public int getVertexCount() {

            return vertices.size();
        }

        @Override
        public Iterable<V> getVertices() {

            return Collections.unmodifiableList(vertices);
        }

        @Override
        public void forEachVertex(Procedure<V> procedure) {

            vertices.forEach(procedure::execute);
        }

        @Override
        public Iterable<SimEdge<V>> getEdges() {

            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachEdge(Procedure<SimEdge<V>> procedure) {

            throw new UnsupportedOperationException();
        }

        @Override
        public Iterable<V> getNeighbors(V vertex) {

            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachNeighbour(V vertex, Procedure<V> procedure) {

            throw new UnsupportedOperationException();
        }

        @Override
        public Iterable<SimEdge<V>> getEdges(V vertex) {

            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachEdge(V vertex, Procedure<SimEdge<V>> procedure) {

            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SimEdge<V>> findEdge(V vertex1, V vertex2) {

            for (SimEdge<V> edge : edges) {

                if (edge.contains(vertex1) && edge.contains(vertex2))
                    return Optional.of(edge);
            }

            return Optional.absent();
        }

        @Override
        public int degree(V vertex) {

            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsVertex(V vertex) {

            throw new UnsupportedOperationException();
        }

        @Override
        public int getEdgeCount() {

            throw new UnsupportedOperationException();
        }
    }
}