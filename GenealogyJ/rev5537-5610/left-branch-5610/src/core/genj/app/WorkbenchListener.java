
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.view.View;


public interface WorkbenchListener {

  
  public void selectionChanged(Context context, boolean isActionPerformed);

  
  public void commitRequested();
  
  
  public boolean workbenchClosing();
  
  
  public void gedcomClosed(Gedcom gedcom);
  
  
  public void gedcomOpened(Gedcom gedcom);
  
  
  public void viewOpened(View view);

  
  public void viewClosed(View view);

}
