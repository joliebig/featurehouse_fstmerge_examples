

package edu.rice.cs.util.docnavigation;

import javax.swing.tree.*;

public class LeafNode<ItemT extends INavigatorItem> extends DefaultMutableTreeNode implements NodeData<ItemT> {
  public String _rep;
  public LeafNode(ItemT i) {
    super(i);
    _rep = i.getName();
  }
  public void setData(ItemT i) { super.setUserObject(i); }
  public ItemT getData() {
    @SuppressWarnings("unchecked") ItemT result = (ItemT) super.getUserObject();
    return result;
  }
  public <Ret> Ret execute(NodeDataVisitor<? super ItemT, Ret> v) { return v.itemCase(getData()); }
  public String toString() { return _rep; }
}
