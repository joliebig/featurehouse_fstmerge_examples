package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;


public class DataSetListModel extends BaseDataSetViewerDestination
								implements IDataSetModel
{
	
	private interface IEventTypes
	{
		int ALL_ROWS_ADDED = 1;
		int MOVE_TO_TOP = 2;
	}

	
	public java.awt.Component getComponent()
	{
		return null;
	}
	
	private List<Object[]> _data = new ArrayList<Object[]>();

	
	

	
	

	
	private EventListenerList _listenerList = new EventListenerList();

	
	public void clear()
	{
		_data.clear();
	}

	
	protected void addRow(Object[] row)
	{
		_data.add(row);
	}

	
	protected void allRowsAdded()
	{
		fireEvent(IEventTypes.ALL_ROWS_ADDED);
	}

	
	public void moveToTop()
	{
		fireEvent(IEventTypes.MOVE_TO_TOP);
	}

	
	public int getRowCount()
	{
		return _data.size();
	}

	
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return (_data.get(rowIndex))[columnIndex];
	}

	
	public void setValueAt(Object value, int rowIndex, int columnIndex)
	{
		(_data.get(rowIndex))[columnIndex] = value;
	}

	
	public synchronized void addListener(IDataSetModelListener lis)
	{
		_listenerList.add(IDataSetModelListener.class, lis);
	}

	
	public synchronized void removeListener(IDataSetModelListener lis)
	{
		_listenerList.remove(IDataSetModelListener.class, lis);
	}

	
	protected void fireEvent(int eventType)
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
		DataSetModelEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == IDataSetModelListener.class)
			{
				
				if (evt == null)
				{
					evt = new DataSetModelEvent(this);
				}
				IDataSetModelListener lis =
					(IDataSetModelListener) listeners[i + 1];
				switch (eventType)
				{
					case IEventTypes.ALL_ROWS_ADDED :
						{
							lis.allRowsAdded(evt);
							break;
						}
					case IEventTypes.MOVE_TO_TOP :
						{
							lis.moveToTop(evt);
							break;
						}
					default :
						{
							throw new IllegalArgumentException(
								"Invalid eventTypes passed: " + eventType);
						}
				}
			}
		}
	}

}
