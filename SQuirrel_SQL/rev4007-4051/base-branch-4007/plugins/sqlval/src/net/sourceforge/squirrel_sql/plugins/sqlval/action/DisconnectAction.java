package net.sourceforge.squirrel_sql.plugins.sqlval.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

import net.sourceforge.squirrel_sql.plugins.sqlval.SQLValidatorPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServicePreferences;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSessionProperties;
import net.sourceforge.squirrel_sql.plugins.sqlval.cmd.DisconnectCommand;

public class DisconnectAction extends SquirrelAction implements ISessionAction
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(DisconnectAction.class);

	
	private final WebServicePreferences _prefs;

	
	private final SQLValidatorPlugin _plugin;

	
	private ISession _session;

	
	public DisconnectAction(IApplication app, Resources rsrc,
									WebServicePreferences prefs,
									SQLValidatorPlugin plugin)
	{
		super(app, rsrc);
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		_prefs = prefs;
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			WebServiceSessionProperties wss = _plugin.getWebServiceSessionProperties(_session);
			try
			{
				new DisconnectCommand(_session, _prefs, wss).execute();
			}
			catch (BaseException ex)
			{
				_session.getApplication().showErrorDialog("Error closing SQL Validation web service", ex);
			}
		}
	}

	public void setSession(ISession session)
	{
		_session = session;
	}
}

