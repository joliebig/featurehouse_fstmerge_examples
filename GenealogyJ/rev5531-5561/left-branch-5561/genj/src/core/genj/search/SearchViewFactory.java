
package genj.search;

import genj.gedcom.Context;
import genj.util.Registry;
import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;



public class SearchViewFactory implements ViewFactory {
  
  
   static final ImageIcon IMG = new ImageIcon(SearchViewFactory.class, "View"); 
  
  
  public View createView(String title, Registry registry, Context context) {
    return new SearchView(context, registry);
  }

  
  public ImageIcon getImage() {
    return IMG;
  }

  
  public String getTitle() {
    return SearchView.resources.getString("title");
  }

} 