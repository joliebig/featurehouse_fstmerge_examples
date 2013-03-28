package net.sourceforge.squirrel_sql.client.session.action;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class SetDefaultCatalogCommand implements ICommand
{
	
	private final ISession _session;

	
	private final String _catalog;

	
	public SetDefaultCatalogCommand(ISession session, String catalog)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (catalog == null)
		{
			throw new IllegalArgumentException("Catalog == null");
		}

		_session = session;
		_catalog = catalog;
	}

	public void execute() throws BaseException
	{
		try
		{
			_session.getSQLConnection().setCatalog(_catalog);
		}
		catch (SQLException ex)
		{
			throw new BaseException(ex);
		}
	}
}
