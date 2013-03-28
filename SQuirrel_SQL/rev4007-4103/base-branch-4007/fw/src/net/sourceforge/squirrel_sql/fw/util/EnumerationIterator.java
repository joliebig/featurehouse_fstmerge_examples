package net.sourceforge.squirrel_sql.fw.util;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterator<E> implements Iterator<E>
{
	
	private Enumeration<E> _en;

	
	public EnumerationIterator(Enumeration<E> en)
	{
		super();
		_en = en != null ? en : new EmptyEnumeration<E>();
	}

	
	public boolean hasNext()
	{
		return _en.hasMoreElements();
	}

	
	public E next()
	{
		return _en.nextElement();
	}

	
	public void remove()
	{
		throw new UnsupportedOperationException("remove()");
	}
}
