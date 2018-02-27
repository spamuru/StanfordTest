package com.test.stanford.my_test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.testng.Assert;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RedirectConfig;
import com.jayway.restassured.config.SSLConfig;

public class HelperTest {


	//Parameters Set of urls , checks for status code of the urls .
	public void StatusCodeCheck(Set<String> UrlList) {

		RedirectConfig redirectConfig = new RedirectConfig(true,true,false,5);
		RestAssured.config.redirect(redirectConfig);
		RestAssured.config.sslConfig(new SSLConfig().relaxedHTTPSValidation().and().allowAllHostnames());
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

		int responseStatusCode=0;

		for(String url :UrlList ) {
			responseStatusCode = RestAssured.get(url).getStatusCode();
			System.out.println(url+" ---> "+responseStatusCode );
			Assert.assertTrue(responseStatusCode <= 399 && responseStatusCode >=200 ||responseStatusCode ==403 ,"For URL: "+url+" Found invalid status code: " + responseStatusCode);
		}
	}

	/*Reads data from xl sheet . 
	Parameters filePath of xl sheet ,
	sheetName in which data is provided,
	tableName on which test has to be run*/

	public static String[][] readExcelData(String filePath, String sheetName,String tableName) {

		String[][] testData = null;
		System.setProperty("sCurrentTestCase", sheetName);
		System.setProperty("sDataSheet", tableName);
		System.out.println("Reading file, sheet, table: " + filePath + ", " + sheetName + ", " + tableName);

		try {
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(filePath));
			HSSFSheet sheet = workbook.getSheet(sheetName);
			HSSFCell[] boundaryCells = findCell(sheet, tableName);
			HSSFCell startCell = boundaryCells[0];
			HSSFCell endCell = boundaryCells[1];
			int startRow = startCell.getRowIndex() + 1;
			int endRow = endCell.getRowIndex();
			int startCol = startCell.getColumnIndex() + 1;
			int endCol = endCell.getColumnIndex() - 1;
			testData = new String[endRow - startRow + 1][endCol - startCol + 1];
			for (int i = startRow; i < endRow + 1; i++) {
				for (int j = startCol; j < endCol + 1; j++) {
					if (sheet.getRow(i).getCell(j).getCellType() == HSSFCell.CELL_TYPE_STRING) {
						testData[i - startRow][j - startCol] = sheet.getRow(i)
								.getCell(j).getStringCellValue();
					} else if (sheet.getRow(i).getCell(j).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						Double temp = sheet.getRow(i).getCell(j)
								.getNumericCellValue();
						testData[i - startRow][j - startCol] = String
								.valueOf(temp.intValue());
					}
				}
			}
		} catch (FileNotFoundException fnfe) {
			System.out.println("Could not read the Excel sheet");
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			System.out.println("Could not read the Excel sheet");
			ioe.printStackTrace();
		}
		return testData;
	}


	public static HSSFCell[] findCell(HSSFSheet sheet, String text) {
		String pos = "start";
		HSSFCell[] cells = new HSSFCell[2];
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING
						&& text.equals(cell.getStringCellValue())) {
					if (pos.equalsIgnoreCase("start")) {
						cells[0] = (HSSFCell) cell;
						System.out.println("First Cell found at:"+cell.getRowIndex()+","+cell.getColumnIndex());
						pos = "end";
					} else {
						cells[1] = (HSSFCell) cell;
						System.out.println("First Cell found at:"+cell.getRowIndex()+","+cell.getColumnIndex());
					}
				}
			}
		}
		return cells;
	}
}
