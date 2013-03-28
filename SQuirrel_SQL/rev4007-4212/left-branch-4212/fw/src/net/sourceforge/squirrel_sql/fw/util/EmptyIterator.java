package net.sourceforge.squirrel_sql.fw.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyIterator<E> implements Iterator<E>
{
	
	public boolean hasNext()
	{
		return false;
	}

	
	public E next()
	{
		throw new NoSuchElementException();
	}

	
	public void remove()
	{
		throw new IllegalStateException();
	}
}
