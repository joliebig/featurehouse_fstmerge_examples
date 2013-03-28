
package genj.edit;

import genj.gedcom.Context;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;




public class EditViewFactory implements ViewFactory {
    
  
  public View createView(String title, Registry registry, Context context) {
    return new EditView(title, context, registry);
  }

  
  public ImageIcon getImage() {
    return Images.imgView;
  }
  
  
  public String getTitle() {
    return EditView.resources.getString("title");
  }

} 
