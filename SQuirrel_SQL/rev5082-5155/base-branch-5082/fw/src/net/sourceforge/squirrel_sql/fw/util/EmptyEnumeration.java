package net.sourceforge.squirrel_sql.fw.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class EmptyEnumeration<E> implements Enumeration<E>
{
	
	public boolean hasMoreElements()
	{
		return false;
	}

	
	public E nextElement()
	{
		throw new NoSuchElementException();
	}
}
