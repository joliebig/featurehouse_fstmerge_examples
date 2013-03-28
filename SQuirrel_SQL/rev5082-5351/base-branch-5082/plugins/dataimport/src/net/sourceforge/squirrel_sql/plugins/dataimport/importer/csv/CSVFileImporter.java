package net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.UnsupportedFormatException;

import com.csvreader.CsvReader;


public class CSVFileImporter implements IFileImporter {
	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(CSVFileImporter.class);
	
	private CSVSettingsBean settings = null;
	private File importFile = null;
	private CsvReader reader = null;
	
	
	public CSVFileImporter(File importFile) {
		this.importFile = importFile;
		this.settings = new CSVSettingsBean();
	}
	
	
	public boolean open() throws IOException {
		reset();
		return true;
	}
	
	
	public boolean close() throws IOException {
		if (reader != null) {
			reader.close();
		}
		return true;
	}

	
	public String[][] getPreview(int noOfLines) throws IOException {
		CsvReader csvReader = new CsvReader(new InputStreamReader(new FileInputStream(importFile), settings.getImportCharset()), settings.getSeperator());
		String[][] data = new String[noOfLines][];
		
		int row = 0;
		int columns = -1;
		while (csvReader.readRecord() && row < noOfLines) {
			if (columns == -1) {
				columns = csvReader.getColumnCount();
			}
			data[row] = new String[columns];
			for (int i = 0; i < columns; i++) {
				data[row][i] = csvReader.get(i);
			}
			row++;
		}
		csvReader.close();
		
		String[][] outData = new String[row][];
		for (int i = 0; i < row; i++) {
			outData[i] = data[i];
		}
		return outData;
	}
	
	
	public int getRows() {
		return -1;
	}
	
	
	public boolean next() throws IOException {
		return reader.readRecord();
	}
	
	
	public boolean reset() throws IOException {
		if (reader != null) {
			reader.close();
		}
		reader = new CsvReader(new InputStreamReader(new FileInputStream(importFile), settings.getImportCharset()), settings.getSeperator());
		return true;
	}
	
	
	public String getString(int column) throws IOException {
		return reader.get(column);
	}
	
	
	public Long getLong(int column) throws IOException, UnsupportedFormatException {
		try {
			return Long.parseLong(reader.get(column));
		} catch (NumberFormatException nfe) {
			throw new UnsupportedFormatException();
		}
	}

	
	public Integer getInt(int column) throws IOException, UnsupportedFormatException {
		try {
			return Integer.parseInt(reader.get(column));
		} catch (NumberFormatException nfe) {
			throw new UnsupportedFormatException(nfe);
		}
	}
	
	
	public Date getDate(int column) throws IOException, UnsupportedFormatException {
		Date d = null;
		try {
			DateFormat f = new SimpleDateFormat(settings.getDateFormat());
			d = f.parse(reader.get(column));
		} catch (IllegalArgumentException e) {
			
			JOptionPane.showMessageDialog(null, stringMgr.getString("CSVFileImporter.invalidDateFormat"));
			throw new UnsupportedFormatException();
		} catch (ParseException pe) {
			throw new UnsupportedFormatException();
		}
		return d;
	}
	
	
	public JComponent getConfigurationPanel() {
		return new CSVSettingsPanel(settings);
	}
}
