
package gj.layout;



import java.awt.Shape;


public interface GraphLayout {

  
  public Shape apply(Graph2D graph2d, LayoutContext context) throws LayoutException;
  
} 
