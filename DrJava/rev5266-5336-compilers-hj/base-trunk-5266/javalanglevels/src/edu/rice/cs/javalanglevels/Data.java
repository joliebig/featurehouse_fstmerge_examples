

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import java.util.*;
import junit.framework.TestCase;
import edu.rice.cs.javalanglevels.parser.JExprParser;


public abstract class Data {
  
  
  
  protected String _name;
  
  
  protected LinkedList<VariableData> _vars;
  
  
  protected LinkedList<Data> _enclosingData;
  
  
  protected ModifiersAndVisibility _modifiersAndVisibility;
  
  
  protected Data _outerData;
  
  
  protected LinkedList<SymbolData> _innerClasses;
  
  
  protected LinkedList<BlockData> _blocks;
  
  
  protected Iterator<BlockData> _blockIterator;

  
  public Data(Data outerData) {
    _name = "";
    _modifiersAndVisibility = null;
    _vars = new LinkedList<VariableData>();
    _enclosingData = new LinkedList<Data>();
    _outerData = outerData;
    if (outerData != null) {
      _enclosingData.addLast(_outerData); 
    }
    _innerClasses = new LinkedList<SymbolData>();
    _blocks = new LinkedList<BlockData>();
    _blockIterator = null;
  }
  
  
  public String getName() { return _name; }
  
  
  void setName(String name) {
    _name = name;
  }
  
  public Boolean isAnonymousClass() {
    int lastIndex = _name.lastIndexOf("$");
    try { return (lastIndex < 0) && Integer.parseInt(_name.substring(lastIndex+1)) >= 0; }
    catch(NumberFormatException e) { return false;  }
  }
  
  public Boolean isDoublyAnonymous() {
    if (! isAnonymousClass()) return false;
    for (Data d: getEnclosingData()) {
      if (d.isAnonymousClass()) return true;
    }
    return false;
  }
     
  
  void setVars(LinkedList<VariableData> vars) { _vars = vars; }
  
  
  public VariableData getVar(String name) {
    Iterator<VariableData> iter = _vars.iterator();
    while (iter.hasNext()) {
      VariableData vd = iter.next();
      if (vd.getName().equals(name)) {
        return vd;
      }
    }
    return null;
  }
  
  
  public LinkedList<VariableData> getVars() { return _vars; }
  
  
  public LinkedList<Data> getEnclosingData() { return _enclosingData; }
  
  
  public void addEnclosingData(Data enclosingData) {
    if (!_enclosingData.contains(enclosingData)) {
      _enclosingData.addFirst(enclosingData);
    }
  }
  
  
  public void setEnclosingData(LinkedList<Data> d) { _enclosingData = d; }
  
  
  private boolean _repeatedName (VariableData vr) {
    Iterator<VariableData> iter = _vars.iterator();
    while (iter.hasNext()) {
      VariableData next = iter.next();
      if (vr.getName().equals(next.getName())) {
        return true;
      }
    }
    return false;
  }
  
  
  public boolean addVar(VariableData var) {
    if (!_repeatedName(var)) {
      _vars.addLast(var);
      return true;
    }
    else return false;
  }
  
  
  public boolean addVars(VariableData[] vars) {
    boolean success = true;
    for (int i = 0; i < vars.length; i++) {

      if (!_repeatedName(vars[i])) {
        _vars.addLast(vars[i]);
      }
      else success = false;
    }
    return success;
  }
  
  
  public boolean addFinalVars(VariableData[] vars) {
    boolean success = true;
    for (int i = 0; i < vars.length; i++) {
      if (! _repeatedName(vars[i])) {
        vars[i].setFinal();
        _vars.addLast(vars[i]);
      }
      else { success = false; }
    }
    return success;
  }
  
  
  public ModifiersAndVisibility getMav() { return _modifiersAndVisibility; }
  
  
  public void setMav(ModifiersAndVisibility modifiersAndVisibility) {
    _modifiersAndVisibility = modifiersAndVisibility;
  }
  
  
  public abstract SymbolData getSymbolData();

  
  public Data getOuterData() { return _outerData; }
  
  
  public void setOuterData(Data outerData) {
    if (outerData == null) {
      assert _outerData == null; 
      return;
    }
    if (_outerData == null) {
      _outerData = outerData;
      _enclosingData.addLast(_outerData);
    }
    else {
      throw new RuntimeException("Internal Program Error: Trying to reset an outer data to " + outerData.getName() +  
                                 " for " + getName() + " that has already been set.  Please report this bug.");
    }
  }
  
  
  public boolean isOuterData(Data d) {
    Data outerData = _outerData;
    while ((outerData != null) && !LanguageLevelVisitor.isJavaLibraryClass(outerData.getName())) {
      if (outerData == d) {
        return true;
      }
      outerData = outerData.getOuterData();
    }
    return false;
  }
  
  
  
  public static String dollarSignsToDots(String s) {
    return s.replace('$', '.');
  }
  
  
  public SymbolData getNextAnonymousInnerClass() {
    String name = getSymbolData().getName() + "$" + getSymbolData().preincrementAnonymousInnerClassNum();
    LinkedList<SymbolData> myDatas = getInnerClasses();
    SymbolData myData = null;
    
    for (int i = 0; i < myDatas.size(); i++) {
      if (myDatas.get(i).getName().equals(name)) {
        myData = myDatas.get(i);
        break;
      }
    }
    return myData;
  }
  
  
  public void resetBlockIterator() { _blockIterator = null; }
  
  
  public BlockData getNextBlock() {
    if (_blockIterator == null) { _blockIterator = _blocks.iterator(); }

    if (_blockIterator.hasNext()) { return _blockIterator.next(); }
    else { return null; }
  }
  
  
  public void addBlock(BlockData b) { _blocks.add(b); }
  
  
  public void removeAllBlocks() { _blocks.clear(); }
  
  
  public SymbolData getInnerClassOrInterface(String name) {
    int firstIndexOfDot = name.indexOf(".");
    int firstIndexOfDollar = name.indexOf("$");
    if (firstIndexOfDot == -1) {
      firstIndexOfDot = firstIndexOfDollar;
    }
    else {
      if (firstIndexOfDollar >= 0 && firstIndexOfDollar < firstIndexOfDot)
        firstIndexOfDot = firstIndexOfDollar;
    }

    
    SymbolData privateResult = null;
    SymbolData result = getInnerClassOrInterfaceHelper(name, firstIndexOfDot);
    if (result != null) {
      SymbolData outerPiece;
      if (firstIndexOfDot > 0) {
        outerPiece = getInnerClassOrInterfaceHelper(name.substring(0, firstIndexOfDot), -1);
      }
      else { outerPiece = result; }
      if (TypeChecker.checkAccessibility(outerPiece.getMav(), outerPiece, this.getSymbolData())) {return result;}
      else {privateResult = result; result = null;}
    }
    
    
    
    if (_outerData != null) {
      result = _outerData.getInnerClassOrInterface(name);
      if (result != null) {return result;}
    }
    
    return privateResult;
  }
  
  
  protected SymbolData getInnerClassOrInterfaceHelper(String nameToMatch, int firstIndexOfDot) {
    Iterator<SymbolData> iter = innerClassesAndInterfacesIterator();
    while (iter.hasNext()) {
      SymbolData sd = iter.next();
      String sdName = sd.getName();

      sdName = LanguageLevelVisitor.getUnqualifiedClassName(sdName);
      if (firstIndexOfDot == -1) {
        if (sdName.equals(nameToMatch)) return sd;
      }
      else {
        if (sdName.equals(nameToMatch.substring(0, firstIndexOfDot))) {
          return sd.getInnerClassOrInterface(nameToMatch.substring(firstIndexOfDot + 1));
        }
      }
    }
    return null;
  }
  
  public Iterator<SymbolData> innerClassesAndInterfacesIterator() {
    return _innerClasses.iterator();
  }
  
 
  public LinkedList<SymbolData> getInnerClasses() {
    return _innerClasses;
  }
  
  
  public void setInnerClasses(LinkedList<SymbolData> innerClasses) { _innerClasses = innerClasses; }  
  
  
  public void addInnerClass(SymbolData innerClass) {
    _innerClasses.addLast(innerClass);
  }
  
  
  public boolean hasModifier(String modifier) {
    if (getMav() == null) {return false;}
    String[] mavStrings = _modifiersAndVisibility.getModifiers();
    for (int i = 0; i < mavStrings.length; i++) {
      if (mavStrings[i].equals(modifier)) {
        return true;
      }
    }
    return false;
  }
  
  
  
  public void addModifier(String modifier) {
    if (! hasModifier(modifier)) {
      if (_modifiersAndVisibility == null) { setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0])); }
      String[] modifiers = _modifiersAndVisibility.getModifiers();
      String[] newModifiers = new String[modifiers.length + 1];
      newModifiers[0] = modifier;
      for (int i = 1; i <= modifiers.length; i++) {
        newModifiers[i] = modifiers[i-1];
      }
      _modifiersAndVisibility = new ModifiersAndVisibility(_modifiersAndVisibility.getSourceInfo(), newModifiers);
    }    
  }
  
  
  public String createUniqueName(String varName) {
    VariableData vd = TypeChecker.getFieldOrVariable(varName, this, getSymbolData(), new NullLiteral(SourceInfo.NO_INFO), getVars(), true, false);
    String newName = varName;
    int counter = 0;  
    while (vd != null && counter != -1) {
      newName = varName + counter; counter++;
      vd = TypeChecker.getFieldOrVariable(newName, this, getSymbolData(), new NullLiteral(SourceInfo.NO_INFO), getVars(), true, false);
    }
    
    if (counter == -1) {throw new RuntimeException("Internal Program Error: Unable to rename variable " + varName + ".  All possible names were taken.  Please report this bug");}

    return newName; 
  }

  
  
  public static class DataTest extends TestCase {
    
    private Data _d;
    
    private ModifiersAndVisibility _publicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _staticMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"static"});
    private ModifiersAndVisibility _lotsaMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public", "final", "abstract"});
    private ModifiersAndVisibility _protectedMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});
    private ModifiersAndVisibility _privateMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"private"});
    
    public DataTest() {
      this("");
    }
    public DataTest(String name) {
      super(name);
    }
    

    public void test_repeatedName() {
      _d = new SymbolData("myname");
      
      VariableData vd = new VariableData("v1", _publicMav, SymbolData.INT_TYPE, false, _d);

      
      assertFalse("No variables to repeat name", _d._repeatedName(vd));
      
      
      _d.addVar(new VariableData("v2", _protectedMav, SymbolData.BOOLEAN_TYPE, true, _d));
      assertFalse("No repeated name", _d._repeatedName(vd));
      
      
      _d.addVar(vd);
      assertTrue("Should be repeated name", _d._repeatedName(vd));
    }
    
    public void testIsAbstract() {
      _d = new SymbolData("myName");
      _d.setMav(_publicMav);
      
      
      assertFalse("Should not be abstract", _d.hasModifier("abstract"));
      
      _d.setMav(_lotsaMav);

      
      assertTrue("Should be abstract", _d.hasModifier("abstract"));
    }
   
    public void testAddVar() {
      _d = new SymbolData("myName");
      VariableData vd = new VariableData("v1", _publicMav, SymbolData.INT_TYPE, true, _d);
      VariableData vd2 = new VariableData("v2", _publicMav, SymbolData.CHAR_TYPE, true, _d);
      LinkedList<VariableData> myVds = new LinkedList<VariableData>();
      myVds.addLast(vd);
      
      
      assertTrue("Should be able to add first variable", _d.addVar(vd));
      assertEquals("Variable list should have 1 variable, vd", myVds, _d.getVars());

      
      assertFalse("Should not be able to add same variable again", _d.addVar(vd));
      assertEquals("Variable list should not have changed", myVds, _d.getVars());
      
      
      myVds.addLast(vd2);
      assertTrue("Should be able to add a different variable", _d.addVar(vd2));
      assertEquals("Variable list should have 2 variables, vd, vd2", myVds, _d.getVars());
      
    }
    
    public void testAddVars() {
      _d = new SymbolData("genius");
      VariableData vd = new VariableData("v1", _publicMav, SymbolData.INT_TYPE, true, _d);
      VariableData vd2 = new VariableData("v2", _publicMav, SymbolData.CHAR_TYPE, true, _d);
      VariableData[] toAdd = new VariableData[] {vd, vd2};
      LinkedList<VariableData> myVds = new LinkedList<VariableData>();
      
      
      myVds.addLast(vd);
      myVds.addLast(vd2);
      assertTrue("Should be able to add new vars array", _d.addVars(toAdd));
      assertEquals("Variable list should have 2 variables", myVds, _d.getVars());
      
      
      assertFalse("Should not be able to add same variables again", _d.addVars(toAdd));
      assertEquals("Variable list should not have changed", myVds, _d.getVars());
      
      
      VariableData vd3 = new VariableData("v3", _publicMav, SymbolData.INT_TYPE, true, _d);
      VariableData[] toAdd2 = new VariableData[] {vd3};
      myVds.addLast(vd3);
      
      assertTrue("Should be able to add new variable array", _d.addVars(toAdd2));
      assertEquals("Variable list should now have 3 variables", myVds, _d.getVars());
       
      
      assertTrue("Should be able to add an empty array", _d.addVars(new VariableData[0]));
      assertEquals("Variable list should not have changed by adding empty array", myVds, _d.getVars());
    }
    
    public void testGetVar() {
     _d = new SymbolData("woah");
     VariableData vd = new VariableData("v1", _publicMav, SymbolData.INT_TYPE, false, _d);
     VariableData vd2 = new VariableData("v2", _publicMav, SymbolData.CHAR_TYPE, true, _d);
     VariableData[] toAdd = new VariableData[] {vd, vd2};
     _d.addVars(toAdd);
     
     
     assertEquals("Should return vd", vd, _d.getVar("v1"));
     
     
      assertEquals("Should return null--no variable with that name", null, _d.getVar("whatever"));
    }
    
    public void test_isOuterData() {
      _d = new SymbolData("asdf");      
      SymbolData d2 = new SymbolData("qwer");
      SymbolData d246 = new SymbolData("fdsa");
      d2.setOuterData(_d);
      _d.setOuterData(d246);
      assertTrue("d246 should be outer data of d2", d2.isOuterData(d246));
      assertTrue("d246 should be outer data of _d", _d.isOuterData(d246));
      assertFalse("d2 should not be outer data of d246", d246.isOuterData(d2));
    }
    
    public void testGetInnerClassOrInterface() {
      SymbolData sd1 = new SymbolData("testing");
      SymbolData sd2 = new SymbolData("testing$test123");
      SymbolData sd3 = new SymbolData("testing$test123$test1234");
      sd1.addInnerClass(sd2);
      sd2.addInnerClass(sd3);
      
      
      
      SymbolData result = sd1.getInnerClassOrInterface("test123");
      assertEquals("The correct inner SymbolData should be returned", sd2, result);
      
      
      result = sd2.getInnerClassOrInterface("test1234");
      assertEquals("The correct nested inner SymbolData should be returned", sd3, result);
      
      
      result = sd1.getInnerClassOrInterface("test123.test1234");
      assertEquals("The correct nested inner SymbolData should be returned", sd3, result);

      
      result = sd1.getInnerClassOrInterface("test123$test1234");
      assertEquals("The correct nested inner SymbolData should be returned", sd3, result);

      
      result = sd1.getInnerClassOrInterface("testing.notYourInnerClass");
      assertEquals("null should be returned", null, result);

    
      SymbolData sd4 = new SymbolData("testing");
      SymbolData sd5 = new SymbolData("testing$test123");
      SymbolData sd6 = new SymbolData("testing$test123$2test1234");
      sd4.addInnerInterface(sd5);
      sd5.addInnerClass(sd6);
      
      
      
      result = sd4.getInnerClassOrInterface("test123");
      assertEquals("The correct inner SymbolData should be returned", sd5, result);
      
      
      result = sd5.getInnerClassOrInterface("test1234");
      assertEquals("The correct nested inner SymbolData should be returned", sd6, result);
      
      
      result = sd4.getInnerClassOrInterface("test123.test1234");
      assertEquals("The correct nested inner SymbolData should be returned", sd6, result);

      
      result = sd4.getInnerClassOrInterface("testing.notYourInnerClass");
      assertEquals("null should be returned", null, result);
      
      
      SymbolData sd7 = new SymbolData("test123.myMethod$bob");
      MethodData md = new MethodData("myMethod", _publicMav, new TypeParameter[0], 
                    SymbolData.INT_TYPE, new VariableData[0], new String[0], sd1, 
                    new NullLiteral(SourceInfo.NO_INFO));
      md.addInnerClass(sd7);
      assertEquals("Should return sd7", sd7, md.getInnerClassOrInterface("bob"));
      
      
      SymbolData interfaceInner = new SymbolData("MyInterface$MyInner");
      SymbolData superInner = new SymbolData("MySuper$MyInner");
      
      SymbolData myInterface = new SymbolData("MyInterface");
      myInterface.addInnerClass(interfaceInner);
      interfaceInner.setOuterData(myInterface);
      
      SymbolData mySuper = new SymbolData("MySuper");
      mySuper.addInnerClass(superInner);
      superInner.setOuterData(mySuper);
      
      
      SymbolData me = new SymbolData("Me");
      me.setSuperClass(mySuper);
      me.addInterface(myInterface);
      
      assertEquals("Should return SymbolData.AMBIGUOUS_REFERENCE", SymbolData.AMBIGUOUS_REFERENCE, me.getInnerClassOrInterface("MyInner"));
      
      
      superInner.setMav(_privateMav);
      assertEquals("Should return interfaceInner", interfaceInner, me.getInnerClassOrInterface("MyInner"));
      
      
      interfaceInner.setMav(_privateMav);
      assertEquals("Should return interfaceInner", interfaceInner, me.getInnerClassOrInterface("MyInner"));
 
      
      interfaceInner.setMav(_publicMav);
      SymbolData innerInterfaceInner = new SymbolData("MyInterface$MyInner$Inner");
      innerInterfaceInner.setMav(_privateMav);
      interfaceInner.addInnerClass(innerInterfaceInner);
      innerInterfaceInner.setOuterData(interfaceInner);
      assertEquals("Should return innerInterfaceInner", innerInterfaceInner, me.getInnerClassOrInterface("MyInner.Inner"));
    }
    
    public void testCreateUniqueName() {
      
      MethodData md = new MethodData("foozle", new VariableData[0]);
      md.addVars(md.getParams());
      md.setOuterData(new SymbolData("Fooz"));
      String result = md.createUniqueName("avar");
      assertEquals("the result is correct", "avar", result);
        
      
      VariableData vd = new VariableData("avar", _publicMav, SymbolData.INT_TYPE, true, null);
      md = new MethodData("foozleWithAvar", new VariableData[] {vd});
      vd.setEnclosingData(md);
      md.addVars(md.getParams());
      md.setOuterData(new SymbolData("Fooz"));
      result = md.createUniqueName("avar");
      assertEquals("the result is correct", "avar0", result);
      
      
      SymbolData sd = new SymbolData("RandomClass");
      VariableData vd0 = new VariableData("avar0", _publicMav, SymbolData.DOUBLE_TYPE, true, sd);
      vd.setEnclosingData(sd);
      sd.addVars(new VariableData[] {vd, vd0});
      result = sd.createUniqueName("avar");
      assertEquals("the result is correct", "avar1", result);
      
      
      sd = new SymbolData("RandomClass");
      sd.setMav(_staticMav);
      SymbolData sd2 = new SymbolData("IAteRandomClass");
      sd2.addInnerClass(sd);
      sd.setOuterData(sd2);
      sd.addVar(vd);
      result = sd.createUniqueName("avar");
      assertEquals("the result is correct", "avar0", result);
      
      
      SymbolData sd3 = new SymbolData("RandomsMama");
      sd.setSuperClass(sd3);
      sd3.addVar(vd0);
      result = sd.createUniqueName("avar");
      assertEquals("the result is correct", "avar1", result);
    }

    
    public void testGetNextAnonymousInnerClass() {
      SymbolData sd1 = new SymbolData("silly");
      sd1.setIsContinuation(false);
      
      _d = new BlockData(sd1);
      
      SymbolData anon1 = new SymbolData("silly$1");
      anon1.setIsContinuation(false);
      SymbolData anon2 = new SymbolData("silly$2");
      anon2.setIsContinuation(false);
      sd1.addInnerClass(anon1);
      anon1.setOuterData(sd1);
      _d.addInnerClass(anon2);
      anon2.setOuterData(_d);
      
      assertEquals("Should return anon1", anon1, sd1.getNextAnonymousInnerClass());
      assertEquals("Should return anon2", anon2, _d.getNextAnonymousInnerClass());
      assertEquals("Should return null", null, _d.getNextAnonymousInnerClass());
      assertEquals("Should return null", null, sd1.getNextAnonymousInnerClass());
      
    }

    
  }

}
