package org.expasy.glycoforest.app;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.expasy.mzjava.glycomics.io.mol.glycoct.GlycoCTReader;
import org.expasy.mzjava.glycomics.mol.Glycan;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycoctCleaner {

    public static void main(String[] args) throws IOException {

        Map<String, String> roots = new HashMap<>();

        File[] files = new File("C:\\Users\\ohorlach\\Documents\\tmp\\glycoforest\\theoretical_forrest\\o_cores").listFiles((FileFilter) new SuffixFileFilter(".glycoct_condensed"));
        if(files == null) files = new File[0];
        for (File file : files) {

            roots.put(
                    FilenameUtils.removeExtension(file.getName()),
                    Files.toString(file, Charset.defaultCharset())
            );
        }

        StringWriter writer = new StringWriter();
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(writer, roots);
        System.out.println(writer);                      //sout
    }

    public static void a_main(String[] args) throws IOException {

        List<Map<String, String>> structures = new ObjectMapper()
                .reader(List.class)
                .readValue(new FileReader("C:\\Users\\ohorlach\\Documents\\IdeaProjects\\glycoforest-spark\\glycoforest-scratch\\src\\main\\resources\\org\\expasy\\glycoforest\\scratch\\jin_structures.json"));

        Set<String> exclusions = Sets.newHashSet(
                "733-2", //has root that the parser can't handle
                "911-1"  //has root that the parser can't handle
        );

        for (Map<String, String> next : structures) {

            final String id = next.get("id");
            final String glycoct = next.get("glycoct");

            if (!exclusions.contains(id)) {
                GlycoCTReader glycoCTReader = new GlycoCTReader();
                Glycan glycan = glycoCTReader.read(glycoct, id);

                final double mz = glycan.calculateMz(1);
                final String nominalMass = id.split("-")[0];
                if (!nominalMass.equals(Integer.toString((int) mz))) {

                    System.out.println(id + "\t" + mz);                      //sout
                }
            }
        }
    }
}
