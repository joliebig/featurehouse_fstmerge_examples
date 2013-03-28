package net.sourceforge.squirrel_sql.fw.util;

import java.util.Properties;

public class SystemProperties
{
	private SystemProperties()
	{
		super();
	}

	public static Properties get()
	{
		return System.getProperties();
	}

	public static String getClassPath()
	{
		return get().getProperty("java.class.path");
	}

	public static boolean isRunningOnOSX()
	{
		return true;

	}
}
