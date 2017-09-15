package org.expasy.glycoforest.app;

import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class TicNormalizer {

    private final double noisePercentage;

    private enum ScaleCandidate {
        PEAK_LIST_1, PEAK_LIST_2
    }

    public TicNormalizer(double noisePercentage) {

        this.noisePercentage = noisePercentage;
    }

    public <PL extends PeakList<LibPeakAnnotation>> void normalize(PeakList<LibPeakAnnotation> src1, PeakList<LibPeakAnnotation> src2, PL target1, PL target2) {

        final ScaleCandidate scaleCandidate;
        final double scale;
        final double threshold;
        if (src1.getTotalIonCurrent() < src2.getTotalIonCurrent()) {

            scaleCandidate = ScaleCandidate.PEAK_LIST_1;
            scale = src2.getTotalIonCurrent() / src1.getTotalIonCurrent();
            threshold = calcThreshold(src1, scale);
        } else {

            scaleCandidate = ScaleCandidate.PEAK_LIST_2;
            scale = src1.getTotalIonCurrent() / src2.getTotalIonCurrent();
            threshold = calcThreshold(src2, scale);
        }

        init(target1, src1);
        init(target2, src2);
        if (scaleCandidate == ScaleCandidate.PEAK_LIST_1) {

            scale(src1, target1, scale, threshold);
            threshold(src2, target2, threshold);
        } else {

            threshold(src1, target1, threshold);
            scale(src2, target2, scale, threshold);
        }
        target1.trimToSize();
        target2.trimToSize();
    }

    private <PL extends PeakList, A extends PeakAnnotation> void init(PL target, PeakList<A> src) {

        target.clear();
        final Peak precursor = src.getPrecursor();
        target.getPrecursor().setValues(precursor.getMz(), precursor.getIntensity(), precursor.getChargeList());
    }

    private <PL extends PeakList<LibPeakAnnotation>> void scale(PeakList<LibPeakAnnotation> src, PL dest, double scale, double threshold) {

        for (int i = 0; i < src.size(); i++) {

            double intensity = src.getIntensity(i) * scale;
            if (intensity >= threshold) {

                dest.add(src.getMz(i), intensity, src.getAnnotations(i));
            }
        }
    }

    private <PL extends PeakList<LibPeakAnnotation>> void threshold(PeakList<LibPeakAnnotation> src, PL dest, double threshold) {

        for (int i = 0; i < src.size(); i++) {

            double intensity = src.getIntensity(i);
            if (intensity >= threshold) {

                dest.add(src.getMz(i), intensity, src.getAnnotations(i));
            }
        }
    }

    private double calcThreshold(PeakList<?> peakList, double scale) {

        double min = Double.MAX_VALUE;
        for (int i = 0; i < peakList.size(); i++) {

            final double intensity = peakList.getIntensity(i);
            if (intensity > 0)
                min = Math.min(min, intensity);
        }
        return (min * scale) + (min * scale * noisePercentage);
    }
}
