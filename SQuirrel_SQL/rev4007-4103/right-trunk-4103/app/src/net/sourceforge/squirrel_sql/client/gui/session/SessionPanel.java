package net.sourceforge.squirrel_sql.client.gui.session;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.FileAppendAction;
import net.sourceforge.squirrel_sql.client.session.action.FileCloseAction;
import net.sourceforge.squirrel_sql.client.session.action.FileNewAction;
import net.sourceforge.squirrel_sql.client.session.action.FileOpenAction;
import net.sourceforge.squirrel_sql.client.session.action.FilePrintAction;
import net.sourceforge.squirrel_sql.client.session.action.FileSaveAction;
import net.sourceforge.squirrel_sql.client.session.action.FileSaveAsAction;
import net.sourceforge.squirrel_sql.client.session.action.NextSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.PreviousSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshSchemaInfoAction;
import net.sourceforge.squirrel_sql.client.session.action.SQLFilterAction;
import net.sourceforge.squirrel_sql.client.session.action.SelectSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.SessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.IObjectTreeListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.SQLCatalogsComboBox;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SessionPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    
    @SuppressWarnings("unused")
	private static final ILogger s_log =
		LoggerController.createLogger(SessionPanel.class);

	
	@SuppressWarnings("unused")
    private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SessionPanel.class);

	
	transient private final IApplication _app;

	
	private IIdentifier _sessionId;

	
	private PropertyChangeListener _propsListener;

	private MainPanel _mainTabPane;


	
	private MyToolBar _toolBar;

	private Vector _externallyAddedToolbarActionsAndSeparators = new Vector();

	private StatusBar _statusBar = new StatusBar();
	private boolean _hasBeenVisible;



	private ObjectTreeSelectionListener _objTreeSelectionLis = null;

   public SessionPanel(ISession session)
	{
		super(new BorderLayout());

		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
        
		_app = session.getApplication();
		_sessionId = session.getIdentifier();
        
        createGUI(session);
		propertiesHaveChanged(null);

		_propsListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				propertiesHaveChanged(evt.getPropertyName());
			}
		};
		session.getProperties().addPropertyChangeListener(_propsListener);

	}

   public void addToToolsPopUp(String selectionString, Action action)
   {
      getSQLPaneAPI().addToToolsPopUp(selectionString, action);
   }




	public void addNotify()
	{

		super.addNotify();

		if (!_hasBeenVisible)
		{
			_hasBeenVisible = true;



			
			

			_mainTabPane.getObjectTreePanel().refreshTree();
		}
	}

	public boolean hasConnection()
	{
		return getSession().getSQLConnection() != null;
	}

	
	public ISession getSession()
	{
		return _app.getSessionManager().getSession(_sessionId);
	}

	public void sessionHasClosed()
	{
		if (_objTreeSelectionLis != null)
		{
			getObjectTreePanel().removeTreeSelectionListener(_objTreeSelectionLis);
			_objTreeSelectionLis = null;
		}

		final ISession session = getSession();
		if (session != null)
		{
			if (_propsListener != null)
			{
				session.getProperties().removePropertyChangeListener(_propsListener);
				_propsListener = null;
			}
			_mainTabPane.sessionClosing(session);
			_sessionId = null;
		}
	}

   public void sessionWindowClosing()
   {
      _mainTabPane.sessionWindowClosing();
   }


	
	public ObjectTreePanel getObjectTreePanel()
	{
		return _mainTabPane.getObjectTreePanel();
	}

	void closeConnection()
	{
		try
		{
			getSession().closeSQLConnection();
		}
		catch (SQLException ex)
		{
			showError(ex);
		}
	}

	
	public void selectMainTab(int tabIndex)
	{
		final JTabbedPane tabPnl = _mainTabPane.getTabbedPane();
		if (tabIndex >= tabPnl.getTabCount())
		{
			throw new IllegalArgumentException("" + tabIndex
					+ " is not a valid index into the main tabbed pane.");
		}
		if (tabPnl.getSelectedIndex() != tabIndex)
		{
			tabPnl.setSelectedIndex(tabIndex);
		}
	}

   public int getSelectedMainTabIndex()
   {
      return _mainTabPane.getTabbedPane().getSelectedIndex();
   }


   
	public int addMainTab(IMainPanelTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("IMainPanelTab == null");
		}
		return _mainTabPane.addMainPanelTab(tab);
	}

   public void insertMainTab(IMainPanelTab tab, int idx)
   {
      insertMainTab(tab, idx, true);
   }

	public void insertMainTab(IMainPanelTab tab, int idx, boolean selectInsertedTab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IMainPanelTab passed");
		}
		if(idx == MainPanel.ITabIndexes.SQL_TAB || idx == MainPanel.ITabIndexes.OBJECT_TREE_TAB)
		{
			throw new IllegalArgumentException("Index " + idx + "conflicts with standard tabs");
		}

		_mainTabPane.insertMainPanelTab(tab, idx, selectInsertedTab);
	}

	public int removeMainTab(IMainPanelTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IMainPanelTab passed");
		}
		return _mainTabPane.removeMainPanelTab(tab);
	}

	public void setStatusBarMessage(final String msg)
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				_statusBar.setText(msg);
			}
		});
	}

   public void setStatusBarProgress(final String msg, final int minimum, final int maximum, final int value)
   {
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         public void run()
         {
            _statusBar.setStatusBarProgress(msg, minimum, maximum, value);
         }
      });
   }

   public void setStatusBarProgressFinished()
   {
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         public void run()
         {
            _statusBar.setStatusBarProgressFinished();
         }
      });

   }



    public String getStatusBarMessage() {
        return _statusBar.getText();
    }
    
	SQLPanel getSQLPanel()
	{
		return _mainTabPane.getSQLPanel();
	}

	public ISQLPanelAPI getSQLPaneAPI()
	{
		return _mainTabPane.getSQLPanel().getSQLPanelAPI();
	}

	
	public ISQLEntryPanel getSQLEntryPanel()
	{
		return getSQLPanel().getSQLEntryPanel();
	}

	
	public synchronized void addToToolbar(Action action)
	{
		_externallyAddedToolbarActionsAndSeparators.add(action);
		if (null != _toolBar)
		{
			_toolBar.add(action);
		}
	}

   public synchronized void addSeparatorToToolbar()
   {
      _externallyAddedToolbarActionsAndSeparators.add(new SeparatorMarker());
      if (null != _toolBar)
      {
         _toolBar.addSeparator();
      }
   }


	
	public void addToStatusBar(JComponent comp)
	{
		_statusBar.addJComponent(comp);
	}

	
	public void removeFromStatusBar(JComponent comp)
	{
		_statusBar.remove(comp);
	}

	private void showError(Exception ex)
	{
		_app.showErrorDialog(ex);
	}

	private void propertiesHaveChanged(String propertyName)
	{
		final ISession session = getSession();
		final SessionProperties props = session.getProperties();
		if (propertyName == null
			|| propertyName.equals(
				SessionProperties.IPropertyNames.COMMIT_ON_CLOSING_CONNECTION))
		{
            _app.getThreadPool().addTask(new Runnable() {
                public void run() {
                    session.getSQLConnection().setCommitOnClose(
                            props.getCommitOnClosingConnection());                    
                }
            });
		}
		if (propertyName == null
			|| propertyName.equals(
				SessionProperties.IPropertyNames.SHOW_TOOL_BAR))
		{
			synchronized(this)
			{
				boolean show = props.getShowToolBar();
				if (show != (_toolBar != null))
				{
					if (show)
					{
						if (_toolBar == null)
						{
							_toolBar = new MyToolBar(session);
							for (int i = 0; i < _externallyAddedToolbarActionsAndSeparators.size(); i++)
							{
							    if(_externallyAddedToolbarActionsAndSeparators.get(i) instanceof Action)
							    {
								   _toolBar.add((Action)_externallyAddedToolbarActionsAndSeparators.get(i));
							    }
							    else
							    {
							        _toolBar.addSeparator();
							    }
							}
							add(_toolBar, BorderLayout.NORTH);
						}
					}
					else
					{
						if (_toolBar != null)
						{
							remove(_toolBar);
							_toolBar = null;
						}
					}
				}
			}
		}
	}

	private void createGUI(ISession session)
	{
		final IApplication app = session.getApplication();

		_mainTabPane = new MainPanel(session);

		add(_mainTabPane, BorderLayout.CENTER);

		Font fn = app.getFontInfoStore().getStatusBarFontInfo().createFont();
		_statusBar.setFont(fn);
		add(_statusBar, BorderLayout.SOUTH);

		_objTreeSelectionLis = new ObjectTreeSelectionListener();
		getObjectTreePanel().addTreeSelectionListener(_objTreeSelectionLis);

		RowColumnLabel lblRowCol = new RowColumnLabel(_mainTabPane.getSQLPanel().getSQLEntryPanel());
		addToStatusBar(lblRowCol);
		validate();
	}

   public boolean isSQLTabSelected()
   {
      return MainPanel.ITabIndexes.SQL_TAB ==_mainTabPane.getTabbedPane().getSelectedIndex();
   }

   public boolean isObjectTreeTabSelected()
   {
      return MainPanel.ITabIndexes.OBJECT_TREE_TAB ==_mainTabPane.getTabbedPane().getSelectedIndex();
   }

   private class MyToolBar extends ToolBar
   {
      private static final long serialVersionUID = 1L;
      private IObjectTreeListener _lis;
      private CatalogsPanel _catalogsPanel;

      MyToolBar(final ISession session)
      {
         super();
         createGUI(session);
      }

      public void addNotify()
      {
         super.addNotify();
         if (!_hasBeenVisible)
         {
            _hasBeenVisible = true;
            _mainTabPane.getObjectTreePanel().refreshTree();
         }
      }

      public void removeNotify()
      {
         super.removeNotify();
         if (_lis != null)
         {
            getObjectTreePanel().removeObjectTreeListener(_lis);
            _lis = null;
         }
      }

      private void createGUI(ISession session)
      {
         _catalogsPanel = new CatalogsPanel(session, this);
         _catalogsPanel.addActionListener(new CatalogsComboListener());


         add(_catalogsPanel);
         ActionCollection actions = session.getApplication().getActionCollection();
         setUseRolloverButtons(true);
         setFloatable(false);
         add(actions.get(SessionPropertiesAction.class));
         add(actions.get(RefreshSchemaInfoAction.class));
         addSeparator();
         add(actions.get(ExecuteSqlAction.class));
         addSeparator();

         add(actions.get(SQLFilterAction.class));

         addSeparator();
         add(actions.get(FileNewAction.class));
         add(actions.get(FileOpenAction.class));
         add(actions.get(FileAppendAction.class));
         add(actions.get(FileSaveAction.class));
         add(actions.get(FileSaveAsAction.class));
         add(actions.get(FilePrintAction.class));
         add(actions.get(FileCloseAction.class));
         addSeparator();
         add(actions.get(PreviousSqlAction.class));
         add(actions.get(NextSqlAction.class));
         add(actions.get(SelectSqlAction.class));

      }
   }

	private final class CatalogsComboListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			String selectedCatalog = SessionPanel.this._toolBar._catalogsPanel.getSelectedCatalog();
			if (selectedCatalog != null)
			{
				try
				{
                    ISession session = getSession();
					session.getSQLConnection().setCatalog(selectedCatalog);
                    expandTablesForCatalog(session, selectedCatalog);
				}
				catch (SQLException ex)
				{
					getSession().showErrorMessage(ex);
					SessionPanel.this._toolBar._catalogsPanel.refreshCatalogs();
				}
			}
		}
        
        
        private void expandTablesForCatalog(ISession session, 
                                            String selectedCatalog) {
            IObjectTreeAPI api = 
                session.getObjectTreeAPIOfActiveSessionWindow();
            api.refreshTree(true);
            if (api.selectInObjectTree(selectedCatalog, null, "TABLE")) {
                ObjectTreeNode[] nodes = api.getSelectedNodes();
                
                if (nodes.length > 0) {
                    ObjectTreeNode tableNode = nodes[0];
                    
                    
                    api.expandNode(tableNode);
                }                        
            }            
        }
	}


	private final class ObjectTreeSelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent evt)
		{
			final TreePath selPath = evt.getNewLeadSelectionPath();
			if (selPath != null)
			{
				StringBuffer buf = new StringBuffer();
				Object[] fullPath = selPath.getPath();
				for (int i = 0; i < fullPath.length; ++i)
				{
					if (fullPath[i] instanceof ObjectTreeNode)
					{
						ObjectTreeNode node = (ObjectTreeNode)fullPath[i];
						buf.append('/').append(node.toString());
					}
				}
				setStatusBarMessage(buf.toString());
			}
		}
	}

   private static class SeparatorMarker
   {

   }
}
