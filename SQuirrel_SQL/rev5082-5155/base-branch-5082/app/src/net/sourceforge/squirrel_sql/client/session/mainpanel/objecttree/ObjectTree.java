package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.CopyQualifiedObjectNameAction;
import net.sourceforge.squirrel_sql.client.session.action.CopySimpleObjectNameAction;
import net.sourceforge.squirrel_sql.client.session.action.DeleteSelectedTablesAction;
import net.sourceforge.squirrel_sql.client.session.action.EditWhereColsAction;
import net.sourceforge.squirrel_sql.client.session.action.FilterObjectsAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeItemAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshSchemaInfoAction;
import net.sourceforge.squirrel_sql.client.session.action.SQLFilterAction;
import net.sourceforge.squirrel_sql.client.session.action.SetDefaultCatalogAction;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.EnumerationIterator;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

class ObjectTree extends JTree
{
    private static final long serialVersionUID = 1L;

    
	private static final ILogger s_log =
		LoggerController.createLogger(ObjectTree.class);

	
	private final ObjectTreeModel _model;

	
	transient private final ISession _session;

	
	private final Map<IIdentifier, JPopupMenu> _popups = 
        new HashMap<IIdentifier, JPopupMenu>();

	
	private final JPopupMenu _globalPopup = new JPopupMenu();

	private final List<Action> _globalActions = new ArrayList<Action>();

	
	private Object _syncObject = new Object();

	
	private Map<String, Object> _expandedPathNames = new HashMap<String, Object>();

	
	private EventListenerList _listenerList = new EventListenerList();

   private boolean _startExpandInThread = true;

   
   ObjectTree(ISession session)
   {
      super(new ObjectTreeModel(session));
      if (session == null)
      {
         throw new IllegalArgumentException("ISession == null");
      }
      setRowHeight(getFontMetrics(getFont()).getHeight());
      _session = session;
      _model = (ObjectTreeModel)getModel();
      setModel(_model);

      addTreeExpansionListener(new NodeExpansionListener());

      addTreeSelectionListener(new TreeSelectionListener()
      {
         public void valueChanged(TreeSelectionEvent e)
         {
            if(null != e.getNewLeadSelectionPath())
            {
               scrollPathToVisible(e.getNewLeadSelectionPath());
            }
         }
      });

      setShowsRootHandles(true);

      
      final ActionCollection actions = session.getApplication().getActionCollection();

      
      addToPopup(actions.get(RefreshSchemaInfoAction.class));
      addToPopup(actions.get(RefreshObjectTreeItemAction.class));

      addToPopup(DatabaseObjectType.TABLE, actions.get(EditWhereColsAction.class));

      addToPopup(DatabaseObjectType.TABLE, actions.get(SQLFilterAction.class));
      addToPopup(DatabaseObjectType.VIEW, actions.get(SQLFilterAction.class));

      addToPopup(DatabaseObjectType.TABLE, actions.get(DeleteSelectedTablesAction.class));

      addToPopup(DatabaseObjectType.SESSION, actions.get(FilterObjectsAction.class));


      session.getApplication().getThreadPool().addTask(new Runnable() {
          public void run() {
            try
            {
                
                
                if (_session.getSQLConnection().getSQLMetaData().supportsCatalogs())
                {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            addToPopup(DatabaseObjectType.CATALOG,
                                       actions.get(SetDefaultCatalogAction.class));
                        }

                    });
                }
            }
            catch (Throwable th)
            {
                
                s_log.debug(th);
            }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    addToPopup(actions.get(CopySimpleObjectNameAction.class));
                    addToPopup(actions.get(CopyQualifiedObjectNameAction.class));


                  addMouseListener(new ObjectTreeMouseListener());
                  setCellRenderer(new ObjectTreeCellRenderer(_model, _session));
                  ObjectTree.this.refresh(false);
                  ObjectTree.this.setSelectionPath(ObjectTree.this.getPathForRow(0));
                }
            });
          }
      });


   }

   
   private class ObjectTreeMouseListener extends MouseAdapter {
      public void mousePressed(MouseEvent evt)
      {      	
      	 
          if (evt.isPopupTrigger())
          {
    			
         	
         	 if (_session.getApplication().getSquirrelPreferences().getSelectOnRightMouseClick()) {         	 
	         	 TreePath path = ObjectTree.this.getPathForLocation(evt.getX(), evt.getY());
	         	 boolean alreadySelected = false;
	         	 TreePath[] selectedPaths = ObjectTree.this.getSelectionPaths();
	         	 if (selectedPaths != null) {	         	 
		         	 for (TreePath selectedPath : selectedPaths) {
		         		 if (path != null && path.equals(selectedPath)) {
		         			 alreadySelected = true;
		         			 break;
		         		 }
		         	 }
	         	 }
	         	 if (!alreadySelected) {
	         		 ObjectTree.this.setSelectionPath(path);
	         	 }
         	 }
             showPopup(evt.getX(), evt.getY());
          }
      }
      public void mouseReleased(MouseEvent evt)
      {
          if (evt.isPopupTrigger())
          {
              showPopup(evt.getX(), evt.getY());
          }
      }   	
   }
   
	
	public void addNotify()
	{
		super.addNotify();
		
		
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	
	public void removeNotify()
	{
		super.removeNotify();

		
		ToolTipManager.sharedInstance().unregisterComponent(this);
	}

	
	public String getToolTipText(MouseEvent evt)
	{
		String tip = null;
		final TreePath path = getPathForLocation(evt.getX(), evt.getY());
		if (path != null)
		{
			tip = path.getLastPathComponent().toString();
		}
		else
		{
			tip = getToolTipText();
		}
		return tip;
	}

	
	public ObjectTreeModel getTypedModel()
	{
		return _model;
	}

	
	public void refresh(final boolean reloadSchemaInfo)
	{
      Runnable task = new Runnable()
      {
         public void run()
         {
            if (reloadSchemaInfo)
            {
               _session.getSchemaInfo().reloadAll();
            }


            GUIUtils.processOnSwingEventThread(new Runnable()
            {
               public void run()
               {
                  refreshTree();
               }
            });
         }
      };

      if(reloadSchemaInfo)
      {
         _session.getApplication().getThreadPool().addTask(task);
      }
      else
      {
         
         task.run();
      }
   }

   private void refreshTree()
   {
      final TreePath[] selectedPaths = getSelectionPaths();
      final Map<String, Object> selectedPathNames = 
          new HashMap<String, Object>();
      if (selectedPaths != null)
      {
         for (int i = 0; i < selectedPaths.length; ++i)
         {
            selectedPathNames.put(selectedPaths[i].toString(), null);
         }
      }
      ObjectTreeNode root = _model.getRootObjectTreeNode();
      root.removeAllChildren();
      fireObjectTreeCleared();
      startExpandingTree(root, false, selectedPathNames, false);
      fireObjectTreeRefreshed();
   }

   
   public void refreshSelectedNodes()
   {

      final TreePath[] selectedPaths = getSelectionPaths();
      ObjectTreeNode[] nodes = getSelectedNodes();
      final Map<String, Object> selectedPathNames = 
          new HashMap<String, Object>();
      if (selectedPaths != null)
      {
         for (int i = 0; i < selectedPaths.length; ++i)
         {
            selectedPathNames.put(selectedPaths[i].toString(), null);
         }
      }
      clearSelection();


      DefaultMutableTreeNode parent = (DefaultMutableTreeNode) nodes[0].getParent();

      if (parent != null)
      {
         parent.removeAllChildren();
         startExpandingTree((ObjectTreeNode) parent, false, selectedPathNames, true);
      }
      else
      {
         nodes[0].removeAllChildren();
         startExpandingTree(nodes[0], false, selectedPathNames, true);
      }
   }

   
	public void addObjectTreeListener(IObjectTreeListener lis)
	{
		_listenerList.add(IObjectTreeListener.class, lis);
	}

	
	void removeObjectTreeListener(IObjectTreeListener lis)
	{
		_listenerList.remove(IObjectTreeListener.class, lis);
	}

	
	private void restoreExpansionState(ObjectTreeNode node,
	                                   Map<String, Object> previouslySelectedTreePathNames, 
                                       List<TreePath> selectedTreePaths)
	{
		if (node == null)
		{
			throw new IllegalArgumentException("ObjectTreeNode == null");
		}

		final TreePath nodePath = new TreePath(node.getPath());
        if (matchKeyPrefix(previouslySelectedTreePathNames, node, nodePath.toString()))
		{
			selectedTreePaths.add(nodePath);
		}


      try
      {
         _startExpandInThread = false;
         expandPath(nodePath);
      }
      finally
      {
         _startExpandInThread = true;
      }



      
		
		
      @SuppressWarnings("unchecked")
      Enumeration<ObjectTreeNode> childEnumeration = 
          (Enumeration<ObjectTreeNode>) node.children();
		Iterator<ObjectTreeNode> it = 
            new EnumerationIterator<ObjectTreeNode>(childEnumeration);
		while (it.hasNext())
		{
			final ObjectTreeNode child = it.next();
			final TreePath childPath = new TreePath(child.getPath());
			final String childPathName = childPath.toString();

         if (matchKeyPrefix(previouslySelectedTreePathNames, child, childPathName))
			{
				selectedTreePaths.add(childPath);
			}

			if (_expandedPathNames.containsKey(childPathName))
			{
				restoreExpansionState(child, previouslySelectedTreePathNames, selectedTreePaths);
         }
		}
	}

    
    protected boolean matchKeyPrefix(Map<String, Object> map, ObjectTreeNode node, String path) {
        
        
        if (node.getDatabaseObjectType() != DatabaseObjectType.TABLE
                && node.getDatabaseObjectType() != DatabaseObjectType.VIEW) 
        {
            return map.containsKey(path);
        }
        Set<String> s = map.keySet();
        Iterator<String> i = s.iterator();
        String pathPrefix = path;
        if (path.indexOf("(") != -1) {
            pathPrefix = path.substring(0, path.lastIndexOf("("));
        }
        boolean result = false;
        while (i.hasNext()) {
            String key = i.next();
            String keyPrefix = key;
            if (key.indexOf("(") != -1) {
                keyPrefix = key.substring(0, key.lastIndexOf("("));
            }
            if (keyPrefix.equals(pathPrefix)) {
                result = true;
                break;
            }
        }
        return result;
    }
        
	private void startExpandingTree(ObjectTreeNode node,
                                   boolean selectNode,
                                   Map<String, Object> selectedPathNames,
                                   boolean refreshSchemaInfo
   )
	{
		ExpansionController exp = new ExpansionController(node, selectNode, selectedPathNames, refreshSchemaInfo);
      exp.run();
	}

	private void expandNode(ObjectTreeNode node, boolean selectNode)
	{
		if (node == null)
		{
			throw new IllegalArgumentException("ObjectTreeNode == null");
		}
		
		if (node.getChildCount() == 0)
		{
			
			
			final DatabaseObjectType dboType = node.getDatabaseObjectType();
			INodeExpander[] stdExpanders = _model.getExpanders(dboType);
			INodeExpander[] extraExpanders = node.getExpanders();
			if (stdExpanders.length > 0 || extraExpanders.length > 0)
			{
				INodeExpander[] expanders = null;
				if (stdExpanders.length > 0 && extraExpanders.length == 0)
				{
					expanders = stdExpanders;
				}
				else if (stdExpanders.length == 0 && extraExpanders.length > 0)
				{
					expanders = extraExpanders;
				}
				else
				{
					expanders = new INodeExpander[stdExpanders.length + extraExpanders.length];
					System.arraycopy(stdExpanders, 0, expanders, 0, stdExpanders.length);
					System.arraycopy(extraExpanders, 0, expanders, stdExpanders.length,
										extraExpanders.length);
				}
				new TreeLoader(node, expanders, selectNode).execute();
			}
		}
	}

	
	void addToPopup(DatabaseObjectType dboType, Action action)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		if (action == null)
		{
			throw new IllegalArgumentException("Null Action passed");
		}

		final JPopupMenu pop = getPopup(dboType, true);
		pop.add(action);
	}

	
	void addToPopup(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Null Action passed");
		}
		_globalPopup.add(action);
		_globalActions.add(action);

		for (Iterator<JPopupMenu> it = _popups.values().iterator(); it.hasNext();)
		{
			JPopupMenu pop = it.next();
			pop.add(action);
		}
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

		final JPopupMenu pop = getPopup(dboType, true);
		pop.add(menu);
	}

	
	public void addToPopup(JMenu menu)
	{
		if (menu == null)
		{
			throw new IllegalArgumentException("JMenu == null");
		}
		_globalPopup.add(menu);
		_globalActions.add(menu.getAction());

		for (Iterator<JPopupMenu> it = _popups.values().iterator(); it.hasNext();)
		{
			JPopupMenu pop = it.next();
			pop.add(menu);
		}
	}

	
	private JPopupMenu getPopup(DatabaseObjectType dboType, boolean create)
	{
		if (dboType == null)
		{
			throw new IllegalArgumentException("Null DatabaseObjectType passed");
		}
		IIdentifier key = dboType.getIdentifier();
		JPopupMenu pop = _popups.get(key);
		if (pop == null && create)
		{
			pop = new JPopupMenu();
			_popups.put(key, pop);
			for (Iterator<Action> it = _globalActions.iterator(); it.hasNext();)
			{
				pop.add(it.next());
			}
		}
		return pop;
	}

	
	ObjectTreeNode[] getSelectedNodes()
	{
		TreePath[] paths = getSelectionPaths();
		List<ObjectTreeNode> list = new ArrayList<ObjectTreeNode>();
		if (paths != null)
		{
			for (int i = 0; i < paths.length; ++i)
			{
				Object obj = paths[i].getLastPathComponent();
				if (obj instanceof ObjectTreeNode)
				{
					list.add((ObjectTreeNode)obj);
				}
			}
		}
		ObjectTreeNode[] ar = list.toArray(new ObjectTreeNode[list.size()]);
		Arrays.sort(ar, new NodeComparator());
		return ar;
	}
    
	
	IDatabaseObjectInfo[] getSelectedDatabaseObjects()
	{
		ObjectTreeNode[] nodes = getSelectedNodes();
		IDatabaseObjectInfo[] dbObjects = new IDatabaseObjectInfo[nodes.length];
		for (int i = 0; i < nodes.length; ++i)
		{
			dbObjects[i] = nodes[i].getDatabaseObjectInfo();
		}
		return dbObjects;
	}

    
    List<ITableInfo> getSelectedTables()
    {
        ObjectTreeNode[] nodes = getSelectedNodes();
        ArrayList<ITableInfo> result = new ArrayList<ITableInfo>(); 
        for (int i = 0; i < nodes.length; ++i)
        {
            if (nodes[i].getDatabaseObjectType() == DatabaseObjectType.TABLE) {
                result.add((ITableInfo)nodes[i].getDatabaseObjectInfo());
            }
        }
        return result;
    }
    
    
	
	private void showPopup(int x, int y)
	{
		ObjectTreeNode[] selObj = getSelectedNodes();
		if (selObj.length > 0)
		{
			
			boolean sameType = true;
			final DatabaseObjectType dboType = selObj[0].getDatabaseObjectType();
			for (int i = 1; i < selObj.length; ++i)
			{
				if (selObj[i].getDatabaseObjectType() != dboType)
				{
					sameType = false;
					break;
				}
			}

			JPopupMenu pop = null;
			if (sameType)
			{
				pop = getPopup(dboType, false);
			}
			if (pop == null)
			{
				pop = _globalPopup;
			}
			pop.show(this, x, y);
		}
	}

	
	private void fireObjectTreeCleared()
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
		ObjectTreeListenerEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == IObjectTreeListener.class)
			{
				
				if (evt == null)
				{
					evt = new ObjectTreeListenerEvent(ObjectTree.this);
				}
				((IObjectTreeListener)listeners[i + 1]).objectTreeCleared(evt);
			}
		}
	}

	
	private void fireObjectTreeRefreshed()
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
		ObjectTreeListenerEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == IObjectTreeListener.class)
			{
				
				if (evt == null)
				{
					evt = new ObjectTreeListenerEvent(ObjectTree.this);
				}
				((IObjectTreeListener)listeners[i + 1]).objectTreeRefreshed(evt);
			}
		}
	}

	public void dispose()
	{
		
		
		_globalPopup.removeAll();
		_globalPopup.setInvoker(null);
		_globalActions.clear();
		for(Iterator<JPopupMenu> i=_popups.values().iterator(); i.hasNext();)
		{
			JPopupMenu popup = i.next();
			popup.removeAll();
			popup.setInvoker(null);
		}
		_popups.clear();
	}

	private final class NodeExpansionListener implements TreeExpansionListener
	{
		public void treeExpanded(TreeExpansionEvent evt)
		{
			
			final TreePath path = evt.getPath();
			final Object parentObj = path.getLastPathComponent();
			if (parentObj instanceof ObjectTreeNode)
			{
				startExpandingTree((ObjectTreeNode)parentObj, false, null, false);
				_expandedPathNames.put(path.toString(), null);
			}
		}

		public void treeCollapsed(TreeExpansionEvent evt)
		{
			_expandedPathNames.remove(evt.getPath().toString());
		}
	}

	
	private static class NodeComparator implements Comparator<ObjectTreeNode>,
                                                   Serializable
	{
        private static final long serialVersionUID = 1L;

        public int compare(ObjectTreeNode obj1, ObjectTreeNode obj2)
		{
			return obj1.toString().compareToIgnoreCase(obj2.toString());
		}
	}

	private class ExpansionController implements Runnable
	{
		private final ObjectTreeNode _node;
		private final boolean _selectNode;
		private final Map<String, Object> _selectedPathNames;
      private boolean _refreshSchemaInfo;

      ExpansionController(ObjectTreeNode node, 
                          boolean selectNode, 
                          Map<String, Object> selectedPathNames, 
                          boolean refreshSchemaInfo)
      {
         super();
         _node = node;
         _selectNode = selectNode;
         _selectedPathNames = selectedPathNames;
         _refreshSchemaInfo = refreshSchemaInfo;
      }

		public void run()
		{
			synchronized (ObjectTree.this._syncObject)
			{
				CursorChanger cursorChg = new CursorChanger(ObjectTree.this);
				cursorChg.show();
				try
				{
               if(_refreshSchemaInfo)
               {
                  _session.getSchemaInfo().reload(_node.getDatabaseObjectInfo());
               }

               expandNode(_node, _selectNode);
					if (_selectedPathNames != null)
					{
						final List<TreePath> newlySelectedTreepaths = new ArrayList<TreePath>();
						
						GUIUtils.processOnSwingEventThread(new Runnable()
						{
							public void run()
							{
                        restoreExpansionState(_node, _selectedPathNames, newlySelectedTreepaths);
                        setSelectionPaths(newlySelectedTreepaths.toArray(new TreePath[newlySelectedTreepaths.size()]));
                     }
						});
					}
				}
				finally
				{
					cursorChg.restore();
				}
			}
		}
	}

	
	private final class TreeLoader
	{
		private ObjectTreeNode _parentNode;
		private INodeExpander[] _expanders;
		private boolean _selectParentNode;

		TreeLoader(ObjectTreeNode parentNode, INodeExpander[] expanders,
					boolean selectParentNode)
		{
			super();
			_parentNode = parentNode;
			_expanders = expanders;
			_selectParentNode= selectParentNode;
		}

		void execute()
		{
			try
			{
				try
				{
					ObjectTreeNode loadingNode = showLoadingNode();
					try
					{
						loadChildren();
					}
					finally
					{
                        if (_parentNode.isNodeChild(loadingNode)){
                            _parentNode.remove(loadingNode);
                        }
					}
				}
				finally
				{
					fireStructureChanged(_parentNode);
					if (_selectParentNode)
					{
						clearSelection();
						setSelectionPath(new TreePath(_parentNode.getPath()));
					}
				}
			}
			catch (Throwable ex)
			{
				final String msg = "Error: " + _parentNode.toString();
				s_log.error(msg, ex);
				_session.showErrorMessage(msg + ": " + ex.toString());
			}
		}

		
		private ObjectTreeNode showLoadingNode()
		{
			IDatabaseObjectInfo doi = new DatabaseObjectInfo(null, null,
								"Loading...", DatabaseObjectType.OTHER,
								_session.getSQLConnection().getSQLMetaData());
			ObjectTreeNode loadingNode = new ObjectTreeNode(_session, doi);
			_parentNode.add(loadingNode);
			fireStructureChanged(_parentNode);
			return loadingNode;
		}

		
		private void loadChildren() throws SQLException
		{
			for (int i = 0; i < _expanders.length; ++i)
			{
				boolean nodeTypeAllowsChildren = false;
				DatabaseObjectType lastDboType = null;
				List<ObjectTreeNode> list = _expanders[i].createChildren(_session, _parentNode);
				Iterator<ObjectTreeNode> it = list.iterator();
				while (it.hasNext())
				{
					Object nextObj = it.next();
					if (nextObj instanceof ObjectTreeNode)
					{
						ObjectTreeNode childNode = (ObjectTreeNode)nextObj;
						if (childNode.getExpanders().length >0)
						{
							childNode.setAllowsChildren(true);
						}
						else
						{
							DatabaseObjectType childNodeDboType = childNode.getDatabaseObjectType();
							if (childNodeDboType != lastDboType)
							{
								getTypedModel().addKnownDatabaseObjectType(childNodeDboType);
								lastDboType = childNodeDboType;
								if (_model.getExpanders(childNodeDboType).length > 0)
								{
									nodeTypeAllowsChildren = true;
								}
								else
								{
									nodeTypeAllowsChildren = false;
								}
							}
							childNode.setAllowsChildren(nodeTypeAllowsChildren);
						}
						_parentNode.add(childNode);
					}
				}
			}
		}

		
		private void fireStructureChanged(final ObjectTreeNode node)
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					ObjectTree.this._model.nodeStructureChanged(node);
				}
			});
		}
	}
}
