package net.sourceforge.squirrel_sql.plugins.dataimport.importer;

import java.io.IOException;
import java.util.Date;

import javax.swing.JComponent;


public interface IFileImporter {
	
	public boolean open() throws IOException;
	
	public boolean close() throws IOException;
	
	public String[][] getPreview(int noOfLines) throws IOException;
	
	
	public int getRows() throws IOException;
	
	
	public boolean reset() throws IOException;
	
	
	public boolean next() throws IOException;
	
	
	public String getString(int column) throws IOException;
	
	
	public Long getLong(int column) throws IOException, UnsupportedFormatException;
	
	
	public Integer getInt(int column) throws IOException, UnsupportedFormatException;
	
	
	public Date getDate(int column) throws IOException, UnsupportedFormatException;
	
	
	public JComponent getConfigurationPanel();
}
