

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import java.util.*;


public class BlockData extends BodyData { 
  
  
  public BlockData(Data outerData) {
    super(outerData);
  }
  
  
  public MethodData getMethodData() {
    return ((BodyData)_enclosingData.get(0)).getMethodData();
  }
  
  
  public boolean isMethodData() { return false; }
}