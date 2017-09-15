package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import com.google.common.collect.Sets;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.hadoop.io.MockDataInput;
import org.expasy.mzjava.hadoop.io.MockDataOutput;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class BetweenRunConsensusSerializerTest {

    private final String base64 = "AAICADeJQWDlpnxAVg4tsp2XU0BQBs3MzMzMzOQ/mpmZmZmZuT/hehSuR9mXQJqZmZmZ2ZdAAoHe\n" +
            "1Y/b2sXV3QH75s/c3J6bzPYBAI/M3fyyy4LfddmJwqimioP54gEACgKPwvUoXD9iQAAAAAAAAChA\n" +
            "AgAISgwCK4cWuT8AAAAAAAAgQAAA";

    @Test
    public void testSerialize() throws Exception {

        BetweenRunConsensus betweenRunConsensus = new BetweenRunConsensus(PeakList.Precision.DOUBLE);
        betweenRunConsensus.setId(UUID.fromString("c520fad2-6834-4cf8-8e86-f9d6cd77bd93"));
        betweenRunConsensus.getPrecursor().setValues(458.431, 78.369, 1);
        betweenRunConsensus.add(145.98, 12, Collections.singletonList(new LibPeakAnnotation(4, 0.098, 8)));
        betweenRunConsensus.setFields(0.65, 0.1, 40, 3, 1526.32, 1526.4, Sets.newHashSet(UUID.fromString("912a7495-2705-487f-84b3-c9851a360642")));

        MockDataOutput out = new MockDataOutput(512);
        BetweenRunConsensusSerializer serializer = new BetweenRunConsensusSerializer();
        serializer.serialize(out, betweenRunConsensus);

        Assert.assertEquals(base64, out.getBase64());
        Assert.assertEquals(-1, serializer.fixedSize());
    }

    @Test
    public void testDeserialize() throws Exception {

        MockDataInput in = new MockDataInput(base64);
        BetweenRunConsensusSerializer serializer = new BetweenRunConsensusSerializer();
        BetweenRunConsensus consensus = serializer.deserialize(in, 124);

        Assert.assertEquals(new Peak(458.431, 78.369, 1), consensus.getPrecursor());
        Assert.assertEquals(UUID.fromString("c520fad2-6834-4cf8-8e86-f9d6cd77bd93"), consensus.getId());
        Assert.assertEquals(1, consensus.size());
    }
}
