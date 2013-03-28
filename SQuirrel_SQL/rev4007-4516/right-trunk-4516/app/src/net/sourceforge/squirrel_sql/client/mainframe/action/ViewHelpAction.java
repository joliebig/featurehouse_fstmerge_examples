package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

public class ViewHelpAction extends SquirrelAction
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ViewHelpAction.class);

	
	public ViewHelpAction(IApplication app)
	{
		super(app);
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		try
		{
			new ViewHelpCommand(getApplication()).execute();
		}
		catch (BaseException ex)
		{
			getApplication().showErrorDialog(s_stringMgr.getString("ViewHelpAction.error.viewerror"), ex);
		}
	}
}
