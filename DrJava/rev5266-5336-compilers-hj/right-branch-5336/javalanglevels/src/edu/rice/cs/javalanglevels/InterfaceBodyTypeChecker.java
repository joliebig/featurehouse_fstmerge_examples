

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;
import java.io.*;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.*;

import junit.framework.TestCase;


public class InterfaceBodyTypeChecker extends Bob {
  
  
  private SymbolData _symbolData;
  
  
    
  public InterfaceBodyTypeChecker(SymbolData sd, File file, String packageName, LinkedList<String> importedFiles, LinkedList<String> importedPackages, LinkedList<VariableData> vars, LinkedList<Pair<SymbolData, JExpression>> thrown) {
    super(sd, file, packageName, importedFiles, importedPackages, vars, thrown);
    _vars.addAll(sd.getVars());
    _symbolData = sd;
  }

  
  protected Data _getData() {
    return _symbolData;
  }

  
  
  public TypeData forUninitializedVariableDeclarator(UninitializedVariableDeclarator that) {
    _addError("All fields in interfaces must be initialized", that);
    return null;
  }

  
  public TypeData forAbstractMethodDef(AbstractMethodDef that) {
    final TypeData mav_result = that.getMav().visit(this);
    final TypeData[] typeParams_result = makeArrayOfRetType(that.getTypeParams().length);
    for (int i = 0; i < that.getTypeParams().length; i++) {
      typeParams_result[i] = that.getTypeParams()[i].visit(this);
    }
    final TypeData result_result = getSymbolData(that.getResult().getName(), _symbolData, that);
    final TypeData name_result = that.getName().visit(this);
    final TypeData[] params_result = makeArrayOfRetType(that.getParams().length);
    for (int i = 0; i<params_result.length; i++) {
      params_result[i] = getSymbolData(that.getParams()[i].getDeclarator().getType().getName(), _symbolData, that.getParams()[i]);
    }
    final TypeData[] throws_result = makeArrayOfRetType(that.getThrows().length);
    for (int i = 0; i < that.getThrows().length; i++) {
      throws_result[i] = getSymbolData(that.getThrows()[i].getName(), _symbolData, that.getThrows()[i]);
    }
    
    MethodData md = _symbolData.getMethod(that.getName().getText(), params_result);
    if (md == null) {
      throw new RuntimeException("Internal Program Error: Could not find the method " + that.getName().getText() + " in interface " + _symbolData.getName() + ".  Please report this bug.");
    }
    SymbolData.checkDifferentReturnTypes(md, _symbolData, LanguageLevelConverter.OPT.javaVersion());
    return result_result;
  }
  

  
   public TypeData forConcreteMethodDef(ConcreteMethodDef that) {
     _addError("Concrete method definitions cannot appear in interfaces", that);
     return null;
   }
  
   
    public TypeData forTypeOnly(Type that) {
    Data sd = getSymbolData(that.getName(), _symbolData, that);
    if (sd != null) {sd = sd.getOuterData();}
    while (sd != null && !LanguageLevelVisitor.isJavaLibraryClass(sd.getSymbolData().getName())) {
      if (!checkAccessibility(that, sd.getMav(), sd.getName(), sd.getSymbolData(), _symbolData, "class or interface")) {
        return null;
      }
      sd = sd.getOuterData();
    }
    return null;
  }
    
  
   
  public static class InterfaceBodyTypeCheckerTest extends TestCase {
    
    private InterfaceBodyTypeChecker _ibbtc;
    
    private SymbolData _sd1;
    private SymbolData _sd2;
    private SymbolData _sd3;
    private SymbolData _sd4;
    private SymbolData _sd5;
    private SymbolData _sd6;
    private ModifiersAndVisibility _publicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _protectedMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});
    private ModifiersAndVisibility _privateMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"private"});
    private ModifiersAndVisibility _packageMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    private ModifiersAndVisibility _abstractMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract"});
    private ModifiersAndVisibility _finalMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final"});
    
    
    public InterfaceBodyTypeCheckerTest() {
      this("");
    }
    public InterfaceBodyTypeCheckerTest(String name) {
      super(name);



    }
    
    public void setUp() {
      _sd1 = new SymbolData("i.like.monkey");
      _sd2 = new SymbolData("i.like.giraffe");
      _sd3 = new SymbolData("zebra");
      _sd4 = new SymbolData("u.like.emu");
      _sd5 = new SymbolData("");
      _sd6 = new SymbolData("cebu");
      errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();
      LanguageLevelConverter._newSDs.clear();
      _ibbtc = 
        new InterfaceBodyTypeChecker(_sd1, new File(""), "", new LinkedList<String>(), new LinkedList<String>(),
                                     new LinkedList<VariableData>(), new LinkedList<Pair<SymbolData, JExpression>>());
      LanguageLevelConverter.OPT = new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make());
      _ibbtc._importedPackages.addFirst("java.lang");
    }
    
    public void testForUninitializedVariableDeclaratorOnly() {
      VariableData vd1 = new VariableData("Mojo", _publicMav, SymbolData.INT_TYPE, false, _sd1);
      _sd1.addVar(vd1);
      UninitializedVariableDeclarator uvd = 
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                            new Word(SourceInfo.NO_INFO, "Mojo"));
      uvd.visit(_ibbtc);
      _ibbtc.forUninitializedVariableDeclaratorOnly(uvd, SymbolData.INT_TYPE, null);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "All fields in interfaces must be initialized", 
                   errors.get(0).getFirst());
    }
    
    public void testForInitializedVariableDeclaratorOnly() {
      VariableData vd1 = new VariableData("Mojo", _publicMav, SymbolData.INT_TYPE, false, _sd1);
      _sd1.addVar(vd1);
      InitializedVariableDeclarator ivd = 
        new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                          new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                          new Word(SourceInfo.NO_INFO, "Mojo"), 
                                          new IntegerLiteral(SourceInfo.NO_INFO, 1));
      ivd.visit(_ibbtc);
      assertEquals("There should be no errors.", 0, errors.size());
      assertTrue("_vars should contain Mojo.", _ibbtc._vars.contains(vd1));
      ivd = new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                              new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                              new Word(SourceInfo.NO_INFO, "Santa's Little Helper"), 
                                              new IntegerLiteral(SourceInfo.NO_INFO, 1));
      try {
        ivd.visit(_ibbtc);
        fail("Should have thrown a RuntimeException because there's no field named Santa's Little Helper.");
      }
      catch (RuntimeException re) {
        assertEquals("The error message should be correct.", 
                     "Internal Program Error: The field or variable Santa's Little Helper was not found in this block.  Please report this bug.", re.getMessage());
      }
    }
    
    public void testForConcreteMethodDef() {
      FormalParameter[] fps = new FormalParameter[] {
        new FormalParameter(SourceInfo.NO_INFO, 
                            new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                                                new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                                new Word (SourceInfo.NO_INFO, "field1")),
                            false),
        new FormalParameter(SourceInfo.NO_INFO, 
                            new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                                                new PrimitiveType(SourceInfo.NO_INFO, "boolean"), 
                                                                new Word (SourceInfo.NO_INFO, "field2")),
                            false)};
      
      ConcreteMethodDef cmd = 
        new ConcreteMethodDef(SourceInfo.NO_INFO, 
                              _packageMav, 
                              new TypeParameter[0], 
                              new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                              new Word(SourceInfo.NO_INFO, "methodName"),
                              fps,
                              new ReferenceType[0], 
                              new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {
        new ValueReturnStatement(SourceInfo.NO_INFO, 
                                 new IntegerLiteral(SourceInfo.NO_INFO, 5))}));
      
      MethodData md = new MethodData("methodName", 
                                     _packageMav, 
                                     new TypeParameter[0], 
                                     SymbolData.INT_TYPE, 
                                     new VariableData[] { new VariableData(SymbolData.DOUBLE_TYPE), new VariableData(SymbolData.BOOLEAN_TYPE) },
                                     new String[0],
                                     _sd1,
                                     null); 
                                     
      md.getParams()[0].setEnclosingData(md);                               
      md.getParams()[1].setEnclosingData(md);                               

      _sd1.addMethod(md);

      cmd.visit(_ibbtc);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct", "Concrete method definitions cannot appear in interfaces", errors.get(0).getFirst());
    }
    
    public void testForTypeOnly() {
      Type t = new PrimitiveType(SourceInfo.NO_INFO, "double");
      t.visit(_ibbtc);
      assertEquals("There should be no errors", 0, errors.size());
      
      SymbolData sd = new SymbolData("Adam");
      sd.setIsContinuation(false);
      symbolTable.put("Adam", sd);
      sd.setMav(_publicMav);
      t = new ClassOrInterfaceType(SourceInfo.NO_INFO, "Adam", new Type[0]);
      t.visit(_ibbtc);
      assertEquals("There should still be no errors", 0, errors.size());
      
      SymbolData innerSd = new SymbolData("Adam$Wulf");
      innerSd.setIsContinuation(false);
      sd.addInnerClass(innerSd);
      innerSd.setOuterData(sd);
      innerSd.setMav(_publicMav);
      _ibbtc.symbolTable.put("USaigehgihdsgslghdlighs", innerSd);
      t = new ClassOrInterfaceType(SourceInfo.NO_INFO, "Adam.Wulf", new Type[0]);
      t.visit(_ibbtc);
      assertEquals("There should still be no errors", 0, errors.size());
      
      innerSd.setMav(_privateMav);
      t = new ClassOrInterfaceType(SourceInfo.NO_INFO, "Adam.Wulf", new Type[0]);
      t.visit(_ibbtc);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", 
                   "The class or interface Adam.Wulf is private and cannot be accessed from " + 
                     _ibbtc._symbolData.getName(),
                   errors.get(0).getFirst());
      
      sd.setMav(_privateMav);
      innerSd.setMav(_publicMav);
      t = new ClassOrInterfaceType(SourceInfo.NO_INFO, "Adam.Wulf", new Type[0]);
      t.visit(_ibbtc);
      assertEquals("There should be two errors", 2, errors.size());
      assertEquals("The error message should be correct", 
                   "The class or interface Adam is private and cannot be accessed from " + _ibbtc._symbolData.getName(),
                   errors.get(1).getFirst());
    }
  }
}