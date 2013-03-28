package net.sourceforge.squirrel_sql.plugins.mysql.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

public class AnalyzeTableAction extends SquirrelAction implements ISessionAction 
{
	
	private ISession _session;

	
	private final MysqlPlugin _plugin;

	
	public AnalyzeTableAction(IApplication app, Resources rsrc,
							MysqlPlugin plugin)
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
			throw new IllegalArgumentException("MysqlPlugin == null");
		}

		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			try
			{
				new AnalyzeTableCommand(_session, _plugin).execute();
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
