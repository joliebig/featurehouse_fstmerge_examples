package net.sourceforge.squirrel_sql.fw.datasetviewer;

 
import javax.swing.JTable;

public interface IDataSetTableControls
{
	
	public boolean isTableEditable();

	
	public boolean isColumnEditable(int col, Object originalValue);
	
	
	public boolean needToReRead(int col, Object originalValue);
	
	
	public Object reReadDatum(Object[] values, int col, StringBuffer message);
	
	
	public void setCellEditors(JTable table);
	
	
	public int[] changeUnderlyingValueAt(
		int rowIndex,
		int columnIndex,
		Object newValue,
		Object oldValue);
	
	
	public void deleteRows(int[] rows);
	
	
	public void insertRow();
}
