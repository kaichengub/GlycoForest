package org.expasy.glycoforest.app.gig.fish_mucin.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.expasy.glycoforest.curated.data.ClusterEntry;
import org.expasy.glycoforest.app.gig.ExcelClusterExtractor;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FishExcelClusterExtractor {

    public static void main(String[] args) throws IOException {

        final File root = new File("C:\\Users\\Oliver\\Documents\\tmp\\glycoforest\\");
        final Map<String, Collection<ClusterEntry>> clusterEntryMap = new ExcelClusterExtractor().extract(new File(root, "fish_curated_structures.xlsx")).asMap();

        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(
                new File(root, "fish_curated_clusters.json"),
                clusterEntryMap
        );
    }
}
