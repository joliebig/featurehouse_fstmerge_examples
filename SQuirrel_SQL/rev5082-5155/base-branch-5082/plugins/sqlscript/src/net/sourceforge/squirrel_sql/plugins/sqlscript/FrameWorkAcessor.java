package net.sourceforge.squirrel_sql.plugins.sqlscript;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;



public class FrameWorkAcessor
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(FrameWorkAcessor.class);

	public static ISQLPanelAPI getSQLPanelAPI(ISession session, SQLScriptPlugin plugin)
	{
		
		

		if(session.getActiveSessionWindow() instanceof ObjectTreeInternalFrame)
		{
			
			session.showMessage(s_stringMgr.getString("sqlscript.scriptWritten"));
			return session.getSessionSheet().getSQLPaneAPI();
		}
		else
		{
			return session.getSQLPanelAPIOfActiveSessionWindow();
		}
	}

	public static IObjectTreeAPI getObjectTreeAPI(ISession session, SQLScriptPlugin sqlScriptPlugin)
	{
		
		

		return session.getObjectTreeAPIOfActiveSessionWindow();
	}
}
