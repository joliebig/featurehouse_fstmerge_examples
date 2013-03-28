

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;
import java.io.*;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.*;

import junit.framework.TestCase;


public class ClassBodyTypeChecker extends Bob {
  
  
  private SymbolData _symbolData;
  
  
  protected boolean hasConstructor;
  
  
  public ClassBodyTypeChecker(SymbolData sd, File file, String packageName, LinkedList<String> importedFiles, 
                              LinkedList<String> importedPackages, LinkedList<VariableData> vars, 
                              LinkedList<Pair<SymbolData, JExpression>> thrown) {
    super(sd, file, packageName, importedFiles, importedPackages, vars, thrown);
    if (sd == null) throw new RuntimeException("SymbolData is null in new ClassBodyTypeChecker operation");
    _symbolData = sd;
    hasConstructor = false;
    assert _vars == vars;
    
    _vars.addAll(sd.getVars());
    
    LinkedList<VariableData> superVars = sd.getAllSuperVars();
    for (int i = 0; i<superVars.size(); i++) {
      VariableData tempVD = superVars.get(i);
      if (tempVD.isFinal() && tempVD.gotValue())
        thingsThatHaveBeenAssigned.addLast(tempVD);
    }
    _vars.addAll(superVars);
  }
  
  
  protected Data _getData() { return _symbolData; }
  
  
  public TypeData forUninitializedVariableDeclaratorOnly(UninitializedVariableDeclarator that, 
                                                         TypeData type_result, 
                                                         TypeData name_result) {
    Word name = that.getName();
    String text = that.getName().getText();
    VariableData vd = getFieldOrVariable(text, _symbolData, _symbolData, name);
    if (vd == null) {
      throw new RuntimeException("The field " + text + " was not found in " + _symbolData.getName() + ".");
    }
    _vars.addLast(vd);
    return null;
  }

  
  private void _checkReturnType(SymbolData expected, SymbolData actual, ConcreteMethodDef that) {
    
    if (expected == SymbolData.VOID_TYPE) {
      if (actual == null || actual == SymbolData.VOID_TYPE) {
        
      }
    }
    else {
      if (actual == null) {
        _addError("This method is missing a return statement.", that);
      }
    }
  }
  
  
  public TypeData forConstructorDef(ConstructorDef that) {
    hasConstructor = true;
    final TypeData mav_result = that.getMav().visit(this);
    final TypeData[] parameters_result = makeArrayOfRetType(that.getParameters().length);
    final TypeData[] throws_result = makeArrayOfRetType(that.getThrows().length);
    for (int i = 0; i < that.getThrows().length; i++) {
      throws_result[i] = getSymbolData(that.getThrows()[i].getName(), _symbolData, that);     
    }

    
    
    MethodData md = null;
    FormalParameter[] fParams = that.getParameters();
    String[] paramTypes = new String[fParams.length];
    for (int i = 0; i < fParams.length; i++) {
      paramTypes[i] = fParams[i].getDeclarator().getType().getName();
    }
    LinkedList<MethodData> mds = _symbolData.getMethods();
    Iterator<MethodData> iter = mds.iterator();
    while (iter.hasNext()) {
      boolean match = true;
      MethodData tempMd = iter.next();
      if (tempMd.getName().equals(LanguageLevelVisitor.getUnqualifiedClassName(_symbolData.getName()))) {
        
        VariableData[] vds = tempMd.getParams();
        if (paramTypes.length == vds.length) {
          for (int i = 0; i < paramTypes.length; i++) {
            
            if(!vds[i].getType().getName().equals(paramTypes[i]) &&
               !LanguageLevelVisitor.getUnqualifiedClassName(vds[i].getType().getName()).equals(paramTypes[i])) {
              match = false;
              break;
            }
          }
          if (match) {
            md = tempMd;            
            break;
          }
        }
      }
    }
    if (md == null) {
      throw new RuntimeException("The constructor " + LanguageLevelVisitor.getUnqualifiedClassName(_symbolData.getName()) + " was not in the class " + _symbolData.getName() + ".");
    }

    LinkedList<VariableData> ll = new LinkedList<VariableData>();
    VariableData[] vds = md.getParams();
    for (int i = 0; i<vds.length; i++) {
      ll.addLast(vds[i]);
    }
    ll.addAll(cloneVariableDataList(_vars));

    ConstructorBodyTypeChecker btc = new ConstructorBodyTypeChecker(md, _file, _package, _importedFiles, _importedPackages, ll, new LinkedList<Pair<SymbolData, JExpression>>());
    final TypeData body_result = that.getStatements().visit(btc);
    
    
    LinkedList<VariableData> sdVars = _symbolData.getVars();
    for (int i = 0; i<sdVars.size(); i++) { 
      if (!sdVars.get(i).hasValue()) {
        _addError("The final field " + sdVars.get(i).getName() + " has not been initialized.  Make sure you give it a value in this constructor", that);
        return null;
      }
    }
    
    _symbolData.decrementConstructorCount();
    
   
    if (_symbolData.getConstructorCount() > 0) {
      unassignVariableDatas(btc.thingsThatHaveBeenAssigned);
    }
    
    return forConstructorDefOnly(that, mav_result, parameters_result, throws_result, body_result);
  }
  

  
  public TypeData forConstructorDefOnly(ConstructorDef that, TypeData mav_result, TypeData[] parameters_result, TypeData[] throws_result, TypeData body_result) {
    return forJExpressionOnly(that);
  }
  
  
  public TypeData forConcreteMethodDef(ConcreteMethodDef that) {
    final TypeData mav_result = that.getMav().visit(this);
    final TypeData[] typeParams_result = makeArrayOfRetType(that.getTypeParams().length);
    for (int i = 0; i < that.getTypeParams().length; i++) {
      typeParams_result[i] = that.getTypeParams()[i].visit(this);
    }
    final SymbolData result_result = getSymbolData(that.getResult().getName(), _symbolData, that);
    final TypeData name_result = that.getName().visit(this);
    final TypeData[] throws_result = makeArrayOfRetType(that.getThrows().length);
    for (int i = 0; i < that.getThrows().length; i++) {
      throws_result[i] = getSymbolData(that.getThrows()[i].getName(), _symbolData, that.getThrows()[i]);
    }
    
    
    MethodData md = null;
    FormalParameter[] fParams = that.getParams();
    String[] paramTypes = new String[fParams.length];
    for (int i = 0; i < fParams.length; i++) {
      paramTypes[i] = fParams[i].getDeclarator().getType().getName();
    }
    LinkedList<MethodData> mds = _symbolData.getMethods();
    Iterator<MethodData> iter = mds.iterator();
    while (iter.hasNext()) {
      boolean match = false;
      MethodData tempMd = iter.next();
      if (tempMd.getName().equals(that.getName().getText())) {
        match = true;

        
        VariableData[] vds = tempMd.getParams();
        if (paramTypes.length == vds.length) {
          for (int i = 0; i < paramTypes.length; i++) {
            
            if(!vds[i].getType().getName().equals(paramTypes[i]) &&
               !LanguageLevelVisitor.getUnqualifiedClassName(vds[i].getType().getName()).equals(paramTypes[i])) {
              match = false;
              break;
            }
          }
          if (match) {
            md = tempMd;            
            break;
          }
        }
      }
    }
    if (md == null) {
      throw new RuntimeException("Internal Program Error: The method " + that.getName().getText() + " was not in the class " + _symbolData.getName() + ".  Please report this bug.");
    }
    
    LinkedList<VariableData> ll = new LinkedList<VariableData>();
    VariableData[] vds = md.getParams();
    for (int i = 0; i<vds.length; i++) {
      ll.addLast(vds[i]);
    }
    ll.addAll(cloneVariableDataList(_vars));
    
    LinkedList<VariableData> thingsWeAssigned = new LinkedList<VariableData>();
    for (int i = 0; i<_symbolData.getVars().size(); i++) {
      VariableData tempVd = _symbolData.getVars().get(i);
      if (tempVd.gotValue()) { 
        thingsWeAssigned.addLast(tempVd);
      }
    }
    
    BodyTypeChecker btc = new BodyTypeChecker(md, _file, _package, _importedFiles, _importedPackages, ll, 
                                              new LinkedList<Pair<SymbolData, JExpression>>());
    
    TypeData body_result = that.getBody().visit(btc); 
    
    
    if (body_result != null) {body_result = body_result.getSymbolData();}
    _checkReturnType(md.getReturnType(), (SymbolData) body_result, that);
    if (md.getReturnType() != null) {
      
      SymbolData.checkDifferentReturnTypes(md, _symbolData, LanguageLevelConverter.OPT.javaVersion());
    }
    
    
    final TypeData[] params_result = makeArrayOfRetType(that.getParams().length);

    for (int i = 0; i<thingsWeAssigned.size(); i++) {
      thingsWeAssigned.get(i).lostValue();
    }
    
    return result_result;

  }

  
  
  public TypeData forAbstractMethodDef(AbstractMethodDef that) {
    final TypeData mav_result = that.getMav().visit(this);
    final TypeData[] typeParams_result = makeArrayOfRetType(that.getTypeParams().length);
    for (int i = 0; i < that.getTypeParams().length; i++) {
      typeParams_result[i] = that.getTypeParams()[i].visit(this);
    }
    final SymbolData result_result = getSymbolData(that.getResult().getName(), _symbolData, that);
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
      throw new RuntimeException("Internal Program Error: Could not find the method " + that.getName().getText() + " in class " + _symbolData.getName() +".  Please report this bug.");
    }
    SymbolData.checkDifferentReturnTypes(md, _symbolData, LanguageLevelConverter.OPT.javaVersion());

    return result_result;
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
    return forJExpressionOnly(that);
  }
  
   
  public static class ClassBodyTypeCheckerTest extends TestCase {
    
    private ClassBodyTypeChecker _cbbtc;
    
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
    
    
    public ClassBodyTypeCheckerTest() {
      this("");
    }
    public ClassBodyTypeCheckerTest(String name) {
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
      _cbbtc = 
        new ClassBodyTypeChecker(_sd1, new File(""), "", new LinkedList<String>(), new LinkedList<String>(), 
                                 new LinkedList<VariableData>(), new LinkedList<Pair<SymbolData, JExpression>>());
      LanguageLevelConverter.OPT = new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make());
      _cbbtc._importedPackages.addFirst("java.lang");
    }
    
    public void testForUninitializedVariableDeclaratorOnly() {
      VariableData vd1 = new VariableData("Mojo", _publicMav, SymbolData.INT_TYPE, false, _cbbtc._data);
      _sd1.addVar(vd1);
      UninitializedVariableDeclarator uvd = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                                                                new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                                                new Word(SourceInfo.NO_INFO, "Mojo"));

      _cbbtc.forUninitializedVariableDeclaratorOnly(uvd, SymbolData.INT_TYPE, null);
      assertTrue("_vars should contain Mojo.", _cbbtc._vars.contains(vd1));      
    }
    
    public void testForInitializedVariableDeclaratorOnly() {
      VariableData vd1 = new VariableData("Mojo", _publicMav, SymbolData.INT_TYPE, false, _cbbtc._data);
      _sd1.addVar(vd1);
      InitializedVariableDeclarator ivd = new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                                                            new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                                            new Word(SourceInfo.NO_INFO, "Mojo"), 
                                                                            new IntegerLiteral(SourceInfo.NO_INFO, 1));
      ivd.visit(_cbbtc);
      assertEquals("There should be no errors.", 0, errors.size());
      assertTrue("_vars should contain Mojo.", _cbbtc._vars.contains(vd1));
      ivd = new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                              new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                              new Word(SourceInfo.NO_INFO, "Santa's Little Helper"), 
                                              new IntegerLiteral(SourceInfo.NO_INFO, 1));
      try {
        ivd.visit(_cbbtc);
        fail("Should have thrown a RuntimeException because there's no field named Santa's Little Helper.");
      }
      catch (RuntimeException re) {
        assertEquals("The error message should be correct.", "Internal Program Error: The field or variable Santa's Little Helper was not found in this block.  Please report this bug.", re.getMessage());
      }
    }
    
    public void test_checkReturnType() {
      
      _cbbtc._checkReturnType(SymbolData.VOID_TYPE, null, null);
      _cbbtc._checkReturnType(SymbolData.VOID_TYPE, SymbolData.VOID_TYPE, null);
      _cbbtc._checkReturnType(SymbolData.INT_TYPE, SymbolData.INT_TYPE, null);
      _cbbtc._checkReturnType(SymbolData.DOUBLE_TYPE, SymbolData.INT_TYPE, null);
      
      assertEquals("There should be no errors.", 0, errors.size());
      
      
      _cbbtc._checkReturnType(SymbolData.INT_TYPE, null, null);
      assertEquals("There should now be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "This method is missing a return statement.", errors.get(0).getFirst());
      
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
      ConcreteMethodDef cmd = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                                    _packageMav, 
                                                    new TypeParameter[0], 
                                                    new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                    new Word(SourceInfo.NO_INFO, "methodName"),
                                                    fps,
                                                    new ReferenceType[0], 
                                                    new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {
        new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5) )}));
      MethodData md = new MethodData("methodName", 
                                     _packageMav, 
                                     new TypeParameter[0], 
                                     SymbolData.INT_TYPE, 
                                     new VariableData[] { new VariableData(SymbolData.DOUBLE_TYPE), new VariableData(SymbolData.BOOLEAN_TYPE) },
                                     new String[0],
                                     _sd1,
                                     null); 
      _sd1.addMethod(md);
      cmd.visit(_cbbtc);
      assertEquals("There should be no errors.", 0, errors.size());
      
      cmd = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                                    _packageMav, 
                                                    new TypeParameter[0], 
                                                    new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                    new Word(SourceInfo.NO_INFO, "Selma"),
                                                    fps,
                                                    new ReferenceType[0], 
                                                    new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {
                                                          new ValueReturnStatement(SourceInfo.NO_INFO, 
                                                                                   new IntegerLiteral(SourceInfo.NO_INFO, 5))}));
      
      try {
        cmd.visit(_cbbtc);
        fail("Should have thrown a RuntimeException because there's no method named Selma.");
      }
      catch (RuntimeException re) {
        assertEquals("The error message should be correct.", "Internal Program Error: The method Selma was not in the class i.like.monkey.  Please report this bug.", re.getMessage());
      }
      
      
      
      PrimitiveType intt = new PrimitiveType(SourceInfo.NO_INFO, "int");
      UninitializedVariableDeclarator uvd = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "i"));

      Statement s = new ValueReturnStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")));
      ConcreteMethodDef cmd0 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], intt,
                                                    new Word(SourceInfo.NO_INFO, "invalidMethod"), new FormalParameter[0],
                                                    new ReferenceType[0], new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), s}));
      VariableData vd = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      MethodData md0 = new MethodData("invalidMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[0], new String[0], _sd1, cmd0);
      _sd1.addMethod(md0);
      vd.setEnclosingData(md0);
      md0.addVar(vd);
      
      _cbbtc = new ClassBodyTypeChecker(_sd1, _cbbtc._file, _cbbtc._package, _cbbtc._importedFiles, _cbbtc._importedPackages, new LinkedList<VariableData>(), new LinkedList<Pair<SymbolData, JExpression>>());
      cmd0.visit(_cbbtc);
      assertEquals("There should be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "You cannot use i because it may not have been given a value", errors.get(0).getFirst());


      
      
      Expression te = new LessThanExpression(SourceInfo.NO_INFO,
                                             new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
                                             new IntegerLiteral(SourceInfo.NO_INFO, 5));
      Statement ts = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));
      IfThenStatement ift = new IfThenStatement(SourceInfo.NO_INFO, te, ts);
      
      
      FormalParameter param = new FormalParameter(SourceInfo.NO_INFO, new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "j")), false);
      BracedBody bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), ift,
        new ValueReturnStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")))});
      
      ConcreteMethodDef cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                                     new ReferenceType[0], bb);

      VariableData vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      VariableData vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      MethodData md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      _sd1.addMethod(md1);
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);
      md1.addVar(vd1);
      md1.addVar(vd2);
      
      _cbbtc = new ClassBodyTypeChecker(_sd1, _cbbtc._file, _cbbtc._package, _cbbtc._importedFiles, _cbbtc._importedPackages, new LinkedList<VariableData>(), new LinkedList<Pair<SymbolData, JExpression>>());
      cmd1.visit(_cbbtc);
      
      assertEquals("There should still be 1 error", 1, errors.size());  
      assertEquals("The error message should be correct", "You cannot use i because it may not have been given a value", 
                   errors.get(0).getFirst());
      
      
      s = new ValueReturnStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")));
      VariableDeclaration i = new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd});
      ExpressionStatement se = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 2)));
      ExpressionStatement se2 = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 5)));
      
      BracedBody b = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {i, se, se2, s});
      ConcreteMethodDef cmd2 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], intt,
                                                    new Word(SourceInfo.NO_INFO, "doubleAssignmentMethod"), new FormalParameter[0],
                                                    new ReferenceType[0], b);
      
      VariableData vdi = new VariableData("i", _finalMav, SymbolData.INT_TYPE, false, null);
      MethodData md2 = new MethodData("doubleAssignmentMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[0], new String[0], _sd1, cmd2);
      _sd1.addMethod(md2);
      vdi.setEnclosingData(md2);

      md2.addVar(vdi);

      _cbbtc = new ClassBodyTypeChecker(_sd1, _cbbtc._file, _cbbtc._package, _cbbtc._importedFiles, _cbbtc._importedPackages, new LinkedList<VariableData>(), new LinkedList<Pair<SymbolData, JExpression>>());
      cmd2.visit(_cbbtc);
      assertEquals("There should now be 2 error2", 2, errors.size());
      assertEquals("The error message should be correct", 
                   "You cannot assign a value to i because it is immutable and has already been given a value", 
                   errors.get(1).getFirst());
 
       
      
      te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
       new IntegerLiteral(SourceInfo.NO_INFO, 5));
      Statement assignStatement = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));
      Statement returnStatement = new ValueReturnStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")));
      ts = new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignStatement, returnStatement}));
      ift = new IfThenStatement(SourceInfo.NO_INFO, te, ts);
      
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), 
        ift, 
        new ValueReturnStatement(SourceInfo.NO_INFO, 
                                 new IntegerLiteral(SourceInfo.NO_INFO, 5))});
      
      ConcreteMethodDef cmd4 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod3"), new FormalParameter[] {param}, 
                                   new ReferenceType[0], bb);

      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      md1 = new MethodData("myMethod3", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                           new VariableData[] {vd1}, new String[0], _sd1, cmd4);
      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      
      _sd1.addMethod(md1);
      _cbbtc = new ClassBodyTypeChecker(_sd1, _cbbtc._file, _cbbtc._package, _cbbtc._importedFiles, _cbbtc._importedPackages, new LinkedList<VariableData>(), new LinkedList<Pair<SymbolData, JExpression>>());
      

      md1.addBlock(new BlockData(md1));
      cmd4.visit(_cbbtc);
      assertEquals("There should still be 2 errors", 2, errors.size());
    }
    
    public void testCheckDifferentReturnTypes() {
      SymbolData superSd = new SymbolData("aiya");
      _sd1.setSuperClass(superSd);
      MethodData md3 = new MethodData("methodName",
                                      _publicMav,
                                      new TypeParameter[0],
                                      SymbolData.CHAR_TYPE,
                                      new VariableData[0],
                                      new String[0],
                                      null,
                                      null);
      superSd.addMethod(md3);
      MethodData md4 = new MethodData("methodName",
                                     _publicMav,
                                     new TypeParameter[0],
                                     SymbolData.INT_TYPE,
                                     new VariableData[0],
                                     new String[0],
                                     superSd,
                                     null);
      MethodDef mDef = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                             new Word(SourceInfo.NO_INFO, "methodName"), new FormalParameter[0], new ReferenceType[0], 
                                             new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 76))}));
      _sd1.addMethod(md4);
      _cbbtc._symbolData = _sd1;
      mDef.visit(_cbbtc);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct", "methodName() in " + _sd1.getName() + " cannot override methodName() in aiya; attempting to use different return types",
                   errors.get(0).getFirst());
      mDef = new AbstractMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                   new Word(SourceInfo.NO_INFO, "methodName"), new FormalParameter[0], new ReferenceType[0]);
      
      mDef.visit(_cbbtc);
      assertEquals("There should be two errors.", 2, errors.size());
      assertEquals("The error message should be correct", "methodName() in " + _sd1.getName() + " cannot override methodName() in aiya; attempting to use different return types",
                   errors.get(1).getFirst());
    }
    
    public void testForTypeOnly() {
      Type t = new PrimitiveType(SourceInfo.NO_INFO, "double");
      t.visit(_cbbtc);
      assertEquals("There should be no errors", 0, errors.size());
      
      SymbolData sd = new SymbolData("Adam");
      sd.setIsContinuation(false);
      symbolTable.put("Adam", sd);
      sd.setMav(_publicMav);
      t = new ClassOrInterfaceType(SourceInfo.NO_INFO, "Adam", new Type[0]);
      t.visit(_cbbtc);
      assertEquals("There should still be no errors", 0, errors.size());
      
      SymbolData innerSd = new SymbolData("Adam$Wulf");
      innerSd.setIsContinuation(false);
      sd.addInnerClass(innerSd);
      innerSd.setOuterData(sd);
      innerSd.setMav(_publicMav);
      _cbbtc.symbolTable.put("USaigehgihdsgslghdlighs", innerSd);
      t = new ClassOrInterfaceType(SourceInfo.NO_INFO, "Adam.Wulf", new Type[0]);
      t.visit(_cbbtc);
      assertEquals("There should still be no errors", 0, errors.size());
      
      innerSd.setMav(_privateMav);
      t = new ClassOrInterfaceType(SourceInfo.NO_INFO, "Adam.Wulf", new Type[0]);
      t.visit(_cbbtc);
      
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "The class or interface Adam.Wulf is private and cannot be accessed from " + _cbbtc._symbolData.getName(),
                   errors.get(0).getFirst());
      
      sd.setMav(_privateMav);
      innerSd.setMav(_publicMav);
      t = new ClassOrInterfaceType(SourceInfo.NO_INFO, "Adam.Wulf", new Type[0]);
      t.visit(_cbbtc);
      assertEquals("There should be two errors", 2, errors.size());
      assertEquals("The error message should be correct", "The class or interface Adam is private and cannot be accessed from " + _cbbtc._symbolData.getName(),
                   errors.get(1).getFirst());
    }
    
    public void testForConstructorDef() {
      VariableDeclaration vd = new VariableDeclaration(SourceInfo.NO_INFO, _finalMav, new VariableDeclarator[] {new UninitializedVariableDeclarator(SourceInfo.NO_INFO, new PrimitiveType(SourceInfo.NO_INFO, "int"), new Word(SourceInfo.NO_INFO, "i"))});
      ExpressionStatement se = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 1)));      
      BracedBody cbb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {se});
      ConstructorDef cd = new ConstructorDef(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "Jimes"), _publicMav, new FormalParameter[0], new ReferenceType[0], cbb);
      BracedBody b = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {vd, cd});
      ClassDef classDef = new ClassDef(SourceInfo.NO_INFO, _publicMav, new Word(SourceInfo.NO_INFO, "Jimes"), new TypeParameter[0], new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), new ReferenceType[0], b);

      SymbolData sd = new SymbolData("Jimes");
      VariableData vData = new VariableData("i", _finalMav, SymbolData.INT_TYPE, false, sd);
      _cbbtc._file = new File("Jimes.dj0");
      sd.setMav(_publicMav);
      sd.setIsContinuation(false);
      sd.addVar(vData);
      SymbolData sd2 = new SymbolData("java.lang.Object");
      sd2.setIsContinuation(false);
      sd2.setMav(_publicMav);
      sd2.setPackage("java.lang");
      sd.setSuperClass(sd2);
      symbolTable.put("Jimes", sd);
      symbolTable.put("java.lang.Object", sd2);
      MethodData md = new MethodData("Jimes", _publicMav, new TypeParameter[0], sd, new VariableData[0], new String[0], sd, cd);
      MethodData objMd = new MethodData("Object", _publicMav, new TypeParameter[0], sd2, new VariableData[0], new String[0], sd2, cd);
      sd.addMethod(md);
      
      
      
      classDef.visit(_cbbtc);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("Error message should be correct", "You must invoke one of java.lang.Object's constructors here.  You can either explicitly invoke one of its exisitng constructors or add a constructor with signature: Object().", errors.getLast().getFirst());

      
      sd2.addMethod(objMd); 
      vData.lostValue();

      classDef.visit(_cbbtc);
      assertEquals("There should still be one error", 1, errors.size());

      
      
      vData.lostValue();
      cbb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {});
      cd = new ConstructorDef(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "Jimes"), _publicMav, new FormalParameter[0], new ReferenceType[0], cbb);
      b = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {vd, cd});
      classDef = new ClassDef(SourceInfo.NO_INFO, _publicMav, new Word(SourceInfo.NO_INFO, "Jimes"), new TypeParameter[0], new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), new ReferenceType[0], b);
 
      classDef.visit(_cbbtc);
      assertEquals("There should be 2 errors now", 2, errors.size());
      assertEquals("The error message should be correct", "The final field i has not been initialized.  Make sure you give it a value in this constructor", errors.getLast().getFirst());
      
      
      vData = new VariableData("j", _finalMav, SymbolData.INT_TYPE, false, sd);
      sd.setVars(new LinkedList<VariableData>());
      sd.addVar(vData);

      LinkedList<VariableData> vs = new LinkedList<VariableData>();
      vs.addLast(vData);
      _cbbtc = new ClassBodyTypeChecker(sd, new File(""), "", new LinkedList<String>(), new LinkedList<String>(), vs, new LinkedList<Pair<SymbolData, JExpression>>());
      ExpressionStatement assign = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")), new IntegerLiteral(SourceInfo.NO_INFO, 45)));
      b = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new ExpressionStatement(SourceInfo.NO_INFO, new SimpleThisConstructorInvocation(SourceInfo.NO_INFO, new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]))), assign});
      cd = new ConstructorDef(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "name"), _publicMav, new FormalParameter[0], new ReferenceType[0], b);
      cd.visit(_cbbtc);
      assertEquals("There should now be 3 errors", 3, errors.size());
      assertEquals("The error message should be correct","You cannot assign a value to j because it is immutable and has already been given a value" , errors.getLast().getFirst());


    }
    
  }
}