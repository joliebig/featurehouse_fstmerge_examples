package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

public class ProcedureTypeExpander implements INodeExpander
{
	
	public List<ObjectTreeNode> createChildren(ISession session, 
                                               ObjectTreeNode parentNode)
		throws SQLException
	{
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSchemaName();

		return createProcedureNodes(session, catalogName, schemaName);
	}

	private List<ObjectTreeNode> createProcedureNodes(ISession session, 
                                                      String catalogName,
										              String schemaName)
		throws SQLException
	{
		final String objFilter = session.getProperties().getObjectFilter();
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		IProcedureInfo[] procs = null;
		

      String procedureNamePattern = objFilter != null && objFilter.length() > 0 ? objFilter : "%";
      session.getSchemaInfo().waitTillStoredProceduresLoaded();
      procs = session.getSchemaInfo().getStoredProceduresInfos(catalogName, schemaName, procedureNamePattern);

      for (int i = 0; i < procs.length; ++i)
		{
			ObjectTreeNode child = new ObjectTreeNode(session, procs[i]);
			childNodes.add(child);
		}
		return childNodes;
	}
}
