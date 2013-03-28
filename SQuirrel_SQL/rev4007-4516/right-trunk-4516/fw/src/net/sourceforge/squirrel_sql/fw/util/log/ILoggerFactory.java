package net.sourceforge.squirrel_sql.fw.util.log;

public interface ILoggerFactory
{
	ILogger createLogger(Class<?> clazz);
	void shutdown();

	void addLoggerListener(ILoggerListener l);
	void removeLoggerListener(ILoggerListener l);
}
