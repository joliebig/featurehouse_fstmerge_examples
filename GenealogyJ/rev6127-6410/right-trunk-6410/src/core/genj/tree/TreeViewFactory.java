
package genj.tree;

import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;



public class TreeViewFactory implements ViewFactory {

  
  public View createView() {
    return new TreeView();
  }

  
  public ImageIcon getImage() {
    return Images.imgView;
  }
  
  
  public String getTitle() {
    return TreeView.TITLE;
  }
  
} 
