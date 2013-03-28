
package net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.control;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.ui.ControlFileGenerationFrame;


public class GenerateControlFileAction extends SquirrelAction {

	private static final long serialVersionUID = 1L;

	private ISession session;

	
	public GenerateControlFileAction(IApplication application) {
		super(application);
	}

	
	public GenerateControlFileAction(IApplication application,
			Resources resources) {
		super(application, resources);
	}

	
	public GenerateControlFileAction(IApplication application,
			Resources properties, ISession session) {
		super(application, properties);
		this.session = session;
	}

	
	public void actionPerformed(ActionEvent e) {
		ControlFileGenerationFrame dialog = new ControlFileGenerationFrame("Control file generation settings", session);
		ControlFileGenerationFrame.centerWithinDesktop(dialog);
		dialog.setVisible(true);
	}
}
