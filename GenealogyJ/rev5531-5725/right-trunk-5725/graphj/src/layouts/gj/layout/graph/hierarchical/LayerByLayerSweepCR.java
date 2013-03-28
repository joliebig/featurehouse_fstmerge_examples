
package gj.layout.graph.hierarchical;

import static gj.geom.Geometry.getIntersection;

import java.awt.geom.Point2D;


public class LayerByLayerSweepCR implements CrossingReduction {

  
  public void reduceCrossings(LayerAssignment layerAssignment) {
    
    
    for (int i=1;i<layerAssignment.getHeight();i++)
      sweepLayer(layerAssignment, i, -1);

    


  }

  
  private int sweepLayer(LayerAssignment layerAssignment, int layer, int direction) {
    
    
    int originalNumCrossings = 0;
    for (int u=0,v=1;u<layerAssignment.getWidth(layer)-1;u++,v++) {
      originalNumCrossings += crossingNumber(layerAssignment, layer, u, v, direction);
    }    
    
    
    if (originalNumCrossings==0)
      return originalNumCrossings;
    
    
    int optimizedNumCrossings = originalNumCrossings;
    while (true) {

      int nc = 0;
      for (int u=0,v=1;u<layerAssignment.getWidth(layer)-1;u++,v++) {
        
        
        int cuv = crossingNumber(layerAssignment, layer, u, v, direction);
        int cvu = crossingNumber(layerAssignment, layer, v, u, direction);
        if (cuv>cvu) layerAssignment.swapVertices(layer, u, v);

        
        nc += Math.min(cuv,cvu);
          
        
      }

      
      if (nc>=optimizedNumCrossings)
        break;
      
      
      optimizedNumCrossings = nc;
    }
    
    
    return optimizedNumCrossings;
  }

  
  private int crossingNumber(LayerAssignment layerAssignment, int layer, int u, int v, int direction) {
    
    
    int cn = 0;

    
    int[] us = layerAssignment.getOutgoingIndices(layer, Math.min(u,v));
    for (int i=0;i<us.length;i++) {
      
      int[] vs = layerAssignment.getOutgoingIndices(layer, Math.max(u,v));
      for (int j=0;j<vs.length;j++) {
        
        if (us[i]!=vs[j] && intersects(u, us[i], v, vs[j]))
          cn ++;
          
      }
    }

    
    return cn;
  }
  
  private boolean intersects(int u1, int u2, int v1, int v2) {
    return getIntersection(new Point2D.Double(u1,0), new Point2D.Double(u2,1), new Point2D.Double(v1,0), new Point2D.Double(v2,1)) != null;
  }
  
}
