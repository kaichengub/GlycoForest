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

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessor;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeInterval;
import org.expasy.mzjava.core.ms.spectrum.ScanNumberInterval;
import org.expasy.mzjava.core.ms.spectrum.Spectrum;

import java.text.NumberFormat;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class WithinRunConsensus extends Spectrum<LibPeakAnnotation> {

    private UUID runId = null;
    private int memberCount;
    private double simScoreMean = 0;
    private double simScoreStdev = 0;
    private RetentionTimeInterval retentionTimeInterval;
    private ScanNumberInterval scanNumberInterval;
    private double minMemberMz, maxMemberMz;

    public WithinRunConsensus(Precision precision) {

        super(100, precision);
    }

    public WithinRunConsensus(UUID id, UUID runId, StatisticalSummary scoreStats, double totalIonCurrent, StatisticalSummary mzStats, int[] charges, ScanNumberInterval scanNumberInterval, RetentionTimeInterval retentionTimeInterval, Precision precision) {

        super(100, precision);
        setId(id);

        getPrecursor().setValues(mzStats.getMean(), totalIonCurrent, charges);
        setMsLevel(2);

        setFields(runId, (int) mzStats.getN(), scoreStats.getMean(), scoreStats.getStandardDeviation(), mzStats.getMin(), mzStats.getMax(), scanNumberInterval, retentionTimeInterval);
    }

    protected WithinRunConsensus(WithinRunConsensus src, PeakProcessorChain<LibPeakAnnotation> processorChain) {

        super(src, processorChain);

        setFields(src);
    }

    protected WithinRunConsensus(WithinRunConsensus src, PeakProcessor<LibPeakAnnotation,LibPeakAnnotation> peakProcessor) {

        super(src, peakProcessor);

        setFields(src);
    }

    private void setFields(WithinRunConsensus src) {

        this.runId = src.getRunId();
        this.memberCount = src.memberCount;
        this.simScoreMean = src.simScoreMean;
        this.simScoreStdev = src.simScoreStdev;
        this.scanNumberInterval= src.scanNumberInterval;
        this.retentionTimeInterval = src.retentionTimeInterval;
        this.minMemberMz = src.minMemberMz;
        this.maxMemberMz = src.maxMemberMz;
    }

    public void setFields(UUID runId, int memberCount, double simScoreMean, double simScoreStdev, double minMz, double maxMz, ScanNumberInterval scanNumberInterval, RetentionTimeInterval retentionTimeInterval) {

        checkNotNull(runId);
        if(maxMz < minMz)
            throw new IllegalArgumentException("maxMz is smaller than minMz. MinMz = " + minMz + ", maxMz = " + maxMz);
        if(memberCount > 1 && (simScoreMean < 0 || simScoreMean > 1)){

            throw new IllegalStateException("simScoreMean is expected to be in the range 0 <= x <= 1");
        }
        checkNotNull(scanNumberInterval);
        checkNotNull(retentionTimeInterval);

        this.runId = runId;
        this.memberCount = memberCount;
        this.simScoreMean = simScoreMean;
        this.simScoreStdev = simScoreStdev;
        this.scanNumberInterval= scanNumberInterval;
        this.retentionTimeInterval = retentionTimeInterval;
        this.minMemberMz = minMz;
        this.maxMemberMz = maxMz;
    }

    @Override
    public WithinRunConsensus copy(PeakProcessor<LibPeakAnnotation, LibPeakAnnotation> peakProcessor) {

        return new WithinRunConsensus(this, peakProcessor);
    }

    @Override
    public WithinRunConsensus copy(PeakProcessorChain<LibPeakAnnotation> peakProcessorChain) {

        return new WithinRunConsensus(this, peakProcessorChain);
    }

    public double getSimScoreMean() {

        return simScoreMean;
    }

    public double getSimScoreStdev() {

        return simScoreStdev;
    }


    public int getMemberCount() {

        return memberCount;
    }

    public UUID getRunId() {

        if(runId == null)
            throw new NullPointerException("Source id was not set");

        return runId;
    }

    public RetentionTimeInterval getRetentionTimeInterval() {

        return retentionTimeInterval;
    }

    public double getRetentionTime(){

        return retentionTimeInterval.getTime();
    }

    public double getMinRetentionTime(){

        return retentionTimeInterval.getMinRetentionTime();
    }

    public double getMaxRetentionTime(){

        return retentionTimeInterval.getMaxRetentionTime();
    }

    public ScanNumberInterval getScanNumberInterval() {

        return scanNumberInterval;
    }

    public double getMinMemberMz() {

        return minMemberMz;
    }

    public double getMaxMemberMz() {

        return maxMemberMz;
    }

    @Override
    public String toString() {

        return "WithinRunConsensus{" +
                "source='" + runId + '\'' +
                " members=" + memberCount +
                " m/z=" + NumberFormat.getNumberInstance().format(getPrecursor().getMz()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WithinRunConsensus consensus = (WithinRunConsensus) o;
        return super.getId().equals(consensus.getId());
    }

    @Override
    public int hashCode() {

        return System.identityHashCode(this);
    }

    public int countPeaksMatching(Predicate<LibPeakAnnotation> annotationPredicate) {

        return (int)IntStream.of(getAnnotationIndexes()).mapToObj(index -> getAnnotations(index).get(0)).filter(annotationPredicate).count();
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
