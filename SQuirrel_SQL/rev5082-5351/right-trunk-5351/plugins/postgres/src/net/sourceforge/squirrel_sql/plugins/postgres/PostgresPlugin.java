package net.sourceforge.squirrel_sql.plugins.postgres;



import javax.swing.JMenu;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableIndexExtractor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.SchemaExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.ViewSourceTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.postgres.actions.VacuumDatabaseAction;
import net.sourceforge.squirrel_sql.plugins.postgres.actions.VacuumTableAction;
import net.sourceforge.squirrel_sql.plugins.postgres.exp.PostgresSequenceInodeExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.postgres.exp.PostgresTableIndexExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.postgres.exp.PostgresTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.postgres.explain.ExplainExecuterPanel;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.IndexDetailsTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.IndexSourceTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.LockTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.ProcedureSourceTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.SequenceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.postgres.types.PostgreSqlOtherTypeDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.plugins.postgres.types.PostgreSqlXmlTypeDataTypeComponentFactory;


public class PostgresPlugin extends DefaultSessionPlugin
{
	private PluginResources _resources;

	
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(PostgresPlugin.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PostgresPlugin.class);

	static interface i18n
	{
		
		String SHOW_INDEX_SOURCE = s_stringMgr.getString("PostgresPlugin.showIndexSource");

		
		String SHOW_VIEW_SOURCE = s_stringMgr.getString("PostgresPlugin.showViewSource");

		
		String SHOW_PROCEDURE_SOURCE = s_stringMgr.getString("PostgresPlugin.showProcedureSource");
	}

	private interface IMenuResourceKeys
	{
		String POSTGRES = "postgres";
	}

	
	public String getInternalName()
	{
		return "postgres";
	}

	
	public String getDescriptiveName()
	{
		return "Postgres Plugin";
	}

	
	public String getVersion()
	{
		return "0.22";
	}

	
	public String getAuthor()
	{
		return "Rob Manning";
	}

	
	public String getContributors()
	{
		return "Daniel Regli, Yannick Winiger";
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

	
	public synchronized void load(IApplication app) throws PluginException
	{
		super.load(app);

		_resources = new PluginResources(getClass().getName(), this);
	}

	
	public synchronized void initialize() throws PluginException
	{
		super.initialize();

		final IApplication app = getApplication();
		final ActionCollection col = getApplication().getActionCollection();

		col.add(new VacuumTableAction(app, _resources));
		col.add(new VacuumDatabaseAction(app, _resources));

		JMenu sessionMenu = createSessionMenu(col);
		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, sessionMenu);
		super.registerSessionMenu(sessionMenu);

		CellComponentFactory.registerDataTypeFactory(new PostgreSqlXmlTypeDataTypeComponentFactory(),
			java.sql.Types.OTHER, "xml");
		CellComponentFactory.registerDataTypeFactory(
			new PostgreSqlOtherTypeDataTypeComponentFactory("interval"), java.sql.Types.OTHER, "interval");

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

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				session.getSQLPanelAPIOfActiveSessionWindow().addExecutor(new ExplainExecuterPanel(session));
			}
		});

		return new PluginSessionCallback()
		{
			public void sqlInternalFrameOpened(final SQLInternalFrame sqlInternalFrame, final ISession sess)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						sqlInternalFrame.getSQLPanelAPI().addExecutor(new ExplainExecuterPanel(sess));
					}
				});
			}

			public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame,
				ISession sess)
			{
				
			}
		};
	}

	
	@Override
	protected boolean isPluginSession(ISession session)
	{
		return DialectFactory.isPostgreSQL(session.getMetaData());
	}

	
	private void updateTreeApi(ISession session)
	{
		IObjectTreeAPI _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
		final String stmtSep = session.getQueryTokenizer().getSQLStatementSeparator();
		final ActionCollection col = getApplication().getActionCollection();

		
		
		_treeAPI.addExpander(DatabaseObjectType.SCHEMA, 
			new SchemaExpander(new PostgresSequenceInodeExpanderFactory(), DatabaseObjectType.SEQUENCE));

		
		
		TableWithChildNodesExpander tableExpander = new TableWithChildNodesExpander();

		
		ITableIndexExtractor indexExtractor = new PostgresTableIndexExtractorImpl();
		ITableTriggerExtractor triggerExtractor = new PostgresTableTriggerExtractorImpl();

		tableExpander.setTableTriggerExtractor(triggerExtractor);
		tableExpander.setTableIndexExtractor(indexExtractor);

		_treeAPI.addExpander(DatabaseObjectType.TABLE, tableExpander);

		
		
		_treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, new ProcedureSourceTab(i18n.SHOW_PROCEDURE_SOURCE));

		
		_treeAPI.addDetailTab(DatabaseObjectType.VIEW, new ViewSourceTab(i18n.SHOW_VIEW_SOURCE, stmtSep));

		
		_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab());
		_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexSourceTab(i18n.SHOW_INDEX_SOURCE, stmtSep));

		
		_treeAPI.addDetailTab(IObjectTypes.TRIGGER_PARENT, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab("The source of the trigger"));

		
		_treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());

		
		_treeAPI.addDetailTab(DatabaseObjectType.SESSION, new LockTab());

		
		JMenu tableMenu = _resources.createMenu(IMenuResourceKeys.POSTGRES);
		_resources.addToMenu(col.get(VacuumTableAction.class), tableMenu);
		_treeAPI.addToPopup(DatabaseObjectType.TABLE, tableMenu);

		_treeAPI.addToPopup(DatabaseObjectType.SESSION, createSessionMenu(col));
	}

	
	private JMenu createSessionMenu(ActionCollection col)
	{
		JMenu sessionMenu = _resources.createMenu(IMenuResourceKeys.POSTGRES);
		_resources.addToMenu(col.get(VacuumDatabaseAction.class), sessionMenu);
		return sessionMenu;
	}
}
