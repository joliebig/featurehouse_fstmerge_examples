package net.sourceforge.squirrel_sql.plugins.informix;



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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.informix.exception.InformixExceptionFormatter;
import net.sourceforge.squirrel_sql.plugins.informix.exp.InformixSequenceInodeExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.informix.exp.InformixTableIndexExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.informix.exp.InformixTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.informix.tab.IndexDetailsTab;
import net.sourceforge.squirrel_sql.plugins.informix.tab.ProcedureSourceTab;
import net.sourceforge.squirrel_sql.plugins.informix.tab.SequenceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.informix.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.informix.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.informix.tab.ViewSourceTab;


public class InformixPlugin extends DefaultSessionPlugin
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(InformixPlugin.class);

	
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(InformixPlugin.class);

	
	private IObjectTreeAPI _treeAPI;

	static interface i18n
	{
		
		String SHOW_VIEW_SOURCE = s_stringMgr.getString("InformixPlugin.showViewSource");

		
		String SHOW_PROCEDURE_SOURCE = s_stringMgr.getString("InformixPlugin.showProcedureSource");
	}

	
	public String getInternalName()
	{
		return "informix";
	}

	
	public String getDescriptiveName()
	{
		return "Informix Plugin";
	}

	
	public String getVersion()
	{
		return "0.03";
	}

	
	public String getAuthor()
	{
		return "Rob Manning";
	}

	
	@Override
	public String getContributors()
	{
		return "Doug Lawry";
	}

	
	@Override
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	
	@Override
	public String getHelpFileName()
	{
		return "readme.html";
	}

	
	@Override
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	
	@Override
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
		InformixExceptionFormatter formatter = new InformixExceptionFormatter(session);
		session.setExceptionFormatter(formatter);
		return new PluginSessionCallbackAdaptor(this);
	}

	
	@Override
	protected boolean isPluginSession(ISession session)
	{
		return DialectFactory.isInformix(session.getMetaData());
	}

	
	private void updateTreeApi(ISession session)
	{
		_treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();

		_treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, new ProcedureSourceTab(i18n.SHOW_PROCEDURE_SOURCE));

		_treeAPI.addDetailTab(DatabaseObjectType.VIEW, new ViewSourceTab(i18n.SHOW_VIEW_SOURCE));

		_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab());
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());

		
		
		_treeAPI.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(
			new InformixSequenceInodeExpanderFactory(), DatabaseObjectType.SEQUENCE));

		TableWithChildNodesExpander tableExp = new TableWithChildNodesExpander();
		tableExp.setTableIndexExtractor(new InformixTableIndexExtractorImpl());
		tableExp.setTableTriggerExtractor(new InformixTableTriggerExtractorImpl());
		_treeAPI.addExpander(DatabaseObjectType.TABLE, tableExp);

		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab("The source of the trigger"));

	}

}
