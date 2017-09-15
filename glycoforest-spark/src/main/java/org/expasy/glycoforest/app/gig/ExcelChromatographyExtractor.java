package org.expasy.glycoforest.app.gig;

import org.apache.commons.io.FilenameUtils;
import org.expasy.glycoforest.app.data.QuantEntry;
import org.expasy.glycoforest.app.data.StructureQuantEntry;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.*;
import java.util.*;
import java.util.function.Function;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ExcelChromatographyExtractor {

    public List<StructureQuantEntry> convertEntries(File file, Collection<String> runs, Function<String, String> rtColumnFunction, Function<String, String> intensityColumnFunction) throws IOException, ClassNotFoundException, SQLException {

        Class.forName("org.relique.jdbc.csv.CsvDriver");
        Properties props = new Properties();
        props.put("charset", "UTF-8");
        Connection conn = DriverManager.getConnection("jdbc:relique:csv:" + file.getParent() + "?separator=" + URLEncoder.encode("\t", "UTF-8"), props);
        Statement stmt = conn.createStatement();

        List<StructureQuantEntry> entries = new ArrayList<>();
        ResultSet results = stmt.executeQuery("SELECT * FROM " + FilenameUtils.removeExtension(file.getName()));
        while (results.next()) {

            Map<String, QuantEntry> quantEntryMap = new LinkedHashMap<>();
            for (String run : runs) {

                quantEntryMap.put(run, new QuantEntry(
                        extractDouble(results.getString(rtColumnFunction.apply(run))),
                        extractDouble(results.getString(intensityColumnFunction.apply(run)))));
            }

            entries.add(new StructureQuantEntry(results.getString("Name"), results.getString("Composition"), results.getString("Supposed structure"), quantEntryMap));
        }
        results.close();

        return entries;
    }

    private double extractDouble(String inputString) {

        String string = inputString.trim();

        if ("nd".equals(string) || string.length() == 0)
            return 0.0;

        if (string.endsWith("**"))
            string = string.substring(0, string.length() - 2);
        else if (string.endsWith("*"))
            string = string.substring(0, string.length() - 1);

        string = string.replace(',', '.');
        try {

            return Double.parseDouble(string);
        } catch (NumberFormatException e) {

            throw new IllegalStateException("|" + inputString + "|", e);
        }
    }
}
