
package genj.tree;

import java.awt.geom.Point2D;

import gj.awt.geom.Path;
import gj.layout.tree.ArcOptions;
import gj.layout.tree.Orientation;
import gj.model.Arc;
import gj.model.Node;


 class TreeArc implements Arc, ArcOptions {
  
  
  private TreeNode start;
   
  
  private TreeNode end;
   
  
  private Path path;
  
  
   TreeArc(TreeNode n1, TreeNode n2, boolean p) {
    
    start = n1;
    end   = n2;
    if (p) path = new Path();
    
    n1.arcs.add(this);
    n2.arcs.add(this);
    
  }
  
  public Node getEnd() {
    return end;
  }
  
  public Node getStart() {
    return start;
  }
  
  public Path getPath() {
    return path;
  }
  
  
  public Point2D getPort(Arc arc, Node node, Orientation o) {
    return node.getPosition();
  }

} 
  