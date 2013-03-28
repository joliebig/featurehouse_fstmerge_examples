
package genj.entity;

import genj.gedcom.Context;
import genj.util.Registry;
import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;



public class EntityViewFactory implements ViewFactory {

  
  public View createView(String title, Registry registry, Context context) {
    return new EntityView(title, context, registry);
  }

  
  public ImageIcon getImage() {
    return new ImageIcon(this, "images/View");
  }

  
  public String getTitle() {
    return EntityView.resources.getString("title");
  }

} 
