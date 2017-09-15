package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.tools.glycoforest.graphdb.GraphRepository;
import org.expasy.mzjava.tools.glycoforest.graphdb.MsnNode;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class HMsnNode extends HSpectrumNode<MsnSpectrum> implements MsnNode {

    private final int scanNumber;
    private final double retentionTime;

    public HMsnNode(MsnSpectrum spectrum) {

        super(spectrum);
        scanNumber = spectrum.getScanNumbers().getFirst().getValue();
        retentionTime = spectrum.getRetentionTimes().getFirst().getTime();
    }

    @Override
    public int getScanNumber() {

        return scanNumber;
    }

    @Override
    public double getRetentionTime() {

        return retentionTime;
    }

    @Override
    public MsnSpectrum loadSpectrum(GraphRepository repository) {

        return repository.getSpectrum(this);
    }
}
