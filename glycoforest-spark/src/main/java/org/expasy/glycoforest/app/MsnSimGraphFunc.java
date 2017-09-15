package org.expasy.glycoforest.app;

import org.apache.spark.api.java.function.Function;
import org.expasy.glycoforest.data.MsnSimGraph;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.spark.Cached;
import scala.Tuple2;

import java.util.List;
import java.util.UUID;

/**
 * Function for generating a similarity graph given a list of spectra. The spectra need to be in sorted by precursor (charge, m/z)
 *
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class MsnSimGraphFunc extends AbstractSimGraphFunc<PeakAnnotation> implements Function<Tuple2<UUID, List<MsnSpectrum>>, MsnSimGraph> {

    /**
     * Constructor
     *
     * @param simFunc the sim function that is used to calculate edge scores
     * @param precursorTolerance the precursor tolerance
     * @param processorChain the processor chain for pre-processing spectra before the similarity is calculated
     */
    public MsnSimGraphFunc(Cached<SimFunc<PeakAnnotation, PeakAnnotation>> simFunc, Cached<Tolerance> precursorTolerance, Cached<PeakProcessorChain<PeakAnnotation>> processorChain) {

        super(processorChain, precursorTolerance, simFunc);
    }

    @Override
    public MsnSimGraph call(Tuple2<UUID, List<MsnSpectrum>> tuple) throws Exception {

        final MsnSimGraph.Builder builder = new MsnSimGraph.Builder(tuple._1());
        buildGraphIgnoreCharge(tuple._2(), builder);
        return builder.build();
    }

}
