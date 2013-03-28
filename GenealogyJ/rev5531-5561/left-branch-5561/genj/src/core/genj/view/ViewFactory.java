
package genj.view;

import genj.gedcom.Context;
import genj.util.Registry;
import genj.util.swing.ImageIcon;



public interface ViewFactory {
  
  
  public View createView(String title, Registry registry, Context context);
  
  
  public ImageIcon getImage();
  
  
  public String getTitle();
  
} 
