

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;

import junit.framework.TestCase;


public class PrimitiveData extends SymbolData {
      
  
  public PrimitiveData(String name) {
    super(name);
    setIsContinuation(false);
    setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
  }
  
  
  public boolean isPrimitiveType() { return true; }
}