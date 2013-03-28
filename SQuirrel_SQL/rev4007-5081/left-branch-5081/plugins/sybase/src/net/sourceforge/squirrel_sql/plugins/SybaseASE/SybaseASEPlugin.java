package net.sourceforge.squirrel_sql.plugins.SybaseASE;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.exp.SybaseTableIndexExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.exp.SybaseTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.prefs.SybasePreferenceBean;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.tab.ViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.tokenizer.SybaseQueryTokenizer;


public class SybaseASEPlugin extends DefaultSessionPlugin
{
    
    private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(SybaseASEPlugin.class);
               
    
    @SuppressWarnings("unused")
    private final static ILogger s_log = 
        LoggerController.createLogger(SybaseASEPlugin.class);
                
	private PluginResources _resources;

    
    private PluginQueryTokenizerPreferencesManager _prefsManager = null;
    
    
    private static final String SCRIPT_SETTINGS_BORDER_LABEL_DBNAME = "Sybase";
        
    static interface i18n {
        
        String title = s_stringMgr.getString("SybaseASEPlugin.title");

        
        String hint = s_stringMgr.getString("SybaseASEPlugin.hint");
        
        
        String SHOW_VIEW_SOURCE = 
            s_stringMgr.getString("SybaseASEPlugin.showViewSource");
        
        
        String TRIGGER_HINT=s_stringMgr.getString("SybaseASEPlugin.triggerHint");

        
    }
    
    
	
	public String getInternalName()
	{
		return "sybase";
	}

	
	public String getDescriptiveName()
	{
		return "SybaseASE Plugin";
	}

	
	public String getVersion()
	{
		return "0.02 (2.6.x)";
	}

	
	public String getAuthor()
	{
		return "Ken McCullough";
	}

	
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	
	public String getHelpFileName()
	{
		return "readme.txt";
	}

	
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	
	public String getContributors()
	{
		return "";
	}

	
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
        boolean includeProcSepPref = false;
        
        PluginQueryTokenizerPreferencesPanel _prefsPanel = 
            new PluginQueryTokenizerPreferencesPanel(
                    _prefsManager,
                    SCRIPT_SETTINGS_BORDER_LABEL_DBNAME, 
                    includeProcSepPref);

        PluginGlobalPreferencesTab tab = new PluginGlobalPreferencesTab(_prefsPanel);

        tab.setHint(i18n.hint);
        tab.setTitle(i18n.title);

        return new IGlobalPreferencesPanel[] { tab };
	}
    
    
    private void installSybaseQueryTokenizer(ISession session) {
        IQueryTokenizerPreferenceBean _prefs = _prefsManager.getPreferences();
        
        if (_prefs.isInstallCustomQueryTokenizer()) {
            session.setQueryTokenizer(new SybaseQueryTokenizer(_prefs));
        }
    }
    
	
	public synchronized void initialize() throws PluginException
	{
		_resources = new PluginResources("net.sourceforge.squirrel_sql.plugins.SybaseASE.SybaseASE", this);
        _prefsManager = new PluginQueryTokenizerPreferencesManager();
        _prefsManager.initialize(this, new SybasePreferenceBean());

	}


	
	public PluginSessionCallback sessionStarted(ISession session)
	{
	    if (!isPluginSession(session)) {
	        return null;
	    }
        installSybaseQueryTokenizer(session);
        String stmtSep = session.getQueryTokenizer().getSQLStatementSeparator();

	    
	    IObjectTreeAPI otApi = session.getSessionInternalFrame().getObjectTreeAPI();
	    otApi.addToPopup(DatabaseObjectType.VIEW, new ScriptSybaseASEViewAction(getApplication(), _resources, session));
	    otApi.addToPopup(DatabaseObjectType.PROCEDURE, new ScriptSybaseASEProcedureAction(getApplication(), _resources, session));

        otApi.addDetailTab(DatabaseObjectType.VIEW, 
                new ViewSourceTab(i18n.SHOW_VIEW_SOURCE, stmtSep));
        
        TableWithChildNodesExpander tableExp = new TableWithChildNodesExpander();
        tableExp.setTableIndexExtractor(new SybaseTableIndexExtractorImpl());
        tableExp.setTableTriggerExtractor(new SybaseTableTriggerExtractorImpl());
        otApi.addExpander(DatabaseObjectType.TABLE, tableExp);
        
        otApi.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
        
        otApi.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
        otApi.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO, new DatabaseObjectInfoTab());
        
        otApi.addDetailTab(DatabaseObjectType.TRIGGER, 
                           new TriggerSourceTab(i18n.TRIGGER_HINT, stmtSep));

        
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
        return DialectFactory.isSyBase(session.getMetaData());
    }
    
}
