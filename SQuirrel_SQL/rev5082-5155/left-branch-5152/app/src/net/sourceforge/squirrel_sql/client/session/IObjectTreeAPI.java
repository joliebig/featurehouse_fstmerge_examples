package net.sourceforge.squirrel_sql.client.session;

import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

public interface IObjectTreeAPI extends IHasIdentifier
{
   
   ISession getSession();

	
	void addExpander(DatabaseObjectType dboType, INodeExpander expander);

	
	void addDetailTab(DatabaseObjectType dboType, IObjectTab tab);

	
	void addTreeModelListener(TreeModelListener lis);

	
	void removeTreeModelListener(TreeModelListener lis);

	
	void addTreeSelectionListener(TreeSelectionListener lis);

	
	void removeTreeSelectionListener(TreeSelectionListener lis);

	
	void addObjectTreeListener(IObjectTreeListener lis);

	
	void removeObjectTreeListener(IObjectTreeListener lis);

	
	void addToPopup(DatabaseObjectType dboType, Action action);

	
	void addToPopup(Action action);

	
	void addToPopup(DatabaseObjectType dboType, JMenu menu);

	
	void addToPopup(JMenu menu);

	
	ObjectTreeNode[] getSelectedNodes();

	
	IDatabaseObjectInfo[] getSelectedDatabaseObjects();

    
    List<ITableInfo> getSelectedTables();
    
	
	void refreshTree();

   
   void refreshTree(boolean reloadSchemaInfo);


   
	void refreshSelectedNodes();

	
	void removeNodes(ObjectTreeNode[] nodes);

   
   DatabaseObjectType[] getDatabaseObjectTypes();

	
	void addKnownDatabaseObjectType(DatabaseObjectType dboType);

	IObjectTab getTabbedPaneIfSelected(DatabaseObjectType dbObjectType, String title);

   
   boolean selectInObjectTree(String catalog, String schema, FilterMatcher objectMatcher);
   
   
   void selectRoot();
   
   
   void expandNode(ObjectTreeNode node);
   
   
   void refreshSelectedTab() throws DataSetException;


   FindInObjectTreeController getFindController();
}
