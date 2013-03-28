

package edu.rice.cs.drjava.model;

import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;


public interface RegionManager<R extends IDocumentRegion> {
  
  
  public R getRegionAt(OpenDefinitionsDocument odd, int offset);
  
  
  public Pair<R, R> getRegionInterval(OpenDefinitionsDocument odd, int offset);
  
  
  public Collection<R> getRegionsOverlapping(OpenDefinitionsDocument odd, int startOffset, int endOffset);

  
  public boolean contains(R r);
  
  
  public void addRegion(R region);

  
  public void removeRegion(R region);
  
  
  public void removeRegions(Iterable<? extends R> regions);
  
  
  public void removeRegions(OpenDefinitionsDocument odd);

  
  public void changeRegion(R region, Lambda<R,Object> cmd);
  
  
  public SortedSet<R> getRegions(OpenDefinitionsDocument odd);
  
  
  public ArrayList<R> getRegions();

  
  public int getRegionCount();
  
  
  public SortedSet<R> getHeadSet(R r);
  
  
  public SortedSet<R> getTailSet(R r);

  
  public void clearRegions();

  
  public Set<OpenDefinitionsDocument> getDocuments();
  
  
  public void updateLines(R firstRegion, R lastRegion);
  
  
  public void addListener(RegionManagerListener<R> listener);
  
  
  public void removeListener(RegionManagerListener<R> listener);

  
  public void removeAllListeners();
  





}
