/**
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
package org.expasy.mzjava.tools.glycoforest.graphdb;

import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraphBuilder;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.stats.FrequencyTable;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public interface GraphRepository {

    void shutdown();

    <C extends Collection<MsnNode>> C loadChildren(WithinRunNode withinRunNode, C collection);

    <C extends Collection<WithinRunNode>> C loadChildren(BetweenRunNode betweenRunNode, C collection);

    <C extends Collection<RunNode>> C loadRunNodes(BetweenRunNode betweenRunNode, C collection);

    RunNode loadRunNode(WithinRunNode node);

    Optional<BetweenRunNode> loadParent(WithinRunNode node);

    <N extends SpectrumNode, G extends SimilarityGraph<N>> void loadGraph(Set<N> nodes, SimilarityGraphBuilder<N, G> graphBuilder);

    <N extends SpectrumNode> void loadScoreDistribution(Set<N> nodes, FrequencyTable frequencyTable);

    <N extends SpectrumNode> void loadScoreDistribution(Set<N> set1, Set<N> set2, FrequencyTable frequencyTable);

    MsnSpectrum getSpectrum(MsnNode node);

    WithinRunConsensus getSpectrum(WithinRunNode withinRunNode);

    BetweenRunConsensus getSpectrum(BetweenRunNode betweenRunNode);

    Stream <RunNode> getRunNodeStream();

    Stream<BetweenRunNode> getBetweenRunNodeStream();

    Optional<RunNode> getRunNode(UUID runId);
}
