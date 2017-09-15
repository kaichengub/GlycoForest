package org.expasy.glycoforest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.expasy.mzjava.core.io.IterativeReaders;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.library.DefaultSpectrumLibrary;
import org.expasy.mzjava.core.ms.library.SpectrumLibrary;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.spectrasim.NdpSimFunc;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrasim.peakpairprocessor.DefaultPeakListAligner;
import org.expasy.mzjava.core.ms.spectrasim.peakpairprocessor.transformer.PeakPairIntensitySqrtTransformer;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.core.ms.spectrum.ScanNumber;
import org.expasy.mzjava.hadoop.io.HadoopSpectraReader;
import org.expasy.mzjava.hadoop.io.HadoopSpectraWriter;
import org.expasy.mzjava.io.ms.hadoop.OldHadoopReaderFactory;
import org.expasy.mzjava.stats.FrequencyTable;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class EpitopeLibSearch {

    public static void main(String[] args) throws IOException {

        File[] hdMsnFiles = new File("C:\\Users\\ohorlach\\Documents\\masspec\\jin\\hdio").listFiles((FileFilter) new SuffixFileFilter(".hdmsn"));
        if (hdMsnFiles == null) hdMsnFiles = new File[0];
        final Multimap<String, MsnSpectrum> spectraMap = ArrayListMultimap.create();
        final Map<String, File> outputFileMap = new HashMap<>();
        for (File file : hdMsnFiles) {

            HadoopSpectraReader<PeakAnnotation, MsnSpectrum> reader = OldHadoopReaderFactory.msnReader(file);
            while (reader.hasNext()) {

                MsnSpectrum querySpectrum = reader.next();
                final String key = new File(querySpectrum.getSpectrumSource()).getName().toLowerCase();
                spectraMap.put(key, querySpectrum);
                if (!outputFileMap.containsKey(key)) {

                    outputFileMap.put(key, new File("C:\\Users\\ohorlach\\Documents\\tmp\\glycoforest\\filtered_spectra", file.getName()));
                }
            }
            reader.close();
        }
        System.out.println("Finished reading query spectra");                      //sout

        final ObjectMapper objectMapper = new ObjectMapper();
        final SimFunc<PeakAnnotation, PeakAnnotation> simFunc = newSimFunc();
        final Tolerance precursorTolerance = new AbsoluteTolerance(0.3);
        final HadoopSpectraReader<PeakAnnotation, MsnSpectrum> reader = HadoopSpectraReader.msnReader(new File("C:\\Users\\ohorlach\\Documents\\tmp\\glycoforest\\epitope_library.hdmsn"));
        final FrequencyTable tableAllSame = new FrequencyTable(0.01, "same");
        final FrequencyTable tableAllDiff = new FrequencyTable(0.01, "diff");
        final FrequencyTable tableBest = new FrequencyTable(0.01, "ndp");
        final SummaryStatistics stats = new SummaryStatistics();
        final Multimap<String, MsnSpectrum> filteredSpectra = HashMultimap.create();

        while (reader.hasNext()) {

            final MsnSpectrum libSpectrum = reader.next();
            final Map map = objectMapper.readValue(libSpectrum.getComment(), Map.class);
            final String source = map.get("source").toString().toLowerCase().replace("raw", "mzxml");
            final ScanNumber scanNumber = libSpectrum.getScanNumbers().getFirst();
            final int minScan = scanNumber.getMinScanNumber();
            final int maxScan = scanNumber.getMaxScanNumber();
            double libMz = libSpectrum.getPrecursor().getMz();
            double bestSim = 0;
            for (MsnSpectrum querySpectrum : spectraMap.get(source)) {

                final int scan = querySpectrum.getScanNumbers().getFirst().getValue();
                final double simScore = simFunc.calcSimilarity(libSpectrum, querySpectrum);
                if (scan >= minScan && scan <= maxScan && precursorTolerance.withinTolerance(libMz, querySpectrum.getPrecursor().getMz())) {

                    tableAllSame.add(simScore);
                    stats.addValue(simScore);

                    if (simScore > bestSim) {
                        bestSim = simScore;
                    }
                    filteredSpectra.put(source, querySpectrum);
                } else if (precursorTolerance.withinTolerance(libMz, querySpectrum.getPrecursor().getMz())) {

                    tableAllDiff.add(simScore);
                }
            }
            System.out.println(bestSim + ", " + map.get("sheet"));                      //sout
            tableBest.add(bestSim);
        }
        reader.close();

        for (Map.Entry<String, File> entry : outputFileMap.entrySet()) {

            System.out.println("Writing " + entry.getKey() + " to " + entry.getValue());                      //sout
            HadoopSpectraWriter<MsnSpectrum> writer = HadoopSpectraWriter.msnWriter(entry.getValue());

            for (MsnSpectrum spectrum : filteredSpectra.get(entry.getKey()))
                writer.write(spectrum);

            writer.close();
        }

        System.out.println("\n" + FrequencyTable.toStringNotNormalize("ndp", tableAllSame, tableAllDiff));                      //sout
        System.out.println("\n" + "score stats " + stats.getMean() + ", " + stats.getStandardDeviation());                      //sout

        System.out.println("\n" + tableBest.toString(false));                      //sout
    }

    private static SimFunc<PeakAnnotation, PeakAnnotation> newSimFunc() {

        //noinspection unchecked
        return new NdpSimFunc<>(0,
                new DefaultPeakListAligner<>(new AbsoluteTolerance(0.3)),
                new PeakPairIntensitySqrtTransformer<>()
        );
    }

    private static SpectrumLibrary<MsnSpectrum> readLib() throws IOException {

        final File spectraFile = new File("C:\\Users\\ohorlach\\Documents\\tmp\\glycoforest\\epitope_library.hdmsn");
        HadoopSpectraReader<PeakAnnotation, MsnSpectrum> reader = HadoopSpectraReader.msnReader(spectraFile);
        SpectrumLibrary<MsnSpectrum> lib = new DefaultSpectrumLibrary<>(new AbsoluteTolerance(0.3), IterativeReaders.toArrayList(reader));
        reader.close();

        return lib;
    }
}
