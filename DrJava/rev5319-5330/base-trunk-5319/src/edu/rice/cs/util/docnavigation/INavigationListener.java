

package edu.rice.cs.util.docnavigation;



public interface INavigationListener<ItemT extends INavigatorItem> {
  
  public void gainedSelection(NodeData<? extends ItemT> dat, boolean modelInitiated);
  
  
  public void lostSelection(NodeData<? extends ItemT> dat, boolean modelInitiated);
}
