
package genj.search;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.swing.ImageIcon;
import genj.view.ViewFactory;

import javax.swing.JComponent;


public class SearchViewFactory implements ViewFactory {
  
  
   static final ImageIcon IMG = new ImageIcon(SearchViewFactory.class, "View"); 
  
  
  public JComponent createView(String title, Gedcom gedcom, Registry registry) {
    return new SearchView(gedcom, registry);
  }

  
  public ImageIcon getImage() {
    return IMG;
  }

  
  public String getTitle() {
    return SearchView.resources.getString("title");
  }

} 