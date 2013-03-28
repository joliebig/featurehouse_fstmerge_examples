
package genj.nav;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.ViewFactory;
import genj.view.ViewManager;

import javax.swing.JComponent;


public class NavigatorViewFactory implements ViewFactory {

  private final static ImageIcon img = new ImageIcon(NavigatorViewFactory.class,"View");

  
  public JComponent createView(String title, Gedcom gedcom, Registry registry, ViewManager manager) {
    return new NavigatorView(title,gedcom,registry);
  }
  
  
  public ImageIcon getImage() {
    return img;
  }
  
  
  public String getTitle(boolean abbreviate) {
    return Resources.get(this).getString("title" + (abbreviate?".short":""));
  }

} 
