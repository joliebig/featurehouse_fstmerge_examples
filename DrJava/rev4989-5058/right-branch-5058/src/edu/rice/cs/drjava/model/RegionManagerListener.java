

package edu.rice.cs.drjava.model;


public interface RegionManagerListener<R extends IDocumentRegion> {
  
  public void regionAdded(R r);
  
  
  public void regionChanged(R r);
  
  
  public void regionRemoved(R r);
}