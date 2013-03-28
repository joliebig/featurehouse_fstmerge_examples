package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import java.util.HashMap;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;
import net.sourceforge.squirrel_sql.plugins.syntax.IConstants;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;

import org.netbeans.editor.DialogSupport;
import org.netbeans.editor.ImplementationProvider;
import org.netbeans.modules.editor.NbImplementationProvider;


public class NetbeansSQLEntryAreaFactory
{
	private SyntaxPugin _plugin;
   private SyntaxFactory _syntaxFactory;

   public NetbeansSQLEntryAreaFactory(SyntaxPugin plugin)
	{
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null NetbeansPlugin passed");
		}

		_plugin = plugin;
      _syntaxFactory = new SyntaxFactory();

      
      DialogSupport.setDialogFactory(new SquirrelNBDialogFactory(_plugin));
      ImplementationProvider.registerDefault(new NbImplementationProvider());
	}

	
	public ISQLEntryPanel createSQLEntryPanel(ISession session, 
                                              HashMap<String, Object> props)
		throws IllegalArgumentException
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		SyntaxPreferences prefs = getPreferences(session);
      return new NetbeansSQLEntryPanel(session, prefs, _syntaxFactory, _plugin, props);
	}

	private SyntaxPreferences getPreferences(ISession session)
	{
		return (SyntaxPreferences)session.getPluginObject(_plugin, IConstants.ISessionKeys.PREFS);
	}


   public void sessionEnding(ISession sess)
   {
      _syntaxFactory.sessionEnding(sess);
   }
}
