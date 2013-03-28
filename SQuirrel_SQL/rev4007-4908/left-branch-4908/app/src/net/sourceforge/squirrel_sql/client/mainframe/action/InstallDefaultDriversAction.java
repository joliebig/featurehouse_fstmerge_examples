package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.net.URL;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.DriversListInternalFrame;

public class InstallDefaultDriversAction extends SquirrelAction
{
	
	private static ILogger s_log =
		LoggerController.createLogger(InstallDefaultDriversAction.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(InstallDefaultDriversAction.class);

	
	public InstallDefaultDriversAction(IApplication app)
	{
		super(app);
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		final IApplication app = getApplication();

		if (Dialogs.showYesNo(app.getMainFrame(),
								s_stringMgr.getString("InstallDefaultDriversAction.confirm")))
		{
			final DriversListInternalFrame tw = app.getWindowManager().getDriversListInternalFrame();
			tw.moveToFront();
			try
			{
				tw.setSelected(true);
			}
			catch (PropertyVetoException ex)
			{
                
				s_log.error(s_stringMgr.getString("InstallDefaultDriversAction.error.selectingwindow"), ex);
			}
			final URL url = app.getResources().getDefaultDriversUrl();
			try
			{
				new InstallDefaultDriversCommand(app, url).execute();
			}
			catch (BaseException ex)
			{
				app.showErrorDialog(s_stringMgr.getString("InstallDefaultDriversAction.error.install"), ex);
			}
		}
	}
}
