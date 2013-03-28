

package edu.rice.cs.util.docnavigation;

class StringNode<ItemT extends INavigatorItem> extends InnerNode<String, ItemT>{
  
  public StringNode(String s) { super(s); }
  public void setData(String f) { super.setUserObject(f);}
  public String getData() { return (String) super.getUserObject(); }
  public <Ret> Ret execute(NodeDataVisitor<? super ItemT, Ret> v) { return v.stringCase(getData()); }
  public String toString() { return getData(); }
}
