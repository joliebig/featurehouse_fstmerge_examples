package net.sourceforge.squirrel_sql.plugins.userscript;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;



public class FrameWorkAcessor
{
   public static ISQLPanelAPI getSQLPanelAPI(ISession session, UserScriptPlugin plugin)
   {
      
      return session.getSessionSheet().getSQLPaneAPI();
   }

   public static IObjectTreeAPI getObjectTreeAPI(ISession session, UserScriptPlugin plugin)
   {
      
      return session.getSessionSheet().getObjectTreePanel();
   }
}
