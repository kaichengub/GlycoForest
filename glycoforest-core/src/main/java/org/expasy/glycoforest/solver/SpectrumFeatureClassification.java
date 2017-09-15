package org.expasy.glycoforest.solver;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SpectrumFeatureClassification<F extends Enum> {

    private final double pIsCorrect;
    private final double pIsWrong;
    private final FeatureStructureMatch classification;
    private final F spectrumFeature;
    private final F structureFeature;
    private final SpectrumFeatureClassifier<F> classifier;

    public SpectrumFeatureClassification(double pIsCorrect, double pIsWrong, FeatureStructureMatch classification, F spectrumFeature, F structureFeature, SpectrumFeatureClassifier<F> classifier) {

        this.pIsCorrect = pIsCorrect;
        this.pIsWrong = pIsWrong;
        this.classification = classification;
        this.spectrumFeature = spectrumFeature;
        this.structureFeature = structureFeature;
        this.classifier = classifier;
    }

    public F getSpectrumFeature() {

        return spectrumFeature;
    }

    public F getStructureFeature() {

        return structureFeature;
    }

    public FeatureStructureMatch getClassification() {

        return classification;
    }

    public double pIsCorrect() {

        return pIsCorrect;
    }

    public double pIsWrong() {

        return pIsWrong;
    }

    public SpectrumFeatureClassifier<F> getClassifier() {

        return classifier;
    }

    @Override
    public String toString() {

        return "PeakListClassification{" +
                "classification=" + classification +
                ", p(Correct)=" + pIsCorrect +
                ", p(Wrong)=" + pIsWrong +
                ", classifier=" + classifier.getClass().getName() +
                '}';
    }
}
