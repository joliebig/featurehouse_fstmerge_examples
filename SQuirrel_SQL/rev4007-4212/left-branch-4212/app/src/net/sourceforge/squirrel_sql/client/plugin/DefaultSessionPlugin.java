package net.sourceforge.squirrel_sql.client.plugin;

import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public abstract class DefaultSessionPlugin extends DefaultPlugin
											implements ISessionPlugin
{
    
    private JMenu sessionMenu = null;
    
    
    private static final ILogger s_log = 
        LoggerController.createLogger(DefaultSessionPlugin.class); 
    
	
	public void sessionCreated(ISession session)
	{
		
	}

   public boolean allowsSessionStartedInBackground()
   {
      return false;
   }

   
	public void sessionEnding(ISession session)
	{
		
	}

	
	public ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session)
	{
		return null;
	}

	
	public IPluginDatabaseObjectType[] getObjectTypes(ISession session)
	{
		return null;
	}

	
	public INodeExpander getDefaultNodeExpander(ISession session, DatabaseObjectType type)
	{
		return null;
	}

    
    protected boolean isPluginSession(ISession session) {
        if (s_log.isDebugEnabled() && sessionMenu != null) {
            s_log.debug(
                "The default isPluginSession() impl was called for session \""+
                session.getAlias().getName()+"\", but sessionMenu ("+
                sessionMenu.getText()+")is not null - this is probably a bug.");
        }
        return true;
    }
    
    
    protected void registerSessionMenu(JMenu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("menu cannot be null");
        }
        sessionMenu = menu;
        _app.getSessionManager().addSessionListener(new SessionListener());
    }
    
    
    private class SessionListener extends SessionAdapter {
        public void sessionActivated(SessionEvent evt) {
            final ISession session = evt.getSession();
            EnableMenuTask task = new EnableMenuTask(session);
            session.getApplication().getThreadPool().addTask(task);
        }
    }
    
    
    private class EnableMenuTask implements Runnable {
        
        private ISession _session = null;
        
        public EnableMenuTask(ISession session) {
            _session = session;
        }
        
        public void run() {
            final boolean enable = isPluginSession(_session);
            GUIUtils.processOnSwingEventThread(new Runnable() {
                public void run() {
                    sessionMenu.setEnabled(enable);
                }
            });                        
        }
    }
}
