

package edu.rice.cs.util.docnavigation;

import java.io.File;


public interface NodeDataVisitor<ItemT extends INavigatorItem, Ret> {
 
  public Ret fileCase(File f);
  public Ret stringCase(String s);
  public Ret itemCase(ItemT i);
}
