

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import java.util.*;

import junit.framework.TestCase;


public abstract class BodyData extends Data{
  
  public BodyData(Data outerData) {
    super(outerData);
  }
  
  
  public SymbolData getSymbolData() {
    return _outerData.getSymbolData();
  }
  
  
  public abstract MethodData getMethodData();
  
  
  public abstract boolean isMethodData();
  
   
  public static class BodyDataTest extends TestCase {
    
    private BodyData _bd1;
    private BodyData _bd2;
    
    private SymbolData _sd1;
    private SymbolData _sd2;
        
    public BodyDataTest() {
      this("");
    }
    public BodyDataTest(String name) {
      super(name);
    }
    
    public void testGetThis() {
      
      _sd1 = new SymbolData("i.like.monkey");
      _sd2 = new SymbolData("i.like.giraffe");
      _bd1 = new BlockData(_sd2);
      _bd2 = new BlockData(_bd1);
      _sd2.setSuperClass(_sd1);
      assertEquals("Should return _sd2", _sd2, _bd2.getSymbolData());
    }
  }
}