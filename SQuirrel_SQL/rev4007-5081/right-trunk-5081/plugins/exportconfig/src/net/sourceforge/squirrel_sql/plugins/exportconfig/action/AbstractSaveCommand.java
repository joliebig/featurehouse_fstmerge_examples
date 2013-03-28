package net.sourceforge.squirrel_sql.plugins.exportconfig.action;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import net.sourceforge.squirrel_sql.fw.gui.ChooserPreviewer;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

import net.sourceforge.squirrel_sql.client.IApplication;

import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPlugin;

abstract class AbstractSaveCommand implements ICommand
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AbstractSaveCommand.class);


	
	private final static ILogger s_log =
		LoggerController.createLogger(AbstractSaveCommand.class);

	
	private static File s_lastDir;

	
	private final Frame _frame;

	
	private ExportConfigPlugin _plugin;

	
	AbstractSaveCommand(Frame frame, ExportConfigPlugin plugin)
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

	public void execute() throws BaseException
	{
		final JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileExtensionFilter("XML files",
												new String[] { ".xml" }));
		File file = null;
		if (s_lastDir != null)
		{
			file = new File(s_lastDir, getDefaultFilename());
		}
		else
		{
			file = new File((File)null, getDefaultFilename());
		}
		chooser.setSelectedFile(file);
		chooser.setAccessory(new ChooserPreviewer());
		chooser.setDialogTitle("Save " + getSaveDescription());

		for (;;)
		{
			if (chooser.showSaveDialog(_frame) == JFileChooser.CANCEL_OPTION)
			{
				break;
			}
			if (saveFile(chooser.getSelectedFile()))
			{
				break;
			}
		}
	}

	private boolean saveFile(File file)
	{
		if (file.exists())
		{
			
			String msg = s_stringMgr.getString("exportconfig.fileExistsReplace", file.getAbsolutePath());

			if (!Dialogs.showYesNo(_frame, msg))
			{
				return false;
			}
			if (!file.canWrite())
			{
				
				msg = s_stringMgr.getString("exportconfig.fileExistsButReadOnly", file.getAbsolutePath());

				Dialogs.showOk(_frame, msg);
				return false;
			}
			file.delete();
		}

		s_lastDir = file.getParentFile();

		final IApplication app = _plugin.getApplication();
		try
		{
			writeToFile(file);

			String[] params = new String[]
				{
					getSaveDescription(),
					file.getAbsolutePath()
				};

			
			String msg = s_stringMgr.getString("exportconfig.fileSavedTo", params);

			Dialogs.showOk(_frame, msg);
		}
		catch (IOException ex)
		{
			
			String msg = s_stringMgr.getString("exportconfig.ioErrorWritingTo", file.getAbsolutePath());
			_plugin.getApplication().showErrorDialog(msg, ex);
		}
		catch (XMLException ex)
		{
			
			String msg = s_stringMgr.getString("exportconfig.xmlErrorWritingTo", file.getAbsolutePath());
			_plugin.getApplication().showErrorDialog(msg, ex);
		}
		return true;
	}

	
	protected abstract String getSaveDescription();

	
	protected abstract String getDefaultFilename();

	
	protected abstract void writeToFile(File file)
					throws IOException, XMLException;
}
