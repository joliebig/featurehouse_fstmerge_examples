
package gj.shell.factory;

import gj.shell.model.EditableEdge;
import gj.shell.model.EditableGraph;
import gj.shell.model.EditableVertex;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;


public abstract class AbstractGraphFactory {
  
  
  protected Shape nodeShape;
  
  
  public void setNodeShape(Shape set) {
    nodeShape = set;
  }
  
  
  public abstract EditableGraph create(Rectangle2D bounds);

  
  protected Point2D getPoint(double x, double y) {
    return new Point2D.Double(x, y);
  }

  
  protected EditableVertex getMinDegNode(EditableGraph graph, List<EditableVertex> list, boolean remove) {
    
    int pos = getRandomIndex(list.size());

    EditableVertex result = list.get(pos);
    int min = graph.getNumAdjacentVertices(result);
    
    for (int i=1;i<list.size();i++) {
      EditableVertex other = list.get( (pos+i)%list.size() );
      if (graph.getNumAdjacentVertices(other) < min) 
        result=other;
    }
    if (remove) list.remove(result);
    
    return result;
  }
  
  
  protected int getRandomIndex(int ceiling) {
    double rnd = 1;
    while (rnd==1) rnd=Math.random();
    return (int)(rnd*ceiling);
  }

  
  protected EditableVertex getRandomNode(List<EditableVertex> list, boolean remove) {
    int i = getRandomIndex(list.size());
    return remove ? list.remove(i) : list.get(i);
  }
  
  
  protected Object getRandomArc(List<EditableEdge> list, boolean remove) {
    int i = getRandomIndex(list.size());
    return remove ? list.remove(i) : list.get(i);
  }
  

  
  protected Point2D getRandomPosition(Rectangle2D canvas, Shape shape) {
    
    Rectangle2D nodeCanvas = shape.getBounds2D();

    double 
      x = canvas.getMinX() - nodeCanvas.getMinX(),
      y = canvas.getMinY() - nodeCanvas.getMinY(),
      w = canvas.getWidth() - nodeCanvas.getWidth(),
      h = canvas.getHeight() - nodeCanvas.getHeight();

    return new Point2D.Double(x + Math.random()*w, y + Math.random()*h);
    
  }

  
  @Override
  public String toString() {
    String s = getClass().getName();
    return s.substring(s.lastIndexOf('.')+1);
  }
  
} 
