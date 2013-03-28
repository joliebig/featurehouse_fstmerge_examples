package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.CatalogsTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.ConnectionStatusTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.DataTypesTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.KeywordsTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.MetaDataTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.NumericFunctionsTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.SchemasTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.StringFunctionsTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.SystemFunctionsTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.TableTypesTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database.TimeDateFunctionsTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.procedure.ProcedureColumnsTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ColumnPriviligesTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ColumnsTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ContentsTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ExportedKeysTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ImportedKeysTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.IndexesTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.PrimaryKeyTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.RowCountTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.RowIDTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.TablePriviligesTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.VersionColumnsTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetUpdateableTableModelListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ObjectTreePanel extends JPanel implements IObjectTreeAPI
{

   private static final long serialVersionUID = -2257109602127706539L;

   
	private static final ILogger s_log =
		LoggerController.createLogger(ObjectTreePanel.class);

	
	private IIdentifier _id = IdentifierFactory.getInstance().createIdentifier();

	
	private ISession _session;

	
	private ObjectTree _tree;

	
	private final JSplitPane _splitPane =
		new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	
	private final ObjectTreeTabbedPane _emptyTabPane;

	
	private final Map<IIdentifier, ObjectTreeTabbedPane> _tabbedPanes = 
        new HashMap<IIdentifier, ObjectTreeTabbedPane>();

	
	private SessionPropertiesListener _propsListener;

	
	private TabbedPaneListener _tabPnlListener;

	private ObjectTreeSelectionListener _objTreeSelLis = null;

   private ObjectTreeTabbedPane _selectedObjTreeTabbedPane = null;
   
    
   private TreePath[] previouslySelectedPaths = null;
   
	
	public ObjectTreePanel(ISession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;

		_emptyTabPane = new ObjectTreeTabbedPane(_session);

		createGUI();

      session.getApplication().getThreadPool().addTask(new Runnable()
      {
         public void run()
         {
            doBackgroundInitializations();
         }
      });
   }

   private void doBackgroundInitializations()
   {
      try
      {
         
         addDetailTab(DatabaseObjectType.SESSION, new MetaDataTab());
         addDetailTab(DatabaseObjectType.SESSION, new ConnectionStatusTab());

         try
         {
            SQLDatabaseMetaData md =
               _session.getSQLConnection().getSQLMetaData();
            if (md.supportsCatalogs())
            {
               _addDetailTab(DatabaseObjectType.SESSION, new CatalogsTab());
            }
         }
         catch (Throwable th)
         {
            s_log.error("Error in supportsCatalogs()", th);
         }

         try
         {
            SQLDatabaseMetaData md =
               _session.getSQLConnection().getSQLMetaData();
            if (md.supportsSchemas())
            {
               _addDetailTab(DatabaseObjectType.SESSION, new SchemasTab());
            }
         }
         catch (Throwable th)
         {
            s_log.error("Error in supportsCatalogs()", th);
         }
         _addDetailTab(DatabaseObjectType.SESSION, new TableTypesTab());
         _addDetailTab(DatabaseObjectType.SESSION, new DataTypesTab());
         _addDetailTab(DatabaseObjectType.SESSION, new NumericFunctionsTab());
         _addDetailTab(DatabaseObjectType.SESSION, new StringFunctionsTab());
         _addDetailTab(DatabaseObjectType.SESSION, new SystemFunctionsTab());
         _addDetailTab(DatabaseObjectType.SESSION, new TimeDateFunctionsTab());
         _addDetailTab(DatabaseObjectType.SESSION, new KeywordsTab());

         
         _addDetailTab(DatabaseObjectType.CATALOG, new DatabaseObjectInfoTab());

         
         _addDetailTab(DatabaseObjectType.SCHEMA, new DatabaseObjectInfoTab());

         _addDetailTabForTableLikeObjects(DatabaseObjectType.TABLE);
         _addDetailTabForTableLikeObjects(DatabaseObjectType.VIEW);

         
         _addDetailTab(DatabaseObjectType.PROCEDURE, new DatabaseObjectInfoTab());
         _addDetailTab(DatabaseObjectType.PROCEDURE, new ProcedureColumnsTab());

         
         _addDetailTab(DatabaseObjectType.UDT, new DatabaseObjectInfoTab());

         _session.getSchemaInfo().addSchemaInfoUpdateListener(new net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoUpdateListener()
         {
            public void schemaInfoUpdated()
            {
               refreshTree(false);
            }
         });

      }
      catch (Throwable th)
      {
         s_log.error("Error doing background initalization of Object tree" , th);
      }
   }

   private void _addDetailTabForTableLikeObjects(final DatabaseObjectType type)
   {
       GUIUtils.processOnSwingEventThread(new Runnable() {
          public void run() {
              addDetailTabForTableLikeObjects(type);
          }
       });
   }
    
   private void addDetailTabForTableLikeObjects(DatabaseObjectType type)
   {
      
      addDetailTab(type, new DatabaseObjectInfoTab());

      ContentsTab conttentsTab = new ContentsTab(this);
      conttentsTab.addListener(new DataSetUpdateableTableModelListener()
      {
         public void forceEditMode(boolean mode)
         {
            onForceEditMode(mode);
         }
      });
      addDetailTab(type, conttentsTab);

      addDetailTab(type, new RowCountTab());
      addDetailTab(type, new ColumnsTab());
      addDetailTab(type, new PrimaryKeyTab());
      addDetailTab(type, new ExportedKeysTab());
      addDetailTab(type, new ImportedKeysTab());
      addDetailTab(type, new IndexesTab());
      addDetailTab(type, new TablePriviligesTab());
      addDetailTab(type, new ColumnPriviligesTab());
      addDetailTab(type, new RowIDTab());
      addDetailTab(type, new VersionColumnsTab());
   }

   
	public IIdentifier getIdentifier()
	{
		return _id;
	}

	public void addNotify()
	{
		super.addNotify();
		_tabPnlListener = new TabbedPaneListener();
		_propsListener = new SessionPropertiesListener();
		_session.getProperties().addPropertyChangeListener(_propsListener);

		Iterator<ObjectTreeTabbedPane> it = _tabbedPanes.values().iterator();
		while (it.hasNext())
		{
			
			ObjectTreeTabbedPane ottp = it.next();
			ottp.getTabbedPane().addChangeListener(_tabPnlListener);
		}

		_objTreeSelLis = new ObjectTreeSelectionListener();
		_tree.addTreeSelectionListener(_objTreeSelLis);
	}

	public void removeNotify()
	{
		super.removeNotify();

		if (_propsListener != null)
		{
			_session.getProperties().removePropertyChangeListener(_propsListener);
			_propsListener = null;
		}

		Iterator<ObjectTreeTabbedPane> it = _tabbedPanes.values().iterator();
		while (it.hasNext())
		{
			ObjectTreeTabbedPane pane = it.next();
			pane.getTabbedPane().removeChangeListener(_tabPnlListener);
		}
		_tabPnlListener = null;
		if (_objTreeSelLis != null)
		{
			_tree.removeTreeSelectionListener(_objTreeSelLis);
			_objTreeSelLis = null;
		}
	}

	
	public void addExpander(DatabaseObjectType dboType, INodeExpander expander)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		if (expander == null)
		{
			throw new IllegalArgumentException("Null INodeExpander passed");
		}
		_tree.getTypedModel().addExpander(dboType, expander);
	}

       
    public void expandNode(ObjectTreeNode node) {
        IDatabaseObjectInfo info = node.getDatabaseObjectInfo();
        TreePath path = getTreePath(info.getCatalogName(), 
                                    info.getSchemaName(), 
                                    info.getSimpleName());    
        _tree.fireTreeExpanded(path);
    }
    
    private void _addDetailTab(final DatabaseObjectType dboType, 
                               final IObjectTab tab) 
    {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                addDetailTab(dboType, tab);
            }
        });
    }
    
	
	public void addDetailTab(DatabaseObjectType dboType, IObjectTab tab)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		if (tab == null)
		{
			throw new IllegalArgumentException("IObjectPanelTab == null");
		}

		getOrCreateObjectPanelTabbedPane(dboType).addObjectPanelTab(tab);
	}

	
	public void addTreeModelListener(TreeModelListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("TreeModelListener == null");
		}
		_tree.getModel().addTreeModelListener(lis);
	}

	
	public void removeTreeModelListener(TreeModelListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("TreeModelListener == null");
		}
		_tree.getModel().removeTreeModelListener(lis);
	}

	
	public void addTreeSelectionListener(TreeSelectionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("TreeSelectionListener == null");
		}
		_tree.addTreeSelectionListener(lis);
	}

	
	public void removeTreeSelectionListener(TreeSelectionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("TreeSelectionListener == null");
		}
		_tree.removeTreeSelectionListener(lis);
	}

	
	public void addObjectTreeListener(IObjectTreeListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("IObjectTreeListener == null");
		}
		_tree.addObjectTreeListener(lis);
	}

	
	public void removeObjectTreeListener(IObjectTreeListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("IObjectTreeListener == null");
		}
		_tree.removeObjectTreeListener(lis);
	}

	
	public void addToPopup(DatabaseObjectType dboType, Action action)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		if (action == null)
		{
			throw new IllegalArgumentException("Null Action passed");
		}
		_tree.addToPopup(dboType, action);
	}

	
	public void addToPopup(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Null Action passed");
		}
		_tree.addToPopup(action);
	}

	
	public void addToPopup(DatabaseObjectType dboType, JMenu menu)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("DatabaseObjectType == null");
		}
		if (menu == null)
		{
			throw new IllegalArgumentException("JMenu == null");
		}
		_tree.addToPopup(dboType, menu);
	}

	
	public void addToPopup(JMenu menu)	{
		if (menu == null)
		{
			throw new IllegalArgumentException("JMenu == null");
		}
		_tree.addToPopup(menu);
	}

   public ISession getSession()
   {
      return _session;
   }

	
	public ObjectTreeNode[] getSelectedNodes()
	{
		return _tree.getSelectedNodes();
	}

   
   public List<ITableInfo> getSelectedTables() {
      return _tree.getSelectedTables();
   }
   
   
   public void saveSelectedPaths() {
      previouslySelectedPaths = _tree.getSelectionPaths();
   }
   
   
   public void restoreSavedSelectedPaths() {
      _tree.setSelectionPaths(previouslySelectedPaths);
      _tree.requestFocusInWindow();
   }
    
	
	public IDatabaseObjectInfo[] getSelectedDatabaseObjects()
	{
		return _tree.getSelectedDatabaseObjects();
	}

	
	public DatabaseObjectType[] getDatabaseObjectTypes()
	{
		return _tree.getTypedModel().getDatabaseObjectTypes();
	}

	
	public void refreshTree()
	{
      refreshTree(false);
   }

   public void refreshTree(boolean reloadSchemaInfo)
   {
      _tree.refresh(reloadSchemaInfo);
   }

   
	public void refreshSelectedNodes()
	{
		_tree.refreshSelectedNodes();
	}

	
	public void removeNodes(ObjectTreeNode[] nodes)
	{
		if (nodes == null)
		{
			throw new IllegalArgumentException("ObjectTreeNode[] == null");
		}
		ObjectTreeModel model = _tree.getTypedModel();
		for (int i = 0; i < nodes.length; ++i)
		{
			model.removeNodeFromParent(nodes[i]);
		}
	}

	public IObjectTab getTabbedPaneIfSelected(DatabaseObjectType dbObjectType, String title)
	{
		return getTabbedPane(dbObjectType).getTabIfSelected(title);
	}

    
    public void refreshSelectedTab() throws DataSetException 
    {
        if (_selectedObjTreeTabbedPane != null) {
            IObjectTab tab= _selectedObjTreeTabbedPane.getSelectedTab();
            if (tab != null) {
                if (tab instanceof BaseDataSetTab) {
                    BaseDataSetTab btab = (BaseDataSetTab) tab;
                    btab.refreshComponent();
                }
            }        
        }
    }
    
   
   public boolean selectInObjectTree(String catalog, String schema, String object)
   {
      if ("".equals(object)) {
          return false;
      }

      TreePath treePath = getTreePath(catalog, schema, object);
      if(null != treePath)
      {
         _tree.setSelectionPath(treePath);
         _tree.scrollPathToVisible(treePath);
         return true;
      }
      else
      {
         return false;
      }
   }

   
   private TreePath getTreePath(String catalog, String schema, String object) {
       ObjectTreeModel otm = (ObjectTreeModel) _tree.getModel();
       TreePath treePath = 
           otm.getPathToDbInfo(catalog, 
                               schema, 
                               object, 
                               (ObjectTreeNode) otm.getRoot(), 
                               false);
       if(null == treePath)
       {
          treePath = otm.getPathToDbInfo(catalog, 
                                         schema, 
                                         object, 
                                         (ObjectTreeNode) otm.getRoot(), 
                                         true);
       }
       return treePath;
   }
   
   
	public void addKnownDatabaseObjectType(DatabaseObjectType dboType)
	{
		_tree.getTypedModel().addKnownDatabaseObjectType(dboType);
	}

	
	private void setSelectedObjectPanel(final TreePath path)
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				ObjectTreeTabbedPane tabPane = null;
				if (path != null)
				{
					Object lastComp = path.getLastPathComponent();
					if (lastComp instanceof ObjectTreeNode)
					{
						ObjectTreeNode node = (ObjectTreeNode)lastComp;
						tabPane = getDetailPanel(node);
						tabPane.setDatabaseObjectInfo(node.getDatabaseObjectInfo());
						tabPane.selectCurrentTab();
					}
				}
				setSelectedObjectPanel(tabPane);
			}
		});
	}

	
	private void setSelectedObjectPanel(ObjectTreeTabbedPane pane)
	{
        _selectedObjTreeTabbedPane = pane;
		JTabbedPane comp = null;
		if (pane != null)
		{
			comp = pane.getTabbedPane();
		}
		if (comp == null)
		{
			comp = _emptyTabPane.getTabbedPane();
		}

		int divLoc = _splitPane.getDividerLocation();
		Component existing = _splitPane.getRightComponent();
		if (existing != null)
		{
			_splitPane.remove(existing);
		}
		_splitPane.add(comp, JSplitPane.RIGHT);
		_splitPane.setDividerLocation(divLoc);

		if (pane != null)
		{
			pane.selectCurrentTab();
		}
	}

	
	private ObjectTreeTabbedPane getDetailPanel(ObjectTreeNode node)
	{
		if (node == null)
		{
			throw new IllegalArgumentException("ObjectTreeNode == null");
		}

		ObjectTreeTabbedPane tabPane = getTabbedPane(node.getDatabaseObjectType());
		if (tabPane != null)
		{
			return tabPane;
		}

		return _emptyTabPane;
	}

	
	private ObjectTreeTabbedPane getTabbedPane(DatabaseObjectType dboType)
	{
		return _tabbedPanes.get(dboType.getIdentifier());
	}

	
	private ObjectTreeTabbedPane getOrCreateObjectPanelTabbedPane(DatabaseObjectType dboType)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}

		final IIdentifier key = dboType.getIdentifier();
		ObjectTreeTabbedPane tabPane = _tabbedPanes.get(key);
		if (tabPane == null)
		{
			tabPane = new ObjectTreeTabbedPane(_session);
			setupTabbedPane(tabPane);
			_tabbedPanes.put(key, tabPane);
		}
		return tabPane;
	}

	
	private void createGUI()
	{
		setLayout(new BorderLayout());

		_tree = new ObjectTree(_session);

		_splitPane.setOneTouchExpandable(true);
		_splitPane.setContinuousLayout(true);





		_splitPane.add(new LeftPanel(), JSplitPane.LEFT);
		add(_splitPane, BorderLayout.CENTER);
		_splitPane.setDividerLocation(200);





		_tree.setSelectionRow(0);
	}

	private synchronized void propertiesHaveChanged(String propName)
	{
		if (propName == null
			|| propName.equals(SessionProperties.IPropertyNames.META_DATA_OUTPUT_CLASS_NAME)
			|| propName.equals(SessionProperties.IPropertyNames.TABLE_CONTENTS_OUTPUT_CLASS_NAME)
			|| propName.equals(SessionProperties.IPropertyNames.SQL_RESULTS_OUTPUT_CLASS_NAME)
			|| propName.equals(SessionProperties.IPropertyNames.OBJECT_TAB_PLACEMENT))
		{
			final SessionProperties props = _session.getProperties();

			Iterator<ObjectTreeTabbedPane> it = _tabbedPanes.values().iterator();
			while (it.hasNext())
			{
				ObjectTreeTabbedPane pane = it.next();

				if (propName == null
					|| propName.equals(SessionProperties.IPropertyNames.META_DATA_OUTPUT_CLASS_NAME)
					|| propName.equals(SessionProperties.IPropertyNames.TABLE_CONTENTS_OUTPUT_CLASS_NAME)
					|| propName.equals(SessionProperties.IPropertyNames.SQL_RESULTS_OUTPUT_CLASS_NAME))
				{
					pane.rebuild();
				}
				if (propName == null
					|| propName.equals(SessionProperties.IPropertyNames.OBJECT_TAB_PLACEMENT))
				{
					pane.getTabbedPane().setTabPlacement(props.getObjectTabPlacement());
				}
			}
		}
	}


   private void onForceEditMode(boolean editable)
   {
      Iterator<ObjectTreeTabbedPane> it = _tabbedPanes.values().iterator();
      while (it.hasNext())
      {
         ObjectTreeTabbedPane pane = it.next();
         pane.rebuild();

      }
   }



	private void setupTabbedPane(ObjectTreeTabbedPane pane)
	{
		final SessionProperties props = _session.getProperties();
		pane.rebuild();
		final JTabbedPane p = pane.getTabbedPane();
		p.setTabPlacement(props.getObjectTabPlacement());
		p.addChangeListener(_tabPnlListener);
	}

	public void sessionWindowClosing()
	{
		_tree.dispose();	
	}

	private final class LeftPanel extends JPanel
	{
		LeftPanel()
		{
			super(new BorderLayout());

			final JScrollPane sp = new JScrollPane();
			sp.setBorder(BorderFactory.createEmptyBorder());
			sp.setViewportView(_tree);
			sp.setPreferredSize(new Dimension(200, 200));
			add(sp, BorderLayout.CENTER);
		}
	}















	
	private final class ObjectTreeSelectionListener
		implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent evt)
		{
			setSelectedObjectPanel(evt.getNewLeadSelectionPath());
		}
	}

	
	private class SessionPropertiesListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			propertiesHaveChanged(evt.getPropertyName());
		}
	}

	
	private class TabbedPaneListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent evt)
		{
			final Object src = evt.getSource();
			if (!(src instanceof JTabbedPane))
			{
				StringBuffer buf = new StringBuffer();
				buf.append("Source object in TabbedPaneListener was not a JTabbedpane")
					.append(" - it was ")
					.append(src == null ? "null" : src.getClass().getName());
				s_log.error(buf.toString());
				return;
			}
			JTabbedPane tabPane = (JTabbedPane)src;

			Object prop = tabPane.getClientProperty(ObjectTreeTabbedPane.IClientPropertiesKeys.TABBED_PANE_OBJ);
			if (!(prop instanceof ObjectTreeTabbedPane))
			{
				StringBuffer buf = new StringBuffer();
				buf.append("Client property in JTabbedPane was not an ObjectTreeTabbedPane")
					.append(" - it was ")
					.append(prop == null ? "null" : prop.getClass().getName());
				s_log.error(buf.toString());
				return;
			}

			((ObjectTreeTabbedPane)prop).selectCurrentTab();
		}
	}

    
    public void selectRoot() {
        
        
        
        
        
        
        
        
        _session.getApplication().getThreadPool().addTask(new delaySelectionRunnable());
    }

    private class delaySelectionRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(50);
            } catch (Exception e) {}
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    TreePath rootPath = _tree.getPathForRow(0);
                    _tree.setSelectionPath(rootPath);
                }
            });            
        }
    }
}
