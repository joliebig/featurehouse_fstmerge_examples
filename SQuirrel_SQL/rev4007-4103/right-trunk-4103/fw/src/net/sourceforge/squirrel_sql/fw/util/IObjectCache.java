package net.sourceforge.squirrel_sql.fw.util;

import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

public interface IObjectCache<E extends IHasIdentifier>
{
	
	IHasIdentifier get(Class<E> objClass, IIdentifier id);

	
	void add(E obj) throws DuplicateObjectException;

	
	void remove(Class<E> objClass, IIdentifier id);

	
	Class<E>[] getAllClasses();

	
	Iterator<E> getAllForClass(Class<E> objClass);

	
	void addChangesListener(IObjectCacheChangeListener lis, Class<E> objClass);

	
	void removeChangesListener(IObjectCacheChangeListener lis, Class<E> objClass);
}
