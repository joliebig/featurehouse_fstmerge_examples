package net.sourceforge.squirrel_sql.fw.util.log;


import net.sourceforge.squirrel_sql.fw.util.Utilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class Log4jLogger implements ILogger
{
	
	private Logger _log;

	private ILoggerListener _listener;

	private Class<?> _clazz;

	
	Log4jLogger(Class<?> clazz, ILoggerListener listener)
	{
		Utilities.checkNull("Log4jLogger.init","clazz", clazz, "listener", listener);
		_listener = listener;
		_clazz = clazz;
		_log = Logger.getLogger(clazz);
	}

	
	public void debug(Object message)
	{
		_log.debug(message);
	}

	
	public void debug(Object message, Throwable th)
	{
		_log.debug(message, th);
	}

	
	public void info(Object message)
	{
		_log.info(message);
		_listener.info(_clazz, message);
	}

	
	public void info(Object message, Throwable th)
	{
		_log.info(message, th);
		_listener.info(_clazz, message, th);
	}

	
	public void warn(Object message)
	{
		_log.warn(message);
		_listener.warn(_clazz, message);
	}

	
	public void warn(Object message, Throwable th)
	{
		_log.warn(message, th);
		_listener.warn(_clazz, message, th);
	}

	
	public void error(Object message)
	{
		_log.error(message);
		_listener.error(_clazz, message);
	}

	
	public void error(Object message, Throwable th)
	{
		_log.error(message, th);
		_listener.error(_clazz, message, th);
	}

	
	public boolean isDebugEnabled()
	{
		return _log.isDebugEnabled();
	}

	
	public boolean isInfoEnabled()
	{
		return _log.isInfoEnabled();
	}

	
	public void setLevel(Level l)
	{
		_log.setLevel(l);
	}
}
