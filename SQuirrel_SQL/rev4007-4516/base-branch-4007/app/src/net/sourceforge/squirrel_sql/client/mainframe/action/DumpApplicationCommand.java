package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextFileDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.HashtableDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.URLWrapper;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.DumpSessionCommand;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

public class DumpApplicationCommand implements ICommand
{
	
	private final ILogger s_log =
		LoggerController.createLogger(DumpApplicationCommand.class);

	
	private static final String PREFIX = "dump";

	
	private static final String SUFFIX = "tmp";

	
	private static String SEP = "===================================================";

	
	private IApplication _app;

	
	private File _outFile;

	
	private IMessageHandler _msgHandler;
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DumpApplicationCommand.class);

	
	public DumpApplicationCommand(IApplication app, File outFile,
									IMessageHandler msgHandler)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (outFile == null)
		{
			throw new IllegalArgumentException("Null File passed");
		}
		_app = app;
		_outFile = outFile;

		_msgHandler = msgHandler != null ? msgHandler : NullMessageHandler.getInstance();
	}

	
	public void execute()
	{
		List<File>   files  = new ArrayList<File>();
		List<String> titles = new ArrayList<String>();
		synchronized(_app)
		{
			ApplicationStatusBean bean = new ApplicationStatusBean();
			bean.load(_app);
			try
			{
				files.add(createJavaBeanDumpFile(bean));
                
				titles.add(s_stringMgr.getString("DumpApplicationCommand.title.status"));
			}
			catch (Throwable th)
			{
                
				final String msg = s_stringMgr.getString("DumpApplicationCommand.error.dumpingstatus");
				_msgHandler.showMessage(msg);
				_msgHandler.showMessage(th, null);
				s_log.error(msg, th);
			}

			
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(new HashtableDataSet(System.getProperties()));
				files.add(tempFile);
				
				titles.add(s_stringMgr.getString("DumpApplicationCommand.title.systemprops"));
			}
			catch (Throwable th)
			{
                
				final String msg = s_stringMgr.getString("DumpApplicationCommand.error.dumpingsystemprops");
				_msgHandler.showMessage(msg);
				_msgHandler.showMessage(th, null);
				s_log.error(msg, th);
			}

			
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				_app.getDataCache().saveDrivers(tempFile);
				files.add(tempFile);
                
				titles.add(s_stringMgr.getString("DumpApplicationCommand.title.drivers"));
			}
			catch (Throwable th)
			{
                
				final String msg = s_stringMgr.getString("DumpApplicationCommand.error.dumpingdrivers");
				_msgHandler.showMessage(msg);
				_msgHandler.showMessage(th, null);
				s_log.error(msg, th);
			}

			
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				_app.getDataCache().saveAliases(tempFile);
				files.add(tempFile);
                
				titles.add(s_stringMgr.getString("DumpApplicationCommand.title.aliases"));
			}
			catch (Throwable th)
			{
                
				final String msg = s_stringMgr.getString("DumpApplicationCommand.error.dumpingaliases");
				_msgHandler.showMessage(msg);
				_msgHandler.showMessage(th, null);
				s_log.error(msg, th);
			}

			
			final ISession[] sessions = _app.getSessionManager().getConnectedSessions();
			final DumpSessionCommand sessionCmd = new DumpSessionCommand();
			for (int i = 0; i < sessions.length; ++i)
			{
				try
				{
					File tempFile = File.createTempFile(PREFIX, SUFFIX);
					sessionCmd.setSession(sessions[i]);
					sessionCmd.setDumpFile(tempFile);
					sessionCmd.execute();
					files.add(tempFile);
                    
                    String title = 
                        s_stringMgr.getString("DumpApplicationCommand.title.sessiondump",
                                              sessions[i].getIdentifier());
					titles.add(title);
				}
				catch (Throwable th)
				{
                    
					final String msg = s_stringMgr.getString("DumpApplicationCommand.error.sessiondump");
					_msgHandler.showMessage(msg);
					_msgHandler.showMessage(th, null);
					s_log.error(msg, th);
				}
			}
		}

		combineTempFiles(titles, files);
		deleteTempFiles(files);
	}

	private void combineTempFiles(List<String> titles, List<File> files)
	{
		try
		{
			PrintWriter wtr = new PrintWriter(new FileWriter(_outFile));
			try
			{
                
                String header = s_stringMgr.getString("DumpApplicationCommand.header",
                                                      Calendar.getInstance().getTime());
				wtr.println(header);
				for (int i = 0, limit = files.size(); i < limit; ++i)
				{
					wtr.println();
					wtr.println();
					wtr.println(SEP);
					wtr.println(titles.get(i));
					wtr.println(SEP);
					BufferedReader rdr = new BufferedReader(new FileReader(files.get(i)));
					try
					{
						String line = null;
						while((line = rdr.readLine()) != null)
						{
							wtr.println(line);
						}
					}
					finally
					{
						rdr.close();
					}
				}
			}
			finally
			{
				wtr.close();
			}
		}
		catch (IOException ex)
		{
            
			final String msg = s_stringMgr.getString("DumpApplicationCommand.error.combiningtempfiles");
			_msgHandler.showMessage(msg);
			_msgHandler.showMessage(ex.toString());
			s_log.error(msg, ex);
		}
	}

	private void deleteTempFiles(List<File> files)
	{
		for (int i = 0, limit = files.size(); i < limit; ++i)
		{
			if (!(files.get(i)).delete())
			{
                
				s_log.error(s_stringMgr.getString("DumpApplicationCommand.error.deletetempfile"));
			}
		}
	}

	private File createJavaBeanDumpFile(Object obj)
		throws IOException, XMLException
	{
		File tempFile = File.createTempFile(PREFIX, SUFFIX);
		XMLBeanWriter wtr = new XMLBeanWriter(obj);
		wtr.save(tempFile);

		return tempFile;
	}

	public final static class ApplicationStatusBean
	{
		private SquirrelPreferences _prefs;
		private PluginInfo[] _plugins;
		private String[] _appArgs;
		private String _version;
		private String _pluginLoc;
		private URLWrapper[] _pluginURLs;

		public ApplicationStatusBean()
		{
			super();
		}

		void load(IApplication app)
		{
			_prefs = app.getSquirrelPreferences();
			_plugins = app.getPluginManager().getPluginInformation();
			_appArgs = ApplicationArguments.getInstance().getRawArguments();
			_version = Version.getVersion();
			_pluginLoc = new ApplicationFiles().getPluginsDirectory().getAbsolutePath();
			URL[] urls = app.getPluginManager().getPluginURLs();
			_pluginURLs = new URLWrapper[urls.length];
			for (int i = 0; i < urls.length; ++i)
			{
				_pluginURLs[i] = new URLWrapper(urls[i]);
			}
		}

		public String getVersion()
		{
			return _version;
		}

		public SquirrelPreferences getPreferences()
		{
			return _prefs;
		}

		public String getPluginLocation()
		{
			return _pluginLoc;
		}

		public PluginInfo[] getPluginInfo()
		{
			return _plugins;
		}

		public URLWrapper[] getPluginURLs()
		{
			return _pluginURLs;
		}

		public PluginInfo getPluginInfo(int idx)
			throws ArrayIndexOutOfBoundsException
		{
			return _plugins[idx];
		}

		public String[] getApplicationArgument()
		{
			return _appArgs;
		}

		public String getApplicationArgument(int idx)
			throws ArrayIndexOutOfBoundsException
		{
			return _appArgs[idx];
		}
	}
}
