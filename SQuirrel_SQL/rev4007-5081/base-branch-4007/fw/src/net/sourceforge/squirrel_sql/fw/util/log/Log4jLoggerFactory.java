package net.sourceforge.squirrel_sql.fw.util.log;

import org.apache.log4j.BasicConfigurator;

import java.util.Vector;

public class Log4jLoggerFactory implements ILoggerFactory
{
	private ILoggerListener _listenerOfAllLoggers;
	private Vector<ILoggerListener> _listeners =new Vector<ILoggerListener>();

	public Log4jLoggerFactory()
	{
		this(true);
	}

	public void addLoggerListener(ILoggerListener l)
	{
		_listeners.add(l);
	}

	public void removeLoggerListener(ILoggerListener l)
	{
		_listeners.remove(l);
	}



	public Log4jLoggerFactory(boolean doBasicConfig)
	{
		_listenerOfAllLoggers = new ILoggerListener()
		{
			public void info(Class<?> source, Object message)
			{
				try
				{
					ILoggerListener[] listeners = _listeners.toArray(new ILoggerListener[_listeners.size()]);
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].info(source, message);
					}
				}
				catch (Throwable t)
				{
					
				}
			}

			public void info(Class<?> source, Object message, Throwable th)
			{
				try
				{
					ILoggerListener[] listeners = _listeners.toArray(new ILoggerListener[_listeners.size()]);
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].info(source, message, th);
					}
				}
				catch (Throwable t)
				{
					
				}
			}

			public void warn(Class<?> source, Object message)
			{
				try
				{
					ILoggerListener[] listeners = _listeners.toArray(new ILoggerListener[_listeners.size()]);
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].warn(source, message);
					}
				}
				catch (Throwable t)
				{
					
				}
			}

			public void warn(Class<?> source, Object message, Throwable th)
			{
				try
				{
					ILoggerListener[] listeners = _listeners.toArray(new ILoggerListener[_listeners.size()]);
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].warn(source, message, th);
					}
				}
				catch (Throwable t)
				{
					
				}
			}

			public void error(Class<?> source, Object message)
			{
				try
				{
					ILoggerListener[] listeners = _listeners.toArray(new ILoggerListener[_listeners.size()]);
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].error(source, message);
					}
				}
				catch (Throwable t)
				{
					
				}
			}

			public void error(Class<?> source, Object message, Throwable th)
			{
				try
				{
					ILoggerListener[] listeners = _listeners.toArray(new ILoggerListener[_listeners.size()]);
					for (int i = 0; i < listeners.length; i++)
					{
						listeners[i].error(source, message, th);
					}
				}
				catch (Throwable t)
				{
					
				}
			}
		};

		if (doBasicConfig)
		{
			BasicConfigurator.configure();
		}
	}

	public ILogger createLogger(Class<?> clazz)
	{
		return new Log4jLogger(clazz, _listenerOfAllLoggers);
	}

	public void shutdown()
	{
	}
}
