package net.sourceforge.squirrel_sql.fw.datasetviewer;

public class DataSetDefinition {
	
	private ColumnDisplayDefinition[] _columnDefs;

	
	public DataSetDefinition(ColumnDisplayDefinition[] columnDefs) {
		super();
		_columnDefs = columnDefs != null ? columnDefs : new ColumnDisplayDefinition[0];
	}

	
	public ColumnDisplayDefinition[] getColumnDefinitions() {
		return _columnDefs;
	}
}