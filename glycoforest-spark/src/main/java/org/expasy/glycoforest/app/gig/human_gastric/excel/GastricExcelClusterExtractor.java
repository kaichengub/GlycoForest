package org.expasy.glycoforest.app.gig.human_gastric.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.expasy.glycoforest.curated.data.ClusterEntry;
import org.expasy.glycoforest.app.data.StructureQuantEntry;
import org.expasy.glycoforest.app.gig.ExcelChromatographyExtractor;
import org.expasy.glycoforest.app.gig.ExcelClusterExtractor;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GastricExcelClusterExtractor {

    public static void a_main(String[] args) throws SQLException, IOException, ClassNotFoundException {

        final List<String> runs = Lists.newArrayList(
                "100929_es1",
                "100929_es08",
                "100929_es10",
                "10062011_ES05",
                "100929_es09",
                "111116es_15",
                "100929_es3",
                "111116es_14",
                "100929_es2",
                "101215_es12",
                "101215_es11",
                "111116es_6",
                "JC_131118ES16",
                "101216_es_13",
                "100929_es7",
                "101215_es17",
                "100929_es4"
        );

        final Map<String, String> intensityColumnMap = new HashMap<>();
        intensityColumnMap.put("100929_es1", "Intensity 100929_es1");
        intensityColumnMap.put("100929_es08", "Intensity2 100929_es08");
        intensityColumnMap.put("100929_es10", "Intensity3 100929_es10");
        intensityColumnMap.put("10062011_ES05", "Intensity4 10062011_ES05");
        intensityColumnMap.put("100929_es09", "Intensity5 100929_es09");
        intensityColumnMap.put("111116es_15", "Intensity6 111116es_15");
        intensityColumnMap.put("100929_es3", "Intensity7 100929_es3");
        intensityColumnMap.put("111116es_14", "Intensity9 111116es_14");
        intensityColumnMap.put("100929_es2", "Intensity8 100929_es2");
        intensityColumnMap.put("101215_es12", "Intensity10 101215_es12");
        intensityColumnMap.put("101215_es11", "Intensity11 101215_es11");
        intensityColumnMap.put("111116es_6", "Intensity12 111116es_6");
        intensityColumnMap.put("JC_131118ES16", "Intesnsity JC_131118ES16");
        intensityColumnMap.put("101216_es_13", "Intensity13 101216_es_13");
        intensityColumnMap.put("100929_es7", "Intensity14 100929_es7");
        intensityColumnMap.put("101215_es17", "Intensity15 101215_es17");
        intensityColumnMap.put("100929_es4", "Intensity16 100929_es4");

        final Function<String, String> rtFunction = run -> "RT " + run;
        final Function<String, String> intensityFunction = intensityColumnMap::get;

        final File file = new File("C:\\Users\\ohorlach\\Documents\\tmp\\glycoforest\\gastric_mucin_updated_certain.csv");

        List<StructureQuantEntry> converted = new ExcelChromatographyExtractor().convertEntries(file, runs, rtFunction, intensityFunction);
        System.out.println(converted.size());                      //sout
    }

    public static void main(String[] args) throws IOException {

        final File root = new File("C:\\Users\\Oliver\\Documents\\tmp\\glycoforest\\");
        final Map<String, Collection<ClusterEntry>> clusterEntryMap = new ExcelClusterExtractor().extract(new File(root, "Jin-with extra epitope mapping.xlsx")).asMap();


        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(
                new File(root, "gastric_curated_clusters.json"),
                clusterEntryMap
        );
    }
}
