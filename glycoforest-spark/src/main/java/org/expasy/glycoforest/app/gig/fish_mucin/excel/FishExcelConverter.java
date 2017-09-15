package org.expasy.glycoforest.app.gig.fish_mucin.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import org.expasy.glycoforest.curated.data.ClusterEntry;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.app.data.QuantEntry;
import org.expasy.glycoforest.app.data.StructureQuantEntry;
import org.expasy.glycoforest.app.gig.ExcelChromatographyExtractor;
import org.expasy.glycoforest.app.gig.ExcelClusterExtractor;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FishExcelConverter {

    public static void main(String[] args) throws Exception {

        final Map<String, String> runMap = new LinkedHashMap<>();
        runMap.put("JC_131209FMS1", "S1");
        runMap.put("JC_131209FMS2", "S2");
        runMap.put("JC_131209FMS3", "S3");
        runMap.put("JC_131209FMS4", "S4");
        runMap.put("JC_131209FMS5", "S5");
        runMap.put("JC_131209PMDc1", "DC1");
        runMap.put("JC_131209PMDc2", "DC2");
        runMap.put("JC_131209PMDc3", "DC3");
        runMap.put("JC_131210FPDc4", "DC4");
        runMap.put("JC_131210PMDc5", "DC5");
        runMap.put("JC_131210FMpc1", "PC1");
        runMap.put("JC_131210FMpc2", "PC2");
        runMap.put("JC_131210FMpc3", "PC3");
        runMap.put("JC_131210FMpc4", "PC4");
        runMap.put("JC_131210FMpc5", "PC5");
        runMap.put("JC_131210FMpx1", "PX1");
        runMap.put("JC_131210FMpx2", "Px2");
        runMap.put("JC_131210PMpx3", "PX3");
        runMap.put("JC_131210PMpx4", "PX4");
        runMap.put("JC_131210PMpx5", "PX5");

        final Function<String, String> rtFunction = runId -> "RT (" + runMap.get(runId) + ")";
        final Function<String, String> intensityFunction = runId -> "Intensity (" + runMap.get(runId) + ")";

        final File file = new File("C:\\Users\\ohorlach\\Documents\\tmp\\fish_mucin\\overview_table.csv");
        final List<StructureQuantEntry> structureEntries = new ExcelChromatographyExtractor().convertEntries(file, runMap.keySet(), rtFunction, intensityFunction);

        System.out.println(structureEntries.size());                      //sout
    }

    public static void a_main(String[] args) throws IOException, SQLException, ClassNotFoundException {

        final Multimap<String, ClusterEntry> map = new ExcelClusterExtractor().extract(new File("C:\\Users\\ohorlach\\Documents\\tmp\\fish_mucin\\curated_structures.xlsx"));

        final Map<String, String> runMap = new LinkedHashMap<>();
        runMap.put("JC_131209FMS1", "S1");
        runMap.put("JC_131209FMS2", "S2");
        runMap.put("JC_131209FMS3", "S3");
        runMap.put("JC_131209FMS4", "S4");
        runMap.put("JC_131209FMS5", "S5");
        runMap.put("JC_131209PMDc1", "DC1");
        runMap.put("JC_131209PMDc2", "DC2");
        runMap.put("JC_131209PMDc3", "DC3");
        runMap.put("JC_131210FPDc4", "DC4");
        runMap.put("JC_131210PMDc5", "DC5");
        runMap.put("JC_131210FMpc1", "PC1");
        runMap.put("JC_131210FMpc2", "PC2");
        runMap.put("JC_131210FMpc3", "PC3");
        runMap.put("JC_131210FMpc4", "PC4");
        runMap.put("JC_131210FMpc5", "PC5");
        runMap.put("JC_131210FMpx1", "PX1");
        runMap.put("JC_131210FMpx2", "Px2");
        runMap.put("JC_131210PMpx3", "PX3");
        runMap.put("JC_131210PMpx4", "PX4");
        runMap.put("JC_131210PMpx5", "PX5");

        final Function<String, String> rtFunction = runId -> "RT (" + runMap.get(runId) + ")";
        final Function<String, String> intensityFunction = runId -> "Intensity (" + runMap.get(runId) + ")";

        final File file = new File("C:\\Users\\ohorlach\\Documents\\tmp\\fish_mucin\\overview_table.csv");
        final List<StructureQuantEntry> structureEntries = new ExcelChromatographyExtractor().convertEntries(file, runMap.keySet(), rtFunction, intensityFunction);

        int matches = 0;
        int total = 0;
        for(StructureQuantEntry structureQuantEntry : structureEntries) {

            ClusterEntry clusterEntry = null;
            for(Map.Entry<String, QuantEntry> entry : structureQuantEntry.getQuantMap().entrySet()) {

                double rt = entry.getValue().getRetentionTime();
                for(ClusterEntry current : map.get(entry.getKey())) {

                    if(current.getNominalMass() == getNominalMass(structureQuantEntry.getName()) && rt >= current.getStartRt() && rt < current.getEndRt()){

                        clusterEntry = current;
                        break;
                    }
                }

                if(clusterEntry != null)
                    break;
            }

            total += 1;
            if (clusterEntry != null) {
                matches += 1;
                System.out.println(structureQuantEntry.getName() + "\t" + clusterEntry.getLabel());                      //sout
            } else {
                System.out.println(structureQuantEntry.getName() + "\t-");                      //sout
            }
        }

        List<SugarStructure> structures = structureEntries.stream().map(StructureQuantEntry::parseStructure).collect(Collectors.toList());

        if(structures.stream().anyMatch(structure -> structure.vertexSet().size() == 0))
            throw new IllegalStateException("Empty structure");

        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(structureEntries));                      //sout

        System.out.println("matches " + matches + " total " + total);                      //sout
        System.out.println("clusters " + map.values().size());                      //sout
    }

    private static Pattern massPatter = Pattern.compile("(\\d+).*");
    private static int getNominalMass(String name) {

        final Matcher matcher = massPatter.matcher(name);

        if (matcher.matches()) {

            return Integer.parseInt(matcher.group(1));
        } else {

            throw new IllegalStateException("Cannot extract mass from " + name);
        }
    }
}
