package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class CloseSessionAction extends SquirrelAction
									implements ISessionAction
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(DumpSessionAction.class);

	private ISession _session;

	public CloseSessionAction(IApplication app)
	{
		super(app);
	}

	public void setSession(ISession session)
	{
		_session = session;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			try
			{
				new CloseSessionCommand(_session).execute();
			}
			catch (Throwable ex)
			{
				final String msg = "Error occured closing session";
				_session.showErrorMessage(msg + ": " + ex);
				s_log.error(msg, ex);
			}
		}
	}
}