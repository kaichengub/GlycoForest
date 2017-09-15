package org.expasy.glycoforest.scratch.n_glycans;

import com.google.common.collect.Lists;
import org.expasy.glycoforest.parser.GigCondensedReader;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class PgslRetentionTimeTable extends RetentionTimeTable {

    public PgslRetentionTimeTable(final GigCondensedReader reader) {

        super(build(reader));
    }

    private static Map<String, List<Entry>> build(final GigCondensedReader reader) {

        //JC_160908Glycan01
        final List<Entry> jc_160908Glycan01List = Lists.newArrayList(
                new Entry(16.94, reader.readStructure("587", "Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(18.08, reader.readStructure("571", "GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(18.3, reader.readStructure("911-1", "Manα1-3Manα1-6Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(18.4, reader.readStructure("1114", "GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(18.9, reader.readStructure("1520", "GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(19.14, reader.readStructure("1438", "GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(19.36, reader.readStructure("1317", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(19.6, reader.readStructure("1276", "GlcNAcβ1-2Manα1-3(Manα1-6Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(19.82, reader.readStructure("1682", "GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.06, reader.readStructure("1479-1", "Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.31, reader.readStructure("1073", "Manα1-3(Man-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.56, reader.readStructure("1260-1", "GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.68, reader.readStructure("1666", "GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.94, reader.readStructure("911-2", "Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.94, reader.readStructure("1584-2", "Galβ1-4GlcNAcβ1-2Manα1-3(Man-Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.94, reader.readStructure("1641", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.41, reader.readStructure("1463", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.41, reader.readStructure("1828", "GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.52, reader.readStructure("1422-1", "Galβ1-4GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.88, reader.readStructure("1235", "Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.0, reader.readStructure("1625", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.59, reader.readStructure("1787", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.94, reader.readStructure("2152", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.32, reader.readStructure("1057", "Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.2, reader.readStructure("1260-2", "Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(24.33, reader.readStructure("2193", "2xGalβ1-4+GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3[GlcNAcβ1-2(GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(24.7, reader.readStructure("1422-2", "Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.95, reader.readStructure("1932", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(25.93, reader.readStructure("2443-1", "NeuAcα2-3+ Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(26.32, reader.readStructure("2078", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.5, reader.readStructure("1916", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(27.33, reader.readStructure("2443-2", "NeuAcα2-3+ Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(29.76, reader.readStructure("2369", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(30.02, reader.readStructure("2734", "2xNeuAcα2-3+ Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(32.7, reader.readStructure("3026", "NeuAcα2-3Galβ1-4GlcNAcβ1-2(NeuAcα2-3Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)
//                  new Entry(36.84, reader.readStructure("3391", "3xNeuAcα2-3+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"))
        );

        //JC_160908Glycan02
        final List<Entry> jc_160908Glycan02List = Lists.newArrayList(
                new Entry(18.04, reader.readStructure("571", "GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(18.37, reader.readStructure("1114", "GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(18.9, reader.readStructure("1520", "GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(19.41, reader.readStructure("1317", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.03, reader.readStructure("1479-1", "Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.4, reader.readStructure("1479-2", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.4, reader.readStructure("1584-1", "Galβ1-4(Fucα1-3)GlcNAcβ1-2Manα1-3(Man-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.53, reader.readStructure("1260-1", "GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.02, reader.readStructure("1641", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.14, reader.readStructure("1746", "Galβ1-4(Fucα1-3)GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.38, reader.readStructure("1463", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.48, reader.readStructure("1422-1", "Galβ1-4GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.84, reader.readStructure("1235", "Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.94, reader.readStructure("1625", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.5, reader.readStructure("1787", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.05, reader.readStructure("2152", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.54, reader.readStructure("2224", "NeuAcα2-3Galβ1-4(Fucα1-3)GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.17, reader.readStructure("1260-2", "Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.53, reader.readStructure("2517", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.66, reader.readStructure("1422-2", "Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.51, reader.readStructure("1916", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(25.51, reader.readStructure("2443-1", "NeuAcα2-3+ Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(25.86, reader.readStructure("2078", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(26.34, reader.readStructure("2443-2", "NeuAcα2-3+ Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(27.75, reader.readStructure("2515", "NeuAcα2-3Galβ1-4(Fucα1-3)GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(29.54, reader.readStructure("2369", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(32.61, reader.readStructure("3026", "NeuAcα2-3Galβ1-4GlcNAcβ1-2(NeuAcα2-3Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)
        );

        final Map<String, List<Entry>> data = new HashMap<>();
        data.put("JC_160908Glycan01", jc_160908Glycan01List);
        data.put("JC_160908Glycan02", jc_160908Glycan02List);
        return data;
    }
}
