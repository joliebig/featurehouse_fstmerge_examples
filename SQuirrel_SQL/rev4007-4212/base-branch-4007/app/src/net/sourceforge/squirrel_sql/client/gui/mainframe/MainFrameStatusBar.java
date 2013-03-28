package net.sourceforge.squirrel_sql.client.gui.mainframe;


import net.sourceforge.squirrel_sql.client.gui.MemoryPanel;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;
import net.sourceforge.squirrel_sql.fw.gui.TimePanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.gui.LogPanel;
import net.sourceforge.squirrel_sql.client.IApplication;


public class MainFrameStatusBar extends StatusBar
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MainFrameStatusBar.class);

	
	public MainFrameStatusBar(IApplication app)
	{
		super();
		createGUI(app);
	}

	private void createGUI(IApplication app)
	{
		clearText();

		addJComponent(new LogPanel(app));
		addJComponent(new MemoryPanel(app));
		addJComponent(new TimePanel());
	}
}
