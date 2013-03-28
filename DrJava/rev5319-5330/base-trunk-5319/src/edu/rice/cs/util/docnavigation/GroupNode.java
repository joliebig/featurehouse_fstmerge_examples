

package edu.rice.cs.util.docnavigation;

public class GroupNode<ItemT extends INavigatorItem> extends StringNode<ItemT> {
  private INavigatorItemFilter<? super ItemT> _filter;
  public GroupNode(String name, INavigatorItemFilter<? super ItemT> filter) {
    super(name);
    if (filter == null) throw new IllegalArgumentException("parameter 'filter' must not be null");
    _filter = filter;
  }
  
  public INavigatorItemFilter<? super ItemT> getFilter(){ return _filter; }
}
