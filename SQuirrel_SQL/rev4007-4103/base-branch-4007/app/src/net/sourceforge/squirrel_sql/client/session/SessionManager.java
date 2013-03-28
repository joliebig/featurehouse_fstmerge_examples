package net.sourceforge.squirrel_sql.client.session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.session.event.ISessionListener;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class SessionManager
{
   
   private static final ILogger s_log =
      LoggerController.createLogger(SessionManager.class);

   
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SessionManager.class);

   
   private final IApplication _app;

   private ISession _activeSession;

   
   private final LinkedList<ISession> _sessionsList = new LinkedList<ISession>();

   
   private final Map<IIdentifier, ISession> _sessionsById = new HashMap<IIdentifier, ISession>();

   private EventListenerList listenerList = new EventListenerList();

   
   private final IntegerIdentifierFactory _idFactory = new IntegerIdentifierFactory(1);
   private ArrayList<IAllowedSchemaChecker> _allowedSchemaCheckers = new ArrayList<IAllowedSchemaChecker>();
   private Hashtable<IIdentifier, String[]> _allowedSchemasBySessionID = new Hashtable<IIdentifier, String[]>();

   
   public SessionManager(IApplication app)
   {
      super();
      if (app == null)
      {
         throw new IllegalArgumentException("IApplication == null");
      }

      _app = app;
   }

   
   public synchronized ISession createSession(IApplication app,
                                              ISQLDriver driver, SQLAlias alias,
                                              SQLConnection conn, String user,
                                              String password)
   {
      if (app == null)
      {
         throw new IllegalArgumentException("null IApplication passed");
      }
      if (driver == null)
      {
         throw new IllegalArgumentException("null ISQLDriver passed");
      }
      if (alias == null)
      {
         throw new IllegalArgumentException("null ISQLAlias passed");
      }
      if (conn == null)
      {
         throw new IllegalArgumentException("null SQLConnection passed");
      }

      final Session sess = new Session(app, driver, alias, conn, user,
                              password, _idFactory.createIdentifier());
      _sessionsList.addLast(sess);
      _sessionsById.put(sess.getIdentifier(), sess);

      fireSessionAdded(sess);
      setActiveSession(sess);

      return sess;
   }

   public void setActiveSession(ISession session)
   {
      if (session != _activeSession)
      {
         _activeSession = session;
         fireSessionActivated(session);
      }
   }

   
   public synchronized ISession[] getConnectedSessions()
   {
      return _sessionsList.toArray(new ISession[_sessionsList.size()]);
   }

   
   public synchronized ISession getActiveSession()
   {
      return _activeSession;
   }

   
   public synchronized ISession getNextSession(ISession session)
   {
      final int sessionCount = _sessionsList.size();
      int idx = _sessionsList.indexOf(session);
      if (idx != -1)
      {
         ++idx;
         if (idx >= sessionCount)
         {
            idx = 0;
         }
         return _sessionsList.get(idx);
      }

      s_log.error("SessionManager.getNextSession()-> Session " +
               session.getIdentifier() + " not found in _sessionsList");
      if (sessionCount > 0)
      {
         s_log.error("SessionManager.getNextSession()-> Returning first session");
         return _sessionsList.getFirst();
      }
      s_log.error("SessionManager.getNextSession()-> List empty so returning passed session");
      return session;
   }

   
   public synchronized ISession getPreviousSession(ISession session)
   {
      final int sessionCount = _sessionsList.size();
      int idx = _sessionsList.indexOf(session);
      if (idx != -1)
      {
         --idx;
         if (idx < 0)
         {
            idx = sessionCount - 1;
         }
         return _sessionsList.get(idx);
      }

      s_log.error("SessionManager.getPreviousSession()-> Session " +
               session.getIdentifier() + " not found in _sessionsList");
      if (sessionCount > 0)
      {
         s_log.error("SessionManager.getPreviousSession()-> Returning last session");
         return _sessionsList.getLast();
      }
      s_log.error("SessionManager.getPreviousSession()-> List empty so returning passed session");
      return session;
   }

   
   public ISession getSession(IIdentifier sessionID)
   {
      return _sessionsById.get(sessionID);
   }

   
   public synchronized boolean closeSession(ISession session)
   {
      if (session == null)
      {
         throw new IllegalArgumentException("ISession == null");
      }

      try
      {
         if (confirmClose(session))
         {
            
            session.getApplication().getPluginManager().sessionEnding(session);

            fireSessionClosing(session);
            try {
            	session.close();
            } catch (SQLException sqle) {
                s_log.error("Error closing Session", sqle);
                session.showErrorMessage(s_stringMgr.getString("SessionManager.ErrorClosingSession", sqle));
            }
            fireSessionClosed(session);

            final IIdentifier sessionId = session.getIdentifier();
            if (!_sessionsList.remove(session))
            {
               s_log.error("SessionManager.closeSession()-> Session " +
                     sessionId +
                     " not found in _sessionsList when trying to remove it.");
            }
            if (_sessionsById.remove(sessionId) == null)
            {
               s_log.error("SessionManager.closeSession()-> Session " +
                     sessionId +
                     " not found in _sessionsById when trying to remove it.");
            }

            if (_sessionsList.isEmpty())
            {
               fireAllSessionsClosed();
            }

            
            
            if (session == _activeSession)
            {
               if (!_sessionsList.isEmpty())
               {
                  setActiveSession(_sessionsList.getLast());
               }
               else
               {
                  _activeSession = null;
               }
            }

            _allowedSchemasBySessionID.remove(session.getIdentifier());

            return true;
         }
      }
      catch (Throwable ex)
      {
         s_log.error("Error closing Session", ex);
         session.showErrorMessage(s_stringMgr.getString("SessionManager.ErrorClosingSession", ex));
      }

      return false;
   }

   
   synchronized public boolean closeAllSessions()
   {
      
      
      final ISession[] sessions = getConnectedSessions();
      for (int i = sessions.length - 1; i >= 0; i--)
      {
         if (!closeSession(sessions[i]))
         {
            return false;
         }
      }
      return true;
   }

   
   public void addSessionListener(ISessionListener lis)
   {
      if (lis != null)
      {
         listenerList.add(ISessionListener.class, lis);
      }
      else
      {
         s_log.error("Attempted to add null listener: SessionManager.addSessionListener");
      }
   }

   
   public void removeSessionListener(ISessionListener lis)
   {
      if (lis != null)
      {
         listenerList.remove(ISessionListener.class, lis);
      }
      else
      {
         s_log.error("Attempted to remove null listener: SessionManager.addSessionListener");
      }
   }

   
   protected void fireSessionAdded(ISession session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            
            if (evt == null)
               evt = new SessionEvent(session);
            ((ISessionListener)listeners[i + 1]).sessionConnected(evt);
         }
      }
   }

   
   protected void fireSessionClosed(ISession session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            
            if (evt == null)
               evt = new SessionEvent(session);
            ((ISessionListener)listeners[i + 1]).sessionClosed(evt);
         }
      }
   }

   
   protected void fireSessionClosing(ISession session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            
            if (evt == null)
            {
               evt = new SessionEvent(session);
            }
            ((ISessionListener)listeners[i + 1]).sessionClosing(evt);
         }
      }
   }

   
   protected void fireAllSessionsClosed()
   {
      Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            ((ISessionListener)listeners[i + 1]).allSessionsClosed();
         }
      }
   }

   
   protected void fireSessionActivated(ISession session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            
            if (evt == null)
               evt = new SessionEvent(session);
            ((ISessionListener)listeners[i + 1]).sessionActivated(evt);
         }
      }
   }

   
   private boolean confirmClose(ISession session)
   {
      if (!_app.getSquirrelPreferences().getConfirmSessionClose())
      {
            return session.confirmClose();
        }

      final String msg = s_stringMgr.getString("SessionManager.confirmClose",
                     session.getTitle());
      if (!Dialogs.showYesNo(_app.getMainFrame(), msg)) {
            return false;
        } else {
            return session.confirmClose();
        }
   }

   protected void fireConnectionClosedForReconnect(Session session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            
            if (evt == null)
               evt = new SessionEvent(session);
            ((ISessionListener)listeners[i + 1]).connectionClosedForReconnect(evt);
         }
      }
   }

   protected void fireReconnected(Session session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            
            if (evt == null)
               evt = new SessionEvent(session);
            ((ISessionListener)listeners[i + 1]).reconnected(evt);
         }
      }
   }

   protected void fireReconnectFailed(Session session)
   {
      Object[] listeners = listenerList.getListenerList();
      SessionEvent evt = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ISessionListener.class)
         {
            
            if (evt == null)
               evt = new SessionEvent(session);
            ((ISessionListener)listeners[i + 1]).reconnectFailed(evt);
         }
      }
   }


   protected void fireSessionFinalized(final IIdentifier sessionIdentifier)
   {
      
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2)
            {
               if (listeners[i] == ISessionListener.class)
               {
                  ((ISessionListener)listeners[i + 1]).sessionFinalized(sessionIdentifier);
               }
            }
         }
      });

   }

   public void addAllowedSchemaChecker(IAllowedSchemaChecker allowedSchemaChecker)
   {
      _allowedSchemaCheckers.add(allowedSchemaChecker);
   }


   public boolean areAllSchemasAllowed(ISession session)
   {
      try
      {
         String[] allowedSchemas = getAllowedSchemas(session);
         String[] schemas = session.getSQLConnection().getSQLMetaData().getSchemas();

         return allowedSchemas.length == schemas.length;
      }
      catch (SQLException e)
      {
         s_log.error("Failed to check allowed Schemas", e);
         return true;
      }
   }

   public String[] getAllowedSchemas(ISession session)
   {
      String[] allowedSchemas = _allowedSchemasBySessionID.get(session.getIdentifier());
      if(null == allowedSchemas)
      {
         allowedSchemas = getAllowedSchemas(session.getSQLConnection(), session.getAlias());
         _allowedSchemasBySessionID.put(session.getIdentifier(), allowedSchemas);
      }

      return allowedSchemas;

   }


   
   public String[] getAllowedSchemas(ISQLConnection con, ISQLAliasExt alias)
   {
      try
      {
         
         HashMap<String, Object> uniqueAllowedSchemas = null;

         for (int i = 0; i < _allowedSchemaCheckers.size(); i++)
         {
            String[] allowedSchemas = null;
            try
            {
               allowedSchemas = (_allowedSchemaCheckers.get(i)).getAllowedSchemas(con, alias);
            }
            catch (Exception e)
            {
               s_log.error("Failed to get allowed Schemas from Plugin", e);
            }

            if(null != allowedSchemas)
            {
               if(null == uniqueAllowedSchemas)
               {
                  uniqueAllowedSchemas = new HashMap<String, Object>();
               }

               for (int j = 0; j < allowedSchemas.length; j++)
               {
                  uniqueAllowedSchemas.put(allowedSchemas[j], null);
               }
            }
         }

         if(null == uniqueAllowedSchemas)
         {
            return con.getSQLMetaData().getSchemas();
         }
         else
         {
            ArrayList<String> list = new ArrayList<String>(uniqueAllowedSchemas.keySet());
            Collections.sort(list);
            return list.toArray(new String[list.size()]);
         }
      }
      catch (Exception e)
      {
         s_log.error("Failed to get allowed Schemas", e);
         return new String[0];
      }
   }

   public void clearAllowedSchemaCache(ISession session)
   {
      _allowedSchemasBySessionID.remove(session.getIdentifier());
   }
}
