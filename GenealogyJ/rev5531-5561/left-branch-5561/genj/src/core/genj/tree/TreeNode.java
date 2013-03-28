
package genj.tree;

import gj.layout.tree.Branch;
import gj.layout.tree.NodeOptions;
import gj.layout.tree.Orientation;
import gj.model.Node;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

 class TreeNode implements Node, NodeOptions {
  
  
  private final static int[] NO_PADDING = new int[4];

  
   Object content;
  
  
   List arcs = new ArrayList(5);
  
  
   Point pos = new Point();
  
  
   Shape shape;
  
  
   int[] padding;
  
  
   int align = 0;
  
  
   TreeNode(Object cOntent, Shape sHape, int[] padDing) {
    
    content = cOntent;
    shape = sHape;
    padding = padDing!=null ? padDing : NO_PADDING;
    
  }
  
  
  public List getArcs() {
    return arcs;
  }

  
  public Object getContent() {
    return content;
  }

  
  public Point2D getPosition() {
    return pos;
  }

  
  public Shape getShape() {
    return shape;
  }
  
  
  public int getLongitude(Node node, Branch[] children, Orientation o) {
    
    if (align==0) 
      return Branch.getLongitude(children, 0.5, o);
    
    if (align<0)
      return Branch.getMaxLongitude(children) + align;
    
    return Branch.getMinLongitude(children) + align;
  }
  
  
  public int[] getPadding(Node node, Orientation o) {
    return padding;
  }

} 

