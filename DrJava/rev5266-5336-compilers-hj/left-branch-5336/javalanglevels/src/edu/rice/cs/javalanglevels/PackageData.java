

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;

import junit.framework.TestCase;


public class PackageData extends TypeData {
  
  
  public PackageData(String s) {
    super(null);
    _name = s;
  }
  
  
  public PackageData(PackageData pd, String s) {
    super(null);
    _name = pd.getName() + "." + s;
  }
  
   
  public boolean isInstanceType() {
    throw new UnsupportedOperationException("Internal Program Error: Attempt to call isInstanceType() on a PackageData.  Please report this bug.");
  }
 
 
 
  public SymbolData getSymbolData() {
    
    throw new UnsupportedOperationException("Internal Program Error: Attempt to call getSymbolData() on a PackageData.  Please report this bug.");
  }
 
 
  public InstanceData getInstanceData() {
    
    throw new UnsupportedOperationException("Internal Program Error: Attempt to call getInstanceData() on a PackageData.  Please report this bug.");
  }
  
}