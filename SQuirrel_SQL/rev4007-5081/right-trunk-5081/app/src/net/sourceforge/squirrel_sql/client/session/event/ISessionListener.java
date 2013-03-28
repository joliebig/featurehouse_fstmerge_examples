package net.sourceforge.squirrel_sql.client.session.event;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import java.util.EventListener;

public interface ISessionListener extends EventListener
{
	
	void sessionClosing(SessionEvent evt);

	
	void sessionClosed(SessionEvent evt);

	
	public void allSessionsClosed();

	
	public void sessionConnected(SessionEvent evt);

	
	public void sessionActivated(SessionEvent evt);

	void connectionClosedForReconnect(SessionEvent evt);

	void reconnected(SessionEvent evt);

	void reconnectFailed(SessionEvent evt);

	void sessionFinalized(IIdentifier sessionIdentifier);
}
