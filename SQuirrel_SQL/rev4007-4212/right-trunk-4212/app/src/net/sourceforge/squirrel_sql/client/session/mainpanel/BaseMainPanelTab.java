package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

public abstract class BaseMainPanelTab implements IMainPanelTab
{
	
	private static ILogger s_log =
		LoggerController.createLogger(BaseMainPanelTab.class);

	
	private ISession _session;

	
	private boolean _hasBeenDisplayed;

	
	public void setSession(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_session = session;
	}

	
	public void sessionClosing(ISession session)
	{
		
	}

	
	public final ISession getSession()
	{
		return _session;
	}

	
	public synchronized void select()
	{
		if (!_hasBeenDisplayed)
		{
			s_log.debug("Refreshing " + getTitle() + " main tab.");
			refreshComponent();
			_hasBeenDisplayed = true;
		}
	}

	
	protected abstract void refreshComponent();
    
    
    public void sessionEnding(ISession session) {
        if (_session == session) {
            _session = null;
        }
    }
}
