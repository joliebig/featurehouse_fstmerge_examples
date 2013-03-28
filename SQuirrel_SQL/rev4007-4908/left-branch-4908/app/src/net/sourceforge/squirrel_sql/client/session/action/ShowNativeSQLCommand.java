package net.sourceforge.squirrel_sql.client.session.action;

import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class ShowNativeSQLCommand implements ICommand
{
	
	private final ISQLPanelAPI _panel;

	
	public ShowNativeSQLCommand(ISQLPanelAPI panel)
	{
		super();
		if (panel == null)
		{
			throw new IllegalArgumentException("ISQLPanelAPI == null");
		}

		_panel = panel;
	}

	public void execute()
	{
		final ISession session = _panel.getSession();
		final Connection conn = session.getSQLConnection().getConnection();
		try
		{
			final String sql = conn.nativeSQL(_panel.getSQLScriptToBeExecuted());
			if (sql.length() > 0)
			{
				_panel.appendSQLScript("\n" + sql, true);
			}
		}
		catch (SQLException ex)
		{
			session.showErrorMessage(ex);
		}
	}
}
