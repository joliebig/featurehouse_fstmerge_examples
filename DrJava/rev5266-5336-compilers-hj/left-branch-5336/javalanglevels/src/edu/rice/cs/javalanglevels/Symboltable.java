

package edu.rice.cs.javalanglevels;
import java.util.*;



public class Symboltable extends Hashtable<String, SymbolData> {
  
  
  public SymbolData put(String name, SymbolData sd) {
    SymbolData inTable = this.get(sd.getName());
    if (inTable != null) {
      
      inTable.setIsContinuation(sd.isContinuation());
      inTable.setTypeParameters(sd.getTypeParameters());
      inTable.setMethods(sd.getMethods());
      inTable.setSuperClass(sd.getSuperClass());
      inTable.setInterfaces(sd.getInterfaces());
      inTable.setOuterData(sd.getOuterData());
      inTable.setInnerClasses(sd.getInnerClasses());
    }
    else {
      super.put(sd.getName(), sd);
    }
    
    return sd;
  }
  
  public SymbolData get (String name) { return super.get(name); }
}