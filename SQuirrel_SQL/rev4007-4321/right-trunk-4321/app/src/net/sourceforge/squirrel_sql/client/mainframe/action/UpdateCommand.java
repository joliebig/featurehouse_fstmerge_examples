package net.sourceforge.squirrel_sql.client.mainframe.action;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.AboutBoxDialog;
import net.sourceforge.squirrel_sql.client.update.UpdateController;
import net.sourceforge.squirrel_sql.client.update.UpdateControllerImpl;
import net.sourceforge.squirrel_sql.client.update.UpdateUtilImpl;
import net.sourceforge.squirrel_sql.client.update.gui.UpdateManagerDialog;

public class UpdateCommand implements ICommand
{
	
	private IApplication _app;

	
	public UpdateCommand(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
	}

	
	public void execute()
	{
	   UpdateControllerImpl updateController = new UpdateControllerImpl(_app);
	   updateController.setUpdateUtil(new UpdateUtilImpl());
	   updateController.showUpdateDialog();
	}
}
