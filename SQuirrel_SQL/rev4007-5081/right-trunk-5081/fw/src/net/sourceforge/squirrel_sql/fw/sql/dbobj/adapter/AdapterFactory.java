package net.sourceforge.squirrel_sql.fw.sql.dbobj.adapter;

import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;



public class AdapterFactory
{
	private final static AdapterFactory s_instance = new AdapterFactory();

	private AdapterFactory()
	{
		super();
	}

	public static AdapterFactory getInstance()
	{
		return s_instance;
	}

	public BestRowIdentifierAdapter[] createBestRowIdentifierAdapter(BestRowIdentifier[] beans)
	{
		if (beans == null)
		{
			throw new IllegalArgumentException("BestRowIdentifier[] == null");
		}

		BestRowIdentifierAdapter[] adapters = new BestRowIdentifierAdapter[beans.length];
		for (int i = 0; i < beans.length; ++i)
		{
			adapters[i] = new BestRowIdentifierAdapter(beans[i]);
		}
		return adapters;
	}
}
