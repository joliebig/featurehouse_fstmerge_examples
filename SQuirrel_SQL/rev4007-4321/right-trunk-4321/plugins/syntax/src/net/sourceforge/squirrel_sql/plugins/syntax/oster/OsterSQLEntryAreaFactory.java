package net.sourceforge.squirrel_sql.plugins.syntax.oster;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.syntax.IConstants;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;

public class OsterSQLEntryAreaFactory
{
	private SyntaxPugin _plugin;

	public OsterSQLEntryAreaFactory(SyntaxPugin plugin)
	{
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null OsterPlugin passed");
		}


		_plugin = plugin;
	}

	
	public ISQLEntryPanel createSQLEntryPanel(ISession session)
		throws IllegalArgumentException
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

      SyntaxPreferences prefs = getPreferences(session);

		return new OsterSQLEntryPanel(session, prefs);
	}


   private SyntaxPreferences getPreferences(ISession session)
   {
      return (SyntaxPreferences)session.getPluginObject(_plugin, IConstants.ISessionKeys.PREFS);
   }
}
