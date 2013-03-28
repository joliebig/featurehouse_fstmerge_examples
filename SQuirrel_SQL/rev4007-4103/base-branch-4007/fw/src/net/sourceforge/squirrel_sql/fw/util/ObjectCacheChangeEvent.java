package net.sourceforge.squirrel_sql.fw.util;

import java.util.EventObject;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;

public class ObjectCacheChangeEvent<E extends IHasIdentifier> extends EventObject
{
	
	private ObjectCache<E> _cache;

	
	private IHasIdentifier _obj;

	
	ObjectCacheChangeEvent(ObjectCache<E> source, IHasIdentifier obj)
	{
		super(source);
		_cache = source;
		_obj = obj;
	}

	
	public IHasIdentifier getObject()
	{
		return _obj;
	}

	
	public ObjectCache<E> getObjectCache()
	{
		return _cache;
	}
}