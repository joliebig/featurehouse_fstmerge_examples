

package edu.rice.cs.util.docnavigation;


public interface NodeData<ItemT extends INavigatorItem> {
  
  <Ret> Ret execute(NodeDataVisitor<? super ItemT, Ret> v);
  
}
