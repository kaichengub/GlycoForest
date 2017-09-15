package org.expasy.glycoforest.mol;

import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.IonType;
import scala.Tuple2;

import java.util.Collection;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FragmentCoverageFunc {

    private final Tolerance tolerance;
    private final GlycanMassCalculator massCalculator;

    public FragmentCoverageFunc(GlycanMassCalculator massCalculator, Tolerance tolerance) {

        this.massCalculator = massCalculator;
        this.tolerance = tolerance;
    }

    public double calcCoverage(SugarStructure structure, PeakList peakList) {

        final List<Tuple2<SugarStructureFragment, SugarStructureFragment>> fragments = structure.fragmentPairs();
        return linkageCount(peakList, fragments) /(double)fragments.size();
    }

    public int missingLinkEvidence(SugarStructure structure, PeakList peakList) {

        final List<Tuple2<SugarStructureFragment, SugarStructureFragment>> fragments = structure.fragmentPairs();
        int linkageCount = linkageCount(peakList, fragments);

        return fragments.size() - linkageCount;
    }

    public int linkageCount(PeakList peakList, Collection<Tuple2<SugarStructureFragment, SugarStructureFragment>> fragments) {

        final int maxCharge = peakList.getPrecursor().getCharge();

        int linkageCount = 0;
        for(Tuple2<SugarStructureFragment, SugarStructureFragment> tuple : fragments) {

            double ionCurrent = 0;
            for(int z = 1; z <= maxCharge; z++) {

                ionCurrent += peakList.getIntensitySum(tuple._1().calculateMz(IonType.b, z, massCalculator), tolerance);
                ionCurrent += peakList.getIntensitySum(tuple._1().calculateMz(IonType.c, z, massCalculator), tolerance);

                ionCurrent += peakList.getIntensitySum(tuple._2().calculateMz(IonType.y, z, massCalculator), tolerance);
                ionCurrent += peakList.getIntensitySum(tuple._2().calculateMz(IonType.z, z, massCalculator), tolerance);
            }

            if(ionCurrent > 0)
                linkageCount += 1;
        }
        return linkageCount;
    }
}
