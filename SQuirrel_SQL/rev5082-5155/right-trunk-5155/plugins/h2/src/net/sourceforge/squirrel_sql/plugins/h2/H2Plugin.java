package net.sourceforge.squirrel_sql.plugins.h2;



import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.SchemaExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.h2.exp.H2SequenceInodeExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.h2.exp.H2TableIndexExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.h2.exp.H2TableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.h2.tab.IndexDetailsTab;
import net.sourceforge.squirrel_sql.plugins.h2.tab.IndexSourceTab;
import net.sourceforge.squirrel_sql.plugins.h2.tab.SequenceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.h2.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.h2.tab.ViewSourceTab;


public class H2Plugin extends DefaultSessionPlugin
{

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(H2Plugin.class);

	
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(H2Plugin.class);

	
	private IObjectTreeAPI _treeAPI;

	static interface i18n
	{
		
		String SHOW_INDEX_SOURCE = s_stringMgr.getString("PostgresPlugin.showIndexSource");

		
		String SHOW_VIEW_SOURCE = s_stringMgr.getString("PostgresPlugin.showViewSource");

		
		String SHOW_PROCEDURE_SOURCE = s_stringMgr.getString("PostgresPlugin.showProcedureSource");
	}

	
	public String getInternalName()
	{
		return "h2";
	}

	
	public String getDescriptiveName()
	{
		return "H2 Plugin";
	}

	
	public String getVersion()
	{
		return "0.01";
	}

	
	public String getAuthor()
	{
		return "Rob Manning";
	}

	
	public String getContributors()
	{
		return "";
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
		return new PluginSessionCallbackAdaptor(this);
	}

	@Override
	protected boolean isPluginSession(ISession session)
	{
		return DialectFactory.isH2(session.getMetaData());
	}

	private void updateTreeApi(ISession session)
	{
		IQueryTokenizer qt = session.getQueryTokenizer();
		String stmtSep = qt.getSQLStatementSeparator();

		_treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
		
		
		_treeAPI.addExpander(DatabaseObjectType.SCHEMA, 
			new SchemaExpander(new H2SequenceInodeExpanderFactory(), DatabaseObjectType.SEQUENCE));

		TableWithChildNodesExpander tableExp = new TableWithChildNodesExpander();
		tableExp.setTableIndexExtractor(new H2TableIndexExtractorImpl());
		tableExp.setTableTriggerExtractor(new H2TableTriggerExtractorImpl());
		_treeAPI.addExpander(DatabaseObjectType.TABLE, tableExp);

		
		_treeAPI.addDetailTab(DatabaseObjectType.VIEW, new ViewSourceTab(i18n.SHOW_VIEW_SOURCE, stmtSep));

		
		_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab());
		_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexSourceTab(i18n.SHOW_INDEX_SOURCE, stmtSep));

		
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());

		
		
		
		
		

		
		_treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());

	}

}
