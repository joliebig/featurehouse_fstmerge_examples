
package genj.geo;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.ViewFactory;
import genj.view.ViewManager;

import javax.swing.JComponent;


public class GeoViewFactory implements ViewFactory {
  
  public final static ImageIcon IMAGE = new ImageIcon(GeoViewFactory.class, "images/View");

  private Resources resources = Resources.get(this);

  
  public JComponent createView(String title, Gedcom gedcom, Registry registry, ViewManager manager) {
    return new GeoView(title, gedcom, registry);
  }

  
  public ImageIcon getImage() {
    return IMAGE;
  }

  
  public String getTitle(boolean abbreviate) {
    return resources.getString("title" + (abbreviate?".short":""));
  }

}
