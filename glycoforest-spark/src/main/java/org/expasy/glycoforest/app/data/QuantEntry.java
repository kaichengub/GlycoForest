package org.expasy.glycoforest.app.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class QuantEntry {

    private final double retentionTime;
    private final double intensity;

    public QuantEntry(@JsonProperty("retentionTime") double retentionTime,
                      @JsonProperty("intensity") double intensity) {

        this.retentionTime = retentionTime;
        this.intensity = intensity;
    }

    public double getRetentionTime() {

        return retentionTime;
    }

    public double getIntensity() {

        return intensity;
    }
}
