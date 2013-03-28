package net.sourceforge.squirrel_sql.plugins.editextras;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;

public class EditExtrasPlugin extends DefaultSessionPlugin
{
	
	private final static ILogger
			s_log = LoggerController.createLogger(EditExtrasPlugin.class);

	private interface IMenuResourceKeys 
	{
		String MENU = "editextras";
	}

	
	static final String USER_PREFS_FILE_NAME = "prefs.xml";

	
	private Resources _resources;

	
	private ISQLPanelListener _lis = new SQLPanelListener();


	
	public String getInternalName()
	{
		return "editextras";
	}

	
	public String getDescriptiveName()
	{
		return "SQL Entry Area Enhancements";
	}

	
	public String getVersion()
	{
		return "1.0.1";
	}

	
	public String getAuthor()
	{
		return "Gerd Wagner";
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

	
	public void initialize() throws PluginException
	{
		super.initialize();

		final IApplication app = getApplication();

		
		_resources = new Resources(this);

		createMenu();
	}

   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   
	public PluginSessionCallback sessionStarted(ISession session)
	{
      ISQLPanelAPI sqlPanelAPI = FrameWorkAcessor.getSQLPanelAPI(session, this);
      initEditExtras(sqlPanelAPI);

      PluginSessionCallback ret = new PluginSessionCallback()
      {
         public void sqlInternalFrameOpened(final SQLInternalFrame sqlInternalFrame, 
                                            final ISession sess)
         {
             initEditExtras(sqlInternalFrame.getSQLPanelAPI());         
         }

         public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
         {
         }
      };

      return ret;
	}

   private void initEditExtras(final ISQLPanelAPI sqlPanelAPI)
   {
       GUIUtils.processOnSwingEventThread(new Runnable() {
           public void run() {
               sqlPanelAPI.addSQLPanelListener(_lis);
               createSQLEntryAreaPopMenuItems(sqlPanelAPI);

               ActionCollection actions = getApplication().getActionCollection();
               sqlPanelAPI.addToToolsPopUp("quote", actions.get(InQuotesAction.class));
               sqlPanelAPI.addToToolsPopUp("unquote", actions.get(RemoveQuotesAction.class));
               sqlPanelAPI.addToToolsPopUp("quotesb", actions.get(ConvertToStringBufferAction.class));
               sqlPanelAPI.addToToolsPopUp("format", actions.get(FormatSQLAction.class));
               sqlPanelAPI.addToToolsPopUp("date", actions.get(EscapeDateAction.class));
               sqlPanelAPI.addToToolsPopUp("sqlcut", actions.get(CutSqlAction.class));
               sqlPanelAPI.addToToolsPopUp("sqlcopy", actions.get(CopySqlAction.class));
           }
       });
   }

   
	public void sessionEnding(ISession session)
	{
      ISessionWidget[] frames =
         session.getApplication().getWindowManager().getAllFramesOfSession(session.getIdentifier());

      for (int i = 0; i < frames.length; i++)
      {
         if(frames[i] instanceof SQLInternalFrame)
         {
            ((SQLInternalFrame)frames[i]).getSQLPanelAPI().removeSQLPanelListener(_lis);
         }

         if(frames[i] instanceof SessionInternalFrame)
         {
            ((SessionInternalFrame)frames[i]).getSQLPanelAPI().removeSQLPanelListener(_lis);
         }
      }

		super.sessionEnding(session);
	}

	
	public PluginResources getResources()
	{
		return _resources;
	}

   private void createMenu()
	{
		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		JMenu menu = _resources.createMenu(IMenuResourceKeys.MENU);
		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);

		Action act = new InQuotesAction(app, this);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new RemoveQuotesAction(app, this);
		coll.add(act);
		_resources.addToMenu(act, menu);

        act = new RemoveNewLinesAction(app, this);
        coll.add(act);
        _resources.addToMenu(act, menu);
        
		act = new ConvertToStringBufferAction(app, this);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new FormatSQLAction(app, this);
		coll.add(act);
		_resources.addToMenu(act, menu);


      act = new EscapeDateAction(getApplication(), _resources);
      coll.add(act);
      _resources.addToMenu(act, menu);

		act = new CutSqlAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new CopySqlAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

	}

	private void createSQLEntryAreaPopMenuItems(ISQLPanelAPI api)
	{
		JMenuItem mnu;

		ActionCollection actions = getApplication().getActionCollection();
		api.addToSQLEntryAreaMenu(actions.get(InQuotesAction.class));
		api.addToSQLEntryAreaMenu(actions.get(RemoveQuotesAction.class));
		api.addToSQLEntryAreaMenu(actions.get(ConvertToStringBufferAction.class));

		
		mnu = api.addToSQLEntryAreaMenu(actions.get(FormatSQLAction.class));
		_resources.configureMenuItem(actions.get(FormatSQLAction.class), mnu);        
        
        mnu = api.addToSQLEntryAreaMenu(actions.get(RemoveNewLinesAction.class));
        _resources.configureMenuItem(actions.get(RemoveNewLinesAction.class), mnu);                
        
		api.addToSQLEntryAreaMenu(actions.get(EscapeDateAction.class));

		mnu = api.addToSQLEntryAreaMenu(actions.get(CutSqlAction.class));
		_resources.configureMenuItem(actions.get(CutSqlAction.class), mnu);

		mnu = api.addToSQLEntryAreaMenu(actions.get(CopySqlAction.class));
		_resources.configureMenuItem(actions.get(CopySqlAction.class), mnu);

	}

	private class SQLPanelListener extends SQLPanelAdapter
	{
		public void sqlEntryAreaReplaced(SQLPanelEvent evt)
		{
			createSQLEntryAreaPopMenuItems(evt.getSQLPanel());
		}
	}


   public Object getExternalService()
   {
      return new EditExtrasExternalServiceImpl();
   }
}
