
package genj.tree;

import genj.gedcom.Context;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;



public class TreeViewFactory implements ViewFactory {

  
  public View createView(String title, Registry registry, Context context) {
    return new TreeView(title, context, registry);
  }

  
  public ImageIcon getImage() {
    return Images.imgView;
  }
  
  
  public String getTitle() {
    return Resources.get(this).getString("title");
  }
  
} 
