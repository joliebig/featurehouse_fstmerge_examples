package net.sourceforge.squirrel_sql.plugins.oracle.invalidobjects;

import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

import net.sourceforge.squirrel_sql.client.session.ISession;



public class NewInvalidObjectsWorksheetAction extends SquirrelAction {
    private Resources _resources;

	
	public NewInvalidObjectsWorksheetAction(IApplication app, Resources rsrc) {
		super(app, rsrc);
                _resources = rsrc;

		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
	}

	
	public void actionPerformed(ActionEvent evt) {
          ISession activeSession = getApplication().getSessionManager().getActiveSession();
          if (activeSession == null)
            throw new IllegalArgumentException("This method should not be called with a null activeSession");


          final InvalidObjectsInternalFrame sif = new InvalidObjectsInternalFrame(activeSession, _resources);
          getApplication().getMainFrame().addInternalFrame(sif, true, null);

          
          
          
          SwingUtilities.invokeLater(new Runnable()
          {
                  public void run()
                  {
                          sif.setVisible(true);
                  }
          });
	}
}
