package net.sourceforge.squirrel_sql.fw.datasetviewer;




public class CellImportExportInfo
{

	
	private String _tableColumnName;
	
	
	private String _fileName;
	
	
	private String _command;
	
	
	public CellImportExportInfo() {
		this("", "", "");
	}

	
	CellImportExportInfo(String tableColumnName, String fileName, String command) {
		_tableColumnName = tableColumnName;
		_fileName = fileName;
		_command = command;
	}

	
	 
	public String getTableColumnName() { return _tableColumnName;}
	public void setTableColumnName(String tableColumnName) {
		_tableColumnName = tableColumnName;
	}
	
	public String getFileName() { return _fileName;}
	public void setFileName(String fileName) {
		_fileName = fileName;
	}
	
	public String getCommand() { return _command;}
	public void setCommand(String command) {
		_command = command;
	}
}
