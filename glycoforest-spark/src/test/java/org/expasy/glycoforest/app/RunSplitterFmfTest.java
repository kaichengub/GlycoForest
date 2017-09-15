package org.expasy.glycoforest.app;

import com.google.common.collect.Lists;
import org.expasy.glycoforest.app.factories.SpectrumReaderFactory;
import org.expasy.mzjava.core.io.IterativeReaders;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeDiscrete;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeList;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;
import org.expasy.mzjava.spark.Cached;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RunSplitterFmfTest {

    @Test
    public void testCall() throws Exception {

        final Cached<Tolerance> precursorTolerance = new Cached<Tolerance>() {
            @Override
            protected Tolerance build() {

                return new AbsoluteTolerance(0.3);
            }
        };

        final Cached<Predicate<MsnSpectrum>> spectrumPredicate = new Cached<Predicate<MsnSpectrum>>() {
            @Override
            protected Predicate<MsnSpectrum> build() {

                return spectrum -> true;
            }
        };

        final MsnSpectrum spectrumA1 = mockSpectrum(587.20, 180, "a1", UUID.randomUUID());
        final MsnSpectrum spectrumC1 = mockSpectrum(733.20, 181, "c1", UUID.randomUUID());
        final MsnSpectrum spectrumA2 = mockSpectrum(587.10, 182, "a2", UUID.randomUUID());
        final MsnSpectrum spectrumC2 = mockSpectrum(733.10, 183, "c2", UUID.randomUUID());
        final MsnSpectrum spectrumB1 = mockSpectrum(587.10, 182 + 121, "b1", UUID.randomUUID());
        final MsnSpectrum spectrumB2 = mockSpectrum(587.20, 184 + 121, "b2", UUID.randomUUID());

        //noinspection unchecked
        final SpectrumReaderFactory<MsnSpectrum> readerFactory = mock(SpectrumReaderFactory.class);
        when(readerFactory.spectrumReader("file")).thenReturn(IterativeReaders.fromCollection(Lists.newArrayList(
                spectrumA1,
                spectrumC1,
                spectrumA2,
                spectrumC2,
                spectrumB1,
                spectrumB2
        )));

        RunSplitterFmf runSplitterFmf = new RunSplitterFmf(readerFactory, precursorTolerance, 120, spectrumPredicate);

        List<Tuple2<UUID, List<MsnSpectrum>>> splits = Lists.newArrayList(runSplitterFmf.call(new Tuple2<>("file", UUID.randomUUID())));

        Assert.assertEquals(3, splits.size());
        Assert.assertEquals(Lists.newArrayList(spectrumA2, spectrumA1), splits.get(0)._2());
        Assert.assertEquals(Lists.newArrayList(spectrumC2, spectrumC1), splits.get(1)._2());
        Assert.assertEquals(Lists.newArrayList(spectrumB1, spectrumB2), splits.get(2)._2());
    }

    @Test
    public void testCallRejectAll() throws Exception {

        final Cached<Tolerance> precursorTolerance = new Cached<Tolerance>() {
            @Override
            protected Tolerance build() {

                return new AbsoluteTolerance(0.3);
            }
        };

        final Cached<Predicate<MsnSpectrum>> spectrumPredicate = new Cached<Predicate<MsnSpectrum>>() {
            @Override
            protected Predicate<MsnSpectrum> build() {

                return spectrum -> false;
            }
        };

        final MsnSpectrum spectrumA1 = mockSpectrum(587.20, 180, "a1", UUID.randomUUID());
        final MsnSpectrum spectrumC1 = mockSpectrum(733.20, 181, "c1", UUID.randomUUID());
        final MsnSpectrum spectrumA2 = mockSpectrum(587.10, 182, "a2", UUID.randomUUID());
        final MsnSpectrum spectrumC2 = mockSpectrum(733.10, 183, "c2", UUID.randomUUID());
        final MsnSpectrum spectrumB1 = mockSpectrum(587.10, 182 + 121, "b1", UUID.randomUUID());
        final MsnSpectrum spectrumB2 = mockSpectrum(587.20, 184 + 121, "b2", UUID.randomUUID());

        //noinspection unchecked
        final SpectrumReaderFactory<MsnSpectrum> readerFactory = mock(SpectrumReaderFactory.class);
        when(readerFactory.spectrumReader("file")).thenReturn(IterativeReaders.fromCollection(Lists.newArrayList(
                spectrumA1,
                spectrumC1,
                spectrumA2,
                spectrumC2,
                spectrumB1,
                spectrumB2
        )));

        RunSplitterFmf runSplitterFmf = new RunSplitterFmf(readerFactory, precursorTolerance, 120, spectrumPredicate);

        List<Tuple2<UUID, List<MsnSpectrum>>> splits = Lists.newArrayList(runSplitterFmf.call(new Tuple2<>("file", UUID.randomUUID())));

        Assert.assertEquals(0, splits.size());
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