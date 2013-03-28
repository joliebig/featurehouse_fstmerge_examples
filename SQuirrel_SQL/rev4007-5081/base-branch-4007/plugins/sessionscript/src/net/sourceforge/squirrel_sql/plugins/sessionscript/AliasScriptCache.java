package net.sourceforge.squirrel_sql.plugins.sessionscript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

public class AliasScriptCache
{
	
	private static ILogger s_log =
		LoggerController.createLogger(AliasScriptCache.class);

	
	private SessionScriptPlugin _plugin;

	
	private XMLObjectCache<AliasScript> _cache = new XMLObjectCache<AliasScript>();

	
	private String _scriptsFileName;

	
	public AliasScriptCache(SessionScriptPlugin plugin)
		throws IOException
	{
		super();
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null SessionScriptPlugin passed");
		}

		_plugin = plugin;

		_scriptsFileName = _plugin.getPluginUserSettingsFolder().getAbsolutePath()
							+ File.separator + "session-scripts.xml";
	}

	
	public synchronized AliasScript get(ISQLAlias alias)
	{
		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLALias == null");
		}

		AliasScript script = (AliasScript)_cache.get(AliasScript.class, alias.getIdentifier());
		if (script == null)
		{
			script = new AliasScript(alias);
			try
			{
				_cache.add(script);
			}
			catch (DuplicateObjectException ex)
			{
				
				throw new InternalError(ex.getMessage());
			}
		}
		return script;
	}

	
	public synchronized void load()
	{
		try
		{
			_cache.load(_scriptsFileName, getClass().getClassLoader());
		}
		catch (FileNotFoundException ignore)
		{ 
		}
		catch (XMLException ex)
		{
			String msg = "Error loading scripts file: " + _scriptsFileName;
			s_log.error(msg, ex);
			_plugin.getApplication().showErrorDialog(msg, ex);
		}
		catch (DuplicateObjectException ex)
		{
			String msg = "Error loading scripts file: " + _scriptsFileName;
			s_log.error(msg, ex);
			_plugin.getApplication().showErrorDialog(msg, ex);
		}
	}

	
	public synchronized void save()
	{
		try
		{
			_cache.save(_scriptsFileName);
		}
		catch (IOException ex)
		{
			String msg = "Error occured saving scripts to " + _scriptsFileName;
			s_log.error(msg, ex);
			_plugin.getApplication().showErrorDialog(msg, ex);
		}
		catch (XMLException ex)
		{
			String msg = "Error occured saving scripts to " + _scriptsFileName;
			s_log.error(msg, ex);
			_plugin.getApplication().showErrorDialog(msg, ex);
		}
	}
}
