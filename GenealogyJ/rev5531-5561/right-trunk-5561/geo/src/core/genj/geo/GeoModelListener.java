
package genj.geo;


public interface GeoModelListener {
  
  
  public void locationAdded(GeoLocation location);

  
  public void locationUpdated(GeoLocation location);

  
  public void locationRemoved(GeoLocation location);
  
  
  public void asyncResolveStart();

  
  public void asyncResolveEnd(int status, String msg);
  
}
