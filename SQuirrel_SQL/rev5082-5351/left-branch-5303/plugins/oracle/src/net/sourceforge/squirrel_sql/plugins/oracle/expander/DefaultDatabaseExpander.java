
package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import java.sql.SQLException;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.DatabaseExpander;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.plugins.oracle.IObjectTypes;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.InstanceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.SessionDetailsTab;


public class DefaultDatabaseExpander extends DatabaseExpander
{


   public DefaultDatabaseExpander(ISession session)
   {
      super(session);
   }

   public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
   {
      try
      {
         final List<ObjectTreeNode> childNodes = super.createChildren(session, parentNode);

         final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();

         
         DatabaseObjectInfo dboInfo = new DatabaseObjectInfo(null, null, "USERS",
            IObjectTypes.USER_PARENT, md);
         ObjectTreeNode node = new ObjectTreeNode(session, dboInfo);
         childNodes.add(node);

         if (InstanceDetailsTab.isAccessible(session))
         {
            
            dboInfo = new DatabaseObjectInfo(null, null, "INSTANCES",
               IObjectTypes.INSTANCE_PARENT, md);
            node = new ObjectTreeNode(session, dboInfo);
            childNodes.add(node);
         }

         if (SessionDetailsTab.isAccessible(session))
         {
            
            dboInfo = new DatabaseObjectInfo(null, null, "SESSIONS",
               IObjectTypes.SESSION_PARENT, md);
            node = new ObjectTreeNode(session, dboInfo);
            childNodes.add(node);
         }

         return childNodes;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }


}
