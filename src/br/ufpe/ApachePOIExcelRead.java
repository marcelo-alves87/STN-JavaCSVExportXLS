package br.ufpe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.ufpe.utils.ExportFileUtils;
import br.ufpe.utils.ExportImportFile;
import br.ufpe.utils.StringUtils;

public class ApachePOIExcelRead {

	public static void main(String[] args) {
		System.out.println("Por favor, aguarde ...");

		Map<ExportImportFile, List<ExportImportFile>> map = ExportFileUtils
				.createXlsxToCSVListMap();
		List<ExportImportFile> csvFiles = null;
		FileOutputStream outputStream = null;
		for (Entry<ExportImportFile, List<ExportImportFile>> entry : map
				.entrySet()) {
			try {
				FileInputStream excelFile = new FileInputStream(new File(entry
						.getKey().getPath()));
				XSSFWorkbook workbook = new XSSFWorkbook(excelFile);

				ExportFileUtils.eraseMeansColumnValues(workbook);

				outputStream = new FileOutputStream(entry.getKey().getPath());
				workbook.write(outputStream);
				outputStream.close();
				excelFile.close();

				csvFiles = entry.getValue();

				Collections.sort(csvFiles);

				XSSFSheet datatypeSheet = workbook
						.getSheet(ExportFileUtils.S11_SHEET_NAME);
				if (datatypeSheet != null) {
					Row currentRow = datatypeSheet.getRow(0);

					excelFile.close();

					for (ExportImportFile csvFile : csvFiles) {

						excelFile = new FileInputStream(new File(entry.getKey()
								.getPath()));
						workbook = new XSSFWorkbook(excelFile);
						datatypeSheet = workbook
								.getSheet(ExportFileUtils.S11_SHEET_NAME);
						currentRow = datatypeSheet.getRow(0);

						Cell lastHeaderCell = currentRow.getCell(currentRow
								.getLastCellNum() - 1);
						String lastHeader = lastHeaderCell.getStringCellValue();

						String newHeader = ExportFileUtils
								.getNewHeader(lastHeader);
						String csvHeader = StringUtils.difference(entry
								.getKey().getName(), csvFile.getName());

						if (!newHeader.equals(csvHeader)) {
							System.out
									.println("Nao foi possivel importar o arquivo "
											+ csvFile.getName()
											+ ": A coluna "
											+ csvHeader
											+ " nao e a ultima da planilha S11");
						} else {

							List<List<Double>> doubles = ExportFileUtils
									.createDoubleList(csvFile.getPath());
							ExportFileUtils
									.putS11Xlsx(doubles.get(0), workbook);
							// putFaseS11Xlsx(doubles.get(1), workbook);

						}

						outputStream = new FileOutputStream(entry.getKey()
								.getPath());
						workbook.write(outputStream);
						outputStream.close();
						excelFile.close();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Processo terminado.");
	}

}
