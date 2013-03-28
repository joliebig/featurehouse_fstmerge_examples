package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;


public class ProcedureExpander implements INodeExpander
{
   
   public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
   {
      final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
      final String schemaName = parentDbinfo.getSchemaName();
      return createProcedureNodes(session, null, schemaName);
   }

   private List<ObjectTreeNode> createProcedureNodes(ISession session, String catalogName,
                                     String schemaName)
   {
      final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
      IProcedureInfo[] procs =
         session.getSchemaInfo().getStoredProceduresInfos(catalogName, schemaName, new ObjFilterMatcher(session.getProperties()));

      for (int i = 0; i < procs.length; ++i)
      {
         if (procs[i].getProcedureType() == DatabaseMetaData.procedureNoResult)
            childNodes.add(new ObjectTreeNode(session, procs[i]));
      }
      return childNodes;
   }
}
