
package genj.search;

import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;



public class SearchViewFactory implements ViewFactory {
  
  
   static final ImageIcon IMG = new ImageIcon(SearchViewFactory.class, "View"); 
  
  
  public View createView() {
    return new SearchView();
  }

  
  public ImageIcon getImage() {
    return IMG;
  }

  
  public String getTitle() {
    return SearchView.RESOURCES.getString("title");
  }

} 