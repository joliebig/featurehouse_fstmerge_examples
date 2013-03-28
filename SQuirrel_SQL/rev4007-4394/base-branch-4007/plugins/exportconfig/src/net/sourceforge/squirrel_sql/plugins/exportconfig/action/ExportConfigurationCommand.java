package net.sourceforge.squirrel_sql.plugins.exportconfig.action;

import java.awt.Frame;

import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;

import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPlugin;
import net.sourceforge.squirrel_sql.plugins.exportconfig.gui.ExportDialog;

class ExportConfigurationCommand implements ICommand
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(ExportConfigurationCommand.class);

	
	private final Frame _frame;

	
	private ExportConfigPlugin _plugin;

	
	public ExportConfigurationCommand(Frame frame, ExportConfigPlugin plugin)
	{
		super();

		if (frame == null)
		{
			throw new IllegalArgumentException("Frame == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("ExportConfigPlugin == null");
		}

		_frame = frame;
		_plugin = plugin;
	}

	
	public void execute()
	{
		final IApplication app = _plugin.getApplication();
		final JDialog dlog = new ExportDialog(app, _plugin);
		dlog.pack();
		GUIUtils.centerWithinParent(dlog);
		dlog.show();
 	}
}
