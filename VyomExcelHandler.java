package all.org.vyomlibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

public class VyomExcelHandler 
{

	// Credentials
	HSSFRow row;
	HSSFCell cell;
	String filewrite;
	HSSFWorkbook wb;
	public HSSFSheet sheet ;
	FileOutputStream fileOut;
	FileInputStream fileInput;
	static int rowPosition;
	public HSSFSheet readSheet;

	public void createExcel(String fileName,String sheetName) throws FileNotFoundException
	{
		filewrite = fileName;
		wb = new HSSFWorkbook();
		sheet = wb.createSheet(sheetName) ;
		fileOut = new FileOutputStream(filewrite);
		rowPosition = 0;
	}//End of createExcel() Function


	public void writeRow(List<String> list)
	{
		row = sheet.createRow(rowPosition);
		Iterator <String>itr = list.iterator();
		int cv=0;

		while(itr.hasNext())
		{
			cell = row.createCell(cv);
			cell.setCellValue(itr.next());
			cv++;
		}
		rowPosition++;	

	}//End of writeRow
	
	public String getday(Row row,int columValue)
	{
		String date1 = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		date1 =  sdf.format((row.getCell(columValue)).getDateCellValue());

		return date1;

	}//End of getday()


	public void writeExcel() throws IOException
	{
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}//Close writeExcel() Function


	public void getExcelFile(String fileName) throws Exception
	{
		fileInput = new FileInputStream(new File(fileName));
		wb = new HSSFWorkbook(fileInput);
		readSheet = wb.getSheetAt(0);
		//readSheet = wb.getSheet(sheetName);
		@SuppressWarnings("unused")
		CreationHelper createHelper = wb.getCreationHelper();

	}//End of getExcelFile()

	
	public String NumericCellValue(Row row,int columValue)
	{
		String cellValue = "";
		DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
		HSSFCell cell = readSheet.getRow(row.getRowNum()).getCell(columValue);
		cellValue = formatter.formatCellValue(cell); //Returns the formatted value of a cell  

		return cellValue;
	}//End of getCell()
	
	
	public String getNumericCellValue(Row row,int columValue)
	{
		String cellValue = "";
		DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
		HSSFCell cell = readSheet.getRow(row.getRowNum()).getCell(columValue);

		if(cell != null)
		{
			cellValue = formatter.formatCellValue(cell); //Returns the formatted value of a cell  
		}

		return cellValue;
	}//End of getNumericCellValue()


	public void createCell(Row row, int columValue, String text)
	{
		Cell cell = null;
		cell = row.createCell(columValue);
		cell.setCellValue(text);
	}//End of createCell()

	
	public void createBooleanCell(Row row,int columValue,boolean res)
	{
		Cell cell=null;
		cell = row.createCell(columValue);
		cell.setCellValue(res);

	}//End of createBooleanCell()

	
	public String getCell(Row row,int columValue)
	{
		String cellText = "";
		Cell cell = null;
		cell = row.getCell(columValue);
		if(cell != null)
		{
			cellText = row.getCell(columValue).toString().trim();
		}
		return cellText;

	}//End of getCell()
	

	public void updateRemark(String filename) throws Exception 
	{
		FileOutputStream out = new FileOutputStream(new File(filename));
		wb.write(out);
		out.flush();
		out.close();
		fileInput.close();
	}//End of updateRemark()

}//End of class


