package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import org.expasy.mzjava.tools.glycoforest.graphdb.GraphRepository;
import org.expasy.mzjava.tools.glycoforest.graphdb.WithinRunNode;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class HWithinRunNode extends HSpectrumNode<WithinRunConsensus> implements WithinRunNode {

    private final double retentionTime;
    private final UUID runId;
    private final double minMemberMz;
    private final double maxMemberMz;
    private final double simScoreMean;
    private final double simScoreStdev;

    public HWithinRunNode(WithinRunConsensus spectrum) {

        super(spectrum);

        retentionTime = spectrum.getRetentionTime();
        runId = spectrum.getRunId();
        minMemberMz = spectrum.getMinMemberMz();
        maxMemberMz = spectrum.getMaxMemberMz();
        simScoreMean = spectrum.getSimScoreMean();
        simScoreStdev = spectrum.getSimScoreStdev();
    }

    @Override
    public double getRetentionTime() {

        return retentionTime;
    }

    @Override
    public UUID getRunId() {

        return runId;
    }

    public double getMinMemberMz() {

        return minMemberMz;
    }

    @Override
    public double getMaxMemberMz() {

        return maxMemberMz;
    }

    @Override
    public WithinRunConsensus loadSpectrum(GraphRepository repository) {

        return repository.getSpectrum(this);
    }

    @Override
    public double getSimScoreMean() {

        return simScoreMean;
    }

    @Override
    public double getSimScoreStdev() {

        return simScoreStdev;
    }
}
