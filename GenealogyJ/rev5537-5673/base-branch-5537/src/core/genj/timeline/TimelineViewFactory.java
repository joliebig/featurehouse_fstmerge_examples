
package genj.timeline;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.ViewFactory;

import javax.swing.JComponent;


public class TimelineViewFactory implements ViewFactory {

  
  public JComponent createView(String title, Gedcom gedcom, Registry registry) {
    return new TimelineView(title,gedcom,registry);
  }
  
  
  public ImageIcon getImage() {
    return new ImageIcon(this, "View");
  }

  
  public String getTitle() {
    return Resources.get(this).getString("title");
  }

} 
