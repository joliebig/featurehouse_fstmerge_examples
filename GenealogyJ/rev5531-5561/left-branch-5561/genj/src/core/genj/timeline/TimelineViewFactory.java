
package genj.timeline;

import genj.gedcom.Context;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;



public class TimelineViewFactory implements ViewFactory {

  
  public View createView(String title, Registry registry, Context context) {
    return new TimelineView(title,context,registry);
  }
  
  
  public ImageIcon getImage() {
    return new ImageIcon(this, "View");
  }

  
  public String getTitle() {
    return Resources.get(this).getString("title");
  }

} 
