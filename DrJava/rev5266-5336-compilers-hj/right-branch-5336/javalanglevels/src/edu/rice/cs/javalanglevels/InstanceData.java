

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;

import junit.framework.TestCase;


public class InstanceData extends TypeData {
  
  
  private SymbolData _classSymbolData;
  
  
  public InstanceData(SymbolData classSD) {
    super(null);
    _classSymbolData = classSD;
    _name = classSD.getName();
  }
  
  
  public boolean isInstanceType() { return true; }
 
 
  public SymbolData getSymbolData() { return _classSymbolData;  }

  
  public InstanceData getInstanceData() { return this; }
  
  public String toString() { return "An instance of type '" + _classSymbolData +"'"; }
  
  public boolean equals(Object o) {
    return o.getClass() == getClass() && ((InstanceData)o)._classSymbolData.equals(_classSymbolData);
  }
  
  public int hashCode() { return _classSymbolData.hashCode(); }
}
