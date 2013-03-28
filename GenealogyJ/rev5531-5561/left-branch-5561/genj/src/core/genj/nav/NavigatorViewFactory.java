
package genj.nav;

import genj.gedcom.Context;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;



public class NavigatorViewFactory implements ViewFactory {

  private final static ImageIcon img = new ImageIcon(NavigatorViewFactory.class,"View");

  
  public View createView(String title, Registry registry, Context context) {
    return new NavigatorView(title,context,registry);
  }
  
  
  public ImageIcon getImage() {
    return img;
  }
  
  
  public String getTitle() {
    return Resources.get(this).getString("title");
  }

} 
