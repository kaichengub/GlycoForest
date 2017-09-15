package org.expasy.glycoforest.avro.io;

import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.expasy.mzjava.avro.io.AbstractAvroReader;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraphBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public abstract class DenseSimilarityGraphReader<V, G extends SimilarityGraph<V>> extends AbstractAvroReader<G>{

    private final AbstractAvroReader<V> vertexReader;

    public DenseSimilarityGraphReader(AbstractAvroReader<V> vertexReader) {

        this.vertexReader = vertexReader;
    }

    protected void createGraphRecordFields(List<Schema.Field> fields) {

        fields.add(createSchemaField("members", Schema.createArray(vertexReader.createSchema())));
        fields.add(createSchemaField("scores", Schema.createArray(Schema.create(Schema.Type.FLOAT))));
    }

    public void readGraph(Decoder in, SimilarityGraphBuilder<V, G> builder) throws IOException {

        long block1 = in.readArrayStart();
        List<V> queries = new ArrayList<>((int)block1);
        for(long i = block1; i != 0; i = in.arrayNext()) {
            for (long j = 0; j < i; j++) {

                V vertex = vertexReader.read(in);
                queries.add(vertex);
            }
        }

        queries.stream().forEach(builder::add);

        int index1 = 0;
        int index2 = 1;
        int nextSize = queries.size();
        for(long i = in.readArrayStart(); i != 0; i = in.arrayNext()) {
            for (long j = 0; j < i; j++) {

                if (index2 >= nextSize) {

                    index1 += 1;
                    index2 = index1 + 1;
                }

                builder.add(queries.get(index1), queries.get(index2), in.readFloat());
                index2 += 1;
            }
        }
    }
}
