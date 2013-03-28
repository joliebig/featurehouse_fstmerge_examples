package net.sourceforge.squirrel_sql.fw.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.IObjectCache;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCache;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class XMLObjectCache<E extends IHasIdentifier> implements IObjectCache<E>
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(XMLObjectCache.class);

	
	private ObjectCache<E> _cache = new ObjectCache<E>();

	
	public XMLObjectCache()
	{
		super();
	}

	
	public IHasIdentifier get(Class<E> objClass, IIdentifier id)
	{
		return _cache.get(objClass, id);
	}

	
	public void add(E obj) throws DuplicateObjectException
	{
		_cache.add(obj);
	}

	
	public void remove(Class<E> objClass, IIdentifier id)
	{
		_cache.remove(objClass, id);
	}

	
	public Class<E>[] getAllClasses()
	{
		return _cache.getAllClasses();
	}

	
	public Iterator<E> getAllForClass(Class<E> objClass)
	{
		return _cache.getAllForClass(objClass);
	}

	
	public void addChangesListener(IObjectCacheChangeListener lis, Class<E> objClass)
	{
		_cache.addChangesListener(lis, objClass);
	}

	
	public void removeChangesListener(IObjectCacheChangeListener lis,
										Class<E> objClass)
	{
		_cache.removeChangesListener(lis, objClass);
	}

	
	public void load(String xmlFileName)
		throws FileNotFoundException, XMLException, DuplicateObjectException
	{
		load(xmlFileName, null);
	}

	
	public void load(String xmlFileName, ClassLoader cl)
		throws FileNotFoundException, XMLException, DuplicateObjectException
	{
		XMLBeanReader rdr = new XMLBeanReader();
		rdr.load(xmlFileName, cl);
		for (Iterator<Object> it = rdr.iterator(); it.hasNext();)
		{
			final Object obj = it.next();
			if (!(obj instanceof IHasIdentifier))
			{
				throw new XMLException(s_stringMgr.getString("XMLObjectCache.error.notimplemented"));
			}
			add((E)obj);
		}
	}

	
	public void load(Reader rdr) throws XMLException, DuplicateObjectException
	{
		load(rdr, null, false);
	}

	
	public void load(Reader rdr, ClassLoader cl, boolean ignoreDuplicates)
		throws XMLException, DuplicateObjectException
	{
		XMLBeanReader xmlRdr = new XMLBeanReader();
		xmlRdr.load(rdr, cl);
		for (Iterator<?> it = xmlRdr.iterator(); it.hasNext();)
		{
			final Object obj = it.next();
			if (!(obj instanceof IHasIdentifier))
			{
				throw new XMLException(s_stringMgr.getString("XMLObjectCache.error.notimplemented"));
			}
			try
			{
				add((E) obj);
			}
			catch (DuplicateObjectException ex)
			{
				if (!ignoreDuplicates)
				{
					throw ex;
				}
			}
		}
	}

	
	public synchronized void save(String xmlFilename)
		throws IOException, XMLException
	{
		XMLBeanWriter wtr = new XMLBeanWriter();
		Class<E>[] classes = _cache.getAllClasses();
		for (int i = 0; i < classes.length; ++i)
		{
			for (Iterator<E> it = _cache.getAllForClass(classes[i]);
					it.hasNext();)
			{
				wtr.addToRoot(it.next());
			}
		}
		wtr.save(xmlFilename);
	}

	
	public synchronized void saveAllForClass(String xmlFilename,
	                                         Class<E> forClass)
		throws IOException, XMLException
	{
		XMLBeanWriter wtr = new XMLBeanWriter();
		for (Iterator<E> it = _cache.getAllForClass(forClass); it.hasNext();)
		{
			wtr.addToRoot(it.next());
		}
		wtr.save(xmlFilename);
	}
}
