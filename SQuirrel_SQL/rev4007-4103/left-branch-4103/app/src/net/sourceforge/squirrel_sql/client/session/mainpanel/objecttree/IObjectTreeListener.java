package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import java.util.EventListener;

public interface IObjectTreeListener extends EventListener
{
	
	void objectTreeCleared(ObjectTreeListenerEvent evt);

	
	void objectTreeRefreshed(ObjectTreeListenerEvent evt);
}
