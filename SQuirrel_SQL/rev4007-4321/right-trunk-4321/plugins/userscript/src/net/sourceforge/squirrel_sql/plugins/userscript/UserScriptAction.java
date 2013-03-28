package net.sourceforge.squirrel_sql.plugins.userscript;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.userscript.kernel.ScriptListController;
import net.sourceforge.squirrel_sql.plugins.userscript.kernel.UserScriptAdmin;

import java.awt.event.ActionEvent;

public class UserScriptAction extends SquirrelAction implements ISessionAction
{

	
	protected ISession _session;

	
	protected final UserScriptPlugin _plugin;

	public UserScriptAction(IApplication app, Resources rsrc, UserScriptPlugin plugin)
	{
		super(app, rsrc);
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			UserScriptAdmin adm = _plugin.getUserScriptAdmin(_session);

			if(0 == adm.getTargets(getTargetType()).getAll().length)
			{
				return;
			}

			new ScriptListController(_session.getApplication().getMainFrame(), _plugin.getUserScriptAdmin(_session), getTargetType());
		}
	}

	
	protected boolean getTargetType()
	{
		return UserScriptAdmin.TARGET_TYPE_DB_OBJECT;
	}



	
	public void setSession(ISession session)
	{
		_session = session;
	}
}
