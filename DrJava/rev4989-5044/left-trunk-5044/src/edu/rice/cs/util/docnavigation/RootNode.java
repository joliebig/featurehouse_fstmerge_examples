

package edu.rice.cs.util.docnavigation;

import javax.swing.tree.*;
import java.io.File;

class RootNode<ItemT extends INavigatorItem> extends DefaultMutableTreeNode implements NodeData<ItemT> {
  
  public RootNode(File f) { super(f); }
  public RootNode(String s) { this(new File(s)); }
  public void setData(File f) { super.setUserObject(f); }
  public File getData() { return (File) super.getUserObject(); }
  public String toString() { return getData().toString(); }

  
  public <Ret> Ret execute(NodeDataVisitor<? super ItemT, Ret> v, Object... p) { return v.fileCase(getData(), p); }
}
