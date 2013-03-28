package net.sourceforge.squirrel_sql.fw.datasetviewer;



public interface IDataSetModel extends IDataSetViewer
{
	
	ColumnDisplayDefinition[] getColumnDefinitions();

	
	int getRowCount();

	
	Object getValueAt(int rowIndex, int columnIndex);

	
	void setValueAt(Object value, int rowIndex, int columnIndex);

	
	void addListener(IDataSetModelListener lis);

	
	void removeListener(IDataSetModelListener lis);
}
