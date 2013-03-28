package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTabbedPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

class ObjectTreeTabbedPane
{
    
    
    private final static ILogger log = 
                      LoggerController.createLogger(ObjectTreeTabbedPane.class);
    
	
	interface IClientPropertiesKeys
	{
		String TABBED_PANE_OBJ = ObjectTreeTabbedPane.class.getName() + "/TabPaneObj";
	}

	
	private final JTabbedPane _tabPnl = UIFactory.getInstance().createTabbedPane();

	
	private final IApplication _app;

	
	private final IIdentifier _sessionId;

	
	private List<IObjectTab> _tabs = new ArrayList<IObjectTab>();

	ObjectTreeTabbedPane(ISession session)
	{
		super();

		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		_sessionId = session.getIdentifier();
		_app = session.getApplication();

		_tabPnl.putClientProperty(IClientPropertiesKeys.TABBED_PANE_OBJ, this);
	}

	
	JTabbedPane getTabbedPane()
	{
		return _tabPnl;
	}

	IObjectTab getTabIfSelected(String title)
	{
		IObjectTab tab = getSelectedTab();
		if ((tab != null) && (tab.getTitle().equals(title)))
		{
			return tab;
		}
		return null;
	}

    IObjectTab getSelectedTab() {
        IObjectTab tab = _tabs.get(_tabPnl.getSelectedIndex());
        return tab;
    }
    
	synchronized void addObjectPanelTab(IObjectTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IObjectTab passed");
		}
        
        
        
        
        
        
        
        
        
		if (_tabs.size() == 1 && _tabPnl.getTabCount() == 0) {
            log.debug(
                "addObjectPanelTab: _tabs.size() == 1, but " +
                "_tabPnl.getTabCount() == 0 - adding first tab component to " +
                "the tabbed page");
            IObjectTab firstTab = _tabs.get(0);
            _tabPnl.addTab(firstTab.getTitle(), 
                           null, 
                           firstTab.getComponent(), 
                           firstTab.getHint());
        }
        
        tab.setSession(_app.getSessionManager().getSession(_sessionId));
        final String title = tab.getTitle();
        _tabPnl.addTab(title, null, tab.getComponent(), tab.getHint());
        _tabs.add(tab);
	}

	void selectCurrentTab()
	{
		if (_tabPnl.getParent() != null)
		{
			int idx = _tabPnl.getSelectedIndex();
			if (idx != -1 && idx < _tabs.size())
			{
				IObjectTab tab = _tabs.get(idx);
				if (tab != null)
				{
					tab.select();
				}
			}
		}
	}

	void setDatabaseObjectInfo(IDatabaseObjectInfo dboInfo)
	{
		Iterator<IObjectTab> it = _tabs.iterator();
		while (it.hasNext())
		{
			IObjectTab tab = it.next();
			tab.setDatabaseObjectInfo(dboInfo);
		}
	}

	
	synchronized void rebuild()
	{
		final int curTabIdx = _tabPnl.getSelectedIndex();
		final List<IObjectTab> oldTabs = new ArrayList<IObjectTab>();
		oldTabs.addAll(_tabs);
		_tabPnl.removeAll();
		_tabs.clear();
		Iterator<IObjectTab> it = oldTabs.iterator();
		while (it.hasNext())
		{
			final IObjectTab tab = it.next();
			tab.rebuild();
			addObjectPanelTab(tab);
		}
		if (curTabIdx >= 0 && curTabIdx < _tabPnl.getTabCount())
		{
			_tabPnl.setSelectedIndex(curTabIdx);
		}
	}
        
}
