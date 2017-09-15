package org.expasy.glycoforest.app.export.poi;

import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.app.evaluator.GsmResultList;
import org.expasy.glycoforest.app.evaluator.PaperExcelExportHelper;
import org.expasy.glycoforest.solver.StructureVertex;
import org.expasy.glycoforest.solver.WithinRunStructureVertex;
import org.expasy.glycoforest.util.DoubleMinMax;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.peakfilter.NPeaksFilter;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.tools.glycoforest.graphdb.GraphRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class AnnotatedSpectrumExporter {

    private final GlycanMassCalculator massCalculator;
    private final ManualAnnotationSupplier manualAnnotationSupplier;

    public AnnotatedSpectrumExporter(final GlycanMassCalculator massCalculator, final ManualAnnotationSupplier manualAnnotationSupplier) {

        this.massCalculator = massCalculator;
        this.manualAnnotationSupplier = manualAnnotationSupplier;
    }

    public List<String> export(List<GsmResultList> results, GraphRepository graphRepository, File file, final IsomorphismType isomorphismType, final Tolerance fragmentTolerance) {

        final XSSFWorkbook workbook = new XSSFWorkbook();

        final NumberFormat labelFormat = new DecimalFormat("#.0");

        final Map<String, Integer> massLabelCounter = new HashMap<>();

        final PaperExcelExportHelper exportHelper = new PaperExcelExportHelper(graphRepository, workbook);

        final List<String> manualAnnotations = new ArrayList<>();
        results.sort(Comparator.<GsmResultList, Double>comparing(gsmResultList -> {
            if (gsmResultList.isEmpty()) {

                return (double)Integer.parseInt(gsmResultList.getSolveTask().getVertex().calcMassLabel(massCalculator));
            } else {
                return gsmResultList.get(0).getStructure().getComposition().getMass();
            }
        }).thenComparing((gsmResultList1) -> -gsmResultList1.getMetaScore()));
        for(GsmResultList resultList : results) {

            final StructureVertex structureVertex = resultList.getSolveTask().getVertex();
            final String massLabel = structureVertex.calcMassLabel(massCalculator);
            final Integer massCount = massLabelCounter.compute(massLabel, (s, counter) -> counter == null ? 1 : counter + 1);

            final XSSFSheet sheet = workbook.createSheet(massLabel + "_" + massCount);

            final XSSFRow spectrumHeaderRow = sheet.createRow(1);
            spectrumHeaderRow.createCell(1).setCellValue("m/z");
            spectrumHeaderRow.createCell(2).setCellValue("Intensity");
            spectrumHeaderRow.createCell(3).setCellValue("Relative");

            final int numberOfPeaks = 100;
            final PeakList<LibPeakAnnotation> filteredSpectrum = structureVertex.getConsensus().copy(new NPeaksFilter<>(numberOfPeaks));
            double tic = filteredSpectrum.getTotalIonCurrent();
            double intensityCutoff = findIntensityCutOff(filteredSpectrum);

            final int plotOffset = 2 + 10 + numberOfPeaks;

            //Round mass to next 50 m/z
            sheet.createRow(plotOffset - 2).createCell(2).setCellValue(((int) (structureVertex.getCharge() == 1 ? structureVertex.getMz() : structureVertex.getMz() * 2) / 50) * 50 + 50);

            for(int i = 0; i < filteredSpectrum.size(); i++) {

                final double mz = filteredSpectrum.getMz(i);
                final double intensity = filteredSpectrum.getIntensity(i);

                final XSSFRow peakRow = sheet.createRow(i + 2);
                peakRow.createCell(1).setCellValue(mz);
                peakRow.createCell(2).setCellValue(intensity);
                peakRow.createCell(3).setCellValue(intensity /tic);

                final Optional<Double> label;
                if (intensity >= intensityCutoff) {
                    label = Optional.of(mz);
                } else {
                    label = Optional.empty();
                }

                int spectrumRowOffset = plotOffset + 3 * i;
                addSpectrumRow(spectrumRowOffset, mz - 0.00001, 0, Optional.empty(), sheet, labelFormat);
                addSpectrumRow(spectrumRowOffset + 1, mz, intensity, label, sheet, labelFormat);
                addSpectrumRow(spectrumRowOffset + 2, mz + 0.00001, 0, Optional.empty(), sheet, labelFormat);
            }

            final Optional<ManualAnnotation> annotationOpt;
            annotationOpt = manualAnnotationSupplier.getManualAnnotation((WithinRunStructureVertex) structureVertex);
            if (annotationOpt.isPresent()) {

                manualAnnotations.add(annotationOpt.get().getType());
            } else if (resultList.size() > 1){

                manualAnnotations.add("Un-annotated");
            } else {

                manualAnnotations.add("Singleton");
            }
            if (annotationOpt.isPresent()) {

                XSSFRow row = sheet.getRow(32);
                if(row == null)
                    row = sheet.createRow(32);
                row.createCell(8).setCellValue(annotationOpt.get().getCommentsText());
            }

            final XSSFDrawing drawing = sheet.createDrawingPatriarch();
            if (resultList.isVertexAnnotated()) {
                exportHelper.addAnnotatedResultList(drawing, sheet, 30, 4, resultList, isomorphismType, fragmentTolerance);
            } else {
                exportHelper.addResultList(drawing, sheet, 30, 4, isomorphismType, fragmentTolerance, resultList);
            }
        }

        try {

            final FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {

            throw new IllegalStateException(e);
        }

        return manualAnnotations;
    }

    private double findIntensityCutOff(final PeakList<LibPeakAnnotation> filteredSpectrum) {


        final PeakList<LibPeakAnnotation> top10 = filteredSpectrum.copy(new NPeaksFilter<>(10));
        final DoubleMinMax minMax = new DoubleMinMax();
        for(int i = 0; i < top10.size(); i++) {

            minMax.add(top10.getIntensity(i));
        }

        return minMax.getMin();
    }

    private void addSpectrumRow(final int spectrumRowOffset, final double mz, final double intensity, final Optional<Double> label, final XSSFSheet sheet, final NumberFormat labelFormat) {

        final XSSFRow peakRow = sheet.createRow(spectrumRowOffset);
        peakRow.createCell(1).setCellValue(mz);
        peakRow.createCell(2).setCellValue(intensity);
        if(label.isPresent()) {
            peakRow.createCell(3).setCellValue(labelFormat.format(label.get()));
        }
    }
}
