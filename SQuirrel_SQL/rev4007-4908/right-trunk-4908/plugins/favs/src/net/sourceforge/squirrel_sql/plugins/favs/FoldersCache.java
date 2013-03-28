package net.sourceforge.squirrel_sql.plugins.favs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

public final class FoldersCache
{
	
	private static final ILogger s_log = LoggerController.createLogger(FoldersCache.class);

	
    @SuppressWarnings("unused")
	private IApplication _app;

	
	private Folder _rootFolder = null;

	private String _queriesFileName;

	public FoldersCache(IApplication app, File userSettingsFolder)
	{
		super();
		_app = app;
		_queriesFileName = userSettingsFolder.getAbsolutePath() + File.separator + "queries.xml";
	}

	public Folder getRootFolder()
	{
		return _rootFolder;
	}

	public void setRootFolder(Folder folder)
	{
		_rootFolder = folder;
	}
	void load()
{
		try
		{
			if (new File(_queriesFileName).exists())
			{
				XMLObjectCache<Folder> cache = new XMLObjectCache<Folder>();
				cache.load(_queriesFileName, getClass().getClassLoader());
				Iterator<Folder> it = cache.getAllForClass(Folder.class);
				if (it.hasNext())
				{
					_rootFolder = it.next();
				}
			}
		}
		catch (FileNotFoundException ignore)
		{
			
		}
		catch (XMLException ex)
		{
			s_log.error("Error loading queries file: " + _queriesFileName, ex);
		}
		catch (DuplicateObjectException ex)
		{
			s_log.error("Error loading queries file: " + _queriesFileName, ex);
		}
	}
	
	void save()
	{
		try
		{
			XMLObjectCache<Folder> cache = new XMLObjectCache<Folder>();
			try
			{
				if (_rootFolder != null)
				{
					cache.add(_rootFolder);
				}
			}
			catch (DuplicateObjectException ignore)
			{
			}
			cache.save(_queriesFileName);
		}
		catch (IOException ex)
		{
			s_log.error("Error occured saving queries to " + _queriesFileName, ex);
		}
		catch (XMLException ex)
		{
			s_log.error("Error occured saving queries to " + _queriesFileName, ex);
		}
	}
}
