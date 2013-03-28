

package edu.rice.cs.util.docnavigation;

import javax.swing.tree.*;


public abstract class InnerNode<T, ItemT extends INavigatorItem> 
    extends DefaultMutableTreeNode implements NodeData<ItemT> {
  
  protected boolean _collapsed; 
  
  public InnerNode(T d) { super(d); }
  abstract public void setData(T d);
  abstract public T getData();
  
  public void setCollapsed(boolean c) { _collapsed = c; }
  public boolean isCollapsed() {  return _collapsed;  }
}

