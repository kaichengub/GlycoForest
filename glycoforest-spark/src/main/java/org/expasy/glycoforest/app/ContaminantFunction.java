package org.expasy.glycoforest.app;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.library.DefaultSpectrumLibrary;
import org.expasy.mzjava.core.ms.library.SpectrumLibrary;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.spark.Cached;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ContaminantFunction implements Function<Tuple2<WithinRunConsensus, List<MsnSpectrum>>, Boolean> {

    private final double scoreThreshold;
    private final Cached<SimFunc<PeakAnnotation, LibPeakAnnotation>> simFuncCached;
    private final Cached<Tolerance> toleranceCached;
    private final Broadcast<List<MsnSpectrum>> contaminants;

    private transient SpectrumLibrary<MsnSpectrum> library;

    public ContaminantFunction(double scoreThreshold, Cached<SimFunc<PeakAnnotation, LibPeakAnnotation>> simFuncCached, Cached<Tolerance> toleranceCached, Broadcast<List<MsnSpectrum>> contaminants) {

        this.scoreThreshold = scoreThreshold;
        this.simFuncCached = simFuncCached;
        this.toleranceCached = toleranceCached;
        this.contaminants = contaminants;
    }

    @Override
    public Boolean call(Tuple2<WithinRunConsensus, List<MsnSpectrum>> tuple) throws Exception {

        if(library == null)
            library = new DefaultSpectrumLibrary<>(toleranceCached.get(), contaminants.getValue());

        final SimFunc<PeakAnnotation, LibPeakAnnotation> simFunc = simFuncCached.get();

        final WithinRunConsensus query = tuple._1();
        final List<MsnSpectrum> libSpectra = new ArrayList<>();
        library.forEach(query.getPrecursor(), libSpectra::add);
        for(MsnSpectrum lib : libSpectra) {

            if(simFunc.calcSimilarity(lib, query) > scoreThreshold)
                return false;
        }

        return Boolean.TRUE;
    }
}
