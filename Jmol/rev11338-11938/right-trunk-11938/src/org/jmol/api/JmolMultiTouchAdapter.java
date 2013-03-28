

package org.jmol.api;

import org.jmol.viewer.Viewer;

public interface JmolMultiTouchAdapter {
  
  
  public void dispose();
  public void setMultiTouchClient(Viewer viewer, JmolMultiTouchClient client, boolean isSimulation);
  public void mouseMoved(int x, int y);
}
