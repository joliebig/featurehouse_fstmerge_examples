package net.sourceforge.squirrel_sql.fw.util;

import java.util.HashMap;
import java.util.Map;

public class StringManagerFactory
{
	
	private static final Map<String, StringManager> s_mgrs = 
	    new HashMap<String, StringManager>();

	
	public static synchronized StringManager getStringManager(Class<?> clazz)
	{
		if (clazz == null)
		{
			throw new IllegalArgumentException("clazz == null");
		}

		final String key = getKey(clazz);
		StringManager mgr = s_mgrs.get(key);
		if (mgr == null)
		{
			mgr = new StringManager(key, clazz.getClassLoader());
			s_mgrs.put(key, mgr);
		}
		return mgr;
	}

	
	private static String getKey(Class<?> clazz)
	{
		if (clazz == null)
		{
			throw new IllegalArgumentException("clazz == null");
		}

		final String clazzName = clazz.getName();
		return clazzName.substring(0, clazzName.lastIndexOf('.'));
	}
}
