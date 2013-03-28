
package genj.tree;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.ViewFactory;

import javax.swing.JComponent;


public class TreeViewFactory implements ViewFactory {

  
  public JComponent createView(String title, Gedcom gedcom, Registry registry) {
    return new TreeView(title, gedcom, registry);
  }

  
  public ImageIcon getImage() {
    return Images.imgView;
  }
  
  
  public String getTitle() {
    return Resources.get(this).getString("title");
  }
  
} 
