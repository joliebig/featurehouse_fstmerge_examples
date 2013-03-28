package net.sourceforge.squirrel_sql.client.plugin;

public class PluginStatus
{
	 
	private String _internalName;

	
	private boolean _loadAtStartup = true;

	public PluginStatus()
	{
		super();
	}

	public PluginStatus(String internalName)
	{
		super();
		_internalName = internalName;
	}

	
	public String getInternalName()
	{
		return _internalName;
	}

	public void setInternalName(String value)
	{
		_internalName = value;
	}

	public boolean isLoadAtStartup()
	{
		return _loadAtStartup;
	}

	public void setLoadAtStartup(boolean loadAtStartup)
	{
		_loadAtStartup = loadAtStartup;
	}
}
