package net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv;

import java.io.Serializable;
import java.nio.charset.Charset;


public class CSVSettingsBean implements Cloneable, Serializable {
	private static final long serialVersionUID = 6633824961073722466L;
	
	private char seperator = ';';
	
	private transient Charset importCharset = Charset.defaultCharset();
	
	private String dateFormat = "yyyy-MM-dd HH:mm:ss";

	
	public Charset getImportCharset() {
		return importCharset;
	}

	
	public void setImportCharset(Charset importCharset) {
		this.importCharset = importCharset;
	}

	
	public char getSeperator() {
		return seperator;
	}

	
	public void setSeperator(char seperator) {
		this.seperator = seperator;
	}

	
	public String getDateFormat() {
		return dateFormat;
	}

	
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
}
