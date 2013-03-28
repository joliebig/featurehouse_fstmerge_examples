package net.sourceforge.squirrel_sql.fw.xml;

import net.sourceforge.squirrel_sql.fw.util.BaseException;

public class XMLException extends BaseException
{
	
	public XMLException(String msg)
	{
		super(msg);
	}

	
	public XMLException(Exception wrapee)
	{
		super(wrapee);
	}
}
