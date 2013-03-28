package org.firebirdsql.squirrel;

import javax.swing.JMenu;

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
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import org.firebirdsql.squirrel.act.ActivateIndexAction;
import org.firebirdsql.squirrel.act.DeactivateIndexAction;
import org.firebirdsql.squirrel.exp.DatabaseExpander;
import org.firebirdsql.squirrel.exp.FirebirdTableIndexExtractorImpl;
import org.firebirdsql.squirrel.exp.FirebirdTableTriggerExtractorImpl;
import org.firebirdsql.squirrel.exp.AllIndexesParentExpander;
import org.firebirdsql.squirrel.tab.DomainDetailsTab;
import org.firebirdsql.squirrel.tab.GeneratorDetailsTab;
import org.firebirdsql.squirrel.tab.IndexInfoTab;
import org.firebirdsql.squirrel.tab.ProcedureSourceTab;
import org.firebirdsql.squirrel.tab.TriggerDetailsTab;
import org.firebirdsql.squirrel.tab.TriggerSourceTab;
import org.firebirdsql.squirrel.tab.ViewSourceTab;

public class FirebirdPlugin extends DefaultSessionPlugin {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(FirebirdPlugin.class);

    
    private IObjectTreeAPI _treeAPI;

	
	private PluginResources _resources;

	
	private JMenu _firebirdMenu;

    
    public String getInternalName()
    {
        return "firebird";
    }

    
    public String getDescriptiveName()
    {
        return "Firebird Plugin";
    }

    
    public String getVersion()
    {
        return "0.02";
    }

    
    public String getAuthor()
    {
        return "Roman Rokytskyy";
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
		_resources = new FirebirdResources(getClass().getName(), this);
	}

	
	public synchronized void initialize() throws PluginException
	{
		super.initialize();
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		coll.add(new ActivateIndexAction(app, _resources, this));
		coll.add(new DeactivateIndexAction(app, _resources, this));

		_firebirdMenu = createFirebirdMenu();
		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, _firebirdMenu);
        super.registerSessionMenu(_firebirdMenu);
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
        return DialectFactory.isFirebird(session.getMetaData());
    }
    
    private void updateTreeApi(ISession session) {
        _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();

        
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexInfoTab());

        
        _treeAPI.addExpander(IObjectTypes.INDEX_PARENT, new AllIndexesParentExpander());

        _treeAPI.addExpander(DatabaseObjectType.SESSION, new DatabaseExpander(this));

        TableWithChildNodesExpander tableExp = new TableWithChildNodesExpander();
        tableExp.setTableTriggerExtractor(new FirebirdTableTriggerExtractorImpl());
        tableExp.setTableIndexExtractor(new FirebirdTableIndexExtractorImpl());
        _treeAPI.addExpander(DatabaseObjectType.TABLE, tableExp);
        
        
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new GeneratorDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.DATATYPE, new DomainDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
        
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, 
                new TriggerSourceTab(s_stringMgr.getString("firebird.showTrigger")));
        
        _treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, 
                new ProcedureSourceTab(s_stringMgr.getString("firebird.showProcedureSource")));
        
        _treeAPI.addDetailTab(DatabaseObjectType.VIEW, 
                new ViewSourceTab(s_stringMgr.getString("firebird.showView")));


        final ActionCollection coll = getApplication().getActionCollection();
        _treeAPI.addToPopup(DatabaseObjectType.INDEX, coll.get(ActivateIndexAction.class));
        _treeAPI.addToPopup(DatabaseObjectType.INDEX, coll.get(DeactivateIndexAction.class));        
    }
    
	
	private JMenu createFirebirdMenu()
	{
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		final JMenu firebirdMenu = _resources.createMenu(FirebirdResources.IMenuResourceKeys.FIREBIRD);

		_resources.addToMenu(coll.get(ActivateIndexAction.class), firebirdMenu);
		_resources.addToMenu(coll.get(DeactivateIndexAction.class), firebirdMenu);

		return firebirdMenu;
	}

}
