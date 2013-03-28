package net.sourceforge.squirrel_sql.plugins.exportconfig.action;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPlugin;

class ExportSettingsCommand extends AbstractSaveCommand
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExportSettingsCommand.class);


	
	private final static ILogger s_log =
		LoggerController.createLogger(ExportSettingsCommand.class);

	
	private final Frame _frame;

	
	private ExportConfigPlugin _plugin;

	
	public ExportSettingsCommand(Frame frame, ExportConfigPlugin plugin)
	{
		super(frame, plugin);
		_frame = frame;
		_plugin = plugin;
	}

	
	protected void writeToFile(File file) throws IOException, XMLException
	{
		final SquirrelPreferences prefs = _plugin.getApplication().getSquirrelPreferences();
		new XMLBeanWriter(prefs).save(file);
	}

	
	protected String getDefaultFilename()
	{
		return new ApplicationFiles().getUserPreferencesFile().getName();
	}

	
	protected String getSaveDescription()
	{
		
		return s_stringMgr.getString("exportconfig.applicationSettings");
	}

}
