
package genj.report;

import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;



public class ReportViewFactory implements ViewFactory {

   final static ImageIcon IMG = new ImageIcon(ReportViewFactory.class, "View");

  
  public View createView() {
    return new ReportView();
  }
  
  
  public ImageIcon getImage() {
    return IMG;
  }
  
  
  public String getTitle() {
    return Resources.get(this).getString("title");
  }

} 
