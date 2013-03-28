
package gj.shell;

import gj.geom.Geometry;
import gj.layout.Routing;
import gj.shell.model.EditableEdge;
import gj.shell.model.EditableGraph;
import gj.shell.model.EditableVertex;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


 class Animation {

  
  private EditableGraph graph;
  
  
  private Motion[] motions;
  
  
  private List<Object> edgesAndShapes;
  
  
  private long 
    totalTime, startFrame;
  
  private double START = 0D, END = 1D;
  
  
  public Animation(EditableGraph graph) {
    this(graph, 1000);
  }
  public Animation(EditableGraph graph, long totalTime) {
    this.graph = graph;
    this.totalTime = totalTime;
  }
    
  
  public void beforeLayout() {
    
    
    motions = new Motion[graph.getNumVertices()];
    Iterator<EditableVertex> vertices = graph.getVertices().iterator();
    for (int m=0;vertices.hasNext();m++) 
      motions[m] = new Motion(vertices.next());
   
  }
  
  public void afterLayout() {
    
    
    for (int m=0;m<motions.length;m++) 
      motions[m].afterLayout();
    
    edgesAndShapes = new ArrayList<Object>(graph.getNumEdges());
    for (Iterator<EditableEdge> edges = graph.getEdges().iterator(); edges.hasNext(); ) {
      EditableEdge edge = edges.next();
      edgesAndShapes.add(edge);
      edgesAndShapes.add(edge.getPath());
    }
    
    
  }
  
  
  public boolean animate() {
    
    
    long now = System.currentTimeMillis();
    
    
    if (startFrame==0)
      startFrame = now;
      
    
    if (startFrame+totalTime<now) {
      stop();
      return true;
    }

    
    if (animate(Math.min(END, ((double)now-startFrame)/totalTime))) {
      stop();
      return true;
    }
    
    
    return false;
  }
    
  
  public void stop() {
    
    
    animate(END);
    
    
    
    Iterator<?> it = edgesAndShapes.iterator();
    while (it.hasNext()) {
      ((EditableEdge)it.next()).setPath((Routing)it.next());
    }
    
    motions=null;
    
    
  }

  
  private boolean animate(double index) {
    
    boolean done = true;
    synchronized (graph) {
      
      for (int m=0;m<motions.length;m++) {
        Motion motion = motions[m];
        
        done &= motion.animate(index);
      }
    }
        
    
    return done;
  }

  
  private class Motion {
    
    private EditableVertex vertex;
    private GeneralPath start, end;
    
    Motion(EditableVertex set) { 
      vertex = set; 
      beforeLayout();
    }
    void beforeLayout() { 
      start = new GeneralPath(vertex.getShape());
    } 
    void afterLayout() { 
      end = new GeneralPath(vertex.getShape());
    } 
    boolean animate(double index) {
      Shape shape = shape(index);
      vertex.setShape(shape);
      return shape.equals(end);
    }

    private Shape shape(double index) {

      
      if (index==START)
        return start;
      if (index==END)
        return end;

      
      return Geometry.getInterpolation(index, start, end);
    }
  }
  
}
