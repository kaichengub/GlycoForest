package org.expasy.glycoforest.app.export.poi;

import com.google.common.base.Preconditions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Set;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ManualAnnotation {

    private final String rank;
    private final String type;
    private final String mass;
    private final String commentsText;
    private final RetentionTimeId fishRt;
    private final RetentionTimeId gastricRt;

    public ManualAnnotation(final ResultSet results, final NumberFormat rtNumberFormat) throws SQLException {

        rank = results.getString("Rank");
        type = results.getString("Type");
        mass = results.getString("Mass");
        commentsText = results.getString("Comments_Text");
        fishRt = new RetentionTimeId(results.getString("Fish"),
                formatRetentionTime(rtNumberFormat, results.getString("Fish_RT_Start")),
                formatRetentionTime(rtNumberFormat, results.getString("Fish_RT_End")));
        gastricRt = new RetentionTimeId(results.getString("Gastric"),
                formatRetentionTime(rtNumberFormat, results.getString("Gastric_RT_Start")),
                formatRetentionTime(rtNumberFormat, results.getString("Gastric_RT_End"))
        );
    }

    private String formatRetentionTime(final NumberFormat rtNumberFormat, final String rt) {

        if ("".equals(rt)) {
            return rt;
        } else {
            return rtNumberFormat.format(Double.parseDouble(rt));
        }
    }

    public boolean containsRt(final Set<RetentionTimeId> fishRetentionTimeIds, final Set<RetentionTimeId> gastricRetentionTimeIds) {

        return fishRetentionTimeIds.contains(fishRt) || gastricRetentionTimeIds.contains(gastricRt);
    }

    public String getRank() {

        return rank;
    }

    public String getType() {

        return type;
    }

    public String getMass() {

        return mass;
    }

    public String getCommentsText() {

        return commentsText;
    }

    public boolean containsRun(final String runName) {

        Preconditions.checkNotNull(runName);

        return runName.equals(fishRt.getRunName()) || runName.equals(gastricRt.getRunName());
    }
}
