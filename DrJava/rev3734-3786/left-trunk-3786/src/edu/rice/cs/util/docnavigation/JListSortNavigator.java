

package edu.rice.cs.util.docnavigation;

class JListSortNavigator<ItemT extends INavigatorItem> extends JListNavigator<ItemT> {
  
  
 
  
  public void addDocument(ItemT doc) { insertDoc(doc); }
 
  
  private int insertDoc(ItemT doc) {
    int i;
    synchronized (_model) {
      for (i = 0; i<_model.size(); i++) { 
        ItemT item = getFromModel(i);
        if (doc.getName().toUpperCase().compareTo(item.getName().toUpperCase()) <= 0) break;
      }
      _model.add(i, doc);
    }
    return i;
  }
  
  public String toString() { 
    synchronized(_model) { return _model.toString(); } 
  }
}
