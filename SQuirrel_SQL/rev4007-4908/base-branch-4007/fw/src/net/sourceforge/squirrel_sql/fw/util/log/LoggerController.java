package net.sourceforge.squirrel_sql.fw.util.log;

import java.util.Vector;

public class LoggerController
{
	private static Vector<ILoggerFactory> s_oldfactories = new Vector<ILoggerFactory>();
	private static ILoggerFactory s_factory = new Log4jLoggerFactory();

	public static void registerLoggerFactory(ILoggerFactory factory)
	{
		s_oldfactories.add(s_factory);
		s_factory = factory != null ? factory : new Log4jLoggerFactory();
	}

	public static ILogger createLogger(Class<?> clazz)
	{
		return s_factory.createLogger(clazz);
	}

	public static void shutdown()
	{
		s_factory.shutdown();
	}

	public static void addLoggerListener(ILoggerListener l)
	{
		s_factory.addLoggerListener(l);

		for (int i = 0; i < s_oldfactories.size(); i++)
		{
			ILoggerFactory iLoggerFactory = s_oldfactories.get(i);
			iLoggerFactory.addLoggerListener(l);
		}
	}

	public static void removeLoggerListener(ILoggerListener l)
	{
		s_factory.removeLoggerListener(l);

		for (int i = 0; i < s_oldfactories.size(); i++)
		{
			ILoggerFactory iLoggerFactory = s_oldfactories.get(i);
			iLoggerFactory.removeLoggerListener(l);
		}

	}

}