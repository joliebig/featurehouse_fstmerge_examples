
package genj.table;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.ViewFactory;
import genj.view.ViewManager;

import javax.swing.JComponent;


public class TableViewFactory implements ViewFactory {
  
  
  private final static ImageIcon IMG = new ImageIcon(TableViewFactory.class, "images/View");

  
  public JComponent createView(String title, Gedcom gedcom, Registry registry, ViewManager manager) {
    return new TableView(title,gedcom,registry,manager);
  }

  
  public ImageIcon getImage() {
    return IMG;
  }
  
  
  public String getTitle(boolean abbreviate) {
    return Resources.get(this).getString("title" + (abbreviate?".short":""));
  }

} 
