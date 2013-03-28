package net.sourceforge.squirrel_sql.client.session.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextFileDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ObjectArrayDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;


public class DumpSessionCommand implements ICommand
{
	
	private final ILogger s_log =
		LoggerController.createLogger(DumpSessionCommand.class);

	
	private static final String PREFIX = "dump";

	
	private static final String SUFFIX = "tmp";

	
	private static String SEP = "===================================================";

	
	private ISession _session;

	
	private File _outFile;

	
	private IMessageHandler _msgHandler;

	
	public DumpSessionCommand()
	{
		this(null, null);
	}

	
	public DumpSessionCommand(File outFile)
	{
		this(outFile, null);
	}

	
	public DumpSessionCommand(File outFile, IMessageHandler msgHandler)
	{
		super();
		_outFile = outFile;
		_msgHandler = msgHandler;
	}

	
	public void setDumpFile(File file)
	{
		if (file == null)
		{
			throw new IllegalArgumentException("Null Dump File passed");
		}
		_outFile = file;
	}

	
	public void setSession(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_session = session;
	}

	
	public void execute()
	{
		if (_session == null)
		{
			throw new IllegalStateException("Trying to dump null session");
		}
		if (_outFile == null)
		{
			throw new IllegalStateException("Trying to dump session to null file");
		}

		final List<File> files = new ArrayList<File>();
		final List<String> titles = new ArrayList<String>();
		synchronized (_session)
		{
			final ISQLConnection conn = _session.getSQLConnection();
			final SQLDatabaseMetaData md = conn.getSQLMetaData();

			
			try
			{
				files.add(createJavaBeanDumpFile(_session.getProperties()));
				titles.add("Session Properties");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping driver info";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			
			try
			{
				files.add(createJavaBeanDumpFile(_session.getDriver()));
				titles.add("Driver");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping driver info";
                showErrorMessage(msg);
                showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			
			try
			{
				files.add(createJavaBeanDumpFile(_session.getAlias()));
				titles.add("Alias");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping alias info";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			
			try
			{
				files.add(createGeneralConnectionDumpFile(conn));
				titles.add("Connection - General");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping general connection info";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(conn.getSQLMetaData().getMetaDataSet());
				files.add(tempFile);
				titles.add("Metadata");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping metadata";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(new ObjectArrayDataSet(md.getCatalogs()));
				files.add(tempFile);
				titles.add("Catalogs");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping catalogs";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(new ObjectArrayDataSet(_session.getSchemaInfo().getSchemas()));
				files.add(tempFile);
				titles.add("Schemas");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping schemas";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(conn.getSQLMetaData().getTypesDataSet());
				files.add(tempFile);
				titles.add("Data Types");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping data types";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
			}
	
			
			try
			{
				File tempFile = File.createTempFile(PREFIX, SUFFIX);
				IDataSetViewer dest = new DataSetViewerTextFileDestination(tempFile);
				dest.show(new ObjectArrayDataSet(md.getTableTypes()));
				files.add(tempFile);
				titles.add("Table Types");
			}
			catch (Throwable th)
			{
				final String msg = "Error dumping table types";
				showErrorMessage(msg);
				showErrorMessage(th);
				s_log.error(msg, th);
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
				wtr.println("SQuirreL SQL Client Session Dump " +
								Calendar.getInstance().getTime());
				for (int i = 0, limit = files.size(); i < limit; ++i)
				{
					wtr.println();
					wtr.println();
					wtr.println(SEP);
					wtr.println(titles.get(i));
					wtr.println(SEP);
					File file = files.get(i);
					BufferedReader rdr = new BufferedReader(new FileReader(file));
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
			final String msg = "Error combining temp files into dump file";
			showErrorMessage(msg);
			showErrorMessage(ex);
			s_log.error(msg, ex);
		}
	}

	private void deleteTempFiles(List<File> files)
	{
		for (int i = 0, limit = files.size(); i < limit; ++i)
		{
			if (!(files.get(i)).delete())
			{
				s_log.error("Couldn't delete temporary DumpSession file");
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

	private File createGeneralConnectionDumpFile(ISQLConnection conn)
		throws IOException
	{
		Connection myConn = conn.getConnection();
	
		File tempFile = File.createTempFile(PREFIX, SUFFIX);
		PrintWriter wtr = new PrintWriter(new FileWriter(tempFile));
		try
		{
			
			String line = null;
			try
			{
				line = String.valueOf(myConn.getTransactionIsolation());
			}
			catch (Throwable th)
			{
				line = th.toString();
			}
			wtr.println("transIsolation: " + line);
			try
			{
				line = String.valueOf(myConn.isReadOnly());
			}
			catch (Throwable th)
			{
				line = th.toString();
			}
			wtr.println("readonly: " + line);

			return tempFile;
		}
		finally
		{
			wtr.close();
		}
	}
    
    private void showErrorMessage(String msg) {
        if (_session != null) {
            _session.showErrorMessage(msg);
        } else if (_msgHandler != null) {
            _msgHandler.showErrorMessage(msg);
        } else {
            s_log.error("No IMessageHandler or ISession configured");
        }        
    }
    
    private void showErrorMessage(Throwable th) {
        if (_session != null) {
            _session.showErrorMessage(th);
        } else if (_msgHandler != null) {
            _msgHandler.showErrorMessage(th, null);
        } else {
            s_log.error("No IMessageHandler or ISession configured");
        }
    }
}
