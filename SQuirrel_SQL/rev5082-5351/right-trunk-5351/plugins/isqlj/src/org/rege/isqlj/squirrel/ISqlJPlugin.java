package org.rege.isqlj.squirrel;



import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.ISession;


public class ISqlJPlugin extends DefaultSessionPlugin
{
	private interface IMenuResourceKeys 
	{
		String SCRIPTS = "isqlj";
	}

	private PluginResources resources;

    public ISqlJPlugin()
    {
    }

	
	public synchronized void initialize() 
			throws PluginException 
	{
		super.initialize();
		IApplication app = getApplication();

		resources = new ISqlJPluginResources("org.rege.isqlj.squirrel.ISqlJ", this);

		ActionCollection coll = app.getActionCollection();
		coll.add( new ExecuteISqlJAction(app, resources, this));
		createMenu();
	}

	public PluginSessionCallback sessionStarted(ISession session)
	{

      PluginSessionCallback ret = new PluginSessionCallback()
      {
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
         {
            
            
         }

         public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
         {
            
            
         }
      };
      return ret;
      
	}


	public void unload() 
	{
		super.unload();
	}

	private void createMenu() 
	{
		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		JMenu menu = resources.createMenu( IMenuResourceKeys.SCRIPTS);
		resources.addToMenu(coll.get(ExecuteISqlJAction.class), menu);

		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
	}
	
	public String getInternalName()
	{
		return "sqsc-isqlj";
	}

	public String getDescriptiveName()
	{
		return "Plugin for iSqlJ Interpreter";
	}

	public String getAuthor()
	{
		return "Stathis Alexopoulos";
	}

	public String getContributors()
	{
		return "";
	}

	public String getWebSite()
	{
		return "http://www.rege.org/isqlj";
	}

	public String getVersion()
	{
		return "0.10";
	}

	public String getHelpFileName()
	{
		return "myHelp.txt";
	}

	public String getChangeLogFileName()
	{
		return "myLog.txt";
	}

	public String getLicenceFileName()
	{
		return "myLicence.txt";
	}
}

