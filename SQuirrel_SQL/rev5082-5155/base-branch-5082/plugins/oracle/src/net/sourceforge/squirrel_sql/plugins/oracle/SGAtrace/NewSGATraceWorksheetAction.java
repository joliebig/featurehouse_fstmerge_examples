package net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace;

import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

import net.sourceforge.squirrel_sql.client.session.ISession;



public class NewSGATraceWorksheetAction extends SquirrelAction {
    private Resources _resources;

	
	public NewSGATraceWorksheetAction(IApplication app, Resources rsrc) {
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


          final SGATraceInternalFrame sif = new SGATraceInternalFrame(activeSession, _resources);
          getApplication().getMainFrame().addWidget(sif);

          
          
          
          SwingUtilities.invokeLater(new Runnable()
          {
                  public void run()
                  {
                          sif.setVisible(true);
                  }
          });
	}
}
