package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class RefreshTreeItemAction extends SquirrelAction
										implements ISessionAction
{
	
	private ISession _session;

	
	public RefreshTreeItemAction(IApplication app)
		throws IllegalArgumentException
	{
		super(app);
	}

	
	public void actionPerformed(ActionEvent e)
	{




















		Dialogs.showNotYetImplemented(_session.getSessionSheet());
	}

	
	public void setSession(ISession session)
	{
		_session = session;
	}

}
