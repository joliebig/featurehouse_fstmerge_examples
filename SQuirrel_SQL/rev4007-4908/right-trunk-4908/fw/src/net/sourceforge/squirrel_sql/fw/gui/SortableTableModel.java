package net.sourceforge.squirrel_sql.fw.gui;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import net.sourceforge.squirrel_sql.fw.datasetviewer.MyTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowNumberTableColumn;

public class SortableTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = -3534263285990454876L;

    transient private MyTableModelListener _actualModelLis = new MyTableModelListener();

	
	protected int _iColumn = -1;

	private boolean _bAscending;

	
	private TableModel _actualModel;

	public TableModel getActualModel()
	{
		return _actualModel;
	}

	
	private Integer[] _indexes = new Integer[0];

	public SortableTableModel(TableModel model)
	{
		super();
		setActualModel(model);
	}

	public void setActualModel(TableModel newModel)
	{
		if (_actualModel != null)
		{
			_actualModel.removeTableModelListener(_actualModelLis);
		}
		_actualModel = newModel;
		if (_actualModel != null)
		{
			_actualModel.addTableModelListener(_actualModelLis);
		}
		tableChangedIntern();
	}

	
	public int getRowCount()
	{
		return _actualModel != null ? _actualModel.getRowCount() : 0;
	}

	
	public int getColumnCount()
	{
		return _actualModel != null ? _actualModel.getColumnCount() : 0;
	}

	
	public Object getValueAt(int row, int col)
	{
		if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == col)
		{
			return Integer.valueOf(row + 1);
		}
		else
		{
            if (row < _indexes.length) {
                return _actualModel.getValueAt(_indexes[row].intValue(), col);
            } else {
                return null;
            }
		}
	}

	
	public void setValueAt(Object value, int row, int col)
	{
		_actualModel.setValueAt(value, _indexes[row].intValue(), col);
	}

	
	public String getColumnName(int col)
	{
		return _actualModel.getColumnName(col);
	}

	
	public Class<?> getColumnClass(int col)
	{
		return _actualModel.getColumnClass(col);
	}

	
	public void deleteRows(int[] rows)
	{
		int[] actualRows = new int[rows.length];
		for (int i=0; i< rows.length; ++i)
		{
            if (rows[i] < _indexes.length) {
                actualRows[i] = _indexes[rows[i]].intValue();
            }
		}
		((MyTableModel)_actualModel).deleteRows(actualRows);
	}

	
	public void insertRow(Object[] values)
	{
		
		((MyTableModel)_actualModel).addRow(values);

		
		
		
		
		
		
		
		
		
		
		
		
		
		((MyTableModel)_actualModel).fireTableChanged(new TableModelEvent(_actualModel));
		fireTableChanged(new TableModelEvent(this));
	}

	
	public boolean isCellEditable(int row, int col)
	{
		return _actualModel.isCellEditable(row,col);
	}

	
	public boolean sortByColumn(int column)
	{
		boolean b = true;
		if (column == _iColumn)
		{
			b = !_bAscending;
		}
		sortByColumn(column, b);
		return b;
	}

	
	public void sortByColumn(int column, boolean ascending)
	{
		_iColumn = column;
		_bAscending = ascending;
		TableModelComparator comparator = new TableModelComparator(column, ascending);
		
		
		
		Arrays.sort(_indexes, comparator);
		fireTableDataChanged();
	}

	public boolean isSortedAscending()
	{
		return _bAscending;
	}

	public void tableChanged()
	{
      tableChangedIntern();

      if(-1 != _iColumn)
      {
         sortByColumn(_iColumn, _bAscending);
      }
      else
      {
         fireTableDataChanged();
      }
   }

	private void tableChangedIntern()
	{
		_indexes = new Integer[getRowCount()];
		for (int i = 0; i < _indexes.length; ++i)
		{
			_indexes[i] = Integer.valueOf(i);
		}
   }

	
	public int transfromToModelRow(int row)
	{
		if(0 > row || row >= _indexes.length)
		{
			return -1;
		}

		return _indexes[row].intValue();
	}


	class TableModelComparator implements Comparator<Integer>
	{
		private int _iColumn;
		private int _iAscending;
		 private final Collator _collator = Collator.getInstance();
		 private boolean _allDataIsString = true;

		public TableModelComparator(int iColumn)
		{
			this(iColumn, true);
		}

		public TableModelComparator(int iColumn, boolean ascending)
		{
			_iColumn = iColumn;
			if (ascending)
			{
				_iAscending = 1;
			}
			else
			{
				_iAscending = -1;
			}
			 _collator.setStrength(Collator.PRIMARY);
			 _collator.setStrength(Collator.TERTIARY);

			 for (int i = 0, limit = _actualModel.getRowCount(); i < limit; ++i)
			 {
				 final Object data = _actualModel.getValueAt(i, _iColumn);
				 if (!(data instanceof String))
				 {
					 _allDataIsString = false;
					 break;
				 }
			 }
		}

		
		public int compare(final Integer i1, final Integer i2)
		{
			final Object data1 = _actualModel.getValueAt(i1.intValue(), _iColumn);
			final Object data2 = _actualModel.getValueAt(i2.intValue(), _iColumn);
			try
			{
				if (data1 == null && data2 == null)
				{
					return 0;
				}
				if (data1 == null)
				{
					return 1 * _iAscending;
				}
				if (data2 == null)
				{
					return -1 * _iAscending;
				}



				 if (!_allDataIsString)
				 {
					 final Comparable c1 = (Comparable)data1;
					 return c1.compareTo(data2) * _iAscending;
				 }
 
				 return _collator.compare((String)data1, (String)data2) * _iAscending;
			}
			catch (ClassCastException ex)
			{
				return data1.toString().compareTo(data2.toString()) * _iAscending;
			}
		}

	}

	protected class MyTableModelListener implements TableModelListener
	{
		public void tableChanged(TableModelEvent evt)
		{
			SortableTableModel.this.tableChangedIntern();
		}
	}
}
