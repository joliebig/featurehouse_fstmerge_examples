package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ShowLoadedDriversOnlyAction extends SquirrelAction
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ShowLoadedDriversOnlyAction.class);

	
	public ShowLoadedDriversOnlyAction(IApplication app)
	{
		super(app);
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		try
		{
			new ShowLoadedDriversOnlyCommand(getApplication()).execute();
		}
		catch (Exception ex)
		{
			getApplication().showErrorDialog(s_stringMgr.getString("ShowLoadedDriversOnlyAction.error"), ex);
		}
	}
}
