package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import java.awt.Component;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetScrollingPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

public abstract class BaseDataSetTab extends BaseObjectTab
{
	
	private static final ILogger s_log =
		LoggerController.createLogger(BaseDataSetTab.class);

	
	private DataSetScrollingPanel _comp;

	public BaseDataSetTab()
	{
		super();
	}

	
	public synchronized Component getComponent()
	{
		if (_comp == null)
		{
			try
			{
				
				IDataSetUpdateableModel modelReference = null;	
				try
				{
					
					if (Class.forName("net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel").isAssignableFrom(this.getClass()))
					{
						modelReference = ((IDataSetUpdateableModel)this);
					}
				}
				catch (Exception ignore)
				{
					
					
				}
									
				_comp = new DataSetScrollingPanel(getDestinationClassName(), modelReference);
			}
			catch (Exception ex)
			{
				s_log.error("Error", ex);
			}
		}
		return _comp;
	}

	
	public void rebuild()
	{
		super.rebuild();
		_comp = null;
	}

	
	public void clear()
	{
		((DataSetScrollingPanel)getComponent()).clear();
	}

	
	public synchronized void refreshComponent() throws DataSetException
	{
		ISession session = getSession();
		if (session == null)
		{
			throw new IllegalStateException("Null ISession");
		}
		super._app.getThreadPool().addTask(new Runnable() {
		    public void run() {
                try {
                ((DataSetScrollingPanel)getComponent()).load(createDataSet());
                } catch (DataSetException e) {
                    s_log.error("", e);
                }
            }
        });
		
	}

	protected abstract IDataSet createDataSet() throws DataSetException;

	protected String getDestinationClassName()
	{
		return getSession().getProperties().getMetaDataOutputClassName();
	}
}
