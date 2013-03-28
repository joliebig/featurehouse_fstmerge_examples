package net.sourceforge.squirrel_sql.plugins.sessionscript;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;

class ScriptsSheet extends DialogWidget
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ScriptsSheet.class);
	

	
	private static ILogger s_log =
		LoggerController.createLogger(ScriptsSheet.class);

	
	private static ScriptsSheet s_instance;

	
	private SessionScriptPlugin _plugin;

	
	private IApplication _app;

	
	private ViewSessionScriptsPanel _mainPnl;

	private ScriptsSheet(SessionScriptPlugin plugin, IApplication app)
	{
		
		super(s_stringMgr.getString("sessionscript.startupScripts"), true, true, true, true, app);
		_plugin = plugin;
		_app = app;

		createUserInterface();
	}

	public void dispose()
	{
		synchronized (getClass())
		{
			s_instance = null;
		}
		super.dispose();
	}

	public static synchronized void showSheet(SessionScriptPlugin plugin,
													IApplication app)
	{
		if (s_instance == null)
		{
			s_instance = new ScriptsSheet(plugin, app);
			app.getMainFrame().addWidget(s_instance);
		}
		s_instance.setVisible(true);
	}

	
	private void createUserInterface()
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);






		makeToolWindow(true);

		_mainPnl = new ViewSessionScriptsPanel(_plugin, _app);

		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		content.add(_mainPnl, BorderLayout.CENTER);

		setPreferredSize(new Dimension(600, 400));
		pack();
	}

}
