package net.sourceforge.squirrel_sql.plugins.sqlval.action;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

import net.sourceforge.squirrel_sql.plugins.sqlval.LogonDialog;
import net.sourceforge.squirrel_sql.plugins.sqlval.SQLValidatorPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServicePreferences;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSessionProperties;

public class ConnectAction extends SquirrelAction implements ISessionAction
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(ConnectAction.class);

	
	private final WebServicePreferences _prefs;

	
	private final SQLValidatorPlugin _plugin;

	
	private ISession _session;

	
	public ConnectAction(IApplication app, Resources rsrc,
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
			final WebServiceSessionProperties sessionProps = _plugin.getWebServiceSessionProperties(_session);
			final JDialog dlog = new LogonDialog(_session, _prefs, sessionProps);
			dlog.setVisible(true);
		}
	}

	public void setSession(ISession session)
	{
		_session = session;
	}

}

