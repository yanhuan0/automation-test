package com.qatest.functional.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFPictureData;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.io.ByteArrayInputStream;
import java.io.File;

import com.qa.framework.imagetool.ImageCompareFinal;
import com.qa.framework.webdriver.BaseWebDriverTest;

public class CompareExcelUtil extends BaseWebDriverTest {

	private String fileName;

	/**
	 * Compare text content,cell style and image of excel file with its base file.
	 * 
	 * @param fileName File name.
	 * 
	 * @return true if excel files's content is same, false otherwise
	 * 
	 */
	public boolean compareDirectDownExcelFiles(String filePath) {
		String baseFilePath;
		if (filePath.lastIndexOf("/") == -1) {
			this.fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
		} else {
			this.fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
		}
		if (System.getProperty("biee.result.mode").equalsIgnoreCase("false"))
			return false;
		baseFilePath = getBaseDir() + "/" + fileName;
		return compareTwoExcelFiles(baseFilePath, filePath);
	}

	/**
	 * Compare text content,cell style and image of excel file with its base file,
	 * base file and reult file has different file name
	 *
	 * @param filePath      result download file path and File name.
	 * @param baseFilename: base file name
	 *
	 * @return true if excel files's content is same, false otherwise
	 *
	 */
	public boolean compareDirectDownExcelFiles_diff_filename(String baseFileName, String filePath) {
		String baseFilePath;
		if (System.getProperty("biee.result.mode").equalsIgnoreCase("false"))
			return false;
		baseFilePath = getBaseDir() + "/" + baseFileName;
		return compareTwoExcelFiles(baseFilePath, filePath);
	}

	/**
	 * Compare text content,cell style and image of excel file with its base file.
	 * 
	 * @param fileName File name.
	 * 
	 * @return true if excel files's content is same, false otherwise
	 * 
	 */
	public boolean compareExcelFiles(String fileName) {
		String baseFilePath, resultFilePath;
		this.fileName = fileName;

		if (System.getProperty("biee.result.mode").equalsIgnoreCase("false"))
			return false;
		baseFilePath = getBaseDir() + "/" + fileName;
		resultFilePath = getBaseDir() + "/" + fileName;

		return compareTwoExcelFiles(baseFilePath, resultFilePath);
	}

	/**
	 * Compare text content of two excel files.
	 * 
	 * @param baseFile    File name.
	 * @param resultFile  File name.
	 * @param contentOnly if true compare content only
	 * 
	 * @return true if base file and result file's content is same, false otherwise
	 * 
	 */
	public boolean compareTwoExcelFiles(String baseFile, String resultFile) {
		String extentionName = baseFile.substring(baseFile.lastIndexOf(".") + 1, baseFile.length());
		System.out.println(" compareExcelFiles baseFile=" + baseFile);
		System.out.println(" compareExcelFiles resultFile=" + resultFile);

		if (baseFile.lastIndexOf("/") == -1) {
			this.fileName = baseFile.substring(baseFile.lastIndexOf("\\") + 1, baseFile.lastIndexOf("."));
		} else {
			this.fileName = baseFile.substring(baseFile.lastIndexOf("/") + 1, baseFile.lastIndexOf("."));
		}

		try {
			if (compareTextTwoExcelFiles(baseFile, resultFile, true)) {
				if (extentionName.equals("xls"))
					return compareImageForTwoXLSFiles(baseFile, resultFile);
				else if (extentionName.equals("xlsx"))
					return compareImageForTwoXLSXFiles(baseFile, resultFile);
				else {
					logger.info("Invalid file extention name" + extentionName);
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Compare text content of two excel files.
	 * 
	 * @param baseFile     File name.
	 * @param resultFile   File name.
	 * @param compareStyle if true compare cell style or else not compare
	 * @throws Exception
	 * 
	 * @return true if base file and result file's content is same, false otherwise
	 * 
	 */
	public boolean compareTextTwoExcelFiles(String baseFile, String resultFile, boolean compareStyle) throws Exception {
		Workbook baseWB = readFile(baseFile);
		Workbook resultWB = readFile(resultFile);
		int baseSheetNumber = baseWB.getNumberOfSheets();
		int resultSheetNumber = resultWB.getNumberOfSheets();
		if (baseSheetNumber != resultSheetNumber) {
			System.out.println("sheets number are different, base sheet number:" + baseSheetNumber
					+ "result sheet number:" + resultSheetNumber);
			return false;
		} else {
			System.out.println("sheets number:" + baseSheetNumber + " are same");
		}
		for (int k = 0; k < baseWB.getNumberOfSheets(); k++) {
			Sheet baseSheet = baseWB.getSheetAt(k);
			Sheet resultSheet = resultWB.getSheetAt(k);
			int baseRows = baseSheet.getLastRowNum();
			int resultRows = resultSheet.getLastRowNum();
			if (baseRows != resultRows) {
				logInfo("Row number are differnet for sheet " + k);
				return false;
			} else {
				logInfo("Row number:" + baseRows + " are same for sheet " + k);
			}

			for (int r = 0; r < baseRows; r++) {
				Row baseRow = baseSheet.getRow(r);
				Row resultRow = resultSheet.getRow(r);
				if ((baseRow == null || resultRow == null) && (baseRow != resultRow)) {
					System.out.println(
							"Base row or resutl row is null for sheet:" + k + " row:" + r + ". There are different");
					return false;
				}
				if (baseRow == null && resultRow == null) {
					System.out.println("Both base row and resutl row are null for sheet:" + k + " row:" + r);
					continue;
				} else {
					int baseCells = baseRow.getLastCellNum();
					int resultCells = resultRow.getLastCellNum();
					if (baseCells != resultCells) {
						System.out.println("Cell number are differnet for sheet:" + k + " row:" + r);
						return false;
					}

					for (int c = 0; c < baseCells; c++) {
						Cell baseCell = baseRow.getCell(c);
						Cell resultCell = resultRow.getCell(c);
						if ((baseCell == null || resultCell == null) && (baseCell != resultCell)) {
							System.out.println("Base cell or result cell is null for sheet:" + k + " row:" + r + "cell"
									+ c + ". They are different");
							return false;
						}
						if (baseCell == null && resultCell == null) {
							System.out.println("Both base cell and resutl cell are null for sheet:" + k + " row:" + r
									+ "cell" + c);
							continue;
						}
						if (!compareCellContent(baseCell, resultCell)) {
							System.out
									.println("Cell content are differnet for sheet:" + k + " row:" + r + " cell:" + c);
							return false;

						}
						if (compareStyle) {
							if (!compareCellStyle(baseCell, resultCell)) {
								System.out.println(
										"Cell style are differnet for sheet:" + k + " row:" + r + " cell:" + c);
								return false;
							}
						}
					}
				}
			}
		}

		return true;

	}

	/**
	 * Compare content of two excel cell.
	 * 
	 * @param baseCell   excel cell.
	 * @param resultCell excel cell.
	 * 
	 * @return true if base cell and result cell's text content is same, false
	 *         otherwise
	 * 
	 */
	private static boolean compareCellContent(Cell baseCell, Cell resultCell) {

		int baseCellType = baseCell.getCellType();
		int resultCellType = resultCell.getCellType();
		if (baseCellType != resultCellType) {
			logger.info("Base Cell Type & Result Cell Type:" + baseCellType + "&" + resultCellType);
			return false;
		}
		switch (baseCell.getCellType()) {

		case Cell.CELL_TYPE_FORMULA:
			if (!(baseCell.getCellFormula().equals(resultCell.getCellFormula()))) {
				logger.info("Base Cell Formula & Result Cell Formual:" + baseCell.getCellFormula() + "&"
						+ resultCell.getCellFormula());
				return false;
			}
			break;

		case Cell.CELL_TYPE_NUMERIC:
			if (baseCell.getNumericCellValue() != (resultCell.getNumericCellValue())) {
				logger.info("Base Cell Numeric Value & Result Cell Numeric Value:" + baseCell.getNumericCellValue()
						+ "&" + resultCell.getNumericCellValue());
				return false;
			}
			break;

		case Cell.CELL_TYPE_STRING:
			String baseValue = baseCell.getStringCellValue();
			String resultValue = resultCell.getStringCellValue();
			if (!(baseValue.equals(resultValue))) {
				logger.info("Base cell and result cell are time");
				if (StringIsTime(baseValue) && StringIsTime(resultValue)) {

					return true;
				} else {
					logger.info("Base Cell String Value & Result Cell String Value:" + baseValue + "&" + resultValue);
					return false;
				}
			}
			break;

		case Cell.CELL_TYPE_BOOLEAN:
			if (baseCell.getBooleanCellValue() != (resultCell.getBooleanCellValue())) {
				logger.info("Base Cell Boolean Value & Result Cell Boolean Value:" + baseCell.getBooleanCellValue()
						+ "&" + resultCell.getBooleanCellValue());
				return false;
			}

		default:
		}
		return true;
	}

	private static boolean StringIsTime(String timeString) {
		if (timeString.contains(":") && timeString.contains("/")
				&& (timeString.contains(" AM") || timeString.contains(" PM")))
			return true;
		else
			return false;

	}

	/**
	 * Compare style of two excel cell.
	 * 
	 * @param baseCell   excel cell.
	 * @param resultCell excel cell.
	 * @return true if cell style is same, false otherwise
	 * 
	 */
	private static Boolean compareCellStyle(Cell baseCell, Cell resultCell) {
		CellStyle baseStyle = baseCell.getCellStyle();
		CellStyle resultStyle = resultCell.getCellStyle();
		if (baseStyle.getAlignment() != resultStyle.getAlignment()) {
			logger.info("Alignment are different");
			return false;
		}
		if (baseStyle.getBorderBottom() != resultStyle.getBorderBottom()) {
			logger.info("BorderBottom are different");
			return false;
		}
		if (baseStyle.getBorderLeft() != resultStyle.getBorderLeft()) {
			logger.info("BorderLeft are different");
			return false;
		}
		if (baseStyle.getBorderRight() != resultStyle.getBorderRight()) {
			logger.info("BorderRight are different");
			return false;
		}
		if (baseStyle.getBorderBottom() != resultStyle.getBorderBottom()) {
			logger.info("BorderBottom are different");
			return false;
		}
		if (baseStyle.getBorderTop() != resultStyle.getBorderTop()) {
			logger.info("BorderTop are different");
			return false;
		}
		if (baseStyle.getBottomBorderColor() != resultStyle.getBottomBorderColor()) {
			logger.info("BorderColor are different");
			return false;
		}
		if (baseStyle.getDataFormat() != resultStyle.getDataFormat()) {
			logger.info("DataFormat are different");
			return false;
		}
		if (!baseStyle.getDataFormatString().equals(resultStyle.getDataFormatString())) {
			logger.info("DataFormatString are different");
			return false;
		}
		if (baseStyle.getFillBackgroundColor() != resultStyle.getFillBackgroundColor()) {
			logger.info("FillBackgroundColor are different");
			return false;
		}
		if (baseStyle.getFillForegroundColor() != resultStyle.getFillForegroundColor()) {
			logger.info("FillForegroundColor are different");
			return false;
		}
		if (baseStyle.getFillPattern() != resultStyle.getFillPattern()) {
			logger.info("FillPattern are different");
			return false;
		}
		if (baseStyle.getFontIndex() != resultStyle.getFontIndex()) {
			logger.info("FontIndex are different");
			return false;
		}
		if (baseStyle.getHidden() != resultStyle.getHidden()) {
			logger.info("Hidden are different");
			return false;
		}
		if (baseStyle.getIndention() != resultStyle.getIndention()) {
			logger.info("Indention are different");
			return false;
		}
		if (baseStyle.getLeftBorderColor() != resultStyle.getLeftBorderColor()) {
			logger.info("LeftBorderColor are different");
			return false;
		}
		if (baseStyle.getLocked() != resultStyle.getLocked()) {
			logger.info("Locked are different");
			return false;
		}
		if (baseStyle.getRightBorderColor() != resultStyle.getRightBorderColor()) {
			logger.info("BorderColor are different");
			return false;
		}
		if (baseStyle.getRotation() != resultStyle.getRotation()) {
			logger.info("Rotation are different");
			return false;
		}
		if (baseStyle.getTopBorderColor() != resultStyle.getTopBorderColor()) {
			logger.info("getTopBorderColor are different");
			return false;
		}
		if (baseStyle.getVerticalAlignment() != resultStyle.getVerticalAlignment()) {
			logger.info("VerticalAlignment are different");
			return false;
		}
		if (baseStyle.getWrapText() != resultStyle.getWrapText()) {
			logger.info("WrapText are different");
			return false;
		}

		return true;

	}

	/**
	 * Read excel file and initialize object according to extension name.
	 * 
	 * @param filename excel file name.
	 * 
	 * 
	 */
	private static Workbook readFile(String filename) throws IOException, Exception {
		String extentionName = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
		if (extentionName.equals("xls"))
			return new HSSFWorkbook(new FileInputStream(filename));
		else if (extentionName.equals("xlsx"))
			return new XSSFWorkbook(new FileInputStream(filename));
		else {
			throw new Exception("Invalid extension name");
		}
	}

	/**
	 * compare image in a base excel 2007 file to image in result excel 2007 file.
	 * 
	 * @param baseFile excel file name.
	 * @param baseFile result file name.
	 * 
	 * @return true if the images in base excel file and result excel file are same,
	 *         false otherwise
	 * 
	 */
	public boolean compareImageForTwoXLSXFiles(String baseFile, String resultFile) throws Exception {
		System.out.println("compare xlsx files start........................");
		Workbook baseWB = readFile(baseFile);
		Workbook resultWB = readFile(resultFile);
		List basePicturelst = baseWB.getAllPictures();
		List resultPicturelst = resultWB.getAllPictures();
		if (basePicturelst.size() != resultPicturelst.size()) {
			logger.info("The two xlsx file has different amount of pictures");
			return false;
		} else {
			XSSFPictureData basePict;
			XSSFPictureData resultPict;
			String baseExtName;
			String resultExtName;
			byte[] baseData;
			byte[] resultData;
			for (int i = 0; i < basePicturelst.size(); i++) {
				basePict = (XSSFPictureData) basePicturelst.get(i);
				baseExtName = basePict.suggestFileExtension();
				baseData = basePict.getData();
				resultPict = (XSSFPictureData) resultPicturelst.get(i);
				resultExtName = resultPict.suggestFileExtension();
				resultData = resultPict.getData();

				if (!baseExtName.equals(resultExtName)) {
					logger.info("extension name of picture are not same.");
					return false;
				}
//				if (!compareTwoByteArray(baseData, resultData));
				if (!compareTwoImages(baseData, resultData))
					return false;
			}

			System.out.println("compare xlsx files end........................");
		}
		return true;
	}

	/**
	 * compare image in a base excel 97-2007 file to image in result excel 97-2007
	 * file.
	 * 
	 * @param baseFile excel file name.
	 * @param baseFile result file name.
	 * 
	 * @return true if the images in base excel file and result excel file are same,
	 *         false otherwise
	 * 
	 */
	public boolean compareImageForTwoXLSFiles(String baseFile, String resultFile) throws Exception {

		Workbook baseWB = readFile(baseFile);
		Workbook resultWB = readFile(resultFile);
		List basePicturelst = baseWB.getAllPictures();
		List resultPicturelst = resultWB.getAllPictures();
		if (basePicturelst.size() != resultPicturelst.size()) {
			logInfo("The two xlsx file has different amount of pictures");
			return false;
		} else {
			HSSFPictureData basePict;
			HSSFPictureData resultPict;
			String baseExtName;
			String resultExtName;
			byte[] baseData;
			byte[] resultData;
			for (int i = 0; i < basePicturelst.size(); i++) {
				basePict = (HSSFPictureData) basePicturelst.get(i);
				baseExtName = basePict.suggestFileExtension();
				baseData = basePict.getData();
				resultPict = (HSSFPictureData) resultPicturelst.get(i);
				resultExtName = resultPict.suggestFileExtension();
				resultData = resultPict.getData();

				if (!baseExtName.equals(resultExtName)) {
					logInfo("extension name of picture are not same.");
					return false;
				}
//				if (!compareTwoByteArray(baseData, resultData));
				if (!compareTwoImages(baseData, resultData))
					return false;

			}
		}

		return true;
	}

	/**
	 * compare two images by accessing pixel for pixel.
	 * 
	 * @param baseData   base image file byte data.
	 * @param resultData result image file byte data.
	 * @return true if base image is same as result image, otherwise false.
	 */
	int i = 0;

	private boolean compareTwoImages(byte[] baseData, byte[] resultData) throws Exception {
		BufferedImage baseImg = null;
		BufferedImage resultImg = null;
		i++;
		logInfo(i + "*******");
		if (baseData.length != resultData.length) {
			logInfo("Picture size are different.");
		}
		baseImg = ImageIO.read(new ByteArrayInputStream(baseData));
		resultImg = ImageIO.read(new ByteArrayInputStream(resultData));
		String pnGDir = getBaseDir() + "/" + fileName;
		String bigDir = pnGDir + "/bigcomponents";
		String smallDir = pnGDir + "/smallcomponents";

		File dir = new File(pnGDir);
		if (!dir.exists())
			dir.mkdir();

		File dir1 = new File(bigDir);
		if (!dir1.exists())
			dir1.mkdir();

		File dir2 = new File(smallDir);
		if (!dir2.exists())
			dir2.mkdir();
		ImageCompareFinal imgcmp = new ImageCompareFinal(baseImg, resultImg, pnGDir, fileName, false, false, true);

		// Get percent from setup.properties file
		double matchThreshold = IMG_MATCH_THRESHOLD * 100;
		logger.info("Expected image match threshold value is: " + matchThreshold + "%");

		double percent = imgcmp.compareImages();
		logger.info("Actual image match threshold value is: " + percent + "%");

		// if (percent > 95){
		if (percent > matchThreshold) {
			return true;
		} else {
			logger.error("Actual image match threshold value " + percent + "%" + " which is less than expected value "
					+ matchThreshold + "%");
			return false;
		}
	}

}
