package net.sourceforge.squirrel_sql.plugins.postgres;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableIndexExtractor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.postgres.exp.PostgresTableIndexExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.postgres.exp.PostgresTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.postgres.exp.SchemaExpander;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.IndexDetailsTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.IndexSourceTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.LockTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.ProcedureSourceTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.SequenceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.ViewSourceTab;



public class PostgresPlugin extends DefaultSessionPlugin {
    
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PostgresPlugin.class);

    
    @SuppressWarnings("unused")
    private final static ILogger s_log = 
        LoggerController.createLogger(PostgresPlugin.class);

    
    private IObjectTreeAPI _treeAPI;

    static interface i18n {
        
        String SHOW_INDEX_SOURCE = 
            s_stringMgr.getString("PostgresPlugin.showIndexSource");
        
        
        String SHOW_VIEW_SOURCE = 
            s_stringMgr.getString("PostgresPlugin.showViewSource");
        
        
        String SHOW_PROCEDURE_SOURCE =
            s_stringMgr.getString("PostgresPlugin.showProcedureSource");
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
        return "0.11";
    }

    
    public String getAuthor()
    {
        return "Rob Manning";
    }

        
    public String getContributors() {
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

	
	public synchronized void load(IApplication app) throws PluginException
	{
		super.load(app);
	}

	
	public synchronized void initialize() throws PluginException
	{
		super.initialize();
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
       if (!isPluginSession(session)) {
           return null;
       }
       GUIUtils.processOnSwingEventThread(new Runnable() {
           public void run() {
               updateTreeApi(session);
           }
       });

       return new PluginSessionCallback()
       {
           public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
           {
               
           }

           public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
           {
               
           }
       };
   }

    @Override
    protected boolean isPluginSession(ISession session) {
        return DialectFactory.isPostgreSQL(session.getMetaData());
    }
    
    private void updateTreeApi(ISession session) {
        
        _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
        _treeAPI.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander());        
        String stmtSep = session.getQueryTokenizer().getSQLStatementSeparator();
        
        
        
        TableWithChildNodesExpander tableExpander = 
            new TableWithChildNodesExpander(); 
        
        
        ITableIndexExtractor indexExtractor = 
            new PostgresTableIndexExtractorImpl();
        ITableTriggerExtractor triggerExtractor = 
            new PostgresTableTriggerExtractorImpl();
        
        tableExpander.setTableTriggerExtractor(triggerExtractor);
        tableExpander.setTableIndexExtractor(indexExtractor);
        
        _treeAPI.addExpander(DatabaseObjectType.TABLE, tableExpander);
        
        
        _treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, 
                new ProcedureSourceTab(i18n.SHOW_PROCEDURE_SOURCE));
        
        
        _treeAPI.addDetailTab(DatabaseObjectType.VIEW, 
                              new ViewSourceTab(i18n.SHOW_VIEW_SOURCE, stmtSep));
        
        
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, 
                              new IndexSourceTab(i18n.SHOW_INDEX_SOURCE, stmtSep));

        
        _treeAPI.addDetailTab(IObjectTypes.TRIGGER_PARENT, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab("The source of the trigger"));

        
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab()); 
        
        _treeAPI.addDetailTab(DatabaseObjectType.SESSION, new LockTab());

        
        
    }
}
