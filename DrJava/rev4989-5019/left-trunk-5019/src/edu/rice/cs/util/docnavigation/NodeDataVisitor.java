

package edu.rice.cs.util.docnavigation;

import java.io.File;


public interface NodeDataVisitor<ItemT extends INavigatorItem, Ret> {
 
  public Ret fileCase(File f, Object... p);
  public Ret stringCase(String s, Object... p);
  public Ret itemCase(ItemT i, Object... p);
}
