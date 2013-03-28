package net.sourceforge.squirrel_sql.client;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistory;

public interface IApplication
{
	public interface IMenuIDs extends MainFrame.IMenuIDs
	{
		
	}

	
	IPlugin getDummyAppPlugin();

	
	IPluginManager getPluginManager();

	
	WindowManager getWindowManager();

	ActionCollection getActionCollection();

	SQLDriverManager getSQLDriverManager();

	DataCache getDataCache();

	SquirrelPreferences getSquirrelPreferences();

	SquirrelResources getResources();


	
	IMessageHandler getMessageHandler();

	
	

	
	SessionManager getSessionManager();

	
	void showErrorDialog(String msg);

	
	void showErrorDialog(Throwable th);

	
	void showErrorDialog(String msg, Throwable th);

	
	MainFrame getMainFrame();

	
	TaskThreadPool getThreadPool();

	
	FontInfoStore getFontInfoStore();

	
	ISQLEntryPanelFactory getSQLEntryPanelFactory();

	
	SQLHistory getSQLHistory();

	
	void setSQLEntryPanelFactory(ISQLEntryPanelFactory factory);

	
	void addToMenu(int menuId, JMenu menu);

	
	void addToMenu(int menuId, Action action);

	
	void addToStatusBar(JComponent comp);

	
	void removeFromStatusBar(JComponent comp);

	
	void startup();

	
	boolean shutdown();
    
    
	void openURL(String url);

    
    void saveApplicationState();
    
    
    public void savePreferences(PreferenceType preferenceType);
    
}
