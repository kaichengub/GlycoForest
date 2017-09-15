package org.expasy.mzjava.tools.glycoforest.ms.spectrum;

import org.expasy.mzjava.core.ms.consensus.ConsensusSpectrum;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessor;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;

import java.util.Set;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycoConsensusSpectrum extends ConsensusSpectrum<LibPeakAnnotation> {

    public GlycoConsensusSpectrum(int initialCapacity, Precision precision, Set<UUID> memberIds) {
        super(initialCapacity, precision, memberIds);
    }

    protected GlycoConsensusSpectrum(GlycoConsensusSpectrum consensusSpectrum,PeakProcessor<LibPeakAnnotation, LibPeakAnnotation> peakProcessor) {
        super(consensusSpectrum, peakProcessor);
    }

    protected GlycoConsensusSpectrum(GlycoConsensusSpectrum consensusSpectrum,PeakProcessorChain<LibPeakAnnotation> peakProcessorChain) {
        super(consensusSpectrum, peakProcessorChain);
    }
}

