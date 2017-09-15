package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.tools.glycoforest.graphdb.BetweenRunNode;
import org.expasy.mzjava.tools.glycoforest.graphdb.MsnNode;
import org.expasy.mzjava.tools.glycoforest.graphdb.SpectrumNode;
import org.expasy.mzjava.tools.glycoforest.graphdb.WithinRunNode;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public enum EdgeType {

    MSN_MSN,
    MSN_WRC,
    WRC_WRC,
    WRC_BRC,
    BRC_BRC;

    public static EdgeType get(PeakList vertex1, PeakList vertex2) {

        if (MsnSpectrum.class.isInstance(vertex1) && MsnSpectrum.class.isInstance(vertex2))
            return MSN_MSN;
        else if (MsnSpectrum.class.isInstance(vertex1) && WithinRunConsensus.class.isInstance(vertex2))
            return MSN_WRC;
        else if (WithinRunConsensus.class.isInstance(vertex1) && WithinRunConsensus.class.isInstance(vertex2))
            return WRC_WRC;
        else if (WithinRunConsensus.class.isInstance(vertex1) && BetweenRunConsensus.class.isInstance(vertex2))
            return WRC_BRC;
        else if (BetweenRunConsensus.class.isInstance(vertex1) && BetweenRunConsensus.class.isInstance(vertex2))
            return BRC_BRC;
        else
            throw new IllegalArgumentException("Could not find edge type for " + vertex1.getClass().getName() + " - " + vertex2.getClass().getName());
    }

    public static EdgeType get(SpectrumNode vertex1, SpectrumNode vertex2) {

        if (MsnNode.class.isInstance(vertex1) && MsnNode.class.isInstance(vertex2))
            return MSN_MSN;
        else if (MsnNode.class.isInstance(vertex1) && WithinRunNode.class.isInstance(vertex2))
            return MSN_WRC;
        else if (WithinRunNode.class.isInstance(vertex1) && WithinRunNode.class.isInstance(vertex2))
            return WRC_WRC;
        else if (WithinRunNode.class.isInstance(vertex1) && BetweenRunNode.class.isInstance(vertex2))
            return WRC_BRC;
        else if (BetweenRunNode.class.isInstance(vertex1) && BetweenRunNode.class.isInstance(vertex2))
            return BRC_BRC;
        else
            throw new IllegalArgumentException("Could not find edge type for " + vertex1.getClass().getName() + " - " + vertex2.getClass().getName());
    }
}
