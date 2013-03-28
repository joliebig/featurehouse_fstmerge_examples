

package edu.rice.cs.drjava.model.debug;

import edu.rice.cs.drjava.model.OrderedDocumentRegion;

public interface Breakpoint extends DebugBreakpointData, OrderedDocumentRegion {
  
  public String getClassName();

  
  public void setEnabled(boolean isEnabled);

  
  public void update();
}
