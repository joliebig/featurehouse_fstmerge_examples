
package genj.edit;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.view.ViewContext;

import javax.swing.JPanel;


 abstract class Editor extends JPanel {

  
  public abstract void init(Gedcom gedcom, EditView view, Registry registry);

  
  public abstract ViewContext getContext();
  
  
  public abstract void setContext(Context context);
  
  
  public abstract void commit();
  
} 
