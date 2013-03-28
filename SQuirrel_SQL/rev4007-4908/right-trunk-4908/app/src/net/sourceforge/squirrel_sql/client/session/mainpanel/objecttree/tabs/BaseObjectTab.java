package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;


public abstract class BaseObjectTab implements IObjectTab
{
	
	private static final ILogger s_log =
		LoggerController.createLogger(BaseObjectTab.class);

	
	protected IApplication _app;

	


	
	private IIdentifier _sessionId;

	
	private IDatabaseObjectInfo _dbObjInfo;

	
	private boolean _hasBeenDisplayed;

	
	public void setSession(ISession session) throws IllegalArgumentException
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_app = session.getApplication();
		_sessionId = session.getIdentifier();
	}

	
	public final ISession getSession()
	{
		return _app.getSessionManager().getSession(_sessionId);
	}

	
	public DialectType getDialectType() {
	   return DialectFactory.getDialectType(getSession().getMetaData());
	}
	
	
	public synchronized void select()
	{
		if (!_hasBeenDisplayed)
		{
			s_log.debug("Refreshing " + getTitle() + " table tab.");
			try
			{
				clear();
				refreshComponent();
			}
			catch (Throwable th)
			{
				th.printStackTrace();
				if (s_log.isDebugEnabled()) {
					s_log.debug("Unexpected exception: "+th.getMessage(), th);
				}
				getSession().showErrorMessage(th);
			}
			_hasBeenDisplayed = true;
		}
	}

	
	public void rebuild()
	{
		_hasBeenDisplayed = false;
	}

	
	protected abstract void refreshComponent() throws DataSetException;

	
	public void setDatabaseObjectInfo(IDatabaseObjectInfo value)
	{
		_dbObjInfo = value;
		_hasBeenDisplayed = false;
	}

	
	protected final IDatabaseObjectInfo getDatabaseObjectInfo()
	{
		return _dbObjInfo;
	}
}
