package org.expasy.glycoforest.cluster;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.expasy.mzjava.core.io.IterativeReader;
import org.expasy.mzjava.core.io.IterativeReaders;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeDiscrete;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeList;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MsRunSplitterTest {

    @Test
    public void testCollect() throws Exception {

        final MsnSpectrum spectrumA1 = mockSpectrum(587.20, 180, "a1", UUID.randomUUID());
        final MsnSpectrum spectrumC1 = mockSpectrum(733.20, 181, "c1", UUID.randomUUID());
        final MsnSpectrum spectrumA2 = mockSpectrum(587.10, 182, "a2", UUID.randomUUID());
        final MsnSpectrum spectrumC2 = mockSpectrum(733.10, 183, "c2", UUID.randomUUID());
        final MsnSpectrum spectrumB1 = mockSpectrum(587.20, 182 + 121, "b1", UUID.randomUUID());
        final MsnSpectrum spectrumB2 = mockSpectrum(587.10, 184 + 121, "b2", UUID.randomUUID());

        final MsRunSplitter runSplitter = new MsRunSplitter(new AbsoluteTolerance(0.3), 120, spectrum -> true);

        List<List<MsnSpectrum>> splits = runSplitter.split(IterativeReaders.fromCollection(Lists.newArrayList(
                spectrumA1,
                spectrumC1,
                spectrumA2,
                spectrumC2,
                spectrumB1,
                spectrumB2
        )));

        Assert.assertEquals(3, splits.size());
        Assert.assertEquals(Lists.newArrayList(spectrumA1, spectrumA2), splits.get(0));
        Assert.assertEquals(Lists.newArrayList(spectrumC1, spectrumC2), splits.get(1));
        Assert.assertEquals(Lists.newArrayList(spectrumB1, spectrumB2), splits.get(2));
    }

    @Test
    public void testCollectRejectAll() throws Exception {

        final MsnSpectrum spectrumA1 = mockSpectrum(587.20, 180, "a1", UUID.randomUUID());
        final MsnSpectrum spectrumC1 = mockSpectrum(733.20, 181, "c1", UUID.randomUUID());
        final MsnSpectrum spectrumA2 = mockSpectrum(587.10, 182, "a2", UUID.randomUUID());
        final MsnSpectrum spectrumC2 = mockSpectrum(733.10, 183, "c2", UUID.randomUUID());
        final MsnSpectrum spectrumB1 = mockSpectrum(587.20, 182 + 121, "b1", UUID.randomUUID());
        final MsnSpectrum spectrumB2 = mockSpectrum(587.10, 184 + 121, "b2", UUID.randomUUID());

        final MsRunSplitter runSplitter = new MsRunSplitter(new AbsoluteTolerance(0.3), 120, spectrum -> false);

        List<List<MsnSpectrum>> splits = runSplitter.split(IterativeReaders.fromCollection(Lists.newArrayList(
                spectrumA1,
                spectrumC1,
                spectrumA2,
                spectrumC2,
                spectrumB1,
                spectrumB2
        )));

        Assert.assertEquals(0, splits.size());
    }

    @Test
    public void testCollectSlidingMz() throws Exception {

        final MsnSpectrum spectrum1 = mockSpectrum(587.2, 180, "", UUID.randomUUID());
        final MsnSpectrum spectrum2 = mockSpectrum(587.4, 181, "", UUID.randomUUID());
        final MsnSpectrum spectrum3 = mockSpectrum(587.5, 182, "", UUID.randomUUID());
        final MsnSpectrum spectrum4 = mockSpectrum(587.6, 183, "", UUID.randomUUID());
        final MsnSpectrum spectrum5 = mockSpectrum(587.8, 184, "", UUID.randomUUID());
        final MsnSpectrum spectrum6 = mockSpectrum(588.0, 185, "", UUID.randomUUID());

        final MsRunSplitter runSplitter = new MsRunSplitter(new AbsoluteTolerance(0.3), 120, spectrum -> true);

        List<List<MsnSpectrum>> splits = runSplitter.split(IterativeReaders.fromCollection(Lists.newArrayList(
                spectrum1,
                spectrum2,
                spectrum3,
                spectrum4,
                spectrum5,
                spectrum6
        )));

        Assert.assertEquals(1, splits.size());
        Assert.assertEquals(Lists.newArrayList(
                spectrum1,
                spectrum2,
                spectrum3,
                spectrum4,
                spectrum5,
                spectrum6), splits.get(0));
    }

    @Test
    public void testCollectUnsortedRT() throws Exception {

        final MsnSpectrum spectrum1 = mockSpectrum(587.20, 180, "a1", UUID.randomUUID());
        final MsnSpectrum spectrum2 = mockSpectrum(733.20, 181, "c1", UUID.randomUUID());

        final MsRunSplitter runSplitter = new MsRunSplitter(new AbsoluteTolerance(0.3), 120, spectrum -> true);

        List<List<MsnSpectrum>> splits = runSplitter.split(IterativeReaders.fromCollection(Lists.newArrayList(
                spectrum2,
                spectrum1
        )));

        Assert.assertEquals(2, splits.size());
        //noinspection unchecked
        Assert.assertEquals(Sets.newHashSet(Collections.singleton(spectrum1), Collections.singleton(spectrum2)),
                splits.stream().map(HashSet::new).collect(Collectors.toSet())
        );
    }

    @Test(expected = IllegalStateException.class)
    public void testCollectIOException() throws Exception {

        //noinspection unchecked
        final IterativeReader<MsnSpectrum> reader = mock(IterativeReader.class);
        when(reader.hasNext()).thenReturn(true);
        when(reader.next()).thenThrow(new IOException());

        final MsRunSplitter runSplitter = new MsRunSplitter(new AbsoluteTolerance(0.3), 120, spectrum -> true);
        runSplitter.split(reader);
    }

    private MsnSpectrum mockSpectrum(double mz, double rt, String label, UUID id) {

        MsnSpectrum spectrum = mock(MsnSpectrum.class);
        when(spectrum.getPrecursor()).thenReturn(new Peak(mz, 100.0, 1));
        when(spectrum.getRetentionTimes()).thenReturn(new RetentionTimeList(new RetentionTimeDiscrete(rt, TimeUnit.SECOND)));
        when(spectrum.toString()).thenReturn(label);
        when(spectrum.getId()).thenReturn(id);
        return spectrum;
    }
}