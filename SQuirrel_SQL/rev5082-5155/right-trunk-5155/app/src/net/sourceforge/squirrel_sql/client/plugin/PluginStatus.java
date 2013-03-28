package net.sourceforge.squirrel_sql.client.plugin;

import java.io.Serializable;

public class PluginStatus implements Serializable
{
	private static final long serialVersionUID = -2149837347357638120L;

	
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
