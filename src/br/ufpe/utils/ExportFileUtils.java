package br.ufpe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportFileUtils {
	
	private static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	private static final String CSV_CONTENT_TYPE = "application/vnd.ms-excel";
	private static final String TEMP_FILE_TYPE = "~$";

	public static final String S11_SHEET_NAME = "|S11|";
	private static final String FASE_S11_SHEET_NAME = "FASE S11";

	private static final String CHAAF_PASTE = "/CHAAF";
	private static final String FASE_SHEET_NAME = "FASE";
	
	private static final String DEFAULT_FILE = "/H2.xlsx";

	private static void fillListFiles(List<String> xlsx_files,
			List<String> csv_files) {
		String currentDirectory = Paths.get(".").toAbsolutePath().normalize()
				.toString();
		File file = new File(currentDirectory + CHAAF_PASTE);
		scanAllFiles(file, XLSX_CONTENT_TYPE, xlsx_files);
		scanAllFiles(file, CSV_CONTENT_TYPE, csv_files);
	}
	
	public static void fillCSVListFiles(List<String> csv_files) {
		String currentDirectory = Paths.get(".").toAbsolutePath().normalize()
				.toString();
		File file = new File(currentDirectory + CHAAF_PASTE);
		scanAllFiles(file, CSV_CONTENT_TYPE, csv_files);
	}
	
	public static String getDefaultFile() {
		String currentDirectory = Paths.get(".").toAbsolutePath().normalize()
				.toString();
		return currentDirectory + CHAAF_PASTE + DEFAULT_FILE;
	}

	private static void scanAllFiles(File dir, String contentType,
			List<String> fileList) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					scanAllFiles(file, contentType, fileList);
				} else {
					Path filePath = Paths.get(file.getCanonicalPath());
					String contentType1 = Files.probeContentType(filePath);
					if (!file.getCanonicalPath().contains(TEMP_FILE_TYPE)
							&& contentType.equals(contentType1)) {
						fileList.add(file.getCanonicalPath());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void eraseMeansColumnValues(XSSFWorkbook workbook) {
		XSSFSheet datatypeSheet = workbook.getSheet(S11_SHEET_NAME);
		List<Integer> columnsToErase = new ArrayList<Integer>();
		Row currentRow = null;
		Cell cell = null;
		if (datatypeSheet != null) {
			currentRow = datatypeSheet.getRow(0);
			if (currentRow != null) {
				Iterator<Cell> cells = currentRow.cellIterator();
				while (cells.hasNext()) {
					cell = cells.next();
					if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
						String value = cell.getStringCellValue();
						if (value.contains("M�dia")) {
							columnsToErase.add(cell.getColumnIndex());
						}
					}
				}
			}
			for (Integer columnIndex : columnsToErase) {
				for (int i = 0; i < datatypeSheet.getLastRowNum() + 1; i++) {
					currentRow = datatypeSheet.getRow(i);
					if (currentRow != null) {
						cell = currentRow.getCell(columnIndex);
						if (cell != null) {
							currentRow.removeCell(cell);
						}
					}
				}
			}
		}
	}

	private static void putFaseS11Xlsx(List<Double> list, XSSFWorkbook workbook) {
		XSSFSheet datatypeSheet = workbook.getSheet(FASE_S11_SHEET_NAME);
		if (datatypeSheet == null) {
			datatypeSheet = workbook.getSheet(FASE_SHEET_NAME);
		}
		Row currentRow = datatypeSheet.getRow(0);
		Cell newCell = null;
		for (int i = 0; i < list.size(); i++) {
			currentRow = datatypeSheet.getRow(i);
			newCell = currentRow.createCell(currentRow.getLastCellNum());
			newCell.setCellType(Cell.CELL_TYPE_NUMERIC);
			newCell.setCellValue(list.get(i));
		}
	}

	public static void putS11Xlsx(List<Double> list, XSSFWorkbook workbook) {
		XSSFSheet datatypeSheet = workbook.getSheet(S11_SHEET_NAME);
		Row currentRow = datatypeSheet.getRow(0);
		Cell lastHeaderCell = currentRow
				.getCell(currentRow.getLastCellNum() - 1);
		String lastHeader = lastHeaderCell.getStringCellValue();
		String newHeader = getNewHeader(lastHeader);
		Cell newCell = currentRow.createCell(currentRow.getLastCellNum());
		newCell.setCellValue(newHeader);
		newCell.setCellType(Cell.CELL_TYPE_STRING);
		for (int i = 0; i < list.size(); i++) {
			currentRow = datatypeSheet.getRow(i + 1);
			if (currentRow != null) {
				newCell = currentRow.createCell(currentRow.getLastCellNum());
				newCell.setCellType(Cell.CELL_TYPE_NUMERIC);
				newCell.setCellValue(list.get(i));
			}
		}

	}

	public static List<List<Double>> createDoubleList(String csvPath) {
		List<List<Double>> doubles = new ArrayList<List<Double>>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		List<Double> doubles2 = null;
		try {

			br = new BufferedReader(new FileReader(csvPath));
			while ((line = br.readLine()) != null) {

				if (line.contains("BEGIN") || line.contains("Freq [GHz]")) {
					doubles2 = new ArrayList<Double>();
					doubles.add(doubles2);
				}
				// use comma as separator
				String[] country = line.split(cvsSplitBy);
				if (country.length > 1 && !line.contains("!")
						&& !line.contains("END") && !line.contains("Freq")) {
					doubles2.add(Double.parseDouble(country[1]));
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return doubles;
	}

	public static String getNewHeader(String lastHeader) {
		String newHeader = "";

		// Todos Iguais a Z
		boolean allZ = true;
		for (int i = 0; i < lastHeader.length(); i++) {
			if (lastHeader.charAt(i) != 'Z') {
				allZ = false;
				break;
			}
		}

		if (allZ) {
			for (int i = 0; i < lastHeader.length() + 1; i++) {
				newHeader += 'A';
			}
		} else if ("Freq, (MHz)".equals(lastHeader)) {
			newHeader = "A";
		} else {

			int posZ = lastHeader.lastIndexOf('Z');
			if (posZ > 0) {
				char c = (char) (lastHeader.charAt(posZ - 1) + 1);
				newHeader = lastHeader.substring(0, posZ - 1) + c + 'A';
			} else {
				char c = (char) (lastHeader.charAt(lastHeader.length() - 1) + 1);
				newHeader = lastHeader.substring(0, lastHeader.length() - 1)
						+ c;
			}
		}

		return newHeader;
	}
	
	public static String strip(String str) {
		String strip = stripDotExtension(str);
		return stringFilePath(strip);
	}

	private static String stringFilePath(String str) {
		// Handle null case specially.

		if (str == null)
			return null;

		// Get position of last '.'.

		int pos = str.lastIndexOf("\\");

		// If there wasn't any '.' just return the string as is.

		if (pos == -1)
			return str;

		// Otherwise return the string, up to the dot.

		return str.substring(pos + 1, str.length());
	}

	private static String stripDotExtension(String str) {
		// Handle null case specially.

		if (str == null)
			return null;

		// Get position of last '.'.

		int pos = str.lastIndexOf(".");

		// If there wasn't any '.' just return the string as is.

		if (pos == -1)
			return str;

		// Otherwise return the string, up to the dot.

		return str.substring(0, pos);
	}
	
	public static Map<ExportImportFile, List<ExportImportFile>> createXlsxToCSVListMap() {
		List<String> xlsx_files = new ArrayList<String>();
		List<String> csv_files = new ArrayList<String>();
		Map<ExportImportFile, List<ExportImportFile>> map = new HashMap<ExportImportFile, List<ExportImportFile>>();

		fillListFiles(xlsx_files, csv_files);

		String[] xlsStringArray = null;
		String csvName = null;
		List<ExportImportFile> csvList = null;
		ExportImportFile xlsxFile = null;
		ExportImportFile csvFile = null;
		for (int i = 0; i < xlsx_files.size(); i++) {
			xlsStringArray = strip(xlsx_files.get(i)).split("-");

			if (xlsStringArray.length == 1) {
				xlsxFile = new ExportImportFile();
				xlsxFile.setName(xlsStringArray[0]);
				if (!map.containsKey(xlsxFile)) {
					xlsxFile = new ExportImportFile();
					xlsxFile.setPath(xlsx_files.get(i));
					xlsxFile.setName(xlsStringArray[0]);
					map.put(xlsxFile, new ArrayList<ExportImportFile>());
				}
			} else if (xlsStringArray.length == 2) {
				xlsxFile = new ExportImportFile();
				xlsxFile.setName(xlsStringArray[0] + "-" + xlsStringArray[1]);
				if (!map.containsKey(xlsxFile)) {
					xlsxFile = new ExportImportFile();
					xlsxFile.setPath(xlsx_files.get(i));
					xlsxFile.setName(xlsStringArray[0] + "-"
							+ xlsStringArray[1]);
					map.put(xlsxFile, new ArrayList<ExportImportFile>());
				}
			}

			for (int j = 0; j < csv_files.size(); j++) {
				csvName = strip(csv_files.get(j));
				csvFile = new ExportImportFile();
				csvFile.setName(csvName);
				csvFile.setPath(csv_files.get(j));
				if (xlsStringArray.length == 1
						&& csvName.matches("(" + xlsStringArray[0] + ")[A-Z]+")) {
					xlsxFile = new ExportImportFile();
					xlsxFile.setName(xlsStringArray[0]);
					csvList = map.get(xlsxFile);
					csvList.add(csvFile);
				} else if (xlsStringArray.length == 2
						&& csvName.matches("(" + xlsStringArray[0]
								+ ")[A-Z]+[-" + xlsStringArray[1] + "]+")) {
					xlsxFile = new ExportImportFile();
					xlsxFile.setName(xlsStringArray[0] + "-"
							+ xlsStringArray[1]);
					csvList = map.get(xlsxFile);
					csvList.add(csvFile);
				}
			}
		}

		return map;
	}

}
