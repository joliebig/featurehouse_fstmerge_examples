package net.sourceforge.squirrel_sql.fw.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.net.URLClassLoader;
import java.net.URL;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class StringManager
{
	
	private static ILogger s_log = LoggerController.createLogger(StringManager.class);

	
	private ResourceBundle _rsrcBundle;
	private String _bundleBaseName;
	private URL[] _bundleLoaderUrLs = new URL[0];

	
	StringManager(String packageName, ClassLoader loader)
	{
		super();
		_bundleBaseName = packageName + ".I18NStrings";
		_rsrcBundle = ResourceBundle.getBundle(_bundleBaseName, Locale.getDefault(), loader);

		if(loader instanceof URLClassLoader)
		{
			_bundleLoaderUrLs = ((URLClassLoader) loader).getURLs();
		}


	}

	
	public String getString(String key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("key == null");
		}

		try
		{
			return _rsrcBundle.getString(key);
		}
		catch (MissingResourceException ex)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("No resource string found for key '" + key + "' in bundle " + _bundleBaseName + "\n\n");

			if(0 < _bundleLoaderUrLs.length)
			{
				sb.append("The following classpath entries are available to the bundle loader:\n");
				for (int i = 0; i < _bundleLoaderUrLs.length; i++)
				{
					sb.append(_bundleLoaderUrLs[i]).append("\n");
				}
			}
			s_log.error(sb.toString());
			return "No resource found for key " + key;
		}
	}

        
    public String getString(String key, String[] args) 
    {
        return getString(key, (Object[])args);
    }
    
	
	public String getString(String key, Object... args)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("key == null");
		}

		if (args == null)
		{
			args = new Object[0];
		}

		final String str = getString(key);
		try
		{
			return MessageFormat.format(str, args);
		}
		catch (IllegalArgumentException ex)
		{
			String msg = "Error formatting i18 string. Key is '" + key + "'";
			s_log.error(msg, ex);
			return msg + ": " + ex.toString();
		}
	}
}