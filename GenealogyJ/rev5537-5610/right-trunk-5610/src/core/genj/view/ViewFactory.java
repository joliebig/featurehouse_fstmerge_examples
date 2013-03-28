
package genj.view;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.swing.ImageIcon;

import javax.swing.JComponent;


public interface ViewFactory {

  
  public JComponent createView(String title, Gedcom gedcom, Registry registry, ViewManager manager);
  
  
  public ImageIcon getImage();
  
  
  public String getTitle(boolean abbreviate);
  
} 
