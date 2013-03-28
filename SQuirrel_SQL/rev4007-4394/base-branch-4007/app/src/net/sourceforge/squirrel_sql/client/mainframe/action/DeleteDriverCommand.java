package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.Frame;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DeleteDriverCommand implements ICommand
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DeleteDriverCommand.class);

	
	private final IApplication _app;

	
	private Frame _frame;

	
	private ISQLDriver _sqlDriver;

	
	public DeleteDriverCommand(IApplication app, Frame frame,
								ISQLDriver sqlDriver)
	{
		super();
		if (sqlDriver == null)
		{
			throw new IllegalArgumentException("Null ISQLDriver passed");
		}
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;
		_frame = frame;
		_sqlDriver = sqlDriver;
	}

	
	public void execute()
	{
		final Object[] args = {_sqlDriver.getName()};
		final net.sourceforge.squirrel_sql.client.gui.db.DataCache cache = _app.getDataCache();
		Iterator<ISQLAlias> it = cache.getAliasesForDriver(_sqlDriver);
		if (it.hasNext())
		{
            StringBuffer aliasList = new StringBuffer();
            while (it.hasNext()) {
                net.sourceforge.squirrel_sql.client.gui.db.SQLAlias alias = (SQLAlias)it.next();
                aliasList.append("\n");
                aliasList.append(alias.getName());
            }
            final Object[] args2 = { _sqlDriver.getName(), aliasList };
            String msg = 
                s_stringMgr.getString("DeleteDriverCommand.used", args2);
			Dialogs.showOk(_frame, msg);
		}
		else
		{
			if (Dialogs.showYesNo(_frame, s_stringMgr.getString("DeleteDriverCommand.comfirm", args)))
			{
				cache.removeDriver(_sqlDriver);
			}
		}
	}
}
