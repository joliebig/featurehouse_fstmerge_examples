package net.sourceforge.squirrel_sql.plugins.derby;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
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
import net.sourceforge.squirrel_sql.plugins.derby.exp.DerbyTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.derby.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.derby.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.derby.tab.ViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.derby.tokenizer.DerbyQueryTokenizer;



public class DerbyPlugin extends DefaultSessionPlugin {
    
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DerbyPlugin.class);

    
    private final static ILogger s_log = 
        LoggerController.createLogger(DerbyPlugin.class);

    
    private IObjectTreeAPI _treeAPI;

    static interface i18n {
        
        String SHOW_VIEW_SOURCE = 
            s_stringMgr.getString("DerbyPlugin.showViewSource");
        
        
        String SHOW_PROCEDURE_SOURCE =
            s_stringMgr.getString("DerbyPlugin.showProcedureSource");
    }
    
    
    public String getInternalName()
    {
        return "derby";
    }

    
    public String getDescriptiveName()
    {
        return "Derby Plugin";
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
       s_log.info("Installing Derby query tokenizer");
       IQueryTokenizer orig = session.getQueryTokenizer();
       DerbyQueryTokenizer tokenizer = 
           new DerbyQueryTokenizer(orig.getSQLStatementSeparator(),
                                   orig.getLineCommentBegin(),
                                   orig.isRemoveMultiLineComment());
       session.setQueryTokenizer(tokenizer);
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
        return DialectFactory.isDerby(session.getMetaData());
    }
    
    private void updateTreeApi(ISession session) {
        
        _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
        
        
        
        
        _treeAPI.addDetailTab(DatabaseObjectType.VIEW, 
                              new ViewSourceTab(i18n.SHOW_VIEW_SOURCE));
        
        
        
        
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO, new DatabaseObjectInfoTab());
        
        
        
        
        
        
        TableWithChildNodesExpander trigExp = new TableWithChildNodesExpander();
        trigExp.setTableTriggerExtractor(new DerbyTableTriggerExtractorImpl());
        _treeAPI.addExpander(DatabaseObjectType.TABLE, trigExp);
        
        
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab("The source of the trigger"));
        
    }
    
}
