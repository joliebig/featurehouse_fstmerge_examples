package net.sourceforge.squirrel_sql.client.util;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifierFactory;

public class IdentifierFactory implements IIdentifierFactory
{
	
	private static final IIdentifierFactory s_instance = new UidIdentifierFactory();

	
	public static IIdentifierFactory getInstance()
	{
		return s_instance;
	}

	
	public IIdentifier createIdentifier()
	{
		return s_instance.createIdentifier();
	}
}
