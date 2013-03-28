package net.sourceforge.squirrel_sql.plugins.mysql.action;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.WrappedSQLException;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;



import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;
import net.sourceforge.squirrel_sql.plugins.mysql.gui.AlterTableDialog;

public class AlterTableCommand implements ICommand
{






















	





	
	private ISession _session;

	
	private final MysqlPlugin _plugin;

	
	private final ITableInfo _ti;

	
	public AlterTableCommand(ISession session, MysqlPlugin plugin,
								ITableInfo ti)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("MysqlPlugin == null");
		}
		if (ti == null)
		{
			throw new IllegalArgumentException("ITableInfo == null");
		}

		_session = session;
		_plugin = plugin;
		_ti = ti;

	}

	public void execute() throws BaseException
	{
		try
		{
			AlterTableDialog dlog = new AlterTableDialog(_session, _plugin, _ti);
			dlog.pack();
			GUIUtils.centerWithinParent(dlog);
			dlog.setVisible(true);
		}
		catch (SQLException ex)
		{
			throw new WrappedSQLException(ex);
		}
	}



















































































































































































	






	









	

















	


















	






	












































































	





















}
