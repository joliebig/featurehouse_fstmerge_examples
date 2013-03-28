

package org.jmol.api;

import java.util.List;

import javax.vecmath.Point3f;

public interface JmolSparshClient {
  
  
  public int getGroupID(int x, int y);
  public List getAllowedGestures(int groupID);
  public void processEvent(int groupID, int eventType, int touchID, 
                           int iData, Point3f pt, long time);
}
