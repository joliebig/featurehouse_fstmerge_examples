package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.FileViewerFactory;
import net.sourceforge.squirrel_sql.client.gui.HtmlViewerSheet;

public class ViewFileCommand implements ICommand
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ViewFileCommand.class);

	
	private static ILogger s_log =
		LoggerController.createLogger(ViewFileCommand.class);

	
	private IApplication _app;

	private File _file;

	
	public ViewFileCommand(IApplication app, File file)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (file == null)
		{
			throw new IllegalArgumentException("Null File passed");
		}
		_app = app;
		_file = file;
	}

	
	public void execute() throws BaseException
	{
		try
		{
			URL url = _file.toURI().toURL();
			FileViewerFactory factory = FileViewerFactory.getInstance();
			HtmlViewerSheet viewer = factory.getViewer(_app.getMainFrame(), url);
			viewer.setVisible(true);
			viewer.toFront();
			viewer.requestFocus();
		}
		catch (IOException ex)
		{
			final String msg = s_stringMgr.getString("ViewFileCommand.error.reading" + _file.getAbsolutePath());
			s_log.error(msg, ex);
			throw new BaseException(ex);
		}
	}
}
