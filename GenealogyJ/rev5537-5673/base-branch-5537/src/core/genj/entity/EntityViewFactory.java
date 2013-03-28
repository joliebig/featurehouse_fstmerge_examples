
package genj.entity;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.swing.ImageIcon;
import genj.view.ViewFactory;

import javax.swing.JComponent;


public class EntityViewFactory implements ViewFactory {

  
  public JComponent createView(String title, Gedcom gedcom, Registry registry) {
    return new EntityView(title, gedcom, registry);
  }

  
  public ImageIcon getImage() {
    return new ImageIcon(this, "images/View");
  }

  
  public String getTitle() {
    return EntityView.resources.getString("title");
  }

} 
