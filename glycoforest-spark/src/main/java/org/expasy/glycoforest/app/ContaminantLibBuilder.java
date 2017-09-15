package org.expasy.glycoforest.app;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.expasy.glycoforest.app.factories.ConsensusPeakMergerFactory;
import org.expasy.glycoforest.app.parameters.DefaultGlycoforestParameters;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.consensus.ConsensusSpectrum;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.hadoop.io.HadoopSpectraReader;
import org.expasy.mzjava.hadoop.io.HadoopSpectraWriter;
import org.expasy.mzjava.io.ms.hadoop.OldHadoopReaderFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ContaminantLibBuilder {

    public static void main(String[] args) throws IOException {

        Map<String, NavigableMap<Integer, MsnSpectrum>> runMap = new HashMap<>();
        final File hdmsnRoot = new File("C:\\Users\\ohorlach\\Documents\\masspec\\jin\\hdio");
        File[] hdMsnFiles = hdmsnRoot.listFiles((FileFilter) new SuffixFileFilter(".hdmsn"));
        if (hdMsnFiles == null) hdMsnFiles = new File[0];
        for (File file : hdMsnFiles) {

            NavigableMap<Integer, MsnSpectrum> scanNumberMap = new TreeMap<>();
            runMap.put(FilenameUtils.removeExtension(file.getName()), scanNumberMap);
            HadoopSpectraReader<PeakAnnotation, MsnSpectrum> reader = OldHadoopReaderFactory.msnReader(file);
            while (reader.hasNext()) {

                MsnSpectrum spectrum = reader.next();
                scanNumberMap.put(spectrum.getScanNumbers().getFirst().getValue(), spectrum);
            }
            reader.close();
        }

        final ConsensusPeakMergerFactory mergePeakFilterFactory = new DefaultGlycoforestParameters().withinRunConsensusPeakMerger();
        final Tolerance tolerance = new AbsoluteTolerance(0.3);
        final List<MsnSpectrum> consensusSpectra = new ArrayList<>();
        for(ContaminantRecord record : readRecords()){

            final double mz = record.mz;
            final List<MsnSpectrum> spectra = runMap.get(record.runName).subMap(record.startScan, true, record.endScan, true).values().stream()
                    .filter(spectrum -> tolerance.withinTolerance(mz, spectrum.getPrecursor().getMz()))
                    .collect(Collectors.toList());

            if (spectra.isEmpty())
                throw new IllegalStateException("Did not find any spectra for " + record.runName + "\t" + record.mz + "\t" + record.startScan + "\t" + record.endScan);

            SummaryStatistics mzStats = new SummaryStatistics();
            Sum intensitySum = new Sum();
            SummaryStatistics chargeStats = new SummaryStatistics();

            spectra.stream().forEach(spectrum -> {

                final Peak precursor = spectrum.getPrecursor();
                mzStats.addValue(precursor.getMz());
                intensitySum.increment(spectrum.getTotalIonCurrent());
                chargeStats.addValue(precursor.getCharge());
            });

            ConsensusSpectrum<LibPeakAnnotation> consensus = new ConsensusSpectrum<>(100, PeakList.Precision.DOUBLE, Collections.emptySet());
            ClusterUtils.addPeaksToConsensus(spectra, consensus, mergePeakFilterFactory);

            MsnSpectrum spectrum = new MsnSpectrum(consensus.size(), PeakList.Precision.DOUBLE);
            spectrum.getPrecursor().setValues(mzStats.getMean(), intensitySum.getResult(), (int) Math.round(chargeStats.getMean()));
            spectrum.getScanNumbers().add(record.startScan, record.endScan);
            spectrum.setSpectrumSource(new File(hdmsnRoot, record.runName + ".hdmsn").toURI());
            for(int i = 0; i < consensus.size(); i++) {
                spectrum.add(consensus.getMz(i), consensus.getIntensity(i));
            }
            consensusSpectra.add(spectrum);
        }

        Collections.sort(consensusSpectra, (c1, c2) -> c1.getPrecursor().compareTo(c2.getPrecursor()));
        final File libFile = new File("C:\\Users\\ohorlach\\Documents\\tmp\\glycoforest\\contaminants.hdmsn");
        if(libFile.exists() && !libFile.delete()) throw new IllegalStateException("Could not delete " + libFile);

        final HadoopSpectraWriter<MsnSpectrum> writer = HadoopSpectraWriter.msnWriter(libFile);
        for(MsnSpectrum spectrum : consensusSpectra)
            writer.write(spectrum);
        writer.close();
    }

    private static List<ContaminantRecord> readRecords() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\ohorlach\\Documents\\tmp\\glycoforest\\contaminants.csv"));

        List<ContaminantRecord> records = new ArrayList<>();
        reader.readLine();
        for(String line = reader.readLine(); line != null; line = reader.readLine()) {

            final String[] nextLine = line.split(",");
            records.add(new ContaminantRecord(nextLine[0], Double.parseDouble(nextLine[1]), Integer.parseInt(nextLine[2]), Integer.parseInt(nextLine[3])));
        }

        return records;
    }

    private static class ContaminantRecord {

        private final String runName;
        private final double mz;
        private final int startScan;
        private final int endScan;

        public ContaminantRecord(String runName, double mz, int startScan, int endScan) {

            this.runName = runName;
            this.mz = mz;
            this.startScan = startScan;
            this.endScan = endScan;
        }
    }
}
