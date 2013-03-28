package net.sourceforge.squirrel_sql.client.plugin;

public class PluginLoadInfo
{
	private IPlugin _plugin;
	private long _creationStart;
	private long _creationEnd;
	private long _startLoading;
	private long _endLoading;
	private long _startInitializing;
	private long _endInitializing;

	public PluginLoadInfo()
	{
		super();
		_creationStart = System.currentTimeMillis();
	}

	public String getInternalName()
	{
		return _plugin.getInternalName();
	}

	public long getCreationTime()
	{
		return _creationEnd - _creationStart;
	}

	public long getLoadTime()
	{
		return _endLoading - _startLoading;
	}

	public long getInitializeTime()
	{
		return _endInitializing - _startInitializing;
	}

	void pluginCreated(IPlugin plugin)
	{
		_creationEnd = System.currentTimeMillis();
		_plugin = plugin;
	}

	void startLoading()
	{
		_startLoading = System.currentTimeMillis();
	}

	void endLoading()
	{
		_endLoading = System.currentTimeMillis();
	}

	void startInitializing()
	{
		_startInitializing = System.currentTimeMillis();
	}

	void endInitializing()
	{
		_endInitializing = System.currentTimeMillis();
	}
}

