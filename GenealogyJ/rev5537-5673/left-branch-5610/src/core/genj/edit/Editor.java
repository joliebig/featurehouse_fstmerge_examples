
package genj.edit;

import genj.gedcom.Context;
import genj.view.ViewContext;

import javax.swing.JPanel;


 abstract class Editor extends JPanel {

  
  public abstract ViewContext getContext();
  
  
  public abstract void setContext(Context context);
  
  
  public abstract void commit();
  
} 
