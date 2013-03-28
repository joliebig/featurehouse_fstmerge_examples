package net.sourceforge.squirrel_sql.client.session.event;

import java.util.EventListener;


public interface IResultTabListener extends EventListener
{
	
	void resultTabAdded(ResultTabEvent evt);

	
	void resultTabRemoved(ResultTabEvent evt);

	
	void resultTabTornOff(ResultTabEvent evt);

	
	void tornOffResultTabReturned(ResultTabEvent evt);
}
