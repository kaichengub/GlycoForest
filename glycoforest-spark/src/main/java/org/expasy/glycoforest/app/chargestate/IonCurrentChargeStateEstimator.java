package org.expasy.glycoforest.app.chargestate;

import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakCursor;
import org.expasy.mzjava.core.ms.peaklist.PeakList;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class IonCurrentChargeStateEstimator implements ChargeStateEstimator {

    private final double cutOff;

    public IonCurrentChargeStateEstimator(double cutOff) {

        this.cutOff = cutOff;
    }

    @Override
    public int estimateChargeState(PeakList peakList) {

        final Peak precursor = peakList.getPrecursor();
        final PeakCursor cursor = peakList.cursor();
        cursor.movePast(precursor.getMz() + 3);
        double sum = 0;
        while (cursor.next()) {

            sum += cursor.currIntensity();
        }

        final double fraction = sum / peakList.getTotalIonCurrent();

        return fraction > cutOff ? 2 : 1;
    }
}
