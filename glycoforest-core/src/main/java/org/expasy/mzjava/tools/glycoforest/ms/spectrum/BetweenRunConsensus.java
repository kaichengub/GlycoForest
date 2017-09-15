/**
 * Copyright (c) 2010, SIB. All rights reserved.
 *
 * SIB (Swiss Institute of Bioinformatics) - http://www.isb-sib.ch Host -
 * http://mzjava.expasy.org
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the SIB/GENEBIO nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL SIB/GENEBIO BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.expasy.mzjava.tools.glycoforest.ms.spectrum;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessor;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.Spectrum;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class BetweenRunConsensus extends Spectrum<LibPeakAnnotation> {

    private int withinRunMemberCount;
    private int msnMemberCount;
    private double simScoreMean = 0;
    private double simScoreStdev = 0;
    private double minWithinRunMz;
    private double maxWithinRunMz;
    private final List<UUID> runIds = new ArrayList<>();

    public BetweenRunConsensus(Precision precision) {

        super(100, precision);
    }

    public BetweenRunConsensus(double simScoreMean, double simScoreStdev, int withinRunMemberCount, int msnMemberCount, double minWithinRunMz, double maxWithinRunMz, Collection<UUID> runIds, Precision precision) {

        super(100, precision);

        setFields(simScoreMean, simScoreStdev, msnMemberCount, withinRunMemberCount, minWithinRunMz, maxWithinRunMz, runIds);
    }

    protected BetweenRunConsensus(BetweenRunConsensus src, PeakProcessor<LibPeakAnnotation,LibPeakAnnotation> peakProcessor) {

        super(src, peakProcessor);

        setFields(src.simScoreMean, src.simScoreStdev, src.msnMemberCount, src.withinRunMemberCount, src.getMinWithinRunMz(), src.getMaxWithinRunMz(), src.runIds);
    }

    protected BetweenRunConsensus(BetweenRunConsensus src, PeakProcessorChain<LibPeakAnnotation> processorChain) {

        super(src, processorChain);

        setFields(src.simScoreMean, src.simScoreStdev, src.msnMemberCount, src.withinRunMemberCount, src.getMinWithinRunMz(), src.getMaxWithinRunMz(), src.runIds);
    }

    public void setFields(double simScoreMean, double simScoreStdev, int msnMemberCount, int withinRunMemberCount, double minWithinRunMz, double maxWithinRunMz, Collection<UUID> runIds) {

        if(withinRunMemberCount > 1 && (simScoreMean < 0 || simScoreMean > 1)){

            throw new IllegalStateException("simScoreMean is expected to be in the range 0 <= x <= 1");
        }
        checkArgument(msnMemberCount > 0);
        checkNotNull(runIds);
        checkArgument(!runIds.isEmpty());
        if(maxWithinRunMz < minWithinRunMz)
            throw new IllegalArgumentException("maxWithinRunMz is smaller than minWithinRunMz. minWithinRunMz = " + minWithinRunMz + ", maxWithinRunMz = " + maxWithinRunMz);

        this.simScoreMean = simScoreMean;
        this.simScoreStdev = simScoreStdev;
        this.msnMemberCount = msnMemberCount;
        this.withinRunMemberCount = withinRunMemberCount;
        this.minWithinRunMz = minWithinRunMz;
        this.maxWithinRunMz = maxWithinRunMz;
        this.runIds.addAll(runIds);
    }

    @Override
    public BetweenRunConsensus copy(PeakProcessor<LibPeakAnnotation, LibPeakAnnotation> peakProcessor) {

        return new BetweenRunConsensus(this, new PeakProcessorChain<>(peakProcessor));
    }

    @Override
    public BetweenRunConsensus copy(PeakProcessorChain<LibPeakAnnotation> peakProcessorChain) {

        return new BetweenRunConsensus(this, peakProcessorChain);
    }

    public double getSimScoreMean() {

        return simScoreMean;
    }

    public double getSimScoreStdev() {

        return simScoreStdev;
    }

    public int getMsnMemberCount() {

        return msnMemberCount;
    }

    public int getWithinRunMemberCount() {

        return withinRunMemberCount;
    }

    public double getMinWithinRunMz() {

        return minWithinRunMz;
    }

    public double getMaxWithinRunMz() {

        return maxWithinRunMz;
    }

    public List<UUID> getRunIds() {

        return Collections.unmodifiableList(runIds);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BetweenRunConsensus consensus = (BetweenRunConsensus) o;
        return getId().equals(consensus.getId());
    }

    @Override
    public int hashCode() {

        return getId().hashCode();
    }

    public String calcMassLabel(final GlycanMassCalculator massCalculator) {

        final Peak precursor = this.getPrecursor();
        final double mzZ1;
        if (precursor.getCharge() == 1) {

            mzZ1 = precursor.getMz();
        } else {

            final double compositionMass = massCalculator.calcCompositionMass(precursor.getMz(), precursor.getCharge());
            mzZ1 = massCalculator.calcMz(compositionMass, 1);
        }
        return Integer.toString((int) mzZ1);
    }
}
