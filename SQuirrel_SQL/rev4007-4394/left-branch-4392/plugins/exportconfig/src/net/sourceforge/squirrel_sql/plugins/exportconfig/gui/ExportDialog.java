package net.sourceforge.squirrel_sql.plugins.exportconfig.gui;

import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;

import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPlugin;
import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPreferences;

public class ExportDialog extends JDialog
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(ExportDialog.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExportDialog.class);

	public ExportDialog(IApplication app, ExportConfigPlugin plugin)
	{
		super(app.getMainFrame(), true);

		setTitle(s_stringMgr.getString("ExportDialog.title")); 
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		final ExportConfigPreferences prefs = plugin.getPreferences();
		setContentPane(new ExportPanelBuilder(app).buildPanel(prefs));
	}

}
