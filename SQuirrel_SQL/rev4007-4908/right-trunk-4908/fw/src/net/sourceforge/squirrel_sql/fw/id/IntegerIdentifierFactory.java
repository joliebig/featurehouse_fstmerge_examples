package net.sourceforge.squirrel_sql.fw.id;


public class IntegerIdentifierFactory implements IIdentifierFactory
{
	private int _next;

	
	public IntegerIdentifierFactory()
	{
		this(0);
	}

	
	public IntegerIdentifierFactory(int initialValue)
	{
		super();
		_next = initialValue;
	}

	
	public synchronized IIdentifier createIdentifier()
	{
		return new IntegerIdentifier(_next++);
	}
}
