package net.sourceforge.squirrel_sql.fw.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

public class ObjectCache<E extends IHasIdentifier> implements IObjectCache<E>
{
	
	private Map<Class<E>, CacheEntry<E>> _entries = new HashMap<Class<E>, CacheEntry<E>>();

	
	public ObjectCache()
	{
		super();
	}

	
	public synchronized IHasIdentifier get(Class<E> objClass, IIdentifier id)
	{
		return getCacheEntry(objClass).get(id);
	}

	
	@SuppressWarnings("unchecked")
	public synchronized void add(E obj) throws DuplicateObjectException
	{
		getCacheEntry((Class<E>) obj.getClass()).add(obj);
	}

	
	public synchronized void remove(Class<E> objClass, IIdentifier id)
	{
		getCacheEntry(objClass).remove(id);
	}

	
	public void addChangesListener(IObjectCacheChangeListener lis, Class<E> objClass)
	{
		getCacheEntry(objClass).addChangesListener(lis);
	}

	
	public void removeChangesListener(IObjectCacheChangeListener lis, Class<E> objClass)
	{
		getCacheEntry(objClass).removeChangesListener(lis);
	}

	
	@SuppressWarnings("unchecked")
	public synchronized Class<E>[] getAllClasses()
	{
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (Iterator<Class<E>> it = _entries.keySet().iterator(); it.hasNext();)
		{
			classes.add(it.next());
			
		}
		if (classes.size() > 0)
		{
			return classes.toArray(new Class[classes.size()]);
		}
		return new Class[0];
	}

	
	public synchronized Iterator<E> getAllForClass(Class<E> objClass)
	{
		return getCacheEntry(objClass).values().iterator();
	}

	
	private CacheEntry<E> getCacheEntry(Class<E> objClass)
	{
		CacheEntry<E> entry = _entries.get(objClass);
		if (entry == null)
		{
			entry = new CacheEntry(objClass);
			_entries.put(objClass, entry);
		}
		return entry;
	}

	
	private final class CacheEntry<T extends E>
	{
		
		private Class<? extends T> _objClass;

		
		private Map<IIdentifier, T> _coll = new HashMap<IIdentifier, T>();

		
		private EventListenerList _listenerList = new EventListenerList();

		
		CacheEntry(Class<? extends T> objClass)
		{
			super();
			_objClass = objClass;
		}

		
		IHasIdentifier get(IIdentifier id)
		{
			return _coll.get(id);
		}

		
		void add(T obj)
				throws DuplicateObjectException, IllegalArgumentException{
			if (get(obj.getIdentifier()) != null)
			{
				throw new DuplicateObjectException(obj);
			}
			if (!_objClass.isInstance(obj))
			{
				throw new IllegalArgumentException("IHasIdentifier is not an instance of " + _objClass.getName()); 
			}
			_coll.put(obj.getIdentifier(), obj);
			fireObjectAdded(obj);
		}

		
		void remove(IIdentifier id)
		{
			IHasIdentifier obj = get(id);
			if (obj != null)
			{
				_coll.remove(id);
				fireObjectRemoved(obj);
			}
		}

		
		Collection<T> values()
		{
			return _coll.values();
		}

		
		void addChangesListener(IObjectCacheChangeListener lis)
		{
			_listenerList.add(IObjectCacheChangeListener.class, lis);
		}

		
		void removeChangesListener(IObjectCacheChangeListener lis)
		{
			_listenerList.remove(IObjectCacheChangeListener.class, lis);
		}

		
		private void fireObjectAdded(IHasIdentifier obj)
		{
			
			Object[] listeners = _listenerList.getListenerList();
			
			
			ObjectCacheChangeEvent evt = null;
			for (int i = listeners.length - 2; i >= 0; i-=2 )
			{
				if (listeners[i] == IObjectCacheChangeListener.class)
				{
					
					if (evt == null)
					{
						evt = new ObjectCacheChangeEvent(ObjectCache.this, obj);
					}
					((IObjectCacheChangeListener)listeners[i + 1]).objectAdded(evt);
				}
			}
		}

		
		private void fireObjectRemoved(IHasIdentifier obj)
		{
			
			Object[] listeners = _listenerList.getListenerList();
			
			
			ObjectCacheChangeEvent evt = null;
			for (int i = listeners.length - 2; i >= 0; i-=2 )
			{
				if (listeners[i] == IObjectCacheChangeListener.class)
				{
					
					if (evt == null)
					{
						evt = new ObjectCacheChangeEvent(ObjectCache.this, obj);
					}
					((IObjectCacheChangeListener)listeners[i + 1]).objectRemoved(evt);
				}
			}
		}
	}
}
