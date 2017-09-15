package org.expasy.glycoforest.app.chargestate;

import org.expasy.mzjava.core.ms.peaklist.PeakList;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public interface ChargeStateEstimator {

    int estimateChargeState(PeakList peakList);
}
