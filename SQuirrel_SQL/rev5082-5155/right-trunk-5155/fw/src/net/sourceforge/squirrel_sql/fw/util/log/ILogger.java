package net.sourceforge.squirrel_sql.fw.util.log;

import org.apache.log4j.Level;



public interface ILogger
{
	public void info(Object message);
	public void info(Object message, Throwable th);
	public void warn(Object message);
	public void warn(Object message, Throwable th);
	public void error(Object message);
	public void error(Object message, Throwable th);
	public void debug(Object message);
	public void debug(Object message, Throwable th);

	boolean isDebugEnabled();
	boolean isInfoEnabled();
    
    
    void setLevel(Level l);
}

