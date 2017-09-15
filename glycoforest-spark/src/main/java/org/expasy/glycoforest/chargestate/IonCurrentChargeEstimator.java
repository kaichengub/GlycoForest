package org.expasy.glycoforest.chargestate;

import org.expasy.mzjava.core.ms.peaklist.PeakCursor;
import org.expasy.mzjava.core.ms.peaklist.PeakList;

import java.util.Collection;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class IonCurrentChargeEstimator implements MemberChargeEstimator {

    private final double cutOff;
    private final double precursorDelta;

    /**
     * Construct IonCurrentChargeEstimator
     *
     * @param cutOff the cut off for
     * @param precursorDelta the m/z to add to the precursor m/z. This can be used to account for isotopes.
     */
    public IonCurrentChargeEstimator(double cutOff, double precursorDelta) {

        this.cutOff = cutOff;
        this.precursorDelta = precursorDelta;
    }

    @Override
    public int[] estimateChargeState(Collection<? extends PeakList> members) {

        double sum = 0;
        double tic = 0;
        for (PeakList peakList : members) {

            final PeakCursor cursor = peakList.cursor();
            final double minMz = peakList.getPrecursor().getMz() + precursorDelta;
            cursor.moveBefore(minMz);
            while (cursor.next()) {

                if (cursor.currMz() > minMz) {

                    sum += cursor.currIntensity();
                }
            }
            tic += peakList.getTotalIonCurrent();
        }

        final double fraction = sum / tic;

        return new int[]{fraction > cutOff ? 2 : 1};
    }
}
