
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.util.Trackable;
import genj.view.View;

public class WorkbenchAdapter implements WorkbenchListener {

	public void commitRequested(Workbench workbench) {
	}

	public void gedcomClosed(Workbench workbench, Gedcom gedcom) {
	}

	public void gedcomOpened(Workbench workbench, Gedcom gedcom) {
	}

	public void selectionChanged(Workbench workbench, Context context, boolean isActionPerformed) {
	}

	public void viewClosed(Workbench workbench, View view) {
	}
	
	public void viewRestored(Workbench workbench, View view) {
	}

	public void viewOpened(Workbench workbench, View view) {
	}

	public boolean workbenchClosing(Workbench workbench) {
		return true;
	}

  public void processStarted(Workbench workbench, Trackable process) {
  }

  public void processStopped(Workbench workbench, Trackable process) {
  }

}
