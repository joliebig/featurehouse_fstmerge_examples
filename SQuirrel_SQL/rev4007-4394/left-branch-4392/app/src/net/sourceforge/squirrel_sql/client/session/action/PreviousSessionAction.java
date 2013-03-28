package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class PreviousSessionAction extends SquirrelAction
									implements ISessionAction
{
	private ISession _session;

	public PreviousSessionAction(IApplication app)
	{
		super(app);
	}

	public void setSession(ISession session)
	{
		_session = session;
	}

	public void actionPerformed(ActionEvent evt)
	{
		getApplication().getWindowManager().activatePreviousSessionWindow();
	}
}
