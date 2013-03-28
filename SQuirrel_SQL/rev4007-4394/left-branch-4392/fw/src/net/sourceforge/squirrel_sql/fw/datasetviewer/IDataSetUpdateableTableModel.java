package net.sourceforge.squirrel_sql.fw.datasetviewer;




public interface IDataSetUpdateableTableModel extends IDataSetUpdateableModel
{
	
	public String getWarningOnCurrentData(Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object oldValue);
	
	
	public String getWarningOnProjectedUpdate(
		Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object newValue);

	
	public Object reReadDatum(
		Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		StringBuffer message);
		
	
	public String updateTableComponent(Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object oldValue,
		Object newValue);
	
	
	public int getRowidCol();
	
	
	public String deleteRows(Object[][] rowData, ColumnDisplayDefinition[] colDefs);


	
	public String[] getDefaultValues(ColumnDisplayDefinition[] colDefs);
		
	
	public String insertRow(Object[] values, ColumnDisplayDefinition[] colDefs);

   void addListener(DataSetUpdateableTableModelListener l);
   void removeListener(DataSetUpdateableTableModelListener l);
}
