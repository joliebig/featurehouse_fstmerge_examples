package net.sourceforge.squirrel_sql.fw.datasetviewer;

 
import javax.swing.DefaultCellEditor;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public abstract class BaseDataSetViewerDestination implements IDataSetViewer
{
	
	private static ILogger s_log =
		LoggerController.createLogger(BaseDataSetViewerDestination.class);

	
	private boolean _showHeadings = true;

	
	protected ColumnDisplayDefinition[] _colDefs = new ColumnDisplayDefinition[0];

	
	private IDataSetUpdateableModel _updateableModelReference = null;
	
	
	protected DefaultCellEditor currentCellEditor = null;
	
	
	
	public void init(IDataSetUpdateableModel updateableObject)
	{
		
		
	}

	
	public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs)
	{
		_colDefs = colDefs != null ? colDefs : new ColumnDisplayDefinition[0];
	}

	
	public ColumnDisplayDefinition[] getColumnDefinitions()
	{
		return _colDefs;
	}

	
	public void showHeadings(boolean show)
	{
		_showHeadings = show;
	}

	
	public boolean getShowHeadings()
	{
		return _showHeadings;
	}

	public synchronized void show(IDataSet ds) throws DataSetException
	{
		show(ds, null);
	}

	
	public synchronized void show(IDataSet ds, IMessageHandler msgHandler)
		throws DataSetException
	{

		
		
		
		if (currentCellEditor != null) {
			currentCellEditor.cancelCellEditing();
			currentCellEditor = null;
		}

		if (ds == null)
		{
			throw new IllegalArgumentException("IDataSet == null");
		}

		clear();
        if (ds.getDataSetDefinition() != null) {
    		setColumnDefinitions(ds.getDataSetDefinition().getColumnDefinitions());
    		final int colCount = ds.getColumnCount();
    		while (ds.next(msgHandler))
    		{
    			addRow(ds, colCount);
    		}
    		allRowsAdded();
    		moveToTop();
        }
	}


	protected void addRow(IDataSet ds, int columnCount) throws DataSetException
	{
		Object[] row = new Object[columnCount];
		for (int i = 0; i < columnCount; ++i)
		{
			row[i] = ds.get(i);
		}
		addRow(row);
	}
	
	
	public void setUpdateableModelReference(IDataSetUpdateableModel updateableObject)
	{
		_updateableModelReference = updateableObject;
	}

	public IDataSetUpdateableModel getUpdateableModelReference(){
		return _updateableModelReference;
	}

	protected abstract void allRowsAdded() throws DataSetException;
	protected abstract void addRow(Object[] row) throws DataSetException;

	
	public static IDataSetViewer getInstance(String sName, 
		IDataSetUpdateableModel updateableModel)
	{
		IDataSetViewer dsv = null;
		try
		{
			Class<?> cls = Class.forName(sName);
			dsv = (IDataSetViewer) cls.newInstance();
			dsv.init(updateableModel);
		}
		catch (Exception e)
		{
			s_log.error("Error", e);
		}
		if (dsv == null)
		{
			dsv = new DataSetViewerTablePanel();
			((DataSetViewerTablePanel)dsv).init(updateableModel);
		}
		return dsv;
	}
}
