package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class GotoNextResultsTabAction extends SquirrelAction
										implements ISQLPanelAction
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(GotoNextResultsTabAction.class);

	
	private ISQLPanelAPI _panel;

	
	private ICommand _cmd;

	
	public GotoNextResultsTabAction(IApplication app)
	{
		super(app);
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;
		_cmd = null;
      setEnabled(null != _panel);
	}

	
	public synchronized void actionPerformed(ActionEvent evt)
	{
		if (_panel != null)
		{
			if (_cmd == null)
			{
				_cmd = new GotoNextResultsTabCommand(_panel);
			}
			try
			{
				_cmd.execute();
			}
			catch (Throwable ex)
			{
				final String msg = "Error occured seting current results tab";
				_panel.getSession().showErrorMessage(msg + ": " + ex);
				s_log.error(msg, ex);
			}
		}
	}
}
