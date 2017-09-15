package org.expasy.glycoforest.app;

import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.expasy.glycoforest.cluster.MsRunSplitter;
import org.expasy.glycoforest.app.factories.SpectrumReaderFactory;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakListComparator;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.spark.Cached;
import scala.Tuple2;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Flat map function that reads a run from file and splits it into groups that contain spectra
 * that are within m/z and rt tolerance. Each group is sorted by the spectrum precursor (charge, m/z)
 * <p>
 * Unwanted spectra can be removed by providing an appropriate Predicate
 *
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class RunSplitterFmf implements PairFlatMapFunction<Tuple2<String, UUID>, UUID, List<MsnSpectrum>> {

    private static final long serialVersionUID = 5692061712174457346L;

    private final Cached<Predicate<MsnSpectrum>> spectrumPredicate;
    private final SpectrumReaderFactory<MsnSpectrum> readerFactory;
    private final Cached<Tolerance> precursorTolerance;
    private final double rtTolerance;

    private transient MsRunSplitter runSplitter;
    private transient PeakListComparator peakListComparator;

    /**
     * Constructor
     *
     * @param readerFactory the factory for creating a reader
     * @param precursorTolerance the precursor tolerance
     * @param rtTolerance the retention time tolerance
     * @param spectrumPredicate predicate to specify which spectra are to be kept
     */
    public RunSplitterFmf(SpectrumReaderFactory<MsnSpectrum> readerFactory, Cached<Tolerance> precursorTolerance, double rtTolerance, Cached<Predicate<MsnSpectrum>> spectrumPredicate) {

        this.spectrumPredicate = spectrumPredicate;
        this.readerFactory = readerFactory;
        this.precursorTolerance = precursorTolerance;
        this.rtTolerance = rtTolerance;
    }

    @Override
    public Iterable<Tuple2<UUID, List<MsnSpectrum>>> call(Tuple2<String, UUID> fileIdTuple) throws Exception {

        if(runSplitter == null) runSplitter = new MsRunSplitter(precursorTolerance.get(), rtTolerance, spectrumPredicate.get());
        if(peakListComparator == null) peakListComparator = new PeakListComparator();

        final String spectrumPath = fileIdTuple._1();

        final List<List<MsnSpectrum>> splits = runSplitter.split(readerFactory.spectrumReader(spectrumPath));
        for(List<MsnSpectrum> split : splits){

            Collections.sort(split, peakListComparator);
        }

        return splits.stream().map(list -> new Tuple2<>(fileIdTuple._2(), list)).collect(Collectors.toList());
    }
}
