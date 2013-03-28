
package genj.table;

import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;



public class TableViewFactory implements ViewFactory {
  
  
  private final static ImageIcon IMG = new ImageIcon(TableViewFactory.class, "images/View");

  
  public View createView() {
    return new TableView();
  }

  
  public ImageIcon getImage() {
    return IMG;
  }
  
  
  public String getTitle() {
    return Resources.get(this).getString("title");
  }

} 
