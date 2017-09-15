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
public class HupoRetentionTimeTable extends RetentionTimeTable {

    public HupoRetentionTimeTable(final GigCondensedReader reader) {

        super(build(reader));
    }

    protected static Map<String, List<Entry>> build(final GigCondensedReader reader) {


        //JC_120418U937MdSNG
        final List<Entry> jC_120418U937MdSNGList = Lists.newArrayList(
                new Entry(23.52, reader.readStructure("1721-1", "Manα1-2Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.78, reader.readStructure("1883", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.45, reader.readStructure("2045", "Glcα1-3Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.49, reader.readStructure("1641", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.49, reader.readStructure("1600", "Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Man α1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.25, reader.readStructure("2006-1", "Galβ1-4GlcNAcβ1-2Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.25, reader.readStructure("1721-2", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(25.87, reader.readStructure("1933", "Fuc+Galβ1-GlcNAcβ1-2Manα1-3(Galβ1-GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(26.4, reader.readStructure("1787", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.63, reader.readStructure("2152-1", "Galβ1-4GlcNAcβ1-2Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.72, reader.readStructure("2006-2", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.14, reader.readStructure("2152-2", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)
        );

        //JC_120418L428MdSNG
        final List<Entry> jC_120418L428MdSNGList = Lists.newArrayList(
                new Entry(22.57, reader.readStructure("749", "Manα1-6Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.31, reader.readStructure("1559", "Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.36, reader.readStructure("1721-1", "Manα1-2Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.58, reader.readStructure("1883", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.85, reader.readStructure("1397", "Manα1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.29, reader.readStructure("2045", "Glcα1-3Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.38, reader.readStructure("1600", "Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Man α1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.6, reader.readStructure("1641", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.9, reader.readStructure("1584", "Galβ1-4GlcNAcβ1-2Manα1-3(Man-Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.13, reader.readStructure("1463", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.48, reader.readStructure("1422-1", "Galβ1-4GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(25.83, reader.readStructure("1933", "Fuc+Galβ1-GlcNAcβ1-2Manα1-3(Galβ1-GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(26.06, reader.readStructure("1235-2", "Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.32, reader.readStructure("1787", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.59, reader.readStructure("2152-1", "Galβ1-4GlcNAcβ1-2Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.64, reader.readStructure("2006-2", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.62, reader.readStructure("1057", "Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.71, reader.readStructure("1990", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.12, reader.readStructure("2152-2", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)
        );

        //JC_120418U937MNG
        final List<Entry> jC_120418U937MNGList = Lists.newArrayList(
                new Entry(22.08, reader.readStructure("1235-1", "Manα1-2Manα1-3(Manα1-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.16, reader.readStructure("911-1", "Manα1-3Manα1-6Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(22.83, reader.readStructure("749", "Manα1-6Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.31, reader.readStructure("1559", "Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.35, reader.readStructure("1721-1", "Manα1-2Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.57, reader.readStructure("1883", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.84, reader.readStructure("1397", "Manα1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.28, reader.readStructure("2045", "Glcα1-3Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.42, reader.readStructure("1073", "Manα1-3(Man-Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.04, reader.readStructure("1729-1", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα1-6Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.13, reader.readStructure("1721-2", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.26, reader.readStructure("911-2", "Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.61, reader.readStructure("895", "Manα1-Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.79, reader.readStructure("2078-1", "NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.06, reader.readStructure("1235-2", "Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.15, reader.readStructure("1891-1", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.37, reader.readStructure("1932", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.42, reader.readStructure("2369", "Glcα1-2Glcα1-3Glcα1-3Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.6, reader.readStructure("1875", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Man-Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.33, reader.readStructure("1713-1", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.6, reader.readStructure("1057", "Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.66, reader.readStructure("2443-1", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.87, reader.readStructure("2078-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.28, reader.readStructure("1729-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα1-3Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.32, reader.readStructure("2517", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.45, reader.readStructure("2589", "NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-3[Galβ1-4Galβ1-4(Fucα1-3)GlcNAcβ1-2Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.65, reader.readStructure("1422-2", "Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.97, reader.readStructure("1891-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα1-2Manα1-3Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(29.03, reader.readStructure("2443-2", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(29.24, reader.readStructure("2369-1", "NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(29.53, reader.readStructure("2443-3", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(30.04, reader.readStructure("2515", "NeuAcα2-3Galβ1-4(Fucα1-3)GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(30.47, reader.readStructure("2078-3", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(30.65, reader.readStructure("2223-2", "NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(30.83, reader.readStructure("2588", "NeuAcα2-6Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-3(NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(31.27, reader.readStructure("3100-1", "2NeuAcα2-6 + Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(31.78, reader.readStructure("2734-1", "NeuAcα2-3Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(31.91, reader.readStructure("2369-2", "NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(32.33, reader.readStructure("3100-2", "2NeuAcα2-6 + Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(32.76, reader.readStructure("1713-2", "Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(32.98, reader.readStructure("2734-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(33.28, reader.readStructure("3026-1", "NeuAcα2-3Galβ1-4GlcNAcβ1-2(NeuAcα2-3Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(33.86, reader.readStructure("2734-3", "NeuAcα2-3Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(34.08, reader.readStructure("2369-3", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)
//                  new Entry(34.12, reader.readStructure("3100-3", "2NeuAcα2-6 + Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(38.09, reader.readStructure("3682-1", "2NeuAcα2-3 + 2NeuAcα2-6 + Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2 (Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
//                  new Entry(40.0, reader.readStructure("3682-2", "2NeuAcα2-3 + 2NeuAcα2-6 + Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2 (Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"))
        );

        //JC_120418L428MNG
        final List<Entry> jC_120418L428MNGList = Lists.newArrayList(
                new Entry(23.17, reader.readStructure("1559", "Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.21, reader.readStructure("1721-1", "Manα1-2Manα1-2Manα1-3[Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.43, reader.readStructure("1883", "Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(23.74, reader.readStructure("1397", "Manα1-2Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(24.18, reader.readStructure("2045", "Glcα1-3Manα1-2Manα1-2Manα1-3[Manα1-2Manα1-3(Manα1-2Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.09, reader.readStructure("1463", "GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.51, reader.readStructure("895", "Manα1-Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.51, reader.readStructure("1567", "NeuAcα2-GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(25.6, reader.readStructure("1933", "Fuc+Galβ1-GlcNAcβ1-2Manα1-3(Galβ1-GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(25.65, reader.readStructure("2078-1", "NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(25.96, reader.readStructure("1235-2", "Manα1-3[Manα1-3(Manα1-6)Manα1-6]Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.14, reader.readStructure("1932", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.36, reader.readStructure("1787", "Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.45, reader.readStructure("1875", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Man-Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(26.63, reader.readStructure("2152-1", "Galβ1-4GlcNAcβ1-2Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.03, reader.readStructure("2223-1", "NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.07, reader.readStructure("1713-1", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.25, reader.readStructure("1916", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(27.66, reader.readStructure("2078-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.06, reader.readStructure("2152-2", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.15, reader.readStructure("2517", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.33, reader.readStructure("2589", "NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-3[Galβ1-4Galβ1-4(Fucα1-3)GlcNAcβ1-2Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(28.9, reader.readStructure("2369-1", "NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(29.87, reader.readStructure("2443-2", "Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(30.25, reader.readStructure("2078-3", "Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(30.25, reader.readStructure("2734-1", "NeuAcα2-3Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(30.51, reader.readStructure("2588", "NeuAcα2-6Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-3(NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(30.96, reader.readStructure("3100-1", "2NeuAcα2-6 + Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(31.39, reader.readStructure("2734-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(31.48, reader.readStructure("2369-2", "NeuAcα2-6Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
//                  new Entry(31.92, reader.readStructure("3100-2", "2NeuAcα2-6 + Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol")),
                new Entry(32.66, reader.readStructure("3026-1", "NeuAcα2-3Galβ1-4GlcNAcβ1-2(NeuAcα2-3Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(32.75, reader.readStructure("2734-3", "NeuAcα2-3Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(33.66, reader.readStructure("2369-3", "NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE),
                new Entry(34.85, reader.readStructure("3026-2", "NeuAcα2-3Galβ1-4GlcNAcβ1-2(NeuAcα2-3Galβ1-4GlcNAcβ1-4)Manα1-3(NeuAcα2-3Galβ1-4GlcNAcβ1-2Manα1-6)Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"), TimeUnit.MINUTE)
//                  new Entry(38.11, reader.readStructure("3682-1", "2NeuAcα2-3 + 2NeuAcα2-6 + Galβ1-4GlcNAcβ1-2(Galβ1-4GlcNAcβ1-4)Manα1-3[Galβ1-4GlcNAcβ1-2 (Galβ1-4GlcNAcβ1-6)Manα1-6]Manβ1-4GlcNAcβ1-4(Fucα1-6)GlcNAcol"))
        );

        final Map<String, List<Entry>> data = new HashMap<>();
        data.put("JC_120418L428MdSNG", jC_120418L428MdSNGList);
        data.put("JC_120418L428MNG", jC_120418L428MNGList);
        data.put("JC_120418U937MdSNG", jC_120418U937MdSNGList);
        data.put("JC_120418U937MNG", jC_120418U937MNGList);
        return data;
    }
}
