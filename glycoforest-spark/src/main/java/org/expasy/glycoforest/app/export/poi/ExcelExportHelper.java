package org.expasy.glycoforest.app.export.poi;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.expasy.glycoforest.cartooner.Cartooner;
import org.expasy.glycoforest.mol.*;
import org.expasy.glycoforest.app.evaluator.GlycanSpectrumMatch;
import org.expasy.glycoforest.app.evaluator.GsmResultList;
import org.expasy.glycoforest.app.evaluator.RunMap;
import org.expasy.glycoforest.solver.StructureVertex;
import org.expasy.glycoforest.writer.IupacCondensedWriter;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
class ExcelExportHelper {

    private final Workbook workbook;
    private final Map<UUID, String> gastricRuns;
    private final Map<UUID, String> fishRuns;
    private final CellStyle greyCell;
    private final CellStyle whiteLeftCell;
    private final CellStyle greyLeftCell;
    private final CellStyle whiteCell;
    private final CellStyle rtLeftHeaderCell;
    private final CellStyle rtRightHeaderCell;
    private final CellStyle greyHeaderCell;
    private final CellStyle whiteHeaderCell;
    private final IupacCondensedWriter iupacCondensedWriter = new NoLinkageIupacCondensedWriter();
    private final Cartooner cartooner = new Cartooner();
    private final GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiNegativeReduced();

    ExcelExportHelper(final Workbook workbook) {

        this.workbook = workbook;

        gastricRuns = RunMap.getGastricRunMap();

        fishRuns = RunMap.getFishRunMap();

        greyCell = workbook.createCellStyle();
        greyCell.setVerticalAlignment(VerticalAlignment.TOP);
        ((XSSFCellStyle) greyCell).setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        greyCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        greyCell.setBorderLeft(BorderStyle.THIN);

        greyHeaderCell = workbook.createCellStyle();
        greyHeaderCell.setVerticalAlignment(VerticalAlignment.TOP);
        ((XSSFCellStyle) greyHeaderCell).setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        greyHeaderCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        whiteLeftCell = workbook.createCellStyle();
        whiteLeftCell.setVerticalAlignment(VerticalAlignment.TOP);
        whiteLeftCell.setAlignment(HorizontalAlignment.LEFT);
        whiteLeftCell.setBorderLeft(BorderStyle.THIN);

        greyLeftCell = workbook.createCellStyle();
        greyLeftCell.setVerticalAlignment(VerticalAlignment.TOP);
        greyLeftCell.setAlignment(HorizontalAlignment.LEFT);
        greyLeftCell.setBorderLeft(BorderStyle.THIN);
        ((XSSFCellStyle) greyLeftCell).setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        greyLeftCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        whiteCell = workbook.createCellStyle();
        whiteCell.setVerticalAlignment(VerticalAlignment.TOP);
        whiteCell.setBorderLeft(BorderStyle.THIN);

        whiteHeaderCell = workbook.createCellStyle();
        whiteHeaderCell.setVerticalAlignment(VerticalAlignment.TOP);

        rtLeftHeaderCell = workbook.createCellStyle();
        rtLeftHeaderCell.setVerticalAlignment(VerticalAlignment.TOP);
        ((XSSFCellStyle) rtLeftHeaderCell).setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 220, 220)));
        rtLeftHeaderCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        rtLeftHeaderCell.setBorderBottom(BorderStyle.THIN);

        rtRightHeaderCell = workbook.createCellStyle();
        rtRightHeaderCell.setVerticalAlignment(VerticalAlignment.TOP);
        ((XSSFCellStyle) rtRightHeaderCell).setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 220, 220)));
        rtRightHeaderCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        rtRightHeaderCell.setBorderLeft(BorderStyle.THIN);
        rtRightHeaderCell.setBorderBottom(BorderStyle.THIN);
    }

    int addResultList(final Drawing drawing, final Sheet sheet, int rowIndex, int columnIndex, final IsomorphismType isomorphismType, final Tolerance fragTolerance, GsmResultList gsmResultList) {

        final int columnCount = (gsmResultList.size() * 2) + 1;

        final StructureVertex structureVertex = gsmResultList.getSolveTask().getVertex();

        final Row[] massRows = new Row[]{createRow(sheet, rowIndex += 1)};
        createCell(sheet, massRows, columnIndex + 1, columnIndex + 1, "Mass", greyHeaderCell);
        createCell(sheet, massRows, columnIndex + 2, columnIndex + columnCount, calcMassLabel(structureVertex), greyCell);

        final Row[] chargeRowRows = new Row[]{createRow(sheet, rowIndex += 1)};
        createCell(sheet, chargeRowRows, columnIndex + 1, columnIndex + 1, "Charge", whiteHeaderCell);
        createCell(sheet, chargeRowRows, columnIndex + 2, columnIndex + columnCount, "-" + structureVertex.getCharge(), whiteLeftCell);

        final Row[] metaScoreRows = new Row[]{createRow(sheet, rowIndex += 1)};
        createCell(sheet, metaScoreRows, columnIndex + 1, columnIndex + 1, "Score", greyHeaderCell);
        createCell(sheet, metaScoreRows, columnIndex + 2, columnIndex + columnCount, gsmResultList.getMetaScore(), greyLeftCell);

        final Row[] idRowRows = new Row[]{createRow(sheet, rowIndex += 1)};
        createCell(sheet, idRowRows, columnIndex + 1, columnIndex + 1, "ID", whiteHeaderCell);
        createCell(sheet, idRowRows, columnIndex + 2, columnIndex + columnCount, structureVertex.getConsensusId().toString(), whiteLeftCell);

        final Row[] rankRows = new Row[]{createRow(sheet, rowIndex += 1)};
        createCell(sheet, rankRows, columnIndex + 1, columnIndex + 1, "Rank", greyHeaderCell);

        final Row[] structureRows = new Row[]{
                createRow(sheet, rowIndex += 1),
                createRow(sheet, rowIndex += 1),
                createRow(sheet, rowIndex += 1),
                createRow(sheet, rowIndex += 1)
        };
        createCell(sheet, structureRows, columnIndex + 1, columnIndex + 1, "Structure", whiteHeaderCell);

        final Row[] gsmScoreRows = new Row[]{createRow(sheet, rowIndex += 1)};
        createCell(sheet, gsmScoreRows, columnIndex + 1, columnIndex + 1, "GSM", greyHeaderCell);
        final Row[] coverageRows = new Row[]{createRow(sheet, rowIndex += 1)};
        createCell(sheet, coverageRows, columnIndex + 1, columnIndex + 1, "Coverage", whiteHeaderCell);
        final Row[] ndpScoreRows = new Row[]{createRow(sheet, rowIndex += 1)};
        createCell(sheet, ndpScoreRows, columnIndex + 1, columnIndex + 1, "NDP", greyHeaderCell);
        final Row[] edgeScoreRows = new Row[]{createRow(sheet, rowIndex += 1)};
        createCell(sheet, edgeScoreRows, columnIndex + 1, columnIndex + 1, "Edge", whiteHeaderCell);
        final Row[] fragDeltaRows = new Row[]{createRow(sheet, rowIndex += 1)};
        createCell(sheet, fragDeltaRows, columnIndex + 1, columnIndex + 1, "f delta", whiteHeaderCell);

        for (int i = 0; i < gsmResultList.size(); i++) {

            final GlycanSpectrumMatch glycanSpectrumMatch = gsmResultList.get(i);
            final int firstCol = columnIndex + 2 + i * 2;
            addResultData(sheet, firstCol, rankRows, i + 1, greyCell);
            addImage(workbook, drawing, sheet, structureRows, firstCol, glycanSpectrumMatch.getHit().getKey(), whiteCell);
            addResultData(sheet, firstCol, gsmScoreRows, glycanSpectrumMatch.getResultScore(), greyCell);
            addResultData(sheet, firstCol, coverageRows, glycanSpectrumMatch.getCoverage(), whiteCell);
            addResultData(sheet, firstCol, ndpScoreRows, glycanSpectrumMatch.getSimScores(), greyCell);
            addResultData(sheet, firstCol, edgeScoreRows, glycanSpectrumMatch.getBestCandidate().getOpenSimScore(), whiteCell);
            if (gsmResultList.isVertexAnnotated()) {
                addResultData(sheet, firstCol, fragDeltaRows, gsmResultList.countMissingFragments(i, isomorphismType, fragTolerance), greyCell);
            }
        }

        final WithinRunConsensus withinRunConsensus = (WithinRunConsensus) structureVertex.getConsensus();
        final UUID runId = withinRunConsensus.getRunId();

        final Row headerRow = createRow(sheet, rowIndex += 2);
        createCell(sheet, new Row[]{headerRow}, columnIndex + 1, columnIndex + 2, fishRuns.containsKey(runId) ? "Fish" : "Gastric", rtLeftHeaderCell);
        createCell(sheet, new Row[]{headerRow}, columnIndex + 3, columnIndex + 4, "RT", rtRightHeaderCell);
        final Row currentRow = createRow(sheet, rowIndex += 1);
        addRt(sheet, columnIndex + 1, currentRow, withinRunConsensus, fishRuns.getOrDefault(runId, gastricRuns.getOrDefault(runId, "?")), false);
        return rowIndex;
    }

    private Row createRow(final Sheet sheet, final int rowIndex) {

        Row row = sheet.getRow(rowIndex);
        if (row == null)
            row = sheet.createRow(rowIndex);
        return row;
    }

    private void addRt(final Sheet sheet, final int columnIndex, final Row row, final WithinRunConsensus withinRunConsensus, String run, boolean highlight) {

        createCell(sheet, new Row[]{row}, columnIndex, columnIndex + 1, run, highlight ? whiteHeaderCell : greyHeaderCell);
        createCell(sheet, new Row[]{row}, columnIndex + 2, columnIndex + 2, withinRunConsensus.getMinRetentionTime() / 60.0, highlight ? whiteCell : greyCell);
        createCell(sheet, new Row[]{row}, columnIndex + 3, columnIndex + 3, withinRunConsensus.getMaxRetentionTime() / 60.0, highlight ? whiteCell : greyCell);
    }

    private String calcMassLabel(StructureVertex node) {

        final Peak precursor = node.getPrecursor();
        final double mzZ1;
        if (precursor.getCharge() == 1) {

            mzZ1 = precursor.getMz();
        } else {

            final double compositionMass = massCalculator.calcCompositionMass(precursor.getMz(), precursor.getCharge());
            mzZ1 = massCalculator.calcMz(compositionMass, 1);
        }
        return Integer.toString((int) mzZ1);
    }

    private void addImage(final Workbook workbook, final Drawing drawing, final Sheet sheet, final Row[] structureRows, final int columnIndex, final SugarStructure sugarStructure, final CellStyle cellStyle) {

        final int rowIndex = structureRows[0].getRowNum();

        final int pictureIdx;
        try {

            final RenderedImage image = cartooner.makeImage(iupacCondensedWriter.write(sugarStructure), 342, 238);
            final File file = new File("C:\\Users\\Oliver\\Documents\\tmp\\export\\test.png");

            if (file.exists() && !file.delete()) throw new IllegalStateException("Could not delete " + file);
            FileOutputStream stream = new FileOutputStream(file);
            ImageIO.write(image, "png", stream);
            stream.close();

            InputStream is = new FileInputStream(file);
            byte[] bytes = IOUtils.toByteArray(is);
            pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            is.close();
        } catch (IOException e) {

            throw new IllegalStateException(e);
        }

        final CreationHelper helper = workbook.getCreationHelper();
        final ClientAnchor anchor = helper.createClientAnchor();
        //set top-left corner of the picture,
        //subsequent call of Picture#resize() will operate relative to it
        anchor.setCol1(columnIndex);
        anchor.setRow1(rowIndex);
        Picture pict = drawing.createPicture(anchor, pictureIdx);

        pict.resize();
        pict.resize(0.37);

        createCell(sheet, structureRows, columnIndex, columnIndex + 1, cellStyle);
    }

    private void addResultData(final Sheet sheet, final int columnIndex, final Row[] rows, final double value, final CellStyle cellStyle) {

        createCell(sheet, rows, columnIndex, columnIndex + 1, value, cellStyle);
    }

    private void createCell(final Sheet sheet, Row[] rows, int columnIndex1, int columnIndex2, String value, final CellStyle cellStyle) {

        final Cell cell = rows[0].getCell(columnIndex1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(value);
        mergeRegion(sheet, rows, cell, columnIndex1, columnIndex2, cellStyle);
    }

    private void createCell(final Sheet sheet, Row[] rows, int columnIndex1, int columnIndex2, double value, final CellStyle cellStyle) {

        final Cell cell = rows[0].getCell(columnIndex1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(value);
        mergeRegion(sheet, rows, cell, columnIndex1, columnIndex2, cellStyle);
    }

    private void createCell(final Sheet sheet, Row[] rows, int columnIndex1, int columnIndex2, final CellStyle cellStyle) {

        final Cell cell = rows[0].getCell(columnIndex1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        mergeRegion(sheet, rows, cell, columnIndex1, columnIndex2, cellStyle);
    }

    private void mergeRegion(final Sheet sheet, final Row[] rows, Cell topLeft, final int columnIndex1, final int columnIndex2, final CellStyle cellStyle) {

        final int rowIndex1 = rows[0].getRowNum();
        final int rowIndex2 = rows[rows.length - 1].getRowNum();
        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {

            for (int columnIndex = columnIndex1; columnIndex <= columnIndex2; columnIndex++) {

                final Cell currentCell;
                if (columnIndex == columnIndex1 && rowIndex == 0) {

                    currentCell = topLeft;
                } else {

                    currentCell = rows[rowIndex].getCell(columnIndex1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                }

                currentCell.setCellStyle(cellStyle);
            }
        }

        if (columnIndex2 > columnIndex1 || rows.length > 1) {

            sheet.addMergedRegion(new CellRangeAddress(rowIndex1, rowIndex2, columnIndex1, columnIndex2));
        }
    }

    void save(final Workbook workbook, final String name) throws IOException {

        final FileOutputStream fileOut = new FileOutputStream(name);
        workbook.write(fileOut);
        fileOut.close();
    }

    private static class NoLinkageIupacCondensedWriter extends IupacCondensedWriter {

        protected String formatLinkage(Optional<StructureLinkage> optionalLinkage, AbstractSugarStructure structure) {

            if (optionalLinkage.isPresent() && structure.getEdgeTarget(optionalLinkage.get()).isUnit(SugarUnit.S)) {

                return "(6)";
            } else {

                return "(-)";
            }

        }
    }
}
