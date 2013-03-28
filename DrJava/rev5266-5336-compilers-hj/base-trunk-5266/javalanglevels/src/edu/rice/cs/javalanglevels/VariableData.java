

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.*;
import java.util.*;
import junit.framework.TestCase;


public class VariableData {
  
  
  private String _name;
  
  
  private ModifiersAndVisibility _modifiersAndVisibility;
  
  
  private InstanceData _type;
  
  
  private boolean _hasBeenAssigned;
  
  
  private boolean _hasInitializer;
  
  
  private Data _enclosingData;
  
  
  private boolean _generated;
  
  
  private boolean _isLocalVariable;
  
  
  public VariableData(String name, ModifiersAndVisibility modifiersAndVisibility, SymbolData type, 
                      boolean hasBeenAssigned, Data enclosingData) {
    _name = name;
    _modifiersAndVisibility = modifiersAndVisibility;
    _type = type.getInstanceData();
    _hasBeenAssigned = hasBeenAssigned;
    _enclosingData = enclosingData;
    _hasInitializer = false;
    _generated = false;
    _isLocalVariable = false;
  }
  
  
  public VariableData(SymbolData type) {
    _name = "";
    _modifiersAndVisibility = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    _type = type.getInstanceData();
    _hasBeenAssigned = false;
    _isLocalVariable = true;
  }
  
  
  public boolean equals(Object obj) { 
    if (obj == null) return false;
    else if (obj.getClass() != this.getClass()) { 

      return false; 
    }
    
    VariableData vd = (VariableData) obj;
    
    if (! _name.equals(vd.getName())) {

      return false;
    }
    if (! _modifiersAndVisibility.equals(vd.getMav())) {

      return false;
    }
    
    if (! _type.equals(vd.getType())) {

      return false;
    }
    
    if (_hasBeenAssigned != vd.hasValue()) {

      return false;
    }
    if (_hasInitializer != vd._hasInitializer) {

      return false;
    }
    Data otherEnclosingData = vd.getEnclosingData();
    
    if (_enclosingData == null) {
      if (otherEnclosingData == null) return true;
      else {

        return false; 
      }
    }  
    else if (_enclosingData != otherEnclosingData) {  

      return false; 
    }
    
    return true;
  }
  
  
  public int hashCode() { return getEnclosingData().hashCode() ^ getName().hashCode(); }
  
  
  public String toString() {
    return "VariableData(" + _name + ", " + Arrays.toString(_modifiersAndVisibility.getModifiers()) + ", " + _type + 
      ", " + _hasBeenAssigned + ")";
  }
  
  
  public String getName() { return _name; }
  
  
  public void setName(String s) { _name = s; }
  
  
  public ModifiersAndVisibility getMav() { return _modifiersAndVisibility; }
  
  
  public void setMav(ModifiersAndVisibility mav) { _modifiersAndVisibility = mav; }
  
  
  public InstanceData getType() {  return _type; }
  
  
  public Data getEnclosingData() { return _enclosingData; }
  
    
  public void setEnclosingData(Data d) { _enclosingData = d; }
  
    
  public boolean isLocalVariable() { return _isLocalVariable; }
  
    
  public void setIsLocalVariable(boolean b) { _isLocalVariable = b; }
  
  
  public void setFinal() {
    if (!isFinal()) {
      String[] modifiers = _modifiersAndVisibility.getModifiers();
      String[] newModifiers = new String[modifiers.length + 1];
      newModifiers[0] = "final";
      for (int i = 1; i <= modifiers.length; i++) {
        newModifiers[i] = modifiers[i-1];
      }
      _modifiersAndVisibility = new ModifiersAndVisibility(SourceInfo.NO_INFO, newModifiers);
    }
  }
  
  
  public void setPrivate() {
    if (! hasModifier("private")) {
      String[] modifiers = _modifiersAndVisibility.getModifiers();
      String[] newModifiers = new String[modifiers.length + 1];
      newModifiers[0] = "private";
      for (int i = 1; i <= modifiers.length; i++) {
        newModifiers[i] = modifiers[i-1];
      }
      _modifiersAndVisibility = new ModifiersAndVisibility(SourceInfo.NO_INFO, newModifiers);
    }
  }
  
  
  public void addModifier(String s) {
    if (! hasModifier(s)) {
      String[] modifiers = _modifiersAndVisibility.getModifiers();
      String[] newModifiers = new String[modifiers.length + 1];
      newModifiers[0] = s;
      for (int i = 1; i <= modifiers.length; i++) {
        newModifiers[i] = modifiers[i-1];
      }
      _modifiersAndVisibility = new ModifiersAndVisibility(SourceInfo.NO_INFO, newModifiers);
    }
  }
  
  
  public void setPrivateAndFinal() {
    setPrivate();
    setFinal();
  }
  
  
  public void setFinalAndStatic() {
    setFinal();
    addModifier("static");
  }
  
  
  public boolean isFinal() { return hasModifier("final"); }
  
  
  public boolean isPrivate() { return hasModifier("private"); }
  
  
  public boolean isStatic() { return hasModifier("static"); }
  
  
  public boolean hasModifier(String modifier) {
    String[] mavStrings = _modifiersAndVisibility.getModifiers();
    for (int i = 0; i < mavStrings.length; i++) {
      if (mavStrings[i].equals(modifier)) {
        return true;
      }
    }
    return false;
  }
  
  
  public void setGenerated(boolean value) { _generated = value; }
  
  
  public boolean isGenerated() { return _generated;  }
  
  
  public boolean hasInitializer() { return _hasInitializer; }
  
  
  public void setHasInitializer(boolean value) { _hasInitializer = value; }
  
  
  public boolean hasValue() { return _hasBeenAssigned; }
  
  
  public boolean gotValue() {
    if (hasValue()) { return false; }
    _hasBeenAssigned = true;
    return true;
  }
  
  
  public boolean lostValue() {
    if (hasValue()) {
      _hasBeenAssigned = false;
      return true;
    }
    return false;
  }
  
  
  public static class VariableDataTest extends TestCase {
    
    private VariableData _vd;
    private VariableData _vd2;
    
    private ModifiersAndVisibility _publicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility 
      _publicMav2 = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility 
      _protectedMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});    
    public VariableDataTest() { this(""); }
    public VariableDataTest(String name) { super(name); }
    
    public void testEquals() {
      _vd = new VariableData("v", _publicMav, SymbolData.INT_TYPE, true, null);
      
      
      _vd2 = new VariableData("v", _publicMav2, SymbolData.INT_TYPE, true, null);
      assertTrue("Equals should return true if two VariableDatas are equal", _vd.equals(_vd2));
      assertTrue("Equals should return true in opposite direction as well", _vd2.equals(_vd));
      
      
      _vd2 = null;
      assertFalse("Equals should return false if VariableData is compared to null",_vd.equals(null));   
      
      
      _vd2 = new VariableData("q", _publicMav, SymbolData.INT_TYPE, true, null);
      assertFalse("Equals should return false if variable names are different", _vd.equals(_vd2));
      
      
      _vd2 = new VariableData("v", _protectedMav, SymbolData.INT_TYPE, true, null);
      assertFalse("Equals should return false if variable modifiers are different", _vd.equals(_vd2));
      
      
      _vd2 = new VariableData("v", _publicMav, SymbolData.BOOLEAN_TYPE, true, null);
      assertFalse("Equals should return false if variable types are different", _vd.equals(_vd2));
    }
  }
}