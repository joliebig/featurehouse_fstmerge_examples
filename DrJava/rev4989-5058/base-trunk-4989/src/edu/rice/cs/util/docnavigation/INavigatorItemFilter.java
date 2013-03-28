

package edu.rice.cs.util.docnavigation;

abstract public class INavigatorItemFilter<ItemT extends INavigatorItem> {
  abstract public boolean accept(ItemT n);
}
