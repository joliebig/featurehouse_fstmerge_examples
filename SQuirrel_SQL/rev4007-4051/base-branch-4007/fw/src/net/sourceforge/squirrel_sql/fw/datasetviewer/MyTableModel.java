package net.sourceforge.squirrel_sql.fw.datasetviewer;



import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import javax.swing.table.AbstractTableModel;


public final class MyTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = 5511368149198548935L;

    private List<Object[]> _data = new ArrayList<Object[]>();
	private ColumnDisplayDefinition[] _colDefs = new ColumnDisplayDefinition[0];
	private IDataSetTableControls _creator = null;


	MyTableModel(IDataSetTableControls creator)
	{
		super();
		_creator = creator;
	}

	
	public boolean isCellEditable(int row, int col)
	{
		
		
		
		
		
		
		
		
		
		
		

		if(col == RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX)
		{
			return false;
		}

		if (_creator.needToReRead(col, getValueAt(row, col)))
		{
			StringBuffer message = new StringBuffer();
			Object newValue = _creator.reReadDatum(_data.get(row), col, message);
			if (message.length() > 0)
			{
				
				
				
				
				return false;	
			}
			(_data.get(row))[col] = newValue;
		}

		return _creator.isColumnEditable(col, getValueAt(row, col));
	}

	public Object getValueAt(int row, int col)
	{
		if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == col)
		{
			return Integer.valueOf(row + 1);
		}
		else
		{
			return _data.get(row)[col];
		}
	}

	public int getRowCount()
	{
		return _data.size();
	}

	public int getColumnCount()
	{
		return _colDefs != null ? _colDefs.length : 0;
	}

	public String getColumnName(int col)
	{
		if(col == RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX)
		{
			return RowNumberTableColumn.ROW_NUMBER_HEADER;
		}
		else
		{
			return _colDefs != null ? _colDefs[col].getLabel() : super.getColumnName(col);
		}
	}

	public Class<?> getColumnClass(int col)
	{
		try
		{
			
			
			if (_colDefs == null)
			{
				return Object.class;
			}
		
			return Class.forName(_colDefs[col].getClassName());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	void setHeadings(ColumnDisplayDefinition[] hdgs)
	{
		_colDefs = hdgs;
	}

	public void addRow(Object[] row)
	{
		_data.add(row);
	}

	void clear()
	{
		_data.clear();
	}

	public void allRowsAdded()
	{
		fireTableStructureChanged();
	}

	
	public void setValueAt(Object newValue, int row, int col) {
      int[] colsToUpdate = _creator.changeUnderlyingValueAt(row, col, newValue, getValueAt(row, col));

      for (int i = 0; i < colsToUpdate.length; i++)
      {
         _data.get(row)[ colsToUpdate[i] ] = newValue;
      }
	}
	
	
	public void deleteRows(int[] rows) {
		
		if (rows.length == 0)
			return;
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Arrays.sort(rows);
		
		
		
		
		for (int i=rows.length - 1; i>=0; i--) {
			
            if (rows[i] < _data.size()) {
                _data.remove(rows[i]);
            }
		}

		
		
		
		
		fireTableDataChanged();
		
	}
}
