package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import org.expasy.mzjava.tools.glycoforest.graphdb.BetweenRunNode;
import org.expasy.mzjava.tools.glycoforest.graphdb.GraphRepository;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class HBetweenRunNode extends HSpectrumNode<BetweenRunConsensus> implements BetweenRunNode {

    private final int withinRunNodeCount;
    private final int msnNodecount;
    private final int runNodeCount;
    private final double simScoreMean;
    private final double simScoreStdev;
    private double minWithinRunMz;
    private double maxWithinRunMz;

    public HBetweenRunNode(BetweenRunConsensus consensus) {

        super(consensus);
        withinRunNodeCount = consensus.getWithinRunMemberCount();
        msnNodecount = consensus.getMsnMemberCount();
        runNodeCount = consensus.getRunIds().size();
        simScoreMean = consensus.getSimScoreMean();
        simScoreStdev = consensus.getSimScoreStdev();
        minWithinRunMz = consensus.getMinWithinRunMz();
        maxWithinRunMz = consensus.getMaxWithinRunMz();
    }

    @Override
    public int getWithinRunNodeCount() {

        return withinRunNodeCount;
    }

    @Override
    public int getMsnNodeCount() {

        return msnNodecount;
    }

    @Override
    public int getRunNodeCount() {

        return runNodeCount;
    }

    @Override
    public double getSimScoreMean() {

        return simScoreMean;
    }

    @Override
    public double getSimScoreStdev() {

        return simScoreStdev;
    }

    @Override
    public double getMinWithinRunMz() {

        return minWithinRunMz;
    }

    @Override
    public double getMaxWithinRunMz() {

        return maxWithinRunMz;
    }

    @Override
    public BetweenRunConsensus loadSpectrum(GraphRepository repository) {

        return repository.getSpectrum(this);
    }
}
