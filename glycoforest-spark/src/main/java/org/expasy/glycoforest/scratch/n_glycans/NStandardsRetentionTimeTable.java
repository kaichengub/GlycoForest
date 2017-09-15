package org.expasy.glycoforest.scratch.n_glycans;

import com.google.common.collect.Range;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.parser.GigCondensedReader;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class NStandardsRetentionTimeTable extends RetentionTimeTable {

    public NStandardsRetentionTimeTable(final GigCondensedReader reader) {

        super(build(reader));
    }

    @Override
    public List<SugarStructure> getStructure(final String run, final Range<Double> retentionTimeInterval, final Predicate<SugarStructure> structurePredicate) {

        final List<Entry> runEntries = data.get(run);
        if (runEntries != null && structurePredicate.test(runEntries.get(0).getStructure())) {

            return Collections.singletonList(runEntries.get(0).getStructure());
        } else {

            return Collections.emptyList();
        }
    }

    private static Map<String, List<Entry>> build(final GigCondensedReader reader) {

        final Map<String, List<Entry>> data = new HashMap<>();
        data.put("080612_SF_C0820_Run3", Collections.singletonList(new Entry(0.0, reader.readStructure("C0820, asialo, agalacto, triantennary N-glycan", "GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_C0860_Run4", Collections.singletonList(new Entry(0.0, reader.readStructure("C0860, asialo, agalacto, bianntennary fucosylated", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_C0870_Run6", Collections.singletonList(new Entry(0.0, reader.readStructure("C0870, monogalactosylated asialo, biantennary", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_C0920_Run7", Collections.singletonList(new Entry(0.0, reader.readStructure("C0920, asialo, galactosylated, biantennary", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_C0940_Run9", Collections.singletonList(new Entry(0.0, reader.readStructure("C0940, asialo, agalacto, tetraantennary", "GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3[GlcNAcβ1-2(GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_C0960_Run10", Collections.singletonList(new Entry(0.0, reader.readStructure("C0960, asialo, agalacto, bisected triantennary", "GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3(GlcNAcβ1-4)(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_C0980_Run12", Collections.singletonList(new Entry(0.0, reader.readStructure("C0980, asialo, agalacto, bisected, biantennary fucosylated", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_C1026_Run13", Collections.singletonList(new Entry(0.0, reader.readStructure("C1026, asialo galacosylated, fucosylated, biantennary", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_C1070_Run15", Collections.singletonList(new Entry(0.0, reader.readStructure("C1070, monogalactosylated, fucosylated, bisected, biantennary", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_C1124_Run16", Collections.singletonList(new Entry(0.0, reader.readStructure("C1124, asialo, galactosylated, triantennary", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_C1160_Run18", Collections.singletonList(new Entry(0.0, reader.readStructure("C1160, asialo, agalacto, bisected pentaantennary", "GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3[GlcNAcβ1-2(GlcNAcβ1-4)(GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_C1170_Run19", Collections.singletonList(new Entry(0.0, reader.readStructure("C1170, monogalactosylated, fucosylated, bisected, biantennary (NA2FB)", "Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_C1224_Run21", Collections.singletonList(new Entry(0.0, reader.readStructure("C1224, asialo, galactosylated, tetraantennary, N-linked glycan (NA4)", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα-Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_MC0320_Run22", Collections.singletonList(new Entry(0.0, reader.readStructure("MC0320, Man-1", "Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_MC0410_Run24", Collections.singletonList(new Entry(0.0, reader.readStructure("MC0410, Man-1-Fuc", "Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_MC0420_Run25", Collections.singletonList(new Entry(0.0, reader.readStructure("MC0420, Man-2(a)", "Manα1-6Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_MC0521_Run27", Collections.singletonList(new Entry(0.0, reader.readStructure("MC0521, high mannose type N-glycans", "Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_MC0661_Run28", Collections.singletonList(new Entry(0.0, reader.readStructure("MC0661, pentasaccharie core (1-6) fucosylated (Man-3F)", "Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_MC0721_Run30", Collections.singletonList(new Entry(0.0, reader.readStructure("MC0721, oligomannose-3 xylose, 1-3 fucose (Man-3-Xyl, Fuc)", "Xylβ1-2(Manα1-3)(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_MC0731_Run31", Collections.singletonList(new Entry(0.0, reader.readStructure("MC0731, oligomannose-5 (Man-5)", "Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_MC0831_Run33", Collections.singletonList(new Entry(0.0, reader.readStructure("MC0831, oligomannose-6 (Man-6)", "Manα1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_MC0941_Run34", Collections.singletonList(new Entry(0.0, reader.readStructure("Dextrauk, MC0941 hydrid with bisecting GlcNAc", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_MC1131_Run36", Collections.singletonList(new Entry(0.0, reader.readStructure("Dextrauk, MC1131 Oligomannose-9", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_MC1231_Run37", Collections.singletonList(new Entry(0.0, reader.readStructure("Dextrauk, MC1231 Oligomannose-9-Glc", "Glcα1-3Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_SC1020_Run39", Collections.singletonList(new Entry(0.0, reader.readStructure("Dextrauk, SC1020 Monosialo (2,6), biantennary (A1)", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));
        data.put("080612_SF_SC1120_Run40", Collections.singletonList(new Entry(0.0, reader.readStructure("Dextrauk, SC1120 Disialo (2,6), biantennary (A2)", "NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE)));

        return data;
    }
}
