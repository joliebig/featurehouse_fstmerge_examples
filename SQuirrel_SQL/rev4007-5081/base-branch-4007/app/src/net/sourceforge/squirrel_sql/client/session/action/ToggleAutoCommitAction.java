package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.ToggleComponentHolder;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


public class ToggleAutoCommitAction extends SquirrelAction implements ISessionAction, IToggleAction
{
   static final long serialVersionUID = 4894924988552643833L;
      
   private ISession _session;
   private PropertyChangeListener _propertyListener;
   private boolean _inActionPerformed;
   private ToggleComponentHolder _toogleComponentHolder;

   public ToggleAutoCommitAction(IApplication app)
   {
      super(app);

      _toogleComponentHolder = new ToggleComponentHolder();

      _propertyListener = new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            if (SessionProperties.IPropertyNames.AUTO_COMMIT.equals(evt.getPropertyName()))
            {
               Boolean autoCom = (Boolean) evt.getNewValue();
               _toogleComponentHolder.setSelected(autoCom.booleanValue());
            }
         }
      };

      setEnabled(false);
   }

   public void setSession(final ISession session)
   {
      if(null != _session)
      {
         _session.getProperties().removePropertyChangeListener(_propertyListener);
      }
      _session = session;

      GUIUtils.processOnSwingEventThread(new Runnable() {
          public void run() {
              if (session == null || session.getProperties() == null) {
                  setEnabled(false);
                 _toogleComponentHolder.setSelected(false);
              } else {
                 SessionProperties props = session.getProperties();
                 setEnabled(true);
                 props.addPropertyChangeListener(_propertyListener);
                 _toogleComponentHolder.setSelected(props.getAutoCommit());
              }
          }
      });

   }

   public ToggleComponentHolder getToggleComponentHolder()
   {
      return _toogleComponentHolder;
   }


   public void actionPerformed(ActionEvent evt)
   {
      try
      {
         if(_inActionPerformed)
         {
            return;
         }

         _inActionPerformed = true;
         _session.getProperties().setAutoCommit(_toogleComponentHolder.isSelected());
      }
      finally
      {
         _inActionPerformed = false;
      }
   }
}
