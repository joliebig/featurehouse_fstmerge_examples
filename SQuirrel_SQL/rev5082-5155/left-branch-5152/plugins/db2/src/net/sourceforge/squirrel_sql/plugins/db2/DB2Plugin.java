package net.sourceforge.squirrel_sql.plugins.db2;



import java.sql.SQLException;
import java.sql.Types;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableIndexExtractor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.db2.exp.DB2TableIndexExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.db2.exp.DB2TableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.db2.exp.SchemaExpander;
import net.sourceforge.squirrel_sql.plugins.db2.tab.IndexDetailsTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.ProcedureSourceTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.SequenceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.TableSourceTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.UDFDetailsTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.UDFSourceTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.ViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.db2.types.DB2XmlTypeDataTypeComponentFactory;


public class DB2Plugin extends DefaultSessionPlugin
{

	private static final String JCC_DRIVER_NAME = "IBM DB2 JDBC Universal Driver Architecture";

	
	private static final String OS_400_PRODUCT_NAME = "DB2 UDB for AS/400";

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DB2Plugin.class);

	
	private final static ILogger s_log = LoggerController.createLogger(DB2Plugin.class);

	
	private IObjectTreeAPI _treeAPI;

	static interface i18n
	{
		
		String SHOW_UDF_SOURCE = s_stringMgr.getString("DB2Plugin.showUdfSource");

		
		String SHOW_VIEW_SOURCE = s_stringMgr.getString("DB2Plugin.showViewSource");

		
		String SHOW_PROCEDURE_SOURCE = s_stringMgr.getString("DB2Plugin.showProcedureSource");

		
		String SHOW_TRIGGER_SOURCE = s_stringMgr.getString("DB2Plugin.showTriggerSource");

	}

	
	public String getInternalName()
	{
		return "db2";
	}

	
	public String getDescriptiveName()
	{
		return "DB2 Plugin";
	}

	
	public String getVersion()
	{
		return "0.04";
	}

	
	public String getAuthor()
	{
		return "Rob Manning";
	}

	
	public String getContributors()
	{
		return "Christoph Schmitz, Tilmann Brenk, Lars Heller";
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
	}

	
	public synchronized void initialize() throws PluginException
	{
		super.initialize();

		
		CellComponentFactory.registerDataTypeFactory(new DB2XmlTypeDataTypeComponentFactory(),
			Types.OTHER,
			"XML");
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

		if (!isPluginSession(session))
		{
			return null;
		}
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				updateTreeApi(session);
			}
		});

		
		try
		{
			if (JCC_DRIVER_NAME.equals(session.getMetaData().getJDBCMetaData().getDriverName()))
			{
				session.setExceptionFormatter(new DB2JCCExceptionFormatter());
			}
		} catch (SQLException e)
		{
			s_log.error("Problem installing exception formatter: " + e.getMessage());
		}

		return new PluginSessionCallbackAdaptor(this);
	}

	@Override
	protected boolean isPluginSession(ISession session)
	{
		return DialectFactory.isDB2(session.getMetaData());
	}

	private void updateTreeApi(ISession session)
	{
		String stmtSep = session.getQueryTokenizer().getSQLStatementSeparator();
		boolean isOS400 = isOS400(session);

		_treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
		_treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, new ProcedureSourceTab(i18n.SHOW_PROCEDURE_SOURCE,
																											isOS400,
																											stmtSep));
		_treeAPI.addDetailTab(DatabaseObjectType.VIEW, new ViewSourceTab(	i18n.SHOW_VIEW_SOURCE,
																								stmtSep,
																								isOS400));

		_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab(isOS400));

		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO, new DatabaseObjectInfoTab());

		_treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab(isOS400));

		_treeAPI.addDetailTab(DatabaseObjectType.UDF, new DatabaseObjectInfoTab());
		_treeAPI.addDetailTab(DatabaseObjectType.UDF, new UDFSourceTab(i18n.SHOW_UDF_SOURCE, stmtSep, isOS400));
		_treeAPI.addDetailTab(DatabaseObjectType.UDF, new UDFDetailsTab(isOS400));

		_treeAPI.addDetailTab(DatabaseObjectType.TABLE, new TableSourceTab("Show MQT Source", stmtSep, isOS400));

		
		
		_treeAPI.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(isOS400));

		
		
		TableWithChildNodesExpander tableExpander = new TableWithChildNodesExpander();

		
		ITableIndexExtractor indexExtractor = new DB2TableIndexExtractorImpl(isOS400);
		tableExpander.setTableIndexExtractor(indexExtractor);

		ITableTriggerExtractor triggerExtractor = new DB2TableTriggerExtractorImpl(isOS400);
		tableExpander.setTableTriggerExtractor(triggerExtractor);

		_treeAPI.addExpander(DatabaseObjectType.TABLE, tableExpander);

		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
		_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab(	i18n.SHOW_TRIGGER_SOURCE,
																										isOS400,
																										stmtSep));

	}

	
	private boolean isOS400(ISession session)
	{
		boolean result = false;
		try
		{
			String prodName = session.getMetaData().getDatabaseProductName();
			if (prodName == null || prodName.equals(""))
			{
				s_log.info("isOS400: product name is null or empty.  " + "Assuming not an OS/400 DB2 session.");
			} else if (prodName.equals(OS_400_PRODUCT_NAME))
			{
				s_log.info("isOS400: session appears to be an OS/400 DB2");
				result = true;
			} else
			{
				s_log.info("isOS400: session doesn't appear to be an OS/400 DB2");
			}
		} catch (SQLException e)
		{
			s_log.error("isOS400: unable to determine the product name: " + e.getMessage(), e);
		}
		return result;
	}
}
