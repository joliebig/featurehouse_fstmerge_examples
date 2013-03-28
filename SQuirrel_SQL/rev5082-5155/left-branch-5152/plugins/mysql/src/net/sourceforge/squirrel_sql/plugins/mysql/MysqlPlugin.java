package net.sourceforge.squirrel_sql.plugins.mysql;


import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.mysql.action.AlterTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.AnalyzeTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.CheckTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.CopyTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.CreateDatabaseAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.CreateMysqlTableScriptAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.DropDatabaseAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.ExplainSelectTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.ExplainTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.OptimizeTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.RenameTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.expander.MysqlTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.mysql.expander.SessionExpander;
import net.sourceforge.squirrel_sql.plugins.mysql.expander.UserParentExpander;
import net.sourceforge.squirrel_sql.plugins.mysql.prefs.MysqlPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.DatabaseStatusTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.MysqlProcedureSourceTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.MysqlTriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.MysqlTriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.MysqlViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.OpenTablesTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ProcessesTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowColumnsTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowIndexesTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowLogsTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowMasterLogsTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowMasterStatusTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowSlaveStatusTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowVariablesTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.TableStatusTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.UserGrantsTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tokenizer.MysqlQueryTokenizer;


public class MysqlPlugin extends DefaultSessionPlugin
{
	
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MysqlPlugin.class);

	
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(MysqlPlugin.class);

	
	private PluginResources _resources;

	
	private IObjectTreeAPI _treeAPI;

	
	private JMenu _mySQLMenu;

	
	private PluginQueryTokenizerPreferencesManager _prefsManager = null;

	interface i18n
	{
		
		String title = s_stringMgr.getString("MysqlPlugin.title");

		
		String hint = s_stringMgr.getString("MysqlPlugin.hint");

		
		String SHOW_PROCEDURE_SOURCE = s_stringMgr.getString("MysqlPlugin.showProcedureSource");

		
		String SHOW_TRIGGER_SOURCE = s_stringMgr.getString("MysqlPlugin.showTriggerSource");

		
		String SHOW_VIEW_SOURCE = s_stringMgr.getString("MysqlPlugin.showViewSource");
	}

	
	public String getInternalName()
	{
		return "mysql";
	}

	
	public String getDescriptiveName()
	{
		return "MySQL Plugin";
	}

	
	public String getVersion()
	{
		return "0.33";
	}

	
	public String getAuthor()
	{
		return "Colin Bell";
	}

	
	public synchronized void load(IApplication app) throws PluginException
	{
		super.load(app);
		_resources = new MysqlResources(getClass().getName(), this);
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

	
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		PluginQueryTokenizerPreferencesPanel _prefsPanel =
			new PluginQueryTokenizerPreferencesPanel(_prefsManager, "MySQL");

		PluginGlobalPreferencesTab tab = new PluginGlobalPreferencesTab(_prefsPanel);

		tab.setHint(i18n.hint);
		tab.setTitle(i18n.title);

		return new IGlobalPreferencesPanel[] { tab };
	}

	
	public synchronized void initialize() throws PluginException
	{
		super.initialize();

		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		coll.add(new AnalyzeTableAction(app, _resources, this));
		coll.add(new CreateMysqlTableScriptAction(app, _resources, this));
		coll.add(new CheckTableAction.ChangedCheckTableAction(app, _resources, this));
		coll.add(new CheckTableAction.ExtendedCheckTableAction(app, _resources, this));
		coll.add(new CheckTableAction.FastCheckTableAction(app, _resources, this));
		coll.add(new CheckTableAction.MediumCheckTableAction(app, _resources, this));
		coll.add(new CheckTableAction.QuickCheckTableAction(app, _resources, this));
		coll.add(new ExplainSelectTableAction(app, _resources, this));
		coll.add(new ExplainTableAction(app, _resources, this));
		coll.add(new OptimizeTableAction(app, _resources, this));
		coll.add(new RenameTableAction(app, _resources, this));

		coll.add(new CreateDatabaseAction(app, _resources, this));
		coll.add(new DropDatabaseAction(app, _resources, this));
		coll.add(new AlterTableAction(app, _resources, this));
		
		coll.add(new CopyTableAction(app, _resources, this));

		_mySQLMenu = createFullMysqlMenu();
		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, _mySQLMenu);
		super.registerSessionMenu(_mySQLMenu);

		_prefsManager = new PluginQueryTokenizerPreferencesManager();
		_prefsManager.initialize(this, new MysqlPreferenceBean());
	}

	
	public void unload()
	{
		super.unload();
	}

	
	public boolean allowsSessionStartedInBackground()
	{
		return true;
	}

	
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		if (!isPluginSession(session)) { return null; }

		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				updateTreeApi(session);
			}
		});

		installMysqlQueryTokenizer(session);

		return new PluginSessionCallbackAdaptor(this);
	}

	
	@Override
	protected boolean isPluginSession(ISession session)
	{
		return DialectFactory.isMySQL(session.getMetaData()) || DialectFactory.isMySQL5(session.getMetaData());
	}

	
	private void installMysqlQueryTokenizer(ISession session)
	{

		IQueryTokenizerPreferenceBean _prefs = _prefsManager.getPreferences();

		if (_prefs.isInstallCustomQueryTokenizer())
		{
			session.setQueryTokenizer(new MysqlQueryTokenizer(_prefs));
		}

	}

	private void updateTreeApi(ISession session)
	{
		_treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
		final ActionCollection coll = getApplication().getActionCollection();

		
		_treeAPI.addExpander(DatabaseObjectType.SESSION, new SessionExpander());
		_treeAPI.addExpander(IObjectTypes.USER_PARENT, new UserParentExpander(this));

		
		_treeAPI.addDetailTab(DatabaseObjectType.SESSION, new DatabaseStatusTab());
		_treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ProcessesTab());
		_treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ShowVariablesTab());
		_treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ShowLogsTab());
		_treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ShowMasterStatusTab());
		_treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ShowMasterLogsTab());
		_treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ShowSlaveStatusTab());

		
		_treeAPI.addDetailTab(DatabaseObjectType.CATALOG, new OpenTablesTab());
		_treeAPI.addDetailTab(DatabaseObjectType.CATALOG, new TableStatusTab());

		
		_treeAPI.addDetailTab(DatabaseObjectType.TABLE, new ShowColumnsTab());
		_treeAPI.addDetailTab(DatabaseObjectType.TABLE, new ShowIndexesTab());

		
		_treeAPI.addDetailTab(DatabaseObjectType.USER, new UserGrantsTab());

		
		_treeAPI.addToPopup(coll.get(CreateDatabaseAction.class));

		
		
		_treeAPI.addToPopup(DatabaseObjectType.CATALOG, coll.get(DropDatabaseAction.class));

		_treeAPI.addToPopup(DatabaseObjectType.TABLE, createMysqlTableMenu());

		updateTreeApiForMysql5(session);
	}

	private void updateTreeApiForMysql5(ISession session)
	{
		if (!DialectFactory.isMySQL5(session.getMetaData())) { return; }
		String stmtSep = session.getQueryTokenizer().getSQLStatementSeparator();

		MysqlProcedureSourceTab procSourceTab = new MysqlProcedureSourceTab(i18n.SHOW_PROCEDURE_SOURCE);
		_treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, procSourceTab);

		
		MysqlViewSourceTab viewSourceTab = new MysqlViewSourceTab(i18n.SHOW_VIEW_SOURCE, stmtSep);
		_treeAPI.addDetailTab(DatabaseObjectType.VIEW, viewSourceTab);

		
		TableWithChildNodesExpander trigExp = new TableWithChildNodesExpander();
		trigExp.setTableTriggerExtractor(new MysqlTableTriggerExtractorImpl());
		_treeAPI.addExpander(DatabaseObjectType.TABLE, trigExp);

		
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new MysqlTriggerDetailsTab());
		MysqlTriggerSourceTab trigSourceTab = new MysqlTriggerSourceTab(i18n.SHOW_TRIGGER_SOURCE, stmtSep);
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, trigSourceTab);

	}

	
	private JMenu createMysqlTableMenu()
	{
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		final JMenu mysqlMenu = _resources.createMenu(MysqlResources.IMenuResourceKeys.MYSQL);

		_resources.addToMenu(coll.get(CreateMysqlTableScriptAction.class), mysqlMenu);

		_resources.addToMenu(coll.get(AnalyzeTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(ExplainTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(ExplainSelectTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(OptimizeTableAction.class), mysqlMenu);

		final JMenu checkTableMenu = _resources.createMenu(MysqlResources.IMenuResourceKeys.CHECK_TABLE);
		_resources.addToMenu(coll.get(CheckTableAction.ChangedCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.ExtendedCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.FastCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.MediumCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.QuickCheckTableAction.class), checkTableMenu);
		mysqlMenu.add(checkTableMenu);

		_resources.addToMenu(coll.get(AlterTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(CopyTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(RenameTableAction.class), mysqlMenu);

		return mysqlMenu;
	}

	
	private JMenu createFullMysqlMenu()
	{
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		final JMenu mysqlMenu = _resources.createMenu(MysqlResources.IMenuResourceKeys.MYSQL);

		_resources.addToMenu(coll.get(CreateDatabaseAction.class), mysqlMenu);
		

		_resources.addToMenu(coll.get(CreateMysqlTableScriptAction.class), mysqlMenu);
		

		_resources.addToMenu(coll.get(AnalyzeTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(ExplainTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(ExplainSelectTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(OptimizeTableAction.class), mysqlMenu);

		final JMenu checkTableMenu = _resources.createMenu(MysqlResources.IMenuResourceKeys.CHECK_TABLE);
		_resources.addToMenu(coll.get(CheckTableAction.ChangedCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.ExtendedCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.FastCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.MediumCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.QuickCheckTableAction.class), checkTableMenu);
		mysqlMenu.add(checkTableMenu);

		return mysqlMenu;
	}

}
