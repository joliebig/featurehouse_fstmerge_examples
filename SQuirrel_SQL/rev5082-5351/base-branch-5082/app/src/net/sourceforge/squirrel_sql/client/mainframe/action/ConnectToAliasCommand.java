package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.sql.SQLException;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectToAliasCallBack;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.ICompletionCallback;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ConnectToAliasCommand implements ICommand
{

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ConnectToAliasCommand.class);

	
	private static final ILogger s_log =
		LoggerController.createLogger(ConnectToAliasCommand.class);

	
	private IApplication _app;

	
	private SQLAlias _sqlAlias;

	
	private boolean _createSession;

	
	private ICompletionCallback _callback;

	
	public ConnectToAliasCommand(IApplication app, SQLAlias sqlAlias)
	{
		this(app, sqlAlias, true, null);
	}

	
	public ConnectToAliasCommand(IApplication app, SQLAlias sqlAlias,
						boolean createSession, ICompletionCallback callback)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (sqlAlias == null)
		{
			throw new IllegalArgumentException("Null ISQLAlias passed");
		}
		_app = app;
		_sqlAlias = sqlAlias;
		_createSession = createSession;
		_callback = callback != null ? callback : new ConnectToAliasCallBack(app, _sqlAlias);
	}

	
	public void execute()
	{
		try
		{
			final SheetHandler hdl = new SheetHandler(_app, _sqlAlias, _createSession, _callback);
			
            if (SwingUtilities.isEventDispatchThread()) {
                createConnectionInternalFrame(hdl);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        createConnectionInternalFrame(hdl);
                    }
                });
            }
		}
		catch (Exception ex)
		{
			_app.showErrorDialog(ex);
		}
	}

    private void createConnectionInternalFrame(SheetHandler hdl) {
        ConnectionInternalFrame sheet = 
            new ConnectionInternalFrame(_app, _sqlAlias, hdl);                        
        _app.getMainFrame().addWidget(sheet);
        DialogWidget.centerWithinDesktop(sheet);
        sheet.moveToFront();
        sheet.setVisible(true);                        
    }
    

	
	private static class SheetHandler implements ConnectionInternalFrame.IHandler, Runnable
	{
		
		private ConnectionInternalFrame _connSheet;

		
		private IApplication _app;

		
		private SQLAlias _alias;

		
		private boolean _createSession;

		
		private String _user;

		
		private String _password;

		
		private SQLDriverPropertyCollection _props;

		
		private boolean _stopConnection;

		
		private ICompletionCallback _callback;

		
		private SheetHandler(IApplication app, SQLAlias alias, boolean createSession,
									ICompletionCallback callback)
		{
			super();
			if (app == null)
			{
				throw new IllegalArgumentException("IApplication == null");
			}
			if (alias == null)
			{
				throw new IllegalArgumentException("ISQLAlias == null");
			}
			if (alias == null)
			{
				throw new IllegalArgumentException("ICompletionCallback == null");
			}
			_app = app;
			_alias = alias;
			_createSession = createSession;
			_callback = callback;
		}

		
		public void performOK(ConnectionInternalFrame connSheet, String user,
								String password, SQLDriverPropertyCollection props)
		{
			_stopConnection = false;
			_connSheet = connSheet;
			_user = user;
			_password = password;
			_props = props;
			_app.getThreadPool().addTask(this);
		}

		
		public void performCancelConnect(ConnectionInternalFrame connSheet)
		{
			
			
			
			synchronized (this)
			{
				_stopConnection = true;
			}
		}

		
		public void performClose(ConnectionInternalFrame connSheet)
		{
			
		}

		
		public void run()
		{
			SQLConnection conn = null;
			final IIdentifier driverID = _alias.getDriverIdentifier();
			final ISQLDriver sqlDriver = _app.getDataCache().getDriver(driverID);

			try
			{
				OpenConnectionCommand cmd = new OpenConnectionCommand(_app,
								_alias, _user, _password, _props);
				cmd.execute();

            if(_alias.isAutoLogon())
            {
               
               
               
               
               _alias.setUserName(_user);
               _alias.setPassword(_password);
            }


            conn = cmd.getSQLConnection();
				synchronized (this)
				{
					if (_stopConnection)
					{
						if (conn != null)
						{
							closeConnection(conn);
							conn = null;
						}
					}
					else
					{
						
						_callback.connected(conn);
						if (_createSession)
						{
							createSession(sqlDriver, conn);
						}
						else
						{
							_connSheet.executed(true);
						}
					}
				}
			}
			catch (Throwable ex)
			{
				_connSheet.executed(false);
				_callback.errorOccured(ex);
			}
		}

		private void closeConnection(ISQLConnection conn)
		{
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (SQLException ex)
				{
                    
					s_log.error(s_stringMgr.getString("ConnectToAliasCommand.error.closeconnection"), ex);
				}
			}
		}

		private ISession createSession(ISQLDriver sqlDriver,
												SQLConnection conn)
		{
			SessionManager sm = _app.getSessionManager();
			final ISession session = sm.createSession(_app, sqlDriver,
												_alias, conn, _user, _password);
			_callback.sessionCreated(session);
			SwingUtilities.invokeLater(new Runner(session, _connSheet));
			return session;
		}
	}

	private static final class Runner implements Runnable
	{
		private final ISession _session;
		private final ConnectionInternalFrame _connSheet;

		Runner(ISession session, ConnectionInternalFrame connSheet)
		{
			super();
			_session = session;
			_connSheet = connSheet;
		}

		public void run()
		{
			final IApplication app = _session.getApplication();
			try
			{
				app.getPluginManager().sessionCreated(_session);
				app.getWindowManager().createInternalFrame(_session);
                _connSheet.executed(true);
			}
			catch (Throwable th)
			{
				app.showErrorDialog(s_stringMgr.getString("ConnectToAliasCommand.error.opensession"), th);
			}
		}
	}
}
