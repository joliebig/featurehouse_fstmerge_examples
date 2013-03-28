

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.util.Log;
import java.util.*;
import junit.framework.TestCase;
import edu.rice.cs.javalanglevels.parser.JExprParser;


public class MethodData extends BodyData {
  


  
  private TypeParameter[] _typeParameters;
  
  
  private SymbolData _returnType;
  
  
  private VariableData[] _params;
  
  
  private String[] _thrown;
  
  
  private JExpression _jexpr;
  
  
  private boolean _generated;

  
  public MethodData(String name, ModifiersAndVisibility modifiersAndVisibility, TypeParameter[] typeParameters, 
                    SymbolData returnType, VariableData[] params, String[] thrown, SymbolData enclosingClass, 
                    JExpression jexpr) {
    super(enclosingClass);
    _name = name;
    _modifiersAndVisibility = modifiersAndVisibility;
    _typeParameters = typeParameters;
    _returnType = returnType;
    _params = params;
    _thrown = thrown;
    _jexpr = jexpr;
    _generated = false;
  }
  
  
  public MethodData(String name, VariableData[] params) {
    this(name, new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]), new TypeParameter[0], null, 
         params, new String[0], null, new NullLiteral(SourceInfo.NO_INFO));
  }
  
  
  public static MethodData make(String name, ModifiersAndVisibility modifiersAndVisibility, TypeParameter[] typeParameters, 
                    SymbolData returnType, VariableData[] params, String[] thrown, SymbolData enclosingClass, 
                     JExpression jexpr) {

    MethodData md = 
      new MethodData(name, modifiersAndVisibility, typeParameters, returnType, params, thrown, enclosingClass, jexpr);

    return md;
  }
  
  public static MethodData make(String name, VariableData[] params) { return new MethodData(name, params); }
  
  
  public boolean isGenerated() { return _generated; }
  
  
  public boolean isStatic() { return hasModifier("static"); }
  
  
  public void setGenerated(boolean generated) { _generated = generated; }
  
   
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null) return false;
    if ((obj.getClass() != this.getClass())) { 
      return false;
    }
    MethodData md = (MethodData) obj;

    return _name.equals(md.getName()) &&
      _modifiersAndVisibility.equals(md.getMav()) &&
      LanguageLevelVisitor.arrayEquals(_typeParameters, md.getTypeParameters()) &&
      LanguageLevelVisitor.arrayEquals(_params, md.getParams()) &&
      LanguageLevelVisitor.arrayEquals(_thrown, md.getThrown()) &&
      _enclosingData.get(0) == md.getEnclosingData().get(0) &&
      _vars.equals(md.getVars());
  }
  
  
  public int hashCode() { return getName().hashCode() ^ getEnclosingData().hashCode(); }
  
  public boolean isMethodData() { return true; }  
  
  public MethodData getMethodData() { return this; }
  
  
  public TypeParameter[] getTypeParameters() { return _typeParameters; }
  
  
  public SymbolData getReturnType() { return _returnType; }
  
  
  public void setReturnType(SymbolData rt) { _returnType = rt; }
  
  
  public VariableData[] getParams() { return _params; }
  
  
  public void setParams(VariableData[] p) { _params = p; }
  
  
  public String[] getThrown() { return _thrown; }
  
  
  public void setThrown(String[] thrown) { _thrown = thrown; }
  
  
  public ModifiersAndVisibility getMav() { return _modifiersAndVisibility; }
    
  
  public void addPublicMav() {
    String[] oldMav = _modifiersAndVisibility.getModifiers();
    String[] modifiers = new String[oldMav.length + 1];
    modifiers[0] = "public";
    for (int i = 0; i < oldMav.length; i++) {
      modifiers[i+1] = oldMav[i];
    }
    _modifiersAndVisibility = new ModifiersAndVisibility(_modifiersAndVisibility.getSourceInfo(), modifiers);
  }
  

  
  public JExpression getJExpression() { return _jexpr; }
  
  public String toString() { return "MethodData<" + _name +  ">" ; }
  
  
  public static class MethodDataTest extends TestCase {
    
    private MethodData _md;
    private MethodData _md2;
    
    private ModifiersAndVisibility _publicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _publicMav2 = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _protectedMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});
    private ModifiersAndVisibility _finalMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final"});
    
    public MethodDataTest() { this(""); }
    
    public MethodDataTest(String name) { super(name); }
    
    public void testEquals() {
      VariableData vd = new VariableData("v", _publicMav, SymbolData.INT_TYPE, true, _md);
      VariableData vd2 = new VariableData("v2", _protectedMav, SymbolData.BOOLEAN_TYPE, true, _md);
      TypeParameter[] tp = new TypeParameter[0];
      
      Type t = new PrimitiveType(SourceInfo.NO_INFO, "int");
      Word name = new Word(SourceInfo.NO_INFO, "m");
      Word paramName = new Word(SourceInfo.NO_INFO, "i");
      FormalParameter fp = new FormalParameter(SourceInfo.NO_INFO, new UninitializedVariableDeclarator(SourceInfo.NO_INFO, t, paramName), false);
      MethodDef mdef = new AbstractMethodDef(SourceInfo.NO_INFO, _publicMav, tp, t, name, new FormalParameter[] {fp}, new ReferenceType[0]);
      _md = new MethodData("m", _publicMav, tp, SymbolData.INT_TYPE, new VariableData[] {vd},
                             new String[] {"I throw this"}, SymbolData.BOOLEAN_TYPE, mdef);

     
     _md2 = new MethodData("m", _publicMav2, tp, SymbolData.INT_TYPE, new VariableData[]{vd},
                           new String[] {"I throw this"}, SymbolData.BOOLEAN_TYPE, mdef);
     assertTrue("Two MethodDatas with same fields should be equal", _md.equals(_md2));

    
     _md2 = new MethodData("m", _publicMav2, tp, SymbolData.DOUBLE_TYPE, new VariableData[]{vd},
                           new String[] {"I throw this"}, SymbolData.BOOLEAN_TYPE, mdef);                        
     assertTrue("Two MethodDatas with same fields but different return types should be equal", _md.equals(_md2));
     
    
      _md2 = new MethodData("m", _publicMav2, tp, SymbolData.INT_TYPE, new VariableData[]{vd},
                           new String[] {"I throw this"}, SymbolData.BOOLEAN_TYPE, null);    
     assertTrue("Two MethodDatas with same fields but different method defs should be equal", _md.equals(_md2));
    
     
     _md2 = null;
     assertFalse("A MethodData is never equal to null", _md.equals(_md2));

    
     assertFalse("A MethodData is never equal to another class", _md.equals(new Integer(5)));

    
     _md2 = new MethodData("q", _publicMav2, tp, SymbolData.INT_TYPE, new VariableData[]{vd},
                           new String[] {"I throw this"}, SymbolData.BOOLEAN_TYPE, mdef);
     assertFalse("Two MethodDatas with different names are not equal", _md.equals(_md2));
     
    
     _md2 = new MethodData("m", _finalMav, tp, SymbolData.INT_TYPE, new VariableData[]{vd},
                           new String[] {"I throw this"}, SymbolData.BOOLEAN_TYPE, mdef);
     assertFalse("Two MethodDatas with different MAVs are not equal", _md.equals(_md2));
     
    
     TypeParameter[] tp2 = new TypeParameter[] {new TypeParameter(SourceInfo.NO_INFO, new TypeVariable(SourceInfo.NO_INFO,"tv"), 
                                                                new TypeVariable(SourceInfo.NO_INFO,"i"))};
   
     _md2 = new MethodData("m", _publicMav2, tp2, SymbolData.INT_TYPE, new VariableData[]{vd},
                           new String[] {"I throw this"}, SymbolData.BOOLEAN_TYPE, mdef);
     assertFalse("Two MethodDatas with different type parameters are not equal", _md.equals(_md2));
     
    
    _md2 = new MethodData("m", _publicMav2, tp, SymbolData.INT_TYPE, new VariableData[]{vd},
                           new String[] {"I throw this", "maybe this too"}, SymbolData.BOOLEAN_TYPE, mdef);
     assertFalse("Two MethodDatas with different thrown arrays are not equal", _md.equals(_md2));

     
     _md2 = new MethodData("m", _publicMav2, tp, SymbolData.INT_TYPE, new VariableData[]{vd},
                           new String[] {"I throw this"}, SymbolData.NULL_TYPE, mdef);
     assertFalse("Two MethodDatas with different enclosing datas are not equal", _md.equals(_md2));
    
     
     _md2 = new MethodData("m", _publicMav2, tp, SymbolData.INT_TYPE, new VariableData[]{vd, vd2},
                           new String[] {"I throw this"}, SymbolData.NULL_TYPE, mdef);
     assertFalse("Two MethodDatas with different variables are not equal", _md.equals(_md2));
     
     
     _md = new MethodData("m", _publicMav2, tp, SymbolData.INT_TYPE, new VariableData[]{vd2, vd},
                           new String[] {"I throw this"}, SymbolData.NULL_TYPE, mdef);
     assertFalse("Two MethodDatas with same parameters in different order are not equal", _md.equals(_md2));
     
    }
  }
}