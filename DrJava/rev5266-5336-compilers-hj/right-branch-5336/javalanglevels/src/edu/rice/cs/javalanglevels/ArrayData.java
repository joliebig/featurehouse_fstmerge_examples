

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.*;
import java.util.*;
import junit.framework.*;
import java.io.File;
import edu.rice.cs.plt.reflect.JavaVersion;



public class ArrayData extends SymbolData {
  
  private SymbolData _elementType;
  
  
  public ArrayData(SymbolData sd, LanguageLevelVisitor llv, SourceInfo si) {
    super(sd.getName() + "[]");
    
    _elementType = sd;
    
    
    addVar(new VariableData("length", 
                            new ModifiersAndVisibility(SourceInfo.NO_INFO, 
                                                       new String[] {"public", "final"}),
                            SymbolData.INT_TYPE, true, this));
    
    
    
    SymbolData object = llv.getSymbolData("java.lang.Object", si);
    setSuperClass(object);
    
    
    SymbolData result = llv.getSymbolData("java.lang.Cloneable", si);
    if (result != null) { addInterface(result); }
    
    result = llv.getSymbolData("java.io.Serializable", si);
    if (result != null) { addInterface(result); }
    
    
    addMethod(new MethodData("clone", 
                             new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}), 
                             new TypeParameter[0],
                             object,
                             new VariableData[0],
                             new String[0], 
                             this,
                             null));                        
    setIsContinuation(false);
  }

  
  public String getPackage() { return _elementType.getPackage(); }
  
  
  public void setPackage(String s) {
    _elementType.setPackage(s);
  }
  
  
  
  public ModifiersAndVisibility getMav() {
    if (_elementType.hasModifier("final")) { return _elementType.getMav(); }
    else {
      String[] elementMavs = _elementType.getMav().getModifiers();
      String[] newMavs = new String[elementMavs.length + 1];
      for (int i = 0; i < elementMavs.length; i++) { newMavs[i] = elementMavs[i]; }
      newMavs[elementMavs.length] = "final";
      
      return new ModifiersAndVisibility(SourceInfo.NO_INFO, newMavs);
    }
  }
  
  
  public void setMav(ModifiersAndVisibility mv) {
    _elementType.setMav(mv);
  }
  
  
  public SymbolData getElementType() {
    return _elementType;
  }
  
  
  public Data getOuterData() {
    return _elementType.getOuterData();
  }
  
  
  public void setOuterData(Data outerData) {

  }
  
  
  public boolean equals(Object obj) {
    if (this == obj) {return true;}
    if (obj == null) return false;
    if ((obj.getClass() != this.getClass())) { 
      return false;
    }
    ArrayData ad = (ArrayData) obj;    
    
    
    return super.equals(obj) && getElementType().equals(ad.getElementType());
  }
  
  
  public int hashCode() { return getName().hashCode(); }
  
  
  public boolean isAssignableTo(SymbolData assignTo, JavaVersion version) {
    if (assignTo instanceof ArrayData) {
      if (this.getElementType().isPrimitiveType()) {
        return this.getElementType() == ((ArrayData)assignTo).getElementType();
      }
      else if (((ArrayData)assignTo).getElementType().isPrimitiveType()) {
        return false;
      }
      else {
        return this.getElementType().isAssignableTo(((ArrayData)assignTo).getElementType(), version);
      }
    }
    else {
      return this.isSubClassOf(assignTo);
    }
  }
  
  
  public boolean isCastableTo(SymbolData castTo, JavaVersion version) {
    if (castTo instanceof ArrayData) {
      if (this.getElementType().isPrimitiveType()) {
        return this.getElementType() == ((ArrayData)castTo).getElementType();
      }
      else if (((ArrayData)castTo).getElementType().isPrimitiveType()) {
        return false;
      }
      else {
        return this.getElementType().isCastableTo(((ArrayData)castTo).getElementType(), version);
      }
    }
    else if (this.isSubClassOf(castTo)) {
      return true;
    }
    else {
      return this.isSubClassOf(castTo);
    }
  }
  
  
  public int getDimensions() {
    int dim = 1;
    SymbolData curData = this.getElementType();
    while(curData instanceof ArrayData) {
      dim ++;
      curData = ((ArrayData) curData).getElementType();
    }
    return dim;
  }
  

   
  public static class ArrayDataTest extends TestCase {
    
    private ArrayData _ad;
    
    private ModifiersAndVisibility _publicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _protectedMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});
    private ModifiersAndVisibility _privateMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"private"});
    private ModifiersAndVisibility _packageMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    private ModifiersAndVisibility _abstractMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract"});
    private ModifiersAndVisibility _finalMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final"});
    private ModifiersAndVisibility _publicFinalMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[]{"public", "final"});
    private ModifiersAndVisibility _privateFinalMav =
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"private", "final"});
    
    private LanguageLevelVisitor llv;
    private SourceInfo si;
    
    public ArrayDataTest() { this(""); }
    public ArrayDataTest(String name) { super(name); }
    
    public void setUp() {
      llv = new LanguageLevelVisitor(new File(""), 
                                     "", 
                                     new LinkedList<String>(), 
                                     new LinkedList<String>(),
                                     new LinkedList<String>(), 
                                     new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      
      LanguageLevelConverter.symbolTable.clear();
      LanguageLevelConverter._newSDs.clear();
      si = SourceInfo.NO_INFO;
      SymbolData e = new SymbolData("elementType");
      e.setIsContinuation(false);
      _ad = new ArrayData(e, llv, si);
      LanguageLevelVisitor.errors = new LinkedList<Pair<String, JExpressionIF>>();
    }
    
    public void testGetDimensions() {
      ArrayData intArray = new ArrayData(SymbolData.INT_TYPE, llv, si);
      intArray.setIsContinuation(false);
      llv.symbolTable.remove("int[]");
      llv.symbolTable.put("int[]", intArray);
      
      ArrayData intArrayArray = new ArrayData(intArray, llv, si);
      intArrayArray.setIsContinuation(false);
      llv.symbolTable.put("int[][]", intArrayArray);

      ArrayData intArray3 = new ArrayData(intArrayArray, llv, si);
      intArray3.setIsContinuation(false);
      llv.symbolTable.put("int[][][]", intArray3);
      
      assertEquals("Should return 1", 1, intArray.getDimensions());
      assertEquals("Should return 2", 2, intArrayArray.getDimensions());
      assertEquals("Should return 3", 3, intArray3.getDimensions());
    }
    
    public void testGetPackage() {
      SymbolData sd = _ad.getElementType();
      sd.setPackage("a.b");
      assertEquals("Should return a.b", "a.b", _ad.getPackage());
      
      sd.setPackage("");
      assertEquals("Should return empty string", "", _ad.getPackage());
      
      sd.setPackage("who.let");
      assertEquals("Should return who.let", "who.let", _ad.getPackage());
    }
    
    public void testSetPackage() {
      SymbolData sd = _ad.getElementType();
      _ad.setPackage("a.b");
      assertEquals("Should return a.b", "a.b", sd.getPackage());
      
      _ad.setPackage("");
      assertEquals("Should return empty string", "", sd.getPackage());
      
      _ad.setPackage("who.let");
      assertEquals("Should return who.let", "who.let", sd.getPackage());
      
    }

    public void testGetMav() {
      SymbolData sd = _ad.getElementType();

      sd.setMav(_publicMav);
      assertEquals("Should return _publicFinal mav", _publicFinalMav, _ad.getMav());
      
      sd.setMav(_privateMav);
      assertEquals("Should return _privateFinal mav", _privateFinalMav, _ad.getMav());
      
      sd.setMav(_packageMav);
      assertEquals("Should return _finalMav", _finalMav, _ad.getMav());
      
      sd.setMav(_publicFinalMav);
      assertEquals("Should return _publicFinalMav", _publicFinalMav, _ad.getMav());
    }

    public void testSetMav() {
      SymbolData sd = _ad.getElementType();
      
      _ad.setMav(_publicMav);
      assertEquals("Should return _publicMav", _publicMav, sd.getMav());
      
      _ad.setMav(_privateMav);
      assertEquals("Should return _privateMav", _privateMav, sd.getMav());
      
      _ad.setMav(_publicFinalMav);
      assertEquals("Should return _publicFinalMav", _publicFinalMav, _ad.getMav());
      
    }
    
    
    public void testIsAssignableTo() {
      
      SymbolData object = llv.symbolTable.get("java.lang.Object");
      assertTrue(_ad.isAssignableTo(object, JavaVersion.JAVA_5));
      assertTrue(_ad.isAssignableTo(object, JavaVersion.JAVA_1_4));
      
      SymbolData notObject = new SymbolData("somethingRandom");
      assertFalse(_ad.isAssignableTo(notObject, JavaVersion.JAVA_5));
      assertFalse(_ad.isAssignableTo(notObject, JavaVersion.JAVA_1_4));
      
      
      SymbolData serializable = _ad.getInterfaces().get(0);
      SymbolData clonable = _ad.getInterfaces().get(1);
      notObject.setInterface(true);
      
      assertTrue(_ad.isAssignableTo(serializable, JavaVersion.JAVA_5));
      assertTrue(_ad.isAssignableTo(serializable, JavaVersion.JAVA_1_4));
      assertTrue(_ad.isAssignableTo(clonable, JavaVersion.JAVA_5));
      assertTrue(_ad.isAssignableTo(clonable, JavaVersion.JAVA_1_4));
      assertFalse(_ad.isAssignableTo(notObject, JavaVersion.JAVA_5));
      assertFalse(_ad.isAssignableTo(notObject, JavaVersion.JAVA_1_4));

      
      _ad = new ArrayData(SymbolData.INT_TYPE, llv, si);
      ArrayData intArray = new ArrayData(SymbolData.INT_TYPE, llv, si);
      ArrayData doubleArray = new ArrayData(SymbolData.DOUBLE_TYPE, llv, si);
      ArrayData charArray = new ArrayData(SymbolData.CHAR_TYPE, llv, si);
      ArrayData objArray = new ArrayData(object, llv, si);
      
      assertTrue(_ad.isAssignableTo(intArray, JavaVersion.JAVA_5));
      assertTrue(_ad.isAssignableTo(intArray, JavaVersion.JAVA_1_4));
      assertFalse(_ad.isAssignableTo(charArray, JavaVersion.JAVA_5));
      assertFalse(_ad.isAssignableTo(charArray, JavaVersion.JAVA_1_4));
      assertFalse(_ad.isAssignableTo(doubleArray, JavaVersion.JAVA_5));
      assertFalse(_ad.isAssignableTo(doubleArray, JavaVersion.JAVA_1_4));
      assertFalse(_ad.isAssignableTo(objArray, JavaVersion.JAVA_5));
      assertFalse(_ad.isAssignableTo(objArray, JavaVersion.JAVA_1_4));

      
      
      SymbolData integerSd = new SymbolData("java.lang.Integer");
      integerSd.setSuperClass(object);
      _ad = new ArrayData(integerSd, llv, si);
      notObject.setInterface(false);
      ArrayData randomArray = new ArrayData(notObject, llv, si);

      assertTrue(_ad.isAssignableTo(objArray, JavaVersion.JAVA_5));
      assertTrue(_ad.isAssignableTo(objArray, JavaVersion.JAVA_1_4));
      assertTrue(_ad.isAssignableTo(_ad, JavaVersion.JAVA_5));
      assertTrue(_ad.isAssignableTo(_ad, JavaVersion.JAVA_1_4));
      assertFalse(_ad.isAssignableTo(randomArray, JavaVersion.JAVA_5));
      assertFalse(_ad.isAssignableTo(randomArray, JavaVersion.JAVA_1_4));
      assertFalse(_ad.isAssignableTo(intArray, JavaVersion.JAVA_5));
      assertFalse(_ad.isAssignableTo(intArray, JavaVersion.JAVA_1_4));
    }
   
    
    public void testIsCastableTo() {
      
      SymbolData object = llv.symbolTable.get("java.lang.Object");
      assertTrue(_ad.isCastableTo(object, JavaVersion.JAVA_5));
      assertTrue(_ad.isCastableTo(object, JavaVersion.JAVA_1_4));
      
      
      SymbolData serializable = _ad.getInterfaces().get(0);
      SymbolData clonable = _ad.getInterfaces().get(1);
      
      assertTrue(_ad.isAssignableTo(serializable, JavaVersion.JAVA_5));
      assertTrue(_ad.isAssignableTo(serializable, JavaVersion.JAVA_1_4));
      assertTrue(_ad.isAssignableTo(clonable, JavaVersion.JAVA_5));
      assertTrue(_ad.isAssignableTo(clonable, JavaVersion.JAVA_1_4));

      
      SymbolData notObject = new SymbolData("somethingRandom");
      assertFalse(_ad.isCastableTo(notObject, JavaVersion.JAVA_5));
      assertFalse(_ad.isCastableTo(notObject, JavaVersion.JAVA_1_4));

      
     
      
      
      
      _ad = new ArrayData(SymbolData.INT_TYPE, llv, si);
      ArrayData intArray = new ArrayData(SymbolData.INT_TYPE, llv, si);
      ArrayData doubleArray = new ArrayData(SymbolData.DOUBLE_TYPE, llv, si);
      ArrayData charArray = new ArrayData(SymbolData.CHAR_TYPE, llv, si);
      ArrayData objArray = new ArrayData(object, llv, si);
      
      assertTrue(_ad.isCastableTo(intArray, JavaVersion.JAVA_5));
      assertTrue(_ad.isCastableTo(intArray, JavaVersion.JAVA_1_4));
      assertFalse(_ad.isCastableTo(charArray, JavaVersion.JAVA_5));
      assertFalse(_ad.isCastableTo(charArray, JavaVersion.JAVA_1_4));
      assertFalse(_ad.isCastableTo(doubleArray, JavaVersion.JAVA_5));
      assertFalse(_ad.isCastableTo(doubleArray, JavaVersion.JAVA_1_4));
      assertFalse(_ad.isCastableTo(objArray, JavaVersion.JAVA_5));
      assertFalse(_ad.isCastableTo(objArray, JavaVersion.JAVA_1_4));

      
      
      SymbolData integerSd = new SymbolData("java.lang.Integer");
      integerSd.setSuperClass(object);
      _ad = new ArrayData(integerSd, llv, si);
      notObject.setInterface(false);
      ArrayData randomArray = new ArrayData(notObject, llv, si);

      assertTrue(_ad.isCastableTo(objArray, JavaVersion.JAVA_5));
      assertTrue(_ad.isCastableTo(objArray, JavaVersion.JAVA_1_4));
      assertTrue(_ad.isCastableTo(_ad, JavaVersion.JAVA_5));
      assertTrue(_ad.isCastableTo(_ad, JavaVersion.JAVA_1_4));
      assertFalse(_ad.isCastableTo(randomArray, JavaVersion.JAVA_5));
      assertFalse(_ad.isCastableTo(randomArray, JavaVersion.JAVA_1_4));
      assertFalse(_ad.isCastableTo(intArray, JavaVersion.JAVA_5));
      assertFalse(_ad.isCastableTo(intArray, JavaVersion.JAVA_1_4));

      _ad = new ArrayData(object, llv, si);
      assertTrue(_ad.isCastableTo(new ArrayData(integerSd, llv, si), JavaVersion.JAVA_5));
      assertTrue(_ad.isCastableTo(new ArrayData(integerSd, llv, si), JavaVersion.JAVA_1_4));
      assertFalse(_ad.isCastableTo(new ArrayData(SymbolData.INT_TYPE, llv, si), JavaVersion.JAVA_5));
      assertFalse(_ad.isCastableTo(new ArrayData(SymbolData.INT_TYPE, llv, si), JavaVersion.JAVA_1_4));
    }
  }
}