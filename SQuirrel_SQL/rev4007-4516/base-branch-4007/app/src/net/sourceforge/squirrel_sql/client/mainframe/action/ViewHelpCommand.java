package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.HelpViewerWindow;

public class ViewHelpCommand implements ICommand
{
	
	private static HelpViewerWindow s_window;

	
	private IApplication _app;

	
	public ViewHelpCommand(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
	}

	
	public void execute() throws BaseException
	{
		synchronized (getClass())
		{
			if (s_window == null)
			{
				s_window = new HelpViewerWindow(_app);
				s_window.setSize(600, 400);
				GUIUtils.centerWithinParent(s_window);
			}
		}
		s_window.setVisible(true);
		s_window.toFront();	
	}

}
