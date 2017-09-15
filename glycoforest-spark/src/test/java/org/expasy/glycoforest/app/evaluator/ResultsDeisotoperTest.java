package org.expasy.glycoforest.app.evaluator;

import com.google.common.collect.Lists;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraphBuilder;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrum.*;
import org.expasy.mzjava.stats.FrequencyTable;
import org.expasy.mzjava.tools.glycoforest.graphdb.*;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.expasy.mzjava.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ResultsDeisotoperTest {

    @Test
    public void testRemoveIsotopes() throws Exception {

        final URI run1 = new URIBuilder("org.expasy", "run1").build();
        final URI run2 = new URIBuilder("org.expasy", "run2").build();
        final URI run3 = new URIBuilder("org.expasy", "run3").build();
        final URI consensusURI = new URIBuilder("org.expasy", "glycoforest").build();

        final MsnSpectrum spectrum1 = newSpectrum(run1, 733.3, 60*3);
        final MsnNode msnNode1 = mock(MsnNode.class);

        final MsnSpectrum spectrum1_1 = newSpectrum(run1, 734.3, 60*3 + 2);
        final MsnNode msnNode1_1 = mock(MsnNode.class);

        final WithinRunNode wrn1 = mock(WithinRunNode.class);
        final WithinRunNode wrn1_1 = mock(WithinRunNode.class);

        final BetweenRunNode brn1 = mock(BetweenRunNode.class);
        final BetweenRunNode brn1_1 = mock(BetweenRunNode.class);

        final MockGraphRepository graphRepository = new MockGraphRepository();
        graphRepository.add(spectrum1, msnNode1, wrn1, brn1);
        graphRepository.add(spectrum1_1, msnNode1_1, wrn1_1, brn1_1);

        final GsmResultList result1 = mock(GsmResultList.class);
        when(result1.getSpectrumNode()).thenReturn(brn1);
        final PeakList<LibPeakAnnotation> processedSpectrum1 = newLibSpectrum(733.3);
        when(result1.getProcessedSpectrum()).thenReturn(processedSpectrum1);

        final GsmResultList result2 = mock(GsmResultList.class);
        when(result2.getSpectrumNode()).thenReturn(brn1_1);
        final PeakList<LibPeakAnnotation> processedSpectrum2 = newLibSpectrum(734.3);
        when(result2.getProcessedSpectrum()).thenReturn(processedSpectrum2);

        //noinspection unchecked
        final SimFunc<LibPeakAnnotation, LibPeakAnnotation> simFunc = mock(SimFunc.class);
        when(simFunc.calcSimilarity(Mockito.any(), Mockito.any())).thenReturn(0.8);

        final List<GsmResultList> cleanedResults = new ResultsDeisotoper(graphRepository, 60, new AbsoluteTolerance(0.3), simFunc)
                .removeIsotopes(Lists.newArrayList(result1, result2));

        Assert.assertEquals(1, cleanedResults.size());
        Assert.assertEquals(result1, cleanedResults.get(0));
    }

    private MsnSpectrum newSpectrum(final URI source, final double mz, double rt) {

        final MsnSpectrum spectrum = mock(MsnSpectrum.class);
        when(spectrum.getSpectrumSource()).thenReturn(source);
        when(spectrum.getPrecursor()).thenReturn(new Peak(mz, 1));
        when(spectrum.getRetentionTimes()).thenReturn(new RetentionTimeList(new RetentionTimeDiscrete(rt, TimeUnit.SECOND)));
        return spectrum;
    }

    private PeakList<LibPeakAnnotation> newLibSpectrum(final double mz) {

        @SuppressWarnings("unchecked")
        final PeakList<LibPeakAnnotation> spectrum = mock(PeakList.class);
        when(spectrum.getPrecursor()).thenReturn(new Peak(mz, 1));
        return spectrum;
    }

    private class MockGraphRepository implements GraphRepository {

        private final Map<BetweenRunNode, List<WithinRunNode>> betweenRunNodeMap = new HashMap<>();
        private final Map<WithinRunNode, List<MsnNode>> withinRunNodeMap = new HashMap<>();
        private final Map<MsnNode, MsnSpectrum> msnNodeMap = new HashMap<>();

        public void add(MsnSpectrum spectrum, MsnNode msnNode, WithinRunNode withinRunNode, BetweenRunNode betweenRunNode){

            msnNodeMap.put(msnNode, spectrum);
            withinRunNodeMap.computeIfAbsent(withinRunNode, (n) -> new ArrayList<>()).add(msnNode);
            betweenRunNodeMap.computeIfAbsent(betweenRunNode, (n) -> new ArrayList<>()).add(withinRunNode);
        }

        @Override
        public void shutdown() {

        }

        @Override
        public <C extends Collection<WithinRunNode>> C loadChildren(final BetweenRunNode betweenRunNode, final C collection) {

            final List<WithinRunNode> withinRunNodes = betweenRunNodeMap.get(betweenRunNode);

            if (withinRunNodes != null) {
                collection.addAll(withinRunNodes);
            }
            return collection;
        }

        @Override
        public <C extends Collection<MsnNode>> C loadChildren(final WithinRunNode withinRunNode, final C collection) {

            final List<MsnNode> msnNodes = withinRunNodeMap.get(withinRunNode);
            if (msnNodes != null) {
                collection.addAll(msnNodes);
            }
            return collection;
        }

        @Override
        public <C extends Collection<RunNode>> C loadRunNodes(final BetweenRunNode betweenRunNode, final C collection) {

            return null;
        }

        @Override
        public RunNode loadRunNode(final WithinRunNode node) {

            return null;
        }

        @Override
        public Optional<BetweenRunNode> loadParent(final WithinRunNode node) {

            return null;
        }

        @Override
        public <N extends SpectrumNode, G extends SimilarityGraph<N>> void loadGraph(final Set<N> nodes, final SimilarityGraphBuilder<N, G> graphBuilder) {

        }

        @Override
        public <N extends SpectrumNode> void loadScoreDistribution(final Set<N> nodes, final FrequencyTable frequencyTable) {

        }

        @Override
        public <N extends SpectrumNode> void loadScoreDistribution(final Set<N> set1, final Set<N> set2, final FrequencyTable frequencyTable) {

        }

        @Override
        public MsnSpectrum getSpectrum(final MsnNode node) {

            return msnNodeMap.get(node);
        }

        @Override
        public WithinRunConsensus getSpectrum(final WithinRunNode withinRunNode) {

            return null;
        }

        @Override
        public BetweenRunConsensus getSpectrum(final BetweenRunNode betweenRunNode) {

            return null;
        }

        @Override
        public Stream<RunNode> getRunNodeStream() {

            return null;
        }

        @Override
        public Stream<BetweenRunNode> getBetweenRunNodeStream() {

            return null;
        }

        @Override
        public Optional<RunNode> getRunNode(final UUID runId) {

            return null;
        }
    }
}