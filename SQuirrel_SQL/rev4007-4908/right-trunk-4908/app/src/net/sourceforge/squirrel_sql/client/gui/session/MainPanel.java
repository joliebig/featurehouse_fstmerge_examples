package net.sourceforge.squirrel_sql.client.gui.session;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ObjectTreeTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class MainPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    
	public interface ITabIndexes
	{
		int OBJECT_TREE_TAB = 0;
		int SQL_TAB = 1;
	}

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MainPanel.class);

	
	private static final ILogger s_log = LoggerController.createLogger(MainPanel.class);

	
	transient private ISession _session;

	
	private final JTabbedPane _tabPnl = UIFactory.getInstance().createTabbedPane();

	
	transient private PropertyChangeListener _propsListener;

	
	transient private ChangeListener _tabPnlListener;

	
	private List<IMainPanelTab> _tabs = new ArrayList<IMainPanelTab>();

   private static final String PREFS_KEY_SELECTED_TAB_IX = "squirrelSql_mainPanel_sel_tab_ix";

	
	MainPanel(ISession session)
	{
		super(new BorderLayout());

		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		_session = session;

		addMainPanelTab(new ObjectTreeTab(), Integer.valueOf('O'));
		addMainPanelTab(new SQLTab(_session), Integer.valueOf('Q'));

		add(_tabPnl, BorderLayout.CENTER);

		propertiesHaveChanged(null);

		
		(_tabs.get(getTabbedPane().getSelectedIndex())).select();
	}

	public void addNotify()
	{
		super.addNotify();

		if (_propsListener == null)
		{
			_propsListener = new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					propertiesHaveChanged(evt.getPropertyName());
				}
			};
			_session.getProperties().addPropertyChangeListener(_propsListener);
		}

		_tabPnlListener = new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt)
			{
				performStateChanged();
			}
		};
		_tabPnl.addChangeListener(_tabPnlListener);
	}

	public void removeNotify()
	{
		super.removeNotify();

		if (_propsListener != null)
		{
			_session.getProperties().removePropertyChangeListener(_propsListener);
			_propsListener = null;
		}

		if (_tabPnlListener != null)
		{
			_tabPnl.removeChangeListener(_tabPnlListener);
			_tabPnlListener = null;
		}
	}

	
   public int addMainPanelTab(IMainPanelTab tab)
   {
      return addMainPanelTab(tab, null);
   }

	public int addMainPanelTab(IMainPanelTab tab, Integer mnemonic)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IMainPanelTab passed");
		}
		tab.setSession(_session);
		final String title = tab.getTitle();
		int idx = _tabPnl.indexOfTab(title);
		if (idx != -1)
		{
			_tabPnl.removeTabAt(idx);
			_tabs.set(idx, tab);
		}
		else
		{
			idx = _tabPnl.getTabCount();
			_tabs.add(tab);
		}
		_tabPnl.insertTab(title, null, tab.getComponent(), tab.getHint(), idx);

      int prefIx = Preferences.userRoot().getInt(PREFS_KEY_SELECTED_TAB_IX, ITabIndexes.OBJECT_TREE_TAB);
      if(idx == prefIx)
      {
         _tabPnl.setSelectedIndex(prefIx);
      }

      if(null != mnemonic)
      {
         _tabPnl.setMnemonicAt(idx, mnemonic.intValue());

      }

      return idx;
	}

	
	public void insertMainPanelTab(IMainPanelTab tab, int idx, boolean selectInsertedTab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IMainPanelTab passed");
		}

		tab.setSession(_session);
		final String title = tab.getTitle();
		int checkIdx = _tabPnl.indexOfTab(title);
		if (checkIdx != -1)
		{
			throw new IllegalArgumentException("A tab with the same title already exists at index " + checkIdx);
		}

		_tabs.add(idx, tab);
		_tabPnl.insertTab(title, null, tab.getComponent(), tab.getHint(), idx);

      if(selectInsertedTab)
      {
         _tabPnl.setSelectedIndex(idx);
      }
   }

	public int removeMainPanelTab(IMainPanelTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IMainPanelTab passed");
		}

		final String title = tab.getTitle();
		int idx = _tabPnl.indexOfTab(title);
		if (idx == -1)
		{
			return idx;
		}

		_tabPnl.removeTabAt(idx);

		return idx;
	}



	private void updateState()
	{
		_session.getApplication().getActionCollection().activationChanged(_session.getSessionInternalFrame());
	}

	
	void sessionClosing(ISession session)
	{
		for (Iterator<IMainPanelTab> it = _tabs.iterator(); it.hasNext();)
		{
			try
			{
				(it.next()).sessionClosing(session);
			}
			catch (Throwable th)
			{
				String msg = s_stringMgr.getString("MainPanel.error.sessionclose");
				_session.getApplication().showErrorDialog(msg, th);
				s_log.error(msg, th);
			}
		}
	}


   public void sessionWindowClosing()
   {
      getSQLPanel().sessionWindowClosing();
		getObjectTreePanel().sessionWindowClosing();
		int selIx = _tabPnl.getSelectedIndex();

      if(selIx == ITabIndexes.OBJECT_TREE_TAB || selIx == ITabIndexes.SQL_TAB)
      {
         Preferences.userRoot().putInt(PREFS_KEY_SELECTED_TAB_IX, selIx);
      }
   }


	
	private void propertiesHaveChanged(String propertyName)
	{
		SessionProperties props = _session.getProperties();
		if (propertyName == null
			|| propertyName.equals(SessionProperties.IPropertyNames.MAIN_TAB_PLACEMENT))
		{
			_tabPnl.setTabPlacement(props.getMainTabPlacement());
		}
	}

	private void performStateChanged()
	{
		
		
		
		_tabPnl.requestFocusInWindow();

		updateState();
		int idx = _tabPnl.getSelectedIndex();
		if (idx != -1)
		{
			(_tabs.get(idx)).select();
		}
	}

	ObjectTreePanel getObjectTreePanel()
	{
		ObjectTreeTab tab = (ObjectTreeTab)_tabs.get(ITabIndexes.OBJECT_TREE_TAB);
		return (ObjectTreePanel)tab.getComponent();
	}

	SQLPanel getSQLPanel()
	{
		return ((SQLTab)_tabs.get(ITabIndexes.SQL_TAB)).getSQLPanel();
	}

	
	JTabbedPane getTabbedPane()
	{
		return _tabPnl;
	}
}
