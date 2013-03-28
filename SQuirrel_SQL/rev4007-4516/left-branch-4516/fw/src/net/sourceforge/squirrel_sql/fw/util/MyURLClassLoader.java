package net.sourceforge.squirrel_sql.fw.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;

@SuppressWarnings("unchecked")
public class MyURLClassLoader extends URLClassLoader
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MyURLClassLoader.class);

	private Map<String, Class> _classes = new HashMap<String, Class>();

    ArrayList<ClassLoaderListener> listeners = 
        new ArrayList<ClassLoaderListener>();
    
	public MyURLClassLoader(String fileName) throws IOException
	{
		this(new File(fileName).toURL());
	}

	public MyURLClassLoader(URL url)
	{
		this(new URL[] { url });
	}

	public MyURLClassLoader(URL[] urls)
	{
		super(urls, ClassLoader.getSystemClassLoader());
	}

    public void addClassLoaderListener(ClassLoaderListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    
    private void notifyListenersLoadedZipFile(String filename) {
        Iterator<ClassLoaderListener> i = listeners.iterator();
        while (i.hasNext()) {
            ClassLoaderListener listener = i.next();
            listener.loadedZipFile(filename);
        }
    }
    
    
    private void notifyListenersFinished() {
        Iterator<ClassLoaderListener> i = listeners.iterator();
        while (i.hasNext()) {
            ClassLoaderListener listener = i.next();
            listener.finishedLoadingZipFiles();
        }        
    }
    
    public void removeClassLoaderListener(ClassLoaderListener listener) {
        listeners.remove(listener);
    }
    
	public Class[] getAssignableClasses(Class type, ILogger logger)
	{
		List<Class> classes = new ArrayList<Class>();
		URL[] urls = getURLs();
		for (int i = 0; i < urls.length; ++i)
		{
			URL url = urls[i];
			File file = new File(url.getFile());
			if (!file.isDirectory() && file.exists() && file.canRead())
			{
				ZipFile zipFile = null;
				try
				{
					zipFile = new ZipFile(file);
				}
				catch (IOException ex)
				{
					Object[] args = {file.getAbsolutePath(),};
					String msg = s_stringMgr.getString(
									"MyURLClassLoader.errorLoadingFile", args);
					logger.error(msg, ex);
                    continue;
				}
                notifyListenersLoadedZipFile(file.getName());
                
				for (Iterator it = new EnumerationIterator(zipFile.entries());
						it.hasNext();)
				{
					Class cls = null;
					String entryName = ((ZipEntry) it.next()).getName();
					String className =
						Utilities.changeFileNameToClassName(entryName);
					if (className != null)
					{
						try
						{
							cls = Class.forName(className, false, this);
						}
						catch (Throwable th)
						{





							
							
							Object[] args = new Object[]{className, file.getAbsolutePath(), type.getName(), th.toString()};
							
							String msg = s_stringMgr.getString("MyURLClassLoader.noAssignCheck", args);
							logger.info(msg);
						}
						if (cls != null)
						{
							if (type.isAssignableFrom(cls))
							{
								classes.add(cls);
							}
						}
					}
				}
			}
		}
        notifyListenersFinished();
		return classes.toArray(new Class[classes.size()]);
	}

	protected synchronized Class findClass(String className)
		throws ClassNotFoundException
	{
		Class cls = _classes.get(className);
		if (cls == null)
		{
			cls = super.findClass(className);
			_classes.put(className, cls);
		}
		return cls;
	}

    @SuppressWarnings("unused")
	protected void classHasBeenLoaded(Class cls)
	{
		
	}
}
