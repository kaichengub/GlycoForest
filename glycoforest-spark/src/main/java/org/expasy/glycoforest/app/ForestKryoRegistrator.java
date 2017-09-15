package org.expasy.glycoforest.app;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.base.Optional;
import org.expasy.glycoforest.avro.io.MsnSimGraphReader;
import org.expasy.glycoforest.avro.io.MsnSimGraphWriter;
import org.expasy.glycoforest.avro.io.WithinRunSimGraphReader;
import org.expasy.glycoforest.avro.io.WithinRunSimGraphWriter;
import org.expasy.glycoforest.data.MsnSimGraph;
import org.expasy.glycoforest.data.WithinRunSimGraph;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.spark.KryoAvroSerializer;
import org.expasy.mzjava.spark.MzJavaKryoRegistrator;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.BetweenRunConsensusReader;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.BetweenRunConsensusWriter;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.WithinRunConsensusReader;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.WithinRunConsensusWriter;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

import java.util.Collections;

/**
 * Kryo registrator that registers WithinRunConsensus and BetweenRunConsensus classes for serialization
 *
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ForestKryoRegistrator extends MzJavaKryoRegistrator {

    @Override
    public void registerClasses(Kryo kryo) {

        kryo.register(WithinRunConsensus.class, new KryoAvroSerializer<>(
                new WithinRunConsensusWriter(Optional.<PeakList.Precision>absent()),
                new WithinRunConsensusReader(Optional.<PeakList.Precision>absent(), Collections.emptyList())));

        kryo.register(BetweenRunConsensus.class, new KryoAvroSerializer<>(
                new BetweenRunConsensusWriter(Optional.<PeakList.Precision>absent()),
                new BetweenRunConsensusReader(Optional.<PeakList.Precision>absent(), Collections.emptyList())));

        kryo.register(MsnSimGraph.class, new KryoAvroSerializer<>(
                new MsnSimGraphWriter(),
                new MsnSimGraphReader()
        ));

        kryo.register(WithinRunSimGraph.class, new KryoAvroSerializer<>(
                new WithinRunSimGraphWriter(),
                new WithinRunSimGraphReader()
        ));
        super.registerClasses(kryo);
    }
}
