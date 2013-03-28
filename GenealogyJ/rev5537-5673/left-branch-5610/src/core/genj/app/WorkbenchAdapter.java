
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.view.View;

public class WorkbenchAdapter implements WorkbenchListener {

	public void commitRequested() {
	}

	public void gedcomClosed(Gedcom gedcom) {
	}

	public void gedcomOpened(Gedcom gedcom) {
	}

	public void selectionChanged(Context context, boolean isActionPerformed) {
	}

	public void viewClosed(View view) {
	}

	public void viewOpened(View view) {
	}

	public boolean workbenchClosing() {
		return true;
	}

}
