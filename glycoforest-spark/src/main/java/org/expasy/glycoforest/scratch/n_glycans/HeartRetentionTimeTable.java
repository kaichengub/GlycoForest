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
public class HeartRetentionTimeTable extends RetentionTimeTable {

    public HeartRetentionTimeTable(final GigCondensedReader reader) {

        super(build(reader));
    }

    protected static Map<String, List<Entry>> build(final GigCondensedReader reader) {

        //Human
        final List<Entry> humanList = Lists.newArrayList(
                new Entry(20.45, reader.readStructure("587", "Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.31, reader.readStructure("749", "Manα1-6Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.29, reader.readStructure("895", "Manα1-Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.78, reader.readStructure("911", "Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.71, reader.readStructure("1235", "Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.29, reader.readStructure("1397", "Manα1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.03, reader.readStructure("1463", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.68, reader.readStructure("1559-1", "Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.55, reader.readStructure("1713", "Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα2-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.83, reader.readStructure("1721-2", "Manα1-2Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.48, reader.readStructure("1787-1", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.98, reader.readStructure("1883", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.2, reader.readStructure("1891", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.55, reader.readStructure("1916", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.26, reader.readStructure("1932-1", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.53, reader.readStructure("2045", "Glcα1-3Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
//  //                    new Entry(27.02, reader.readStructure("2078-1", "NeuAc+Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(28.32, reader.readStructure("2223-2", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(33.79, reader.readStructure("2223-3", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(30.3, reader.readStructure("2369-2", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(35.13, reader.readStructure("2369-3", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)
//  //                    new Entry(29.6, reader.readStructure("2589", "NeuAcα2+Fuc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//  //                    new Entry(36.59, reader.readStructure("2734-2", "2NeuAc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"))
        );

        //Bovine pericardium
        final List<Entry> bovinePericardiumList = Lists.newArrayList(

                new Entry(20.95, reader.readStructure("571", "GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.54, reader.readStructure("749", "Manα1-6Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.45, reader.readStructure("895", "Manα1-Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.74, reader.readStructure("1235", "Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.04, reader.readStructure("1317-2", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.49, reader.readStructure("1397", "Manα1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.04, reader.readStructure("1438-2", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.15, reader.readStructure("1463", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.1, reader.readStructure("1479-3", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(19.02, reader.readStructure("1520", "GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.04, reader.readStructure("1559-1", "Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.42, reader.readStructure("1600-1", "Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Man α1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.86, reader.readStructure("1600-2", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.89, reader.readStructure("1625-2", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.71, reader.readStructure("1641-2", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.3, reader.readStructure("1666-1", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.58, reader.readStructure("1666-2", "GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(19.98, reader.readStructure("1682", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.04, reader.readStructure("1721-2", "Manα1-2Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.71, reader.readStructure("1762-1", "Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.19, reader.readStructure("1762-3", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.49, reader.readStructure("1787-1", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.57, reader.readStructure("1787-2", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.04, reader.readStructure("1803", "Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.04, reader.readStructure("1828", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.78, reader.readStructure("1844-1", "Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.19, reader.readStructure("1883", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.34, reader.readStructure("1932-1", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.98, reader.readStructure("1932-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.57, reader.readStructure("1949", "Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.41, reader.readStructure("1965", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.49, reader.readStructure("1990-1", "Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.26, reader.readStructure("1990-2", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(27.03, reader.readStructure("2078-1", "NeuAc+Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(30.38, reader.readStructure("2078-3", "NeuAc+Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(27.03, reader.readStructure("2094-1", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(30.07, reader.readStructure("2094-2", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(29.6, reader.readStructure("2110", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.8, reader.readStructure("2111", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.71, reader.readStructure("2152", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.53, reader.readStructure("2223-2", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.38, reader.readStructure("2239-2", "NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(31.74, reader.readStructure("2240-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.23, reader.readStructure("2256-1", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(31.59, reader.readStructure("2256-2", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(33.45, reader.readStructure("2281", "NeuAc+Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(30.07, reader.readStructure("2369-2", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(38.38, reader.readStructure("2369-4", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(34.99, reader.readStructure("2358-2", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(37.32, reader.readStructure("2358-3", "NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(34.53, reader.readStructure("2401-1", "NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(37.32, reader.readStructure("2401-2", "NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)
//                  new Entry(38.69, reader.readStructure("2531", "2NeuAc+NeuGc+Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol")),
//                  new Entry(38.38, reader.readStructure("2547", "NeuAc+2NeuGc+Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol")),
//                  new Entry(33.6, reader.readStructure("2605", "NeuAc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(41.62, reader.readStructure("2734-3", "2NeuAc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(40.39, reader.readStructure("2752", "NeuAc+Fuc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(36.23, reader.readStructure("2970", "NeuAc+NeuGc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol")),
//                  new Entry(42.54, reader.readStructure("3100", "2NeuAc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(43.29, reader.readStructure("3116", "NeuAc+NeuGc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(43.92, reader.readStructure("3465", "2NeuAc+Galβ1-4GlcNAcβ1-3+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"))
        );

        //Porcine pericardium
        final List<Entry> porcinePericardium = Lists.newArrayList(
                new Entry(20.94, reader.readStructure("571", "GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.51, reader.readStructure("749", "Manα1-6Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.21, reader.readStructure("1114", "GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.79, reader.readStructure("1235", "Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.6, reader.readStructure("1260-2", "Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.84, reader.readStructure("1276", "GlcNAcβ1-2Manα1-3(Manα1-6Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.46, reader.readStructure("1397", "Manα1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.19, reader.readStructure("1463", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.07, reader.readStructure("1479-3", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(19.07, reader.readStructure("1520", "GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.86, reader.readStructure("1559-1", "Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.46, reader.readStructure("1559-2", "Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.22, reader.readStructure("1600-1", "Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Man α1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.89, reader.readStructure("1600-2", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.49, reader.readStructure("1625-1", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(Manα1-Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.94, reader.readStructure("1625-2", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.74, reader.readStructure("1641-2", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.94, reader.readStructure("1641-3", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.26, reader.readStructure("1666-1", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(19.94, reader.readStructure("1682", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.01, reader.readStructure("1721-2", "Manα1-2Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.55, reader.readStructure("1746", "Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.24, reader.readStructure("1762-3", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.55, reader.readStructure("1787-1", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.63, reader.readStructure("1787-2", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.09, reader.readStructure("1803", "Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.83, reader.readStructure("1828", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.37, reader.readStructure("1844-2", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.31, reader.readStructure("1883", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.4, reader.readStructure("1932-1", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.63, reader.readStructure("1933", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.94, reader.readStructure("1949", "Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.47, reader.readStructure("1965", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.22, reader.readStructure("1990-1", "Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.32, reader.readStructure("1990-2", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(27.09, reader.readStructure("2078-1", "NeuAc+Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(28.32, reader.readStructure("2078-2", "NeuAc+Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(27.09, reader.readStructure("2094-1", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(29.86, reader.readStructure("2110", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.86, reader.readStructure("2111", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.74, reader.readStructure("2152", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.94, reader.readStructure("2223-2", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(32.5, reader.readStructure("2223-3", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.79, reader.readStructure("2239-2", "NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(31.28, reader.readStructure("2240-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.16, reader.readStructure("2256-1", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(31.13, reader.readStructure("2256-2", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(36.51, reader.readStructure("2338", "NeuAcα2+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol")),
                new Entry(31.28, reader.readStructure("2369-2", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(30.0, reader.readStructure("2385-1", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(35.12, reader.readStructure("2401-1", "NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(38.67, reader.readStructure("2401-2", "NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(40.84, reader.readStructure("2752", "NeuAc+Fuc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(37.28, reader.readStructure("3026", "NeuAc-Galβ1-4GlcNAcβ1-2(NeuAc-Galβ1-4GlcNAcβ1-2)Manα1-3(NeuAc-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)
        );

        //Porcine pulmonary
        final List<Entry> porcinePulmonaryList = Lists.newArrayList(
                new Entry(20.49, reader.readStructure("1317-1", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-4Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.07, reader.readStructure("571", "GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.28, reader.readStructure("1114", "GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.59, reader.readStructure("749", "Manα1-6Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.73, reader.readStructure("1479-1", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(Manα1-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.11, reader.readStructure("1559-1", "Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.11, reader.readStructure("1721-2", "Manα1-2Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.26, reader.readStructure("1317-2", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.4, reader.readStructure("1559-2", "Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.4, reader.readStructure("1883", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.55, reader.readStructure("1397", "Manα1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.01, reader.readStructure("1641-1", "GalNAcβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.16, reader.readStructure("1479-3", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.46, reader.readStructure("1600-1", "Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Man α1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.61, reader.readStructure("1260-1", "GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.76, reader.readStructure("1625-1", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(Manα1-Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.76, reader.readStructure("1641-2", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(23.76, reader.readStructure("1869-1", "GalNAcβ1-4+GlcNAcβ1-2(GlcNAcβ1-4)Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(23.92, reader.readStructure("1600-2", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.36, reader.readStructure("1260-2", "Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.36, reader.readStructure("1463", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.8, reader.readStructure("1666-3", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.95, reader.readStructure("1235", "Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.95, reader.readStructure("1787-1", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.1, reader.readStructure("1625-2", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.31, reader.readStructure("2111", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.55, reader.readStructure("1869-2", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(GalNAcβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.55, reader.readStructure("1932-1", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.71, reader.readStructure("1787-2", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.47, reader.readStructure("1990-2", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.63, reader.readStructure("1933", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.78, reader.readStructure("1949", "Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(27.25, reader.readStructure("2078-1", "NeuAc+Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(27.25, reader.readStructure("2094-1", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(28.32, reader.readStructure("2078-2", "NeuAc+Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(28.32, reader.readStructure("2256-1", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.63, reader.readStructure("2223-2", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.63, reader.readStructure("2239-2", "NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(29.88, reader.readStructure("2369-2", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(29.88, reader.readStructure("2385-1", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(30.95, reader.readStructure("2256-2", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(32.08, reader.readStructure("2223-3", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(32.08, reader.readStructure("2240-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(35.53, reader.readStructure("2338", "NeuAcα2+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol")),
                new Entry(36.9, reader.readStructure("3026", "NeuAc-Galβ1-4GlcNAcβ1-2(NeuAc-Galβ1-4GlcNAcβ1-2)Manα1-3(NeuAc-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)
//                  new Entry(41.51, reader.readStructure("2734-3", "2NeuAc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"))
        );

        //Porcine aorta
        final List<Entry> porcineAortaList = Lists.newArrayList(
                new Entry(20.37, reader.readStructure("1317-1", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-4Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.92, reader.readStructure("571", "GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.13, reader.readStructure("1114", "GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.28, reader.readStructure("1666-1", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-4)(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.6, reader.readStructure("749", "Manα1-6Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.6, reader.readStructure("1479-1", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(Manα1-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.95, reader.readStructure("1559-1", "Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.95, reader.readStructure("1721-2", "Manα1-2Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.1, reader.readStructure("1317-2", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.24, reader.readStructure("1883", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.39, reader.readStructure("1559-2", "Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.55, reader.readStructure("1397", "Manα1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.15, reader.readStructure("1479-3", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.3, reader.readStructure("1600-1", "Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Man α1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.61, reader.readStructure("1260-1", "GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.61, reader.readStructure("1625-1", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(Manα1-Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.76, reader.readStructure("1641-2", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.91, reader.readStructure("1600-2", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.06, reader.readStructure("1723", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(GalNAcβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.21, reader.readStructure("1463", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.35, reader.readStructure("1260-2", "Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.8, reader.readStructure("1235", "Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.8, reader.readStructure("1666-3", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.8, reader.readStructure("1787-1", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.95, reader.readStructure("1625-2", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.55, reader.readStructure("1869-2", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(GalNAcβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.55, reader.readStructure("1932-1", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.48, reader.readStructure("1990-2", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.09, reader.readStructure("1949", "Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(27.09, reader.readStructure("2078-1", "NeuAc+Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(27.09, reader.readStructure("2094-1", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.7, reader.readStructure("2281", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3GalNAcβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.02, reader.readStructure("2111", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.15, reader.readStructure("2256-1", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.3, reader.readStructure("2239-2", "NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.74, reader.readStructure("2223-2", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(29.59, reader.readStructure("2385-1", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(29.83, reader.readStructure("2369-2", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(30.62, reader.readStructure("2078-3", "NeuAc+Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(31.17, reader.readStructure("2240-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(32.19, reader.readStructure("2223-3", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(36.68, reader.readStructure("3026", "NeuAc-Galβ1-4GlcNAcβ1-2(NeuAc-Galβ1-4GlcNAcβ1-2)Manα1-3(NeuAc-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)
        );

        //Horse pericardium
        final List<Entry> horsePericardiumList = Lists.newArrayList(
                new Entry(16.01, reader.readStructure("425", "GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.2, reader.readStructure("1276", "GlcNAcβ1-2Manα1-3(Manα1-6Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.5, reader.readStructure("1114", "GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(20.62, reader.readStructure("2369-1", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.25, reader.readStructure("1559-1", "Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.25, reader.readStructure("1721-1", "Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.5, reader.readStructure("1317-2", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.5, reader.readStructure("1883", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.88, reader.readStructure("1397", "Manα1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(21.88, reader.readStructure("1438-1", "GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.26, reader.readStructure("2045", "Glcα1-3Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.38, reader.readStructure("1641-1", "GalNAcβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.51, reader.readStructure("1479-2", "Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.76, reader.readStructure("1600-1", "Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Man α1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.76, reader.readStructure("1762-1", "GlcNAcβ1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.26, reader.readStructure("1641-2", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.26, reader.readStructure("1729-1", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.39, reader.readStructure("1600-2", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.64, reader.readStructure("1463", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.14, reader.readStructure("1770", "GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.27, reader.readStructure("1235", "Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.39, reader.readStructure("1625-2", "GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.52, reader.readStructure("1803", "Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.64, reader.readStructure("1762-3", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.77, reader.readStructure("1932-1", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.89, reader.readStructure("1869-2", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(GalNAcβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.02, reader.readStructure("1787-1", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.78, reader.readStructure("1990-2", "GalNAcβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.91, reader.readStructure("1965", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.04, reader.readStructure("1787-2", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.4, reader.readStructure("2239-1", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.51, reader.readStructure("2223-1", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.57, reader.readStructure("1949", "Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.62, reader.readStructure("1729-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.33, reader.readStructure("2111", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.45, reader.readStructure("2240-1", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.45, reader.readStructure("2256-1", "Galα1-3Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.2, reader.readStructure("2110", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(29.23, reader.readStructure("2078-2", "NeuAc+Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(29.61, reader.readStructure("2256-2", "NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(29.86, reader.readStructure("2240-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(30.88, reader.readStructure("2094-2", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(32.29, reader.readStructure("2605", "NeuAc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galα1-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(32.54, reader.readStructure("2401-1", "NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(32.8, reader.readStructure("2358-2", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(34.71, reader.readStructure("2358-3", "NeuGcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(34.71, reader.readStructure("2734-1", "2NeuAc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(34.96, reader.readStructure("2369-4", "NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)
//                  new Entry(34.96, reader.readStructure("2896", "2NeuAc+NeuGc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol")),
//                  new Entry(36.86, reader.readStructure("3336", "NeuAc+Gal+Galβ1-4GlcNAcβ1-3+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(38.38, reader.readStructure("3042", "2NeuAc+NeuGc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(38.76, reader.readStructure("3116", "NeuAc+NeuGc+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(39.39, reader.readStructure("3465", "2NeuAc+Galβ1-4GlcNAcβ1-3+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(40.79, reader.readStructure("3830", "2NeuAc+2(Galβ1-4GlcNAcβ1-3)+Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-2)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"))
        );

        final Map<String, List<Entry>> data = new HashMap<>();
//        JC_150619N5	Artery epithelial cell line
        data.put("JC_150619N5", humanList);
//        JC_150619N4	Total pnt lysate bovine pericardium
        data.put("JC_150619N4", bovinePericardiumList);
//        JC_150619N1	Total ptn lysate porcine pericardium
        data.put("JC_150619N1", porcinePericardium);
//        JC_150619N2	Total ptn lysate porcine pulmonary
        data.put("JC_150619N2", porcinePulmonaryList);
//        JC_150619N3	Total ptn lysate porcine aorta
        data.put("JC_150619N3", porcineAortaList);
//        JC_160226N3	Total pnt lysate horse pericardium
        data.put("JC_160226N3", horsePericardiumList);

        return data;
    }
}
