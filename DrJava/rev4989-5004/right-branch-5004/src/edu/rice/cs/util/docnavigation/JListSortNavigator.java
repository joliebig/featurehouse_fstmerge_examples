

package edu.rice.cs.util.docnavigation;

import java.awt.dnd.*;
import edu.rice.cs.drjava.DrJavaRoot;

class JListSortNavigator<ItemT extends INavigatorItem> extends JListNavigator<ItemT> 
  implements DropTargetListener {
  
  
 
  
  public void addDocument(ItemT doc) { insertDoc(doc); }
 
  
  private int insertDoc(ItemT doc) {
    int i;
    synchronized(_model) {
      for (i = 0; i<_model.size(); i++) { 
        ItemT item = getFromModel(i);
        if (doc.getName().toUpperCase().compareTo(item.getName().toUpperCase()) <= 0) break;
      }
      _model.add(i, doc);
    }
    return i;
  }
  
  public String toString() { synchronized(_model) { return _model.toString(); } }
  
  
  DropTarget dropTarget = new DropTarget(this, this);  
  
  
  public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
    DrJavaRoot.dragEnter(dropTargetDragEvent);
  }
  
  public void dragExit(DropTargetEvent dropTargetEvent) {}
  public void dragOver(DropTargetDragEvent dropTargetDragEvent) {}
  public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent){}
  
  
  public synchronized void drop(DropTargetDropEvent dropTargetDropEvent) {
    DrJavaRoot.drop(dropTargetDropEvent);
  }
}
