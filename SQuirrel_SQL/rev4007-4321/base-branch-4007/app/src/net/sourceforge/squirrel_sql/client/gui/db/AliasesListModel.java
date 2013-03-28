package net.sourceforge.squirrel_sql.client.gui.db;

import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.gui.SortedListModel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;

import net.sourceforge.squirrel_sql.client.IApplication;



public class AliasesListModel extends SortedListModel
{
    private static final long serialVersionUID = 1L;
    
	private IApplication _app;

	
	public AliasesListModel(IApplication app)
	{
		super();
		_app = app;
		load();
		_app.getDataCache().addAliasesListener(new MyAliasesListener());
	}

	
	private void load()
	{
		Iterator<ISQLAlias> it = _app.getDataCache().aliases();
		while (it.hasNext())
		{
			addAlias(it.next());
		}
	}

	
	private void addAlias(ISQLAlias alias)
	{
		addElement(alias);
	}

	
	private void removeAlias(ISQLAlias alias)
	{
		removeElement(alias);
	}

	
	private class MyAliasesListener implements IObjectCacheChangeListener
	{
		
		public void objectAdded(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLAlias)
			{
				addAlias((ISQLAlias)obj);
			}
		}

		
		public void objectRemoved(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLAlias)
			{
				removeAlias((ISQLAlias)obj);
			}
		}
	}
}
