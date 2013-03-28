

package org.jmol.api;

import java.awt.Component;

public interface JmolSparshAdapter {
  
  
  public void dispose();
  public void setSparshClient(Component display, JmolSparshClient client);
  public void mouseMoved(int x, int y);
}
