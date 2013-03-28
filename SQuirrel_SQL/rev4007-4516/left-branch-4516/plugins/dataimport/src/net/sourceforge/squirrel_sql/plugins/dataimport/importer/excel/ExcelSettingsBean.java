package net.sourceforge.squirrel_sql.plugins.dataimport.importer.excel;

import java.io.Serializable;


public class ExcelSettingsBean implements Cloneable, Serializable {
	private static final long serialVersionUID = 4141322162824378258L;
	
	private String sheetName = null;

	
	public String getSheetName() {
		return sheetName;
	}

	
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

}
