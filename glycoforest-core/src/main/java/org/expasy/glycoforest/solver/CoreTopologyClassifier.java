package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakLists;

import static org.expasy.glycoforest.solver.CoreTopology.UNDETERMINED;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class CoreTopologyClassifier extends SpectrumFeatureClassifier<CoreTopology> {

    //Linear
    private final Composition neutralLoss1 = Composition.parseComposition("C3H8O4");
    //Branched
    private final Composition neutralLoss2 = Composition.parseComposition("C2H2O");
    private final Composition neutralLoss3 = Composition.parseComposition("C2H4O2");

    private double pCorrectAgree = 0.29, pWrongAgree = 0.29;
    private double pCorrectDisagree = 0.01, pWrongDisagree = 0.04;
    private double pCorrectUnknown = 0.7, pWrongUnknown = 0.7;

    public CoreTopologyClassifier(Tolerance tolerance) {

        super(tolerance);
    }

    @Override
    public SpectrumFeatureClassification<CoreTopology> classify(PeakList peakList, SugarStructure structureCandidate) {

        final CoreTopology peakListCoreTopology = findPeakListCoreTopology(peakList);
        final CoreTopology structureCoreTopology = findStructureCoreTopology(structureCandidate);

        final FeatureStructureMatch fsm;
        if(peakListCoreTopology == UNDETERMINED || structureCoreTopology == UNDETERMINED){

            fsm = FeatureStructureMatch.UNKNOWN;
        } else if(peakListCoreTopology == structureCoreTopology){

            fsm = FeatureStructureMatch.SAME;
        } else {

            fsm = FeatureStructureMatch.DIFFERENT;
        }

        final double pCorrect;
        final double pWrong;

        switch (fsm) {
            case SAME:

                pCorrect = pCorrectAgree;
                pWrong = pWrongAgree;
                break;
            case DIFFERENT:

                pCorrect = pCorrectDisagree;
                pWrong = pWrongDisagree;
                break;
            case UNKNOWN:

                pCorrect = pCorrectUnknown;
                pWrong = pWrongUnknown;
                break;
            default:

                throw new IllegalStateException("Unknown FeatureStructureMatch " + fsm);
        }

        return new SpectrumFeatureClassification<>(pCorrect, pWrong, fsm, peakListCoreTopology, structureCoreTopology, this);
    }

    public CoreTopology findPeakListCoreTopology(PeakList peakList) {

        final Double fractionLinear = getFractionLinear(peakList);
        return fractionLinear > 0.035 ? CoreTopology.LINEAR : CoreTopology.BRANCHED;
    }

    private Double getFractionLinear(PeakList peakList) {

        final double precursorMz = peakList.getPrecursor().getMz();
        final double intensitySum1 = PeakLists.getIntensitySum(precursorMz - neutralLoss1.getMolecularMass(), peakList, tolerance);
        final double intensitySum2 = PeakLists.getIntensitySum(precursorMz - neutralLoss2.getMolecularMass(), peakList, tolerance);
        final double intensitySum3 = PeakLists.getIntensitySum(precursorMz - neutralLoss3.getMolecularMass(), peakList, tolerance);
        final double total = intensitySum1 + intensitySum2 + intensitySum3;

        return intensitySum1 / total;
    }

    public CoreTopology findStructureCoreTopology(SugarStructure structure) {

        CoreTopology topology;
        if (structure.outDegreeOf(structure.getRoot()) == 1)
            topology = CoreTopology.LINEAR;
        else
            topology = CoreTopology.BRANCHED;
        return topology;
    }

    @Override
    public String toString() {

        return "Topology";
    }
}
