package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
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
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
      session.getSchemaInfo().waitTillStoredProceduresLoaded();
      IProcedureInfo[] procs = session.getSchemaInfo().getStoredProceduresInfos(catalogName, schemaName, new ObjFilterMatcher(session.getProperties()));

      for (int i = 0; i < procs.length; ++i)
		{
			ObjectTreeNode child = new ObjectTreeNode(session, procs[i]);
			childNodes.add(child);
		}
		return childNodes;
	}
}
