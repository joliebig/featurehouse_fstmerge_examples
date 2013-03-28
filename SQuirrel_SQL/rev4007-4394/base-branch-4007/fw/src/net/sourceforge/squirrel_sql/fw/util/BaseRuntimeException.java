package net.sourceforge.squirrel_sql.fw.util;

import java.io.PrintStream;
import java.io.PrintWriter;

public class BaseRuntimeException extends RuntimeException
{
	
	private Throwable _wrapee;

	
	public BaseRuntimeException()
	{
		this("");
	}

	
	public BaseRuntimeException(String msg)
	{
		super(msg != null ? msg : "");
	}

	
	public BaseRuntimeException(Throwable wrapee)
	{
		super(getMessageFromException(wrapee));
		_wrapee = wrapee;
	}

	public String toString()
	{
		if (_wrapee != null)
		{
			return _wrapee.toString();
		}
		return super.toString();
	}

	public void printStackTrace()
	{
		if (_wrapee != null)
		{
			_wrapee.printStackTrace();
		}
		else
		{
			super.printStackTrace();
		}
	}

	public void printStackTrace(PrintStream s)
	{
		if (_wrapee != null)
		{
			_wrapee.printStackTrace(s);
		}
		else
		{
			super.printStackTrace(s);
		}
	}

	public void printStackTrace(PrintWriter wtr)
	{
		if (_wrapee != null)
		{
			_wrapee.printStackTrace(wtr);
		}
		else
		{
			super.printStackTrace(wtr);
		}
	}

	
	public Throwable getWrappedThrowable()
	{
		return _wrapee;
	}

	private static String getMessageFromException(Throwable th)
	{
		String rtn = "";
		if (th != null)
		{
			String msg = th.getMessage();
			if (msg != null)
			{
				rtn = msg;
			}
		}
		return rtn;
	}
}
