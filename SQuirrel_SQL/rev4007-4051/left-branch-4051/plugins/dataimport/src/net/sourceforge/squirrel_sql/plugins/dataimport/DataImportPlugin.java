package net.sourceforge.squirrel_sql.plugins.dataimport;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.plugins.dataimport.action.ImportTableDataAction;
import net.sourceforge.squirrel_sql.plugins.dataimport.prefs.PreferencesManager;


public class DataImportPlugin extends DefaultSessionPlugin {
	private Resources resources = null;

	
	public String getInternalName() {
		return "dataimport";
	}

	
	public String getDescriptiveName() {
		return "Data Import Plugin";
	}

	
	public String getVersion() {
		return "0.02";
	}

	
	public String getAuthor() {
		return "Thorsten Mürell";
	}

	@Override
	public String getContributors() {
		return "";
	}


	@Override
	public String getChangeLogFileName() {
		return "changes.txt";
	}

	@Override
	public String getLicenceFileName() {
		return "licence.txt";
	}
	
	@Override
	public String getHelpFileName() {
		return "readme.html";
	}

	@Override
	public void load(IApplication app) throws PluginException {
		super.load(app);
		resources = new Resources(getClass().getName(), this);
	}

	
	@Override
	public synchronized void initialize() throws PluginException {
		super.initialize();
		
		PreferencesManager.initialize(this);

		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		coll.add(new ImportTableDataAction(app, resources));
	}

	
	@Override
	public void unload() {
		super.unload();
		PreferencesManager.unload();
	}

	@Override
	public boolean allowsSessionStartedInBackground()
	{
		return true;
	}

	
	public PluginSessionCallback sessionStarted(final ISession session) {
		updateTreeApi(session);
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

	private void updateTreeApi(ISession session) {
		IObjectTreeAPI treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
		final ActionCollection coll = getApplication().getActionCollection();

		treeAPI.addToPopup(DatabaseObjectType.TABLE, coll.get(ImportTableDataAction.class));        
	}

	
	@Override
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
		
        
        
        return new IGlobalPreferencesPanel[] { };
	}
}