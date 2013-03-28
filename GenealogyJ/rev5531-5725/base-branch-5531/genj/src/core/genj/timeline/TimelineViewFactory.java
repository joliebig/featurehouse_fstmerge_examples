
package genj.timeline;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.ViewFactory;
import genj.view.ViewManager;

import javax.swing.JComponent;


public class TimelineViewFactory implements ViewFactory {

  
  public JComponent createView(String title, Gedcom gedcom, Registry registry, ViewManager manager) {
    return new TimelineView(title,gedcom,registry,manager);
  }
  
  
  public ImageIcon getImage() {
    return new ImageIcon(this, "View");
  }

  
  public String getTitle(boolean abbreviate) {
    return Resources.get(this).getString("title" + (abbreviate?".short":""));
  }

} 
