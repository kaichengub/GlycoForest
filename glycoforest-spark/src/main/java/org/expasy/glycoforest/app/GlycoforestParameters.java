/*
 * Copyright (c) 2010, SIB. All rights reserved.
 *
 * SIB (Swiss Institute of Bioinformatics) - http://www.isb-sib.ch Host -
 * https://sourceforge.net/projects/javaprotlib/
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
package org.expasy.glycoforest.app;

import org.expasy.glycoforest.chargestate.MemberChargeEstimator;
import org.expasy.glycoforest.app.factories.ConsensusPeakMergerFactory;
import org.expasy.glycoforest.app.factories.SpectrumReaderFactory;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.cluster.ClusterBuilder;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.spark.Cached;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public interface GlycoforestParameters extends Serializable {

    Map<UUID, String> getRunIdPathMap();

    SpectrumReaderFactory<MsnSpectrum> readerFactory();

    Cached<Tolerance> precursorTolerance();

    double retentionTimeTolerance();

    Cached<SimFunc<PeakAnnotation,PeakAnnotation>> msnSimFunc();

    Cached<SimFunc<PeakAnnotation,LibPeakAnnotation>> contaminantSimFunc();

    Cached<PeakProcessorChain<PeakAnnotation>> msnPeakProcessor();

    ConsensusPeakMergerFactory<PeakAnnotation> withinRunConsensusPeakMerger();

    Cached<ClusterBuilder<MsnSpectrum>> msnClusterBuilder();

    PeakList.Precision precision();

    Cached<Predicate<WithinRunConsensus>> withinRunConsensusPredicate();

    Cached<Predicate<MsnSpectrum>> msnSpectrumPredicate();

    Cached<MemberChargeEstimator> withinRunChargeStateEstimator();
}
