package net.sourceforge.squirrel_sql.fw.util;

import java.util.EventListener;

public interface IObjectCacheChangeListener extends EventListener
{
	
	void objectAdded(ObjectCacheChangeEvent evt);

	
	void objectRemoved(ObjectCacheChangeEvent evt);
}
