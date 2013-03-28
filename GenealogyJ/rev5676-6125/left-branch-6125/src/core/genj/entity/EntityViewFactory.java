
package genj.entity;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.swing.ImageIcon;
import genj.view.ViewFactory;
import genj.view.ViewManager;

import javax.swing.JComponent;


public class EntityViewFactory implements ViewFactory {

  
  public JComponent createView(String title, Gedcom gedcom, Registry registry, ViewManager manager) {
    return new EntityView(title, gedcom, registry, manager);
  }

  
  public ImageIcon getImage() {
    return new ImageIcon(this, "images/View");
  }

  
  public String getTitle(boolean abbreviate) {
    return EntityView.resources.getString("title" + (abbreviate?".short":""));
  }

} 
