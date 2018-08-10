package br.ufpe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.ufpe.utils.ExportFileUtils;
import br.ufpe.utils.ExportImportFile;

public class ApachePOICSVToExcel {

	public static void main(String[] args) throws IOException {
		System.out.println("Por favor, aguarde ...");

		List<String> csv_files = new ArrayList<String>();
		ExportFileUtils.fillCSVListFiles(csv_files);

		String csvName = null;
		ExportImportFile csvFile = null;
		List<ExportImportFile> exportImportFiles = new ArrayList<ExportImportFile>();
		for (int j = 0; j < csv_files.size(); j++) {
			csvName = ExportFileUtils.strip(csv_files.get(j));
			csvFile = new ExportImportFile();
			csvFile.setName(csvName);
			csvFile.setPath(csv_files.get(j));
			exportImportFiles.add(csvFile);
		}

		FileInputStream excelFile = new FileInputStream(new File(
				ExportFileUtils.getDefaultFile()));
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
		XSSFSheet sheet = workbook.getSheet(ExportFileUtils.S11_SHEET_NAME);
		for (ExportImportFile exportImportFile : exportImportFiles) {
			List<Double> doubles = ExportFileUtils.createDoubleList(
					exportImportFile.getPath()).get(0);
			FileOutputStream outputStream = new FileOutputStream(
					exportImportFile.getName() + ".xlsx");
			XSSFWorkbook myWorkbook = new XSSFWorkbook();
			Sheet mySheet = myWorkbook.createSheet("|S11|");

			if (sheet != null && mySheet != null) {
				for (int i = 0; i < sheet.getLastRowNum(); i++) {
					Row row = sheet.getRow(i);
					if (row != null) {
						Row myRow = mySheet.createRow(i);
						for (int j = 0; j < row.getLastCellNum(); j++) {
							Cell cell = row.getCell(j);
							if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								double d = cell.getNumericCellValue();
								Cell myCell = myRow.createCell(j,
										Cell.CELL_TYPE_NUMERIC);
								myCell.setCellValue(Double.toString(d));
								Cell myCell2 = myRow.createCell(j + 1,
										Cell.CELL_TYPE_NUMERIC);
								myCell2.setCellValue(doubles.get(i - 1));
							} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								String s = cell.getStringCellValue();
								Cell myCell = myRow.createCell(j,
										Cell.CELL_TYPE_STRING);
								myCell.setCellValue(s);
							}
						}
					}
				}
			}
			myWorkbook.write(outputStream);
			outputStream.close();
		}
		excelFile.close();
	}
}
