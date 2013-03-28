
package genj.timeline;

import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;



public class TimelineViewFactory implements ViewFactory {

  
  public View createView() {
    return new TimelineView();
  }
  
  
  public ImageIcon getImage() {
    return new ImageIcon(this, "View");
  }

  
  public String getTitle() {
    return Resources.get(this).getString("title");
  }

} 
