package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import com.google.common.base.Preconditions;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.tools.glycoforest.graphdb.SpectrumNode;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public abstract class HSpectrumNode<S extends PeakList> implements SpectrumNode<S> {

    private final UUID spectrumId;
    private final double totalIonCurrent;
    private final double precursorIntensity;
    private final double precursorMz;
    private final int precursorCharge;

    public HSpectrumNode(PeakList spectrum) {

        Preconditions.checkNotNull(spectrum);

        spectrumId = spectrum.getId();
        totalIonCurrent = spectrum.getTotalIonCurrent();
        Peak precursor = spectrum.getPrecursor();
        precursorIntensity = precursor.getIntensity();
        precursorMz = precursor.getMz();
        precursorCharge = precursor.getCharge();
    }

    @Override
    public UUID getSpectrumId() {

        return spectrumId;
    }

    @Override
    public double getTotalIonCurrent() {

        return totalIonCurrent;
    }

    @Override
    public double getPrecursorIntensity() {

        return precursorIntensity;
    }

    @Override
    public double getPrecursorMz() {

        return precursorMz;
    }

    @Override
    public int getPrecursorCharge() {

        return precursorCharge;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HSpectrumNode that = (HSpectrumNode) o;
        return Objects.equals(spectrumId, that.spectrumId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(spectrumId);
    }
}
