package net.sourceforge.squirrel_sql.client;

import net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin;

public class DummyAppPlugin extends DefaultPlugin
{
	
	public String getInternalName()
	{
		return "app";
	}

	
	public String getDescriptiveName()
	{
		return "Dummy Application Plugin";
	}

	
	public String getVersion()
	{
		return "0.1";
	}

	
	public String getAuthor()
	{
		return "Colin Bell";
	}
}
