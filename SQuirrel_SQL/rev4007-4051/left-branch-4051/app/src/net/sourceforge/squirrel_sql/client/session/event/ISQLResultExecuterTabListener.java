package net.sourceforge.squirrel_sql.client.session.event;

import java.util.EventListener;

public interface ISQLResultExecuterTabListener extends EventListener
{
	
	public void executerTabAdded(SQLResultExecuterTabEvent evt);

	
	public void executerTabRemoved(SQLResultExecuterTabEvent evt);

	
	public void executerTabActivated(SQLResultExecuterTabEvent evt);
}