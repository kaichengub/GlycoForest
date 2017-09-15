package org.expasy.glycoforest.app.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureLabel {

    private final String label;
    private final Set<String> synonyms;

    public StructureLabel(@JsonProperty("label") String label, @JsonProperty("synonyms") Stream<String> synonyms) {

        this.label = label;
        this.synonyms = Collections.unmodifiableSet(synonyms.collect(Collectors.toSet()));
    }

    public String getLabel() {

        return label;
    }

    public Set<String> getSynonyms() {

        return synonyms;
    }

    @Override
    public String toString() {

        return "StructureLabel{" +
                "label='" + label + '\'' +
                ", synonyms=" + synonyms +
                '}';
    }
}
