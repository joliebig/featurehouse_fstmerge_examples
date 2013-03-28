package net.sourceforge.squirrel_sql.fw.util;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;

public class DuplicateObjectException extends BaseException
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DuplicateObjectException.class);

	
	private IHasIdentifier _obj;

	
	public DuplicateObjectException(IHasIdentifier obj)
	{
		super(generateMessage(obj));
	}

	
	public IHasIdentifier getObject()
	{
		return _obj;
	}

	
	private static String generateMessage(IHasIdentifier obj)
	{
		final Object[] args =
		{
			obj.getClass().getName(), obj.getIdentifier().toString()
		};
		return s_stringMgr.getString("DuplicateObjectException.msg", args);
	}
}
