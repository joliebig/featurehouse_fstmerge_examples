package net.sourceforge.squirrel_sql.plugins.exportconfig.action;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPlugin;

class ExportAliasesCommand extends AbstractSaveCommand
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExportAliasesCommand.class);

	
	private final static ILogger s_log =
		LoggerController.createLogger(ExportAliasesCommand.class);

	
	private final Frame _frame;

	
	private ExportConfigPlugin _plugin;

	
	public ExportAliasesCommand(Frame frame, ExportConfigPlugin plugin)
	{
		super(frame, plugin);
		_frame = frame;
		_plugin = plugin;
	}

	
	protected void writeToFile(File file) throws IOException, XMLException
	{
		final net.sourceforge.squirrel_sql.client.gui.db.DataCache cache = _plugin.getApplication().getDataCache();
		cache.saveAliases(file);
	}

	
	protected String getDefaultFilename()
	{
		return new ApplicationFiles().getDatabaseAliasesFile().getName();
	}

	
	protected String getSaveDescription()
	{
		
		return s_stringMgr.getString("exportconfig.databaseAliases");
	}
}
