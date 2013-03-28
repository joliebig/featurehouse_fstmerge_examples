

package edu.rice.cs.util.docnavigation;
import edu.rice.cs.util.Pair;
import java.util.List;



public interface IDocumentNavigatorFactory<ItemT extends INavigatorItem> {
  
  
  public IDocumentNavigator<ItemT> makeListNavigator();
  
  
  public IDocumentNavigator<ItemT> makeTreeNavigator(String name);
  
  
  
  
  
  public IDocumentNavigator<ItemT> makeListNavigator(IDocumentNavigator<ItemT> parent);
  
  
  public IDocumentNavigator<ItemT> makeTreeNavigator(String name, IDocumentNavigator<ItemT> parent, 
                                                     List<Pair<String, INavigatorItemFilter<ItemT>>> l);
}
