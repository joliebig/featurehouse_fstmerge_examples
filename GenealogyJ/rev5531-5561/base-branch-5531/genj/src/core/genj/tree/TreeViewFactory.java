
package genj.tree;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.ViewFactory;
import genj.view.ViewManager;

import javax.swing.JComponent;


public class TreeViewFactory implements ViewFactory {

  
  public JComponent createView(String title, Gedcom gedcom, Registry registry, ViewManager manager) {
    return new TreeView(title, gedcom, registry, manager);
  }

  
  public ImageIcon getImage() {
    return Images.imgView;
  }
  
  
  public String getTitle(boolean abbreviate) {
    return Resources.get(this).getString("title" + (abbreviate?".short":""));
  }
  
} 
