package net.sourceforge.squirrel_sql.fw.datasetviewer;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class DataSetScrollingPanel extends JScrollPane
{
	
	private static final ILogger s_log =
		LoggerController.createLogger(DataSetScrollingPanel.class);

	private boolean _fullyCreated = false;
	private IDataSetViewer _viewer;

	
	public DataSetScrollingPanel()
	{
		super();
	}

	public DataSetScrollingPanel(String destClassName,
									IDataSetUpdateableModel updateableModel)
		throws DataSetException
	{
		super();
		createUserInterface(destClassName, updateableModel);
		_fullyCreated = true;
	}

	public void load(IDataSet ds)
	{
		load(ds, null);
	}

	
	public void load(IDataSet ds, String destClassName)
	{
		try
		{
			if (!_fullyCreated)
			{
				createUserInterface(destClassName, null);
				_fullyCreated = true;
			}
			Runnable run = new UIUpdater(_viewer, ds);
			SwingUtilities.invokeLater(run);
		}
		catch (Exception ex)
		{
			s_log.error("Error", ex);
		}
	}

	public void clear()
	{
		if (_viewer != null)
		{
			_viewer.clear();
		}
	}

	private void createUserInterface(String destClassName, IDataSetUpdateableModel updateableModel)
		throws DataSetException
	{
		setBorder(BorderFactory.createEmptyBorder());
		_viewer = BaseDataSetViewerDestination.getInstance(destClassName, updateableModel);
		Runnable run = new Runnable()
		{
			public void run()
			{
				setViewportView(_viewer.getComponent());
			}
		};
		SwingUtilities.invokeLater(run);
	}

	private final static class UIUpdater implements Runnable
	{
		
		private static final ILogger s_log =
			LoggerController.createLogger(UIUpdater.class);

		private final IDataSetViewer _viewer;
		private final IDataSet _ds;

		UIUpdater(IDataSetViewer viewer, IDataSet ds)
		{
			super();
			_viewer = viewer;
			_ds = ds;
		}

		public void run()
		{
			try
			{
				_viewer.show(_ds);
			}
			catch (Throwable th)
			{
				s_log.error("Error processing a DataSet", th);
			}
		}
	}
	
	
	public IDataSetViewer getViewer()
	{
		return _viewer;
	}
}
