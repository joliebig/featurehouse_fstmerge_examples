

package edu.rice.cs.util.docnavigation;

import java.io.*;


public class FileNode<ItemT extends INavigatorItem> extends InnerNode<File, ItemT>{
  
  public FileNode(File f) { super(f); }
  
  public void setData(File f){ super.setUserObject(f); }
  
  public File getData(){ return (File) super.getUserObject(); }
  
  public <Ret> Ret execute(NodeDataVisitor<? super ItemT, Ret> v, Object... p) { return v.fileCase(getData(), p); }
  
  public String toString() {
    try {
      String path = getData().getCanonicalPath();
      int index = path.lastIndexOf(File.separator);
      path = path.substring(index+1);
      return path;
    }
    catch(IOException e) { return getData().toString(); }
  }
}
