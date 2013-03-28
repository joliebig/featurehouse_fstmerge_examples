package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.ListMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

public class DumpApplicationAction extends SquirrelAction
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DumpApplicationAction.class);

	
	private final static ILogger s_log =
		LoggerController.createLogger(DumpApplicationAction.class);

	
	public DumpApplicationAction(IApplication app)
	{
		super(app);
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		final IApplication app = getApplication();
		final Frame parentFrame = getParentFrame(evt);
		final FileExtensionFilter[] filters = new FileExtensionFilter[1];
		filters[0] = new FileExtensionFilter(s_stringMgr.getString("DumpApplicationAction.textfiles"), new String[] { ".txt" });
		final JLabel lbl = new JLabel(s_stringMgr.getString("DumpApplicationAction.warning"));
		lbl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		final File outFile = Dialogs.selectFileForWriting(parentFrame, filters, lbl);
		if (outFile != null)
		{
			ListMessageHandler msgHandler = new ListMessageHandler();
			ICommand cmd = new DumpApplicationCommand(app, outFile, msgHandler);
			try
			{
				cmd.execute();
				String[] msgs = msgHandler.getMessages();
            String[] warnings = msgHandler.getWarningMessages();
				Throwable[] errors = msgHandler.getExceptions();
            if (msgs.length > 0 || errors.length > 0 || warnings.length > 0)
				{
					for (int i = 0; i < msgs.length; ++i)
					{
						app.showErrorDialog(msgs[i]);
					}
					for (int i = 0; i < warnings.length; ++i)
					{
						app.showErrorDialog(warnings[i]);
					}
					for (int i = 0; i < errors.length; ++i)
					{
						app.showErrorDialog(errors[i]);
					}
				}
				else
				{
					final String msg = s_stringMgr.getString("DumpApplicationAction.success", outFile.getAbsolutePath());
					ErrorDialog dlg = new ErrorDialog(getApplication().getMainFrame(), msg);
					
					dlg.setTitle(s_stringMgr.getString("DumpApplicationAction.titleSuccess"));
					dlg.setVisible(true);
				}
			}
			catch (Throwable ex)
			{
				final String msg = s_stringMgr.getString("DumpApplicationAction.failure");
				app.showErrorDialog(msg, ex);
				s_log.error(msg, ex);
			}
		}
	}
}
