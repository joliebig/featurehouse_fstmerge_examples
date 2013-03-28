package net.sourceforge.squirrel_sql.fw.id;


public class UidIdentifierFactory implements IIdentifierFactory
{
	
	public UidIdentifierFactory()
	{
		super();
	}

	
	public synchronized IIdentifier createIdentifier()
	{
		return new UidIdentifier();
	}
}
