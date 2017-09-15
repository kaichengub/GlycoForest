package org.expasy.glycoforest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.IonType;
import org.expasy.mzjava.glycomics.io.mol.glycoct.GlycoCTReader;
import org.expasy.mzjava.glycomics.mol.Glycan;
import org.expasy.mzjava.glycomics.mol.GlycanFragment;
import org.expasy.mzjava.glycomics.ms.fragment.GlycanFragmenter;
import org.expasy.mzjava.glycomics.ms.spectrum.GlycanFragAnnotation;
import org.expasy.mzjava.glycomics.ms.spectrum.GlycanSpectrum;

import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class Fragmenter {

    public static void main(String[] args) throws IOException {

        Map<String, String> glycoctMap = new ObjectMapper()
                .reader(Map.class)
                .readValue(new FileReader("C:\\Users\\ohorlach\\Documents\\IdeaProjects\\glycoforest-spark\\glycoforest-scratch\\src\\main\\resources\\org\\expasy\\glycoforest\\scratch\\jin_structures.json"));

//        fragment(new GlycoCTReader().read(glycoctMap.get("952-4"), "952-4"));
//        System.out.println();                      //sout
        fragment(new GlycoCTReader().read(glycoctMap.get("1114-1"), "1114-1"));
        System.out.println();                      //sout

        String glycoct = "RES\n" +
                "1b:o-dgal-HEX-0:0|1:aldi\n" +
                "2b:x-HEX-1:5\n" +
                "3b:x-HEX-1:5\n" +
                "4b:x-HEX-1:5\n" +
                "5b:x-HEX-1:5\n" +
                "6b:x-HEX-1:5\n" +
                "7s:n-acetyl\n" +
                "8s:n-acetyl\n" +
                "9s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+1)2d\n" +
                "2:2o(-1+1)3d\n" +
                "3:3o(-1+1)4d\n" +
                "4:4o(-1+1)5d\n" +
                "5:5o(-1+1)6d\n" +
                "6:5d(2+1)7n\n" +
                "7:3d(2+1)8n\n" +
                "8:1d(2+1)9n";
        fragment(new GlycoCTReader().read(glycoct, "ID"));
    }

    private static void fragment(Glycan glycan) {

        final GlycanFragmenter glycanFragmenter = new GlycanFragmenter(EnumSet.of(IonType.b), false, false, PeakList.Precision.DOUBLE, 1, 0);
        final GlycanSpectrum spectrum = glycanFragmenter.fragment(glycan, 1);
        for(int i = 0; i < spectrum.size(); i++) {

            final List<GlycanFragAnnotation> annotations = spectrum.getAnnotations(i);
            final GlycanFragment fragment = annotations.get(0).getFragment();
            final IonType ionType = fragment.getIonType(fragment.getCleavedEdges().iterator().next());
            if (ionType.equals(IonType.b)) {
                System.out.println(NumberFormat.getNumberInstance().format(spectrum.getMz(i)) + "\t" + ionType + "\t" + annotations.size());                      //sout
            }
        }
    }
}