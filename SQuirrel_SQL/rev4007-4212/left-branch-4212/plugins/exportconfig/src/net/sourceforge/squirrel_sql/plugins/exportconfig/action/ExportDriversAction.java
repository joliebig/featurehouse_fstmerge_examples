package net.sourceforge.squirrel_sql.plugins.exportconfig.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPlugin;

public class ExportDriversAction extends SquirrelAction 
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExportDriversAction.class);


	
	private final IApplication _app;

	
	private final ExportConfigPlugin _plugin;

	
	public ExportDriversAction(IApplication app, Resources rsrc,
							ExportConfigPlugin plugin)
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
			throw new IllegalArgumentException("ExportConfigPlugin == null");
		}

		_app = app;
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		try
		{
			new ExportDriversCommand(getParentFrame(evt), _plugin).execute();
		}
		catch (BaseException ex)
		{
			
			_app.showErrorDialog(s_stringMgr.getString("exportconfig.errorSavingDrivers"), ex);
		}
	}
}
