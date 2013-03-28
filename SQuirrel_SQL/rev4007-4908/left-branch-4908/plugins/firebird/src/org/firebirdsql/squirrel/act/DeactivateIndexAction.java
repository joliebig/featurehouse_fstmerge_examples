package org.firebirdsql.squirrel.act;
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

public class DeactivateIndexAction extends SquirrelAction implements ISessionAction 
{
	
	private ISession _session;

	
	private final IPlugin _plugin;

	
	public DeactivateIndexAction(IApplication app, Resources rsrc,
							IPlugin plugin)
	{
		super(app, rsrc);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (rsrc == null)
		{
			throw new IllegalArgumentException("Resources == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("IPlugin == null");
		}

		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			try
			{
				new AlterIndexCommand(_session, _plugin, false).execute();
			}
			catch (Throwable th)
			{
				_session.showErrorMessage(th);
			}
		}
	}

	
	public void setSession(ISession session)
	{
		_session = session;
	}
}
