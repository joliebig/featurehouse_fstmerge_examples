
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.util.Trackable;
import genj.util.swing.ImageIcon;
import genj.view.View;

import javax.swing.JComponent;

import swingx.docking.DefaultDockable;
import swingx.docking.Docked;


 class GedcomDockable extends DefaultDockable implements WorkbenchListener {
  
  private Workbench workbench;
  
   GedcomDockable(Workbench workbench, String title, ImageIcon img, JComponent content) {
    this.workbench = workbench;
    setContent(content);
    setTitle(title);
    setIcon(img);
  }
  
  @Override
  public void docked(Docked docked) {
    super.docked(docked);
    workbench.addWorkbenchListener(this);
  }
  
  @Override
  public void undocked() {
    super.undocked();
    workbench.removeWorkbenchListener(this);
  }

  public void commitRequested(Workbench workbench) {
  }

  public void gedcomClosed(Workbench workbench, Gedcom gedcom) {
    workbench.closeDockable(this);
  }

  public void gedcomOpened(Workbench workbench, Gedcom gedcom) {
  }

  public void processStarted(Workbench workbench, Trackable process) {
  }

  public void processStopped(Workbench workbench, Trackable process) {
  }

  public void selectionChanged(Workbench workbench, Context context, boolean isActionPerformed) {
  }

  public void viewClosed(Workbench workbench, View view) {
  }
  
  public void viewOpened(Workbench workbench, View view) {
  }

  public void workbenchClosing(Workbench workbench) {
  }
}