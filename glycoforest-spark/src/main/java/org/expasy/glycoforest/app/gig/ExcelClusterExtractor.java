package org.expasy.glycoforest.app.gig;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.expasy.glycoforest.curated.data.ClusterEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ExcelClusterExtractor {

    public Multimap<String, ClusterEntry> extract(File file) throws IOException {

        Multimap<String, ClusterEntry> clusters = ArrayListMultimap.create();

        final Pattern sheetPattern = Pattern.compile("(\\d\\d\\d+)(-\\d+)?");

        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
        for (Sheet sheet : workbook) {

            final String sheetName = sheet.getSheetName();
            final Matcher matcher = sheetPattern.matcher(sheetName);
            if (matcher.matches()) {

                int firstRow = getFirstRow(sheet);
                final String sourceFile = FilenameUtils.removeExtension(getText(sheet, firstRow + 1));
                final String title = getText(sheet, firstRow + 2);

                final int nominalMass = Integer.parseInt(matcher.group(1));
                final double precursorMz = Double.parseDouble(title.substring(26, title.indexOf('@')).replace(',', '.'));

                final int[] scanRange = extractIntRange(sheet, firstRow, 3, "Scan #: ");
                final double[] rtRange = extractDoubleRange(sheet, firstRow, 4, "RT: ");
                final int spectraCount = Integer.parseInt(getText(sheet, firstRow + 5).substring("AV: ".length()));

                final TDoubleList mzList = new TDoubleArrayList();
                final TDoubleList intensityList = new TDoubleArrayList();
                final int lastRow = sheet.getLastRowNum();
                for (int i = firstRow + 8; i <= lastRow; i++) {

                    Row row = sheet.getRow(i);
                    Cell mzCell = row.getCell(1);
                    Cell intensityCell = row.getCell(2);

                    mzList.add(mzCell.getNumericCellValue());
                    intensityList.add(intensityCell.getNumericCellValue());
                }

                clusters.put(sourceFile, new ClusterEntry(sheetName, nominalMass, precursorMz, scanRange[0], scanRange[1], rtRange[0], rtRange[1], spectraCount));
            }
        }

        return clusters;
    }

    /**
     * Finds the row that has "SPECTRUM - MS"
     *
     * @param sheet the sheet
     * @return the index
     */
    private int getFirstRow(Sheet sheet) {

        final Iterator<Row> it = sheet.rowIterator();
        int firstRow = 0;
        while (it.hasNext()) {

            final Row row = it.next();
            final short firstCellNum = row.getFirstCellNum();
            final Cell cell = firstCellNum == -1 ? null : row.getCell(firstCellNum);
            if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && "SPECTRUM - MS".equals(cell.getStringCellValue().trim())) {

                firstRow = row.getRowNum();
                break;
            }
        }

        return firstRow;
    }

    private int[] extractIntRange(Sheet sheet, int firstRow, int rowOffset, String name) {

        final String[] rangeText = getText(sheet, firstRow + rowOffset).substring(name.length()).split("-");
        final int[] range = new int[2];
        if (rangeText.length == 1) {

            range[0] = Integer.parseInt(rangeText[0].replace(",", ""));
            range[1] = range[0];
        } else if (rangeText.length == 2) {

            range[0] = Integer.parseInt(rangeText[0].replace(",", ""));
            range[1] = Integer.parseInt(rangeText[1].replace(",", ""));
        } else {

            throw new IllegalStateException("Cannot extract " + name + " number from " + Arrays.toString(rangeText));
        }
        return range;
    }

    private double[] extractDoubleRange(Sheet sheet, int firstRow, int rowOffset, String name) {

        final String[] rangeText = getText(sheet, firstRow + rowOffset).substring(name.length()).split("-");
        final double[] range = new double[2];
        if (rangeText.length == 1) {

            range[0] = Double.parseDouble(rangeText[0].replace(",", "."));
            range[1] = range[0];
        } else if (rangeText.length == 2) {

            range[0] = Double.parseDouble(rangeText[0].replace(",", "."));
            range[1] = Double.parseDouble(rangeText[1].replace(",", "."));
        } else {

            throw new IllegalStateException("Cannot extract " + name + " number from " + Arrays.toString(rangeText));
        }
        return range;
    }

    private String getText(Sheet sheet, int rowIndex) {

        final Row row = sheet.getRow(rowIndex);
        return row.getCell(row.getFirstCellNum()).getStringCellValue();
    }
}
