package net.sourceforge.squirrel_sql.client.gui.db;

import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.gui.SortedListModel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;

import net.sourceforge.squirrel_sql.client.IApplication;

class DriversListModel extends SortedListModel
{
    private static final long serialVersionUID = 1L;

    
	private IApplication _app;

	
	private boolean _showLoadedDriversOnly;

	
	public DriversListModel(IApplication app) throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		load();
		_app.getDataCache().addDriversListener(new MyDriversListener());
	}

	
	public void setShowLoadedDriversOnly(boolean show)
	{
		if (show != _showLoadedDriversOnly)
		{
			_showLoadedDriversOnly = show;
			load();
		}
	}

	
	private void load()
	{
		clear();
		Iterator<ISQLDriver> it = _app.getDataCache().drivers();
		while (it.hasNext())
		{
			addDriver(it.next());
		}
	}

	
	private void addDriver(ISQLDriver driver)
	{
		if (!_showLoadedDriversOnly || driver.isJDBCDriverClassLoaded())
		{
			addElement(driver);
		}
	}

	
	private void removeDriver(ISQLDriver driver)
	{
		removeElement(driver);
	}

	
	private class MyDriversListener implements IObjectCacheChangeListener
	{
		
		public void objectAdded(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLDriver)
			{
				addDriver((ISQLDriver)obj);
			}
		}

		
		public void objectRemoved(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLDriver)
			{
				removeDriver((ISQLDriver)obj);
			}
		}
	}
}
