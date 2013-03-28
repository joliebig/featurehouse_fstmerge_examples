
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;


public interface WorkbenchListener {

  
  public void selectionChanged(Context context, boolean isActionPerformed);

  
  public void commitRequested();
  
  
  public boolean workbenchClosing();
  
  
  public void gedcomClosed(Gedcom gedcom);
  
  
  public void gedcomOpened(Gedcom gedcom);

}
