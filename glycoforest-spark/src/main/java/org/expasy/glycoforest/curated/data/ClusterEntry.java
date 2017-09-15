package org.expasy.glycoforest.curated.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ClusterEntry {

    private final String label;
    private final int nominalMass;
    private final double mz;
    private final int startScan, endScan;
    private final double startRt, endRt;
    private final int spectraCount;

    public ClusterEntry(@JsonProperty("label") String label,
                        @JsonProperty("nominalMass") int nominalMass, @JsonProperty("mz") double mz,
                        @JsonProperty("startScan") int startScan, @JsonProperty("endScan") int endScan,
                        @JsonProperty("startRt") double startRt, @JsonProperty("endRt") double endRt,
                        @JsonProperty("spectraCount") int spectraCount) {

        this.label = label;
        this.nominalMass = nominalMass;
        this.mz = mz;
        this.startScan = startScan;
        this.endScan = endScan;
        this.startRt = startRt;
        this.endRt = endRt;
        this.spectraCount = spectraCount;
    }

    public String getLabel() {

        return label;
    }

    public int getNominalMass() {

        return nominalMass;
    }

    public double getMz() {

        return mz;
    }

    public int getStartScan() {

        return startScan;
    }

    public int getEndScan() {

        return endScan;
    }

    public double getStartRt() {

        return startRt;
    }

    public double getEndRt() {

        return endRt;
    }

    public int getSpectraCount() {

        return spectraCount;
    }

    @JsonIgnore
    public int getCharge(){

        return (int)Math.round(nominalMass/mz);
    }

    @Override
    public String toString() {

        return "ClusterEntry{" +
                "label='" + label + '\'' +
                ", nominalMass=" + nominalMass +
                ", mz=" + mz +
                ", startScan=" + startScan +
                ", endScan=" + endScan +
                ", startRt=" + startRt +
                ", endRt=" + endRt +
                ", spectraCount=" + spectraCount +
                '}';
    }
}
