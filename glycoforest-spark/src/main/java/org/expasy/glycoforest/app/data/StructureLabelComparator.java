package org.expasy.glycoforest.app.data;

import com.google.common.collect.ComparisonChain;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureLabelComparator implements Comparator<String> {

    private Pattern pattern = Pattern.compile("(\\d+)-?((\\d+)|(\\D+))?");

    @Override
    public int compare(String l1, String l2) {

        final String[] s1 = split(l1);
        final String[] s2 = split(l2);
        return ComparisonChain.start().compare(Integer.parseInt(s1[0]), Integer.parseInt(s2[0])).compare(s1[1], s2[1]).result();
    }

    private String[] split(String label) {

        Matcher matcher = pattern.matcher(label);

        if(matcher.matches()) {

            String p1 = matcher.group(1);
            String p2 = matcher.group(3) != null ? matcher.group(3) : matcher.group(4) != null ? matcher.group(4) : "";

            return new String[]{p1, p2};
        } else {

            return new String[]{"", ""};
        }
    }
}
