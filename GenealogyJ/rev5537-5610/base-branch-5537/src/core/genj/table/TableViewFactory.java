
package genj.table;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.ViewFactory;

import javax.swing.JComponent;


public class TableViewFactory implements ViewFactory {
  
  
  private final static ImageIcon IMG = new ImageIcon(TableViewFactory.class, "images/View");

  
  public JComponent createView(String title, Gedcom gedcom, Registry registry) {
    return new TableView(title,gedcom,registry);
  }

  
  public ImageIcon getImage() {
    return IMG;
  }
  
  
  public String getTitle() {
    return Resources.get(this).getString("title");
  }

} 
