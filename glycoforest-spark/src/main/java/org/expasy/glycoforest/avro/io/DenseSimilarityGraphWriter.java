package org.expasy.glycoforest.avro.io;

import com.google.common.base.Optional;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.expasy.mzjava.avro.io.AbstractAvroWriter;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public abstract class DenseSimilarityGraphWriter<V, G extends SimilarityGraph<V>> extends AbstractAvroWriter<G> {

    private final AbstractAvroWriter<V> vertexWriter;

    public DenseSimilarityGraphWriter(AbstractAvroWriter<V> vertexWriter) {

        this.vertexWriter = vertexWriter;
    }

    protected void createGraphRecordFields(List<Schema.Field> fields) {

        fields.add(createSchemaField("members", Schema.createArray(vertexWriter.createSchema())));
        fields.add(createSchemaField("scores", Schema.createArray(Schema.create(Schema.Type.FLOAT))));
    }

    protected void writeGraph(G graph, Encoder out) throws IOException {

        List<V> queries = new ArrayList<>(graph.getVertexCount());
        graph.forEachVertex(queries::add);
        int n = queries.size();

        out.writeArrayStart();
        out.setItemCount(n);
        for (V vertex : queries) {

            out.startItem();
            vertexWriter.write(vertex, out);
        }
        out.writeArrayEnd();

        out.writeArrayStart();
        out.setItemCount((n * (n - 1)) / 2);
        for (int i = 0; i < queries.size(); i++) {

            V vertex1 = queries.get(i);
            for (int j = i + 1; j < queries.size(); j++) {

                out.startItem();
                V vertex2 = queries.get(j);
                Optional<SimEdge<V>> edge = graph.findEdge(vertex1, vertex2);
                float score = edge.isPresent() ? (float)edge.get().getScore() : 0.0f;
                out.writeFloat(score);
            }
        }
        out.writeArrayEnd();
    }
}
