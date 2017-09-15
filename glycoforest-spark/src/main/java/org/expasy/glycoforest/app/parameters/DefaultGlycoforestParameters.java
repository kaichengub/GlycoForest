package org.expasy.glycoforest.app.parameters;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.expasy.glycoforest.app.GlycoforestParameters;
import org.expasy.glycoforest.chargestate.FrequencyChargeEstimator;
import org.expasy.glycoforest.chargestate.MemberChargeEstimator;
import org.expasy.glycoforest.glycomod.GlycomodPredicate;
import org.expasy.glycoforest.app.factories.ConsensusPeakMergerFactory;
import org.expasy.glycoforest.app.factories.SpectrumReaderFactory;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.cluster.ClusterBuilder;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.peaklist.peakfilter.AbstractMergePeakFilter;
import org.expasy.mzjava.core.ms.peaklist.peakfilter.LibraryMergePeakFilter;
import org.expasy.mzjava.core.ms.spectrasim.NdpSimFunc;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrasim.peakpairprocessor.DefaultPeakListAligner;
import org.expasy.mzjava.core.ms.spectrasim.peakpairprocessor.transformer.PeakCountEqualizer;
import org.expasy.mzjava.core.ms.spectrasim.peakpairprocessor.transformer.PeakPairIntensitySqrtTransformer;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.hadoop.io.HadoopSpectraReader;
import org.expasy.mzjava.io.ms.hadoop.OldHadoopReaderFactory;
import org.expasy.mzjava.spark.Cached;
import org.expasy.mzjava.spark.CachedAbsoluteTolerance;
import org.expasy.mzjava.tools.glycoforest.bayescluster.UpdatableBeta;
import org.expasy.mzjava.tools.glycoforest.bayescluster.UpdatableNormal;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.expasy.mzjava.tools.glycoforest.rtcluster.RTBayesClusterBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class DefaultGlycoforestParameters implements GlycoforestParameters {

    private static final double precursorTolerance = 0.3;
    private static final double fragmentTolerance = 0.3;
    private static final double minPeakCount = 5;

    private final Broadcast<GlycomodPredicate> singleChargeMzPredicate;
    private final Broadcast<GlycomodPredicate> doubleChargeMzPredicate;
    private final String inputPath;

    public DefaultGlycoforestParameters() {

        singleChargeMzPredicate = null;
        doubleChargeMzPredicate = null;
        inputPath = null;
    }

    public DefaultGlycoforestParameters(JavaSparkContext sc, String inputPath, String rangeDataPath) {

        this.inputPath = inputPath;
        final Configuration conf = sc.hadoopConfiguration();
        singleChargeMzPredicate = sc.broadcast(new GlycomodPredicate(conf, new Path(rangeDataPath, "ranges_minus_1.hdrange")));
        doubleChargeMzPredicate = sc.broadcast(new GlycomodPredicate(conf, new Path(rangeDataPath, "ranges_minus_2.hdrange")));
    }

    @Override
    public Map<UUID, String> getRunIdPathMap() {

        final Map<String, String> gastricRuns = new HashMap<>();
        gastricRuns.put("10062011_ES5", "37715776-80a9-4081-9ef1-93819019ae0e");
        gastricRuns.put("100929_es1", "b1ae30c4-9108-4b10-a869-7550f94a86e1");
        gastricRuns.put("100929_es10", "08584f03-caf6-4978-baab-62ad777efe48");
        gastricRuns.put("100929_es2", "61d7d746-ba5a-431a-a664-fef92fbc226a");
        gastricRuns.put("100929_es3", "c23028ad-b349-4cf9-ad81-4ca3556fefdb");
        gastricRuns.put("100929_es4", "538b7170-52ac-4f50-8368-128197e3b527");
        gastricRuns.put("100929_es7", "f46165b5-9089-436a-b8f1-d2815a71a7fe");
        gastricRuns.put("100929_es8", "e7b56400-e153-43b9-b39f-50d799185f34");
        gastricRuns.put("100929_es9", "950ae626-0ab1-44f9-a5ab-7231de8646d4");
        gastricRuns.put("101215_es11", "d6086b8e-fa41-4c08-8002-c54aaf9a87f7");
        gastricRuns.put("101215_es12", "932d7ba5-ce3a-4ad4-aa44-18f7b4b82ecc");
        gastricRuns.put("101215_es17", "d1f341b4-3090-478a-a787-9704b97ab605");
        gastricRuns.put("101216_es_13", "09d4de67-d996-4dcc-bf49-4b6f46ef9aad");
        gastricRuns.put("111116es_14", "f0a05a1b-af39-49ce-bafc-573b8e99148e");
        gastricRuns.put("111116es_15", "2467c139-2bb0-4bd8-bbe9-430e96b3372b");
        gastricRuns.put("111116es_6", "0663044c-8893-4ccc-ad38-6dc6e165623f");

        final Map<String, String> fishRuns = new HashMap<>();
        fishRuns.put("JC_131209FMDc1", "728f4b78-0521-4b51-b6a3-c60df82dca97");
        fishRuns.put("JC_131209FMDc2", "4bb1bdb5-1e77-4f79-95a9-2fe9c9959c42");
        fishRuns.put("JC_131209FMDc3", "953615d3-b7ee-4294-b33b-61b33cb8f997");
        fishRuns.put("JC_131209FMS1", "70b08d71-3f2d-465b-beeb-d7fca6f5f19c");
        fishRuns.put("JC_131209FMS2", "c920ea15-c88f-4acb-b3c4-dd3a739ba371");
        fishRuns.put("JC_131209FMS3", "6137d5f6-797d-4e55-8908-3a8020832155");
        fishRuns.put("JC_131209FMS4", "08bf7f1f-06ef-426a-b8a9-719908335eea");
        fishRuns.put("JC_131209FMS5", "8dc0c77d-0754-4092-8c50-66d36c86afc7");
        fishRuns.put("JC_131210FMDc4", "9011c058-6dcc-4c9b-8364-60baac29daf0");
        fishRuns.put("JC_131210FMDc5", "6544dd5c-22ea-436d-a8a5-18a4dec3e629");
        fishRuns.put("JC_131210FMpx1", "e469cd48-cc8a-4874-b84b-ee54c43f1dc9");
        fishRuns.put("JC_131210FMpx2", "f103792a-5405-4e47-a79c-a288e93ad291");
        fishRuns.put("JC_131210PMpc1", "9037034f-3c8e-4616-b2a6-b59255d7b305");
        fishRuns.put("JC_131210PMpc2", "2f629563-5de2-4395-885a-28ee0a4c9b4f");
        fishRuns.put("JC_131210PMpc3", "6074e664-d6c8-4074-93d8-11b599ba0d6e");
        fishRuns.put("JC_131210PMpc4", "ace756ea-879d-41f7-a20d-c90b82abd237");
        fishRuns.put("JC_131210PMpc5", "0de86d47-b1a5-40cf-a8bb-b0971bb51733");
        fishRuns.put("JC_131210PMpx3", "f87bb257-67ae-482f-918d-a8310de80d7e");
        fishRuns.put("JC_131210PMpx4", "5bedd92f-55bb-4fe5-9b41-f22f6e6d99bc");
        fishRuns.put("JC_131210PMpx5", "500941d0-d2ff-41c3-9c73-ca0a9583091b");

        return Stream.concat(gastricRuns.entrySet().stream(), fishRuns.entrySet().stream())
                .collect(Collectors.toMap(entry -> UUID.fromString(entry.getValue()), entry -> inputPath + "/" + entry.getKey() + ".hdmsn"));
    }

    @Override
    public SpectrumReaderFactory<MsnSpectrum> readerFactory() {

        return spectrumPathString -> {
            try {

                final SequenceFile.Reader reader = new SequenceFile.Reader(new Configuration(), SequenceFile.Reader.file(new Path(spectrumPathString)));
                final Class<?> keyClass = reader.getKeyClass();
                reader.close();
                if (keyClass.equals(org.expasy.mzjava.io.ms.hadoop.DefaultSpectrumKey.class)) {

                    return OldHadoopReaderFactory.msnReader(new File(spectrumPathString));
                } else {

                    return HadoopSpectraReader.msnReader(new File(spectrumPathString));
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    @Override
    public Cached<Tolerance> precursorTolerance() {

        return new CachedAbsoluteTolerance(precursorTolerance);
    }

    @Override
    public double retentionTimeTolerance() {

        return 10;
    }

    @Override
    public Cached<SimFunc<PeakAnnotation, PeakAnnotation>> msnSimFunc() {

        return new Cached<SimFunc<PeakAnnotation, PeakAnnotation>>() {
            @Override
            protected SimFunc<PeakAnnotation, PeakAnnotation> build() {

                return newSimFunc();
            }
        };
    }

    @Override
    public Cached<SimFunc<PeakAnnotation, LibPeakAnnotation>> contaminantSimFunc() {

        return new Cached<SimFunc<PeakAnnotation, LibPeakAnnotation>>() {
            @Override
            protected SimFunc<PeakAnnotation, LibPeakAnnotation> build() {

                return newSimFunc();
            }
        };
    }

    @Override
    public Cached<PeakProcessorChain<PeakAnnotation>> msnPeakProcessor() {

        return new Cached<PeakProcessorChain<PeakAnnotation>>() {
            @Override
            protected PeakProcessorChain<PeakAnnotation> build() {

                return new PeakProcessorChain<>();
            }
        };
    }

    @Override
    public ConsensusPeakMergerFactory<PeakAnnotation> withinRunConsensusPeakMerger() {

        return spectraCount -> new LibraryMergePeakFilter<>(0.3, 0.6, AbstractMergePeakFilter.IntensityMode.SUM_INTENSITY, spectraCount);
    }

    @Override
    public Cached<ClusterBuilder<MsnSpectrum>> msnClusterBuilder() {

        return new Cached<ClusterBuilder<MsnSpectrum>>() {
            @Override
            protected ClusterBuilder<MsnSpectrum> build() {

                return new RTBayesClusterBuilder<>(
                        new UpdatableBeta(0.99, 10, UpdatableBeta.Type.VERTEX), new UpdatableNormal(0.8, 0.002, 0.001), 0.0,
                        spectrum -> spectrum.getRetentionTimes().getFirst().getTime(),
                        PeakList::getTotalIonCurrent,
                        spectrum -> spectrum.getScanNumbers().getFirst().getValue()
                );
            }
        };
    }

    @Override
    public PeakList.Precision precision() {

        return PeakList.Precision.FLOAT;
    }

    @Override
    public Cached<Predicate<MsnSpectrum>> msnSpectrumPredicate() {

        return new Cached<Predicate<MsnSpectrum>>() {
            @Override
            protected Predicate<MsnSpectrum> build() {

                Predicate<MsnSpectrum> mzPredicate = peakList -> new AbsoluteTolerance(1).withinTolerance(384.2, peakList.getPrecursor().getMz()) ||
                        new AbsoluteTolerance(1).withinTolerance(425.2, peakList.getPrecursor().getMz()) ||
                        new AbsoluteTolerance(1).withinTolerance(587.2, peakList.getPrecursor().getMz()) ||
                        new AbsoluteTolerance(1).withinTolerance(733.2, peakList.getPrecursor().getMz()) ||
                        new AbsoluteTolerance(1).withinTolerance(895.2, peakList.getPrecursor().getMz());

                return peakList -> peakList.getPrecursor().getMz() > 383 && peakList.size() > minPeakCount &&
                        (singleChargeMzPredicate.getValue().test(peakList) || doubleChargeMzPredicate.getValue().test(peakList)) &&
                        peakList.getRetentionTimes().getFirst().getTime() > 9 * 60 && peakList.getRetentionTimes().getFirst().getTime() < 40 * 60;
            }
        };
    }

    @Override
    public Cached<Predicate<WithinRunConsensus>> withinRunConsensusPredicate() {

        return new Cached<Predicate<WithinRunConsensus>>() {
            @Override
            protected Predicate<WithinRunConsensus> build() {

                return consensus -> consensus.countPeaksMatching(annotation -> annotation.getMergedPeakCount() > 1) > 5;
            }
        };
    }

    private <A extends PeakAnnotation, B extends PeakAnnotation> SimFunc<A, B> newSimFunc() {

        //noinspection unchecked
        return new NdpSimFunc<>(0,
                new DefaultPeakListAligner<>(new AbsoluteTolerance(fragmentTolerance)),
                new PeakCountEqualizer<>(),
                new PeakPairIntensitySqrtTransformer<>()
        );
    }

    @Override
    public Cached<MemberChargeEstimator> withinRunChargeStateEstimator() {

        return new Cached<MemberChargeEstimator>() {
            @Override
            protected MemberChargeEstimator build() {

                return new FrequencyChargeEstimator();
            }
        };
    }
}
