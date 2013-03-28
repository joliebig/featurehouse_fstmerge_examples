
package genj.search;

import javax.swing.JComponent;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.swing.ImageIcon;
import genj.view.ViewFactory;
import genj.view.ViewManager;


public class SearchViewFactory implements ViewFactory {
  
  
   static final ImageIcon IMG = new ImageIcon(SearchViewFactory.class, "View"); 
  
  
  public JComponent createView(String title, Gedcom gedcom, Registry registry, ViewManager manager) {
    return new SearchView(gedcom, registry, manager);
  }

  
  public ImageIcon getImage() {
    return IMG;
  }

  
  public String getTitle(boolean abbreviate) {
    return SearchView.resources.getString("title" + (abbreviate?".short":""));
  }

} 