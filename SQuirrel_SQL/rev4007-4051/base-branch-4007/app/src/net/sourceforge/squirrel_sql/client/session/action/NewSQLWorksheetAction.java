package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class NewSQLWorksheetAction extends SquirrelAction implements ISessionAction
{
   private ISession _session;

   
   public NewSQLWorksheetAction(IApplication app)
   {
      super(app);
      if (app == null)
      {
         throw new IllegalArgumentException("Null IApplication passed");
      }

      setEnabled(false);
   }

	
	public void actionPerformed(ActionEvent evt)
	{
		getApplication().getWindowManager().createSQLInternalFrame(_session);
	}

   public void setSession(ISession session)
   {
      _session = session;
      GUIUtils.processOnSwingEventThread(new Runnable() {
          public void run() {
              setEnabled(null != _session);
          }
      });
   }
}
