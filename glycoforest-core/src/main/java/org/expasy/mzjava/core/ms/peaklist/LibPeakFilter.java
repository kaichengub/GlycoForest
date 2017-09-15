package org.expasy.mzjava.core.ms.peaklist;

import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;

import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class LibPeakFilter extends AbstractPeakProcessor<LibPeakAnnotation, LibPeakAnnotation> {

    private final int minMergePeakCount;

    public LibPeakFilter(int minMergePeakCount) {

        this.minMergePeakCount = minMergePeakCount;
    }

    @Override
    public void processPeak(double mz, double intensity, List<LibPeakAnnotation> annotations) {

        if (annotations.get(0).getMergedPeakCount() > minMergePeakCount) {

            sink.processPeak(mz, intensity, annotations);
        }
    }
}
