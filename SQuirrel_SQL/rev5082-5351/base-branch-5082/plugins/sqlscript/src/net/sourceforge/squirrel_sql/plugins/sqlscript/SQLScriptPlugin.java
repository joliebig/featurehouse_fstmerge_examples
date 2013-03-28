package net.sourceforge.squirrel_sql.plugins.sqlscript;


import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferencesManager;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferencesTab;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateDataScriptAction;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateDataScriptOfCurrentSQLAction;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateSelectScriptAction;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateTableOfCurrentSQLAction;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateTableScriptAction;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateTemplateDataScriptAction;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.DropTableScriptAction;


public class SQLScriptPlugin extends DefaultSessionPlugin
{
	public static final String BUNDLE_BASE_NAME = "net.sourceforge.squirrel_sql.plugins.sqlscript.sqlscript";

	private interface IMenuResourceKeys
	{
		String SCRIPTS = "scripts";
	}

	private PluginResources _resources;

	
	public String getInternalName()
	{
		return "sqlscript";
	}

	
	public String getDescriptiveName()
	{
		return "SQL Scripts Plugin";
	}

	
	public String getVersion()
	{
		return "1.2";
	}

	
	public String getAuthor()
	{
		return "Johan Compagner";
	}

	
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	
	public String getHelpFileName()
	{
		return "readme.html";
	}

	
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	
	public String getContributors()
	{
		return "Gerd Wagner, John Murga, Rob Manning";
	}

	
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		SQLScriptPreferencesTab tab = new SQLScriptPreferencesTab();
		return new IGlobalPreferencesPanel[]
			{ tab };
	}

	
	public synchronized void initialize() throws PluginException
	{
		super.initialize();
		IApplication app = getApplication();

		_resources = new SQLPluginResources(BUNDLE_BASE_NAME, this);

		ActionCollection coll = app.getActionCollection();
		coll.add(new CreateTableScriptAction(app, _resources, this));
		coll.add(new CreateSelectScriptAction(app, _resources, this));
		coll.add(new DropTableScriptAction(app, _resources, this));
		coll.add(new CreateDataScriptAction(app, _resources, this));
		coll.add(new CreateTemplateDataScriptAction(app, _resources, this));
		coll.add(new CreateDataScriptOfCurrentSQLAction(app, _resources, this));
		coll.add(new CreateTableOfCurrentSQLAction(app, _resources, this));
		createMenu();

		SQLScriptPreferencesManager.initialize(this);
	}

	
	public void unload()
	{
		super.unload();
		SQLScriptPreferencesManager.unload();
	}

	public boolean allowsSessionStartedInBackground()
	{
		return true;
	}

	
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				addActionsToPopup(session);
			}
		});

		PluginSessionCallback ret = new PluginSessionCallback()
		{
			public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
			{
				ActionCollection coll = sess.getApplication().getActionCollection();
				sqlInternalFrame.addSeparatorToToolbar();
				sqlInternalFrame.addToToolbar(coll.get(CreateTableOfCurrentSQLAction.class));

				sqlInternalFrame.addToToolsPopUp("sql2table", coll.get(CreateTableOfCurrentSQLAction.class));
				sqlInternalFrame.addToToolsPopUp("sql2ins", coll.get(CreateDataScriptOfCurrentSQLAction.class));
			}

			public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame,
			      ISession sess)
			{
				ActionCollection coll = sess.getApplication().getActionCollection();
				objectTreeInternalFrame.getObjectTreeAPI().addToPopup(
				   DatabaseObjectType.TABLE, coll.get(CreateTableScriptAction.class));
				objectTreeInternalFrame.getObjectTreeAPI().addToPopup(
				   DatabaseObjectType.TABLE, coll.get(CreateSelectScriptAction.class));
				objectTreeInternalFrame.getObjectTreeAPI().addToPopup(
				   DatabaseObjectType.TABLE, coll.get(DropTableScriptAction.class));
				objectTreeInternalFrame.getObjectTreeAPI().addToPopup(
				   DatabaseObjectType.TABLE, coll.get(CreateDataScriptAction.class));
				objectTreeInternalFrame.getObjectTreeAPI().addToPopup(
				   DatabaseObjectType.TABLE, coll.get(CreateTemplateDataScriptAction.class));
			}
		};

		return ret;
	}

	private void addActionsToPopup(ISession session)
	{
		ActionCollection coll = getApplication().getActionCollection();
		IObjectTreeAPI api = FrameWorkAcessor.getObjectTreeAPI(session, this);

		api.addToPopup(DatabaseObjectType.TABLE, getTableMenu(true));
		api.addToPopup(DatabaseObjectType.VIEW, getTableMenu(false));

		session.addSeparatorToToolbar();
		session.addToToolbar(coll.get(CreateTableOfCurrentSQLAction.class));

		session.getSessionInternalFrame().addToToolsPopUp(
		   "sql2table", coll.get(CreateTableOfCurrentSQLAction.class));
		session.getSessionInternalFrame().addToToolsPopUp(
		   "sql2ins", coll.get(CreateDataScriptOfCurrentSQLAction.class));
	}

	private void createMenu()
	{
		IApplication app = getApplication();
		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, getSessionMenu());
	}

	private JMenu getSessionMenu()
	{
		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		JMenu menu = _resources.createMenu(IMenuResourceKeys.SCRIPTS);
		_resources.addToMenu(coll.get(CreateDataScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateTemplateDataScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateTableScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateSelectScriptAction.class), menu);
		_resources.addToMenu(coll.get(DropTableScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateDataScriptOfCurrentSQLAction.class), menu);
		_resources.addToMenu(coll.get(CreateTableOfCurrentSQLAction.class), menu);
		return menu;
	}

	private JMenu getTableMenu(boolean includeDrop)
	{
		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		JMenu menu = _resources.createMenu(IMenuResourceKeys.SCRIPTS);
		_resources.addToMenu(coll.get(CreateDataScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateTemplateDataScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateTableScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateSelectScriptAction.class), menu);
		if (includeDrop)
		{
			_resources.addToMenu(coll.get(DropTableScriptAction.class), menu);
		}
		return menu;
	}

	public Object getExternalService()
	{
		return new SQLScriptExternalService(this);
	}

}
