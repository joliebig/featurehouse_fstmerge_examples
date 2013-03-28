
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.util.Trackable;
import genj.view.View;


public interface WorkbenchListener {

  
  public void selectionChanged(Workbench workbench, Context context, boolean isActionPerformed);
  
  
  public void processStarted(Workbench workbench, Trackable process);

  
  public void processStopped(Workbench workbench, Trackable process);

  
  public void commitRequested(Workbench workbench);
  
  
  public void workbenchClosing(Workbench workbench);
  
  
  public void gedcomClosed(Workbench workbench, Gedcom gedcom);
  
  
  public void gedcomOpened(Workbench workbench, Gedcom gedcom);
  
  
  public void viewOpened(Workbench workbench, View view);

  
  public void viewClosed(Workbench workbench, View view);

}
