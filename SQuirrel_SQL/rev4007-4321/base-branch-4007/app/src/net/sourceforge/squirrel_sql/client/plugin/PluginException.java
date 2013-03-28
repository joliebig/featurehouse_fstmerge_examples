package net.sourceforge.squirrel_sql.client.plugin;

import net.sourceforge.squirrel_sql.fw.util.BaseException;

public class PluginException extends BaseException
{
	public PluginException(String msg)
	{
		super(msg);
	}

	public PluginException(Exception wrapee)
	{
		super(wrapee);
	}
}
