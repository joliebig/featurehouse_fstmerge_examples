
package genj.edit;

import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;




public class EditViewFactory implements ViewFactory {
  
  public final static String NAME = EditView.RESOURCES.getString("title");
    
  
  public View createView() {
    return new EditView();
  }

  
  public ImageIcon getImage() {
    return Images.imgView;
  }
  
  
  public String getTitle() {
    return NAME;
  }

} 
