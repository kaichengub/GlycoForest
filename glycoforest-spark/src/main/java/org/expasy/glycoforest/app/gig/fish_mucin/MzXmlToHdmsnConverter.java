package org.expasy.glycoforest.app.gig.fish_mucin;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.expasy.mzjava.core.io.ms.spectrum.MzxmlReader;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.hadoop.io.HadoopSpectraWriter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class MzXmlToHdmsnConverter {

    public static void main(String[] args) throws IOException {

        final File outRoot = new File("C:\\Users\\ohorlach\\Documents\\masspec\\jin\\fish_mucin\\hdio");
        final File[] xmlFiles = new File("C:\\Users\\ohorlach\\Documents\\masspec\\jin\\fish_mucin\\mzXML").listFiles((FileFilter) new SuffixFileFilter(".mzXML"));
        final Map<String, TIntIntMap> fileChargeCount = new LinkedHashMap<>();
        for(File xmlFile : xmlFiles) {

            final MzxmlReader xmlReader = MzxmlReader.newTolerantReader(xmlFile, PeakList.Precision.DOUBLE);
            final File outFile = new File(outRoot, FilenameUtils.removeExtension(xmlFile.getName()) + ".hdmsn");
            final HadoopSpectraWriter<MsnSpectrum> writer = HadoopSpectraWriter.msnWriter(outFile);
            final TIntIntMap chargeCountMap = new TIntIntHashMap();
            while (xmlReader.hasNext()) {

                MsnSpectrum spectrum = xmlReader.next();
                if(spectrum.getMsLevel() == 2){

                    chargeCountMap.adjustOrPutValue(spectrum.getPrecursor().getCharge(), 1, 1);
                    writer.write(spectrum);
                }
            }

            fileChargeCount.put(outFile.getName(), chargeCountMap);
            xmlReader.close();
            writer.close();
        }

        for (Map.Entry<String, TIntIntMap> entry : fileChargeCount.entrySet()) {

            final TIntIntMap chargeCountMap = entry.getValue();
            System.out.println(entry.getKey());                      //sout
            final TIntArrayList chargeList = new TIntArrayList(chargeCountMap.keySet());
            chargeList.sort();
            chargeList.forEach(charge -> {
                System.out.println(charge + "\t" + chargeCountMap.get(charge));                      //sout
                return true;
            });
            System.out.println();                      //sout
        }
    }
}
