
package genj.entity;

import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;



public class EntityViewFactory implements ViewFactory {

  
  public View createView() {
    return new EntityView();
  }

  
  public ImageIcon getImage() {
    return new ImageIcon(this, "images/View");
  }

  
  public String getTitle() {
    return EntityView.resources.getString("title");
  }

} 
