
package genj.report;

import genj.gedcom.Context;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;



public class ReportViewFactory implements ViewFactory {

   final static ImageIcon IMG = new ImageIcon(ReportViewFactory.class, "View");

  
  public View createView(String title, Registry registry, Context context) {
    return new ReportView(title,context,registry);
  }
  
  
  public ImageIcon getImage() {
    return IMG;
  }
  
  
  public String getTitle() {
    return Resources.get(this).getString("title");
  }

} 
