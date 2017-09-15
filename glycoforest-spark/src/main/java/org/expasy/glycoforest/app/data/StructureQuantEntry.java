package org.expasy.glycoforest.app.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.parser.GigCondensedReader;
import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.glycomics.mol.Glycan;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureQuantEntry {

    private final String name;
    private final String composition;
    private final String structure;
    private final Map<String, QuantEntry> quantMap;

    private Glycan glycan;

    public StructureQuantEntry(@JsonProperty("name") String name,
                               @JsonProperty("composition") String composition,
                               @JsonProperty("structure") String structure,
                               @com.fasterxml.jackson.annotation.JsonProperty("quantMap") Map<String, QuantEntry> quantMap) {

        this.name = name;
        this.composition = composition;
        this.structure = structure;
        this.quantMap = Collections.unmodifiableMap(new LinkedHashMap<>(quantMap));
    }

    public String getName() {

        return name;
    }

    public String getComposition() {

        return composition;
    }

    public String getStructure() {

        return structure;
    }

    public Map<String, QuantEntry> getQuantMap() {

        return quantMap;
    }

    public SugarStructure parseStructure(){

        return new GigCondensedReader().readStructure(name, structure);
    }

    public Glycan toGlycan(Composition endComposition) {

        if(glycan == null || !endComposition.equals(glycan.getEndComposition()))
            glycan = parseStructure().toGlycan(endComposition);

        return glycan;
    }

    public static class Builder {

        private final String name;
        private final String composition;
        private final String structure;
        private Map<String, QuantEntry> quantMap = new HashMap<>();

        public Builder(String name, String composition, String structure) {

            this.name = name;
            this.composition = composition;
            this.structure = structure;
        }

        public Builder addQuantEntry(String run, QuantEntry quantEntry) {

            quantMap.put(run, quantEntry);
            return this;
        }

        public StructureQuantEntry build(){

            Preconditions.checkNotNull(quantMap, "StructureQuantEntry builder can not be reused");

            final Map<String, QuantEntry> tmp = quantMap;
            quantMap = null;

            return new StructureQuantEntry(name, composition, structure, tmp);
        }
    }

    @Override
    public String toString() {

        return "StructureQuantEntry{" +
                "name='" + name + '\'' +
                '}';
    }
}
