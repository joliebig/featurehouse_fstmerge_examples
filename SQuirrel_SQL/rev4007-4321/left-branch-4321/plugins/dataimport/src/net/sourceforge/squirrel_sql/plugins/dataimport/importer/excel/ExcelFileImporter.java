package net.sourceforge.squirrel_sql.plugins.dataimport.importer.excel;


import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.JComponent;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.UnsupportedFormatException;


public class ExcelFileImporter implements IFileImporter {

	private File importFile = null;
	private int pointer = -1;
	private int size = 0;
	private Workbook workbook = null;
	private Sheet sheet = null;
	private ExcelSettingsBean settings = null;
	
	
	public ExcelFileImporter(File importFile) {
		this.importFile = importFile;
		this.settings = new ExcelSettingsBean();
	}
	
	
	public boolean open() throws IOException {
		try {
			workbook = Workbook.getWorkbook(importFile);
		} catch (BiffException be) {
			throw new IOException(be.toString());
		}
		reset();
		return true;
	}
	
	
	public boolean close() throws IOException {
		workbook.close();
		return true;
	}

	
	public String[][] getPreview(int noOfLines) throws IOException {
		String[][] data = null;
		Workbook wb = null;
		Sheet sht = null; 
		try {
			wb = Workbook.getWorkbook(importFile);
			sht = getSheet(wb);
		} catch (BiffException be) {
			throw new IOException(be.toString());
		}

		int y = 0;
		int x = 0;
		int maxLines = (noOfLines < sht.getRows()) ? noOfLines : sht.getRows();
		data = new String[maxLines][sht.getColumns()];

		for (y = 0; y < maxLines; y++) {
			for (x = 0; x < sht.getColumns(); x++) {
				data[y][x] = sht.getCell(x, y).getContents();
			}
		}
		wb.close();
		
		return data;
	}
	
	
	public boolean reset() throws IOException {
		sheet = getSheet(workbook);
		size = sheet.getRows();
		pointer = -1;
		return true;
	}
	
	
	public int getRows() {
		return size;
	}
	
	
	public boolean next() throws IOException {
		if (pointer >= size - 1) {
			return false;
		}
		pointer++;
		return true;
	}

	private void checkPointer() throws IOException {
		if (pointer < 0)
			throw new IOException("Use next() to get to the first record.");
	}
	
	
	public String getString(int column) throws IOException {
		checkPointer();
		return sheet.getCell(column, pointer).getContents();
	}
	
	
	public Integer getInt(int column) throws IOException, UnsupportedFormatException {
		checkPointer();
		Cell cell = sheet.getCell(column, pointer);
		if (cell.getType() != CellType.NUMBER) {
			throw new UnsupportedFormatException();
		}
		return (new Double(((NumberCell) cell).getValue())).intValue();
	}
	
	
	public Date getDate(int column) throws IOException, UnsupportedFormatException {
		checkPointer();
		Cell cell = sheet.getCell(column, pointer);
		if (cell.getType() != CellType.DATE) {
			throw new UnsupportedFormatException();
		}
		return ((DateCell) cell).getDate(); 
	}
	
	
	public Long getLong(int column) throws IOException, UnsupportedFormatException {
		checkPointer();
		Cell cell = sheet.getCell(column, pointer);
		if (cell.getType() != CellType.NUMBER) {
			throw new UnsupportedFormatException();
		}
		return (new Double(((NumberCell) cell).getValue())).longValue();
	}
	
	
	public JComponent getConfigurationPanel() {
		return new ExcelSettingsPanel(settings, importFile);
	}
	
	private Sheet getSheet(Workbook wb) {
		Sheet s = null;
		if (settings.getSheetName() != null) {
			s = wb.getSheet(settings.getSheetName());
		}
		if (s == null) {
			s = wb.getSheet(0);
		}
		return s;
	}
}
