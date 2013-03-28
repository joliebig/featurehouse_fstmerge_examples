package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class RollbackAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;
   private PropertyChangeListener _propertyListener;

   public RollbackAction(IApplication app)
   {
      super(app);

      _propertyListener = new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            if (SessionProperties.IPropertyNames.AUTO_COMMIT.equals(evt.getPropertyName()))
            {
               Boolean autoCom = (Boolean) evt.getNewValue();
               setEnabled(false == autoCom.booleanValue());
            }
         }
      };

      setEnabled(false);

   }


   public void setSession(ISession session)
   {
      _session = session;

      if(null != _session)
      {
         _session.getProperties().removePropertyChangeListener(_propertyListener);
      }
      _session = session;

      if (session == null)
      {
         setEnabled(false);
      }
      else
      {
         setEnabled(false == _session.getProperties().getAutoCommit());
         _session.getProperties().addPropertyChangeListener(_propertyListener);
      }
   }

   public void actionPerformed(ActionEvent evt)
   {
      CursorChanger cursorChg = new CursorChanger(getApplication().getMainFrame());
      cursorChg.show();
      try
      {
         IPlugin plugin = _session.getApplication().getDummyAppPlugin();
         _session.rollback();
      }
      finally
      {
         cursorChg.restore();
      }
   }
}
