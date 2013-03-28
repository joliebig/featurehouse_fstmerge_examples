

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;

import junit.framework.TestCase;


public abstract class TypeData extends Data {
  
  public TypeData(Data d) { super(d); }
  
  
  public abstract boolean isInstanceType();
  
  
  public abstract SymbolData getSymbolData();
  
  
  public abstract InstanceData getInstanceData();    
}