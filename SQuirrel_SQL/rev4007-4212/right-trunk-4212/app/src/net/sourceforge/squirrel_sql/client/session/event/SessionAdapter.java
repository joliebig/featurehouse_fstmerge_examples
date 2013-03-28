package net.sourceforge.squirrel_sql.client.session.event;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;


public class SessionAdapter implements ISessionListener
{
	
	public void sessionClosed(SessionEvent evt)
	{
		
	}

	public void allSessionsClosed()
	{
		
	}

	
	public void sessionClosing(SessionEvent evt)
	{
		
	}

	public void sessionConnected(SessionEvent evt)
	{
		
	}

	public void sessionActivated(SessionEvent evt)
	{
		
	}

	public void connectionClosedForReconnect(SessionEvent evt)
	{
	}

	public void reconnected(SessionEvent evt)
	{
	}

	public void reconnectFailed(SessionEvent evt)
	{
	}

	public void sessionFinalized(IIdentifier sessionIdentifier)
	{
	}
}
