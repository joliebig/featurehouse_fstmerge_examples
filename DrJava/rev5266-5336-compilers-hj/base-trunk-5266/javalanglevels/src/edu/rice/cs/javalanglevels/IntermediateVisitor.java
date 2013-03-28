

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.*;
import java.util.*;
import java.io.*;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.*;

import junit.framework.TestCase;



public class IntermediateVisitor extends LanguageLevelVisitor {
  
  
  public IntermediateVisitor(File file, 
                             String packageName, 
                             LinkedList<String> importedFiles, 
                             LinkedList<String> importedPackages,
                             LinkedList<String> classNamesInThisFile, 
                             Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations) {
    super(file, packageName, importedFiles, importedPackages, classNamesInThisFile, continuations);
  }
  
  
  public IntermediateVisitor(File file) {
    this(file, 
         new LinkedList<Pair<String, JExpressionIF>>(),
         new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>(), 
         new LinkedList<Pair<LanguageLevelVisitor, SourceFile>>());
  }
  
  
  public IntermediateVisitor(File file, 
                             LinkedList<Pair<String, JExpressionIF>> errors,
                             Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations,
                             LinkedList<Pair<LanguageLevelVisitor, SourceFile>> visitedFiles) {
    super(file, "", new LinkedList<String>(), new LinkedList<String>(), new LinkedList<String>(), continuations);
    this.errors = errors;
    this.visitedFiles= visitedFiles;
    _hierarchy = new Hashtable<String, TypeDefBase>(); 
  }
  
  
  protected void handleInnerClassDef(InnerClassDef that, Data data, String name) {

    forInnerClassDefDoFirst(that);
    if (prune(that)) return;
    
    that.getMav().visit(this);
    that.getName().visit(this);
    for (int i = 0; i < that.getTypeParameters().length; i++) that.getTypeParameters()[i].visit(this);
    that.getSuperclass().visit(this);  
    
    for (int i = 0; i < that.getInterfaces().length; i++) that.getInterfaces()[i].visit(this);
    
    SymbolData sd = defineInnerSymbolData(that, name, data);
    if (sd != null) { 
      that.getBody().visit(new ClassBodyIntermediateVisitor(sd, 
                                                            _file, 
                                                            _package,
                                                            _importedFiles, 
                                                            _importedPackages, 
                                                            _classNamesInThisFile, 
                                                            continuations));
      
      createAccessors(sd, _file);
      createToString(sd);
      createHashCode(sd);
      createEquals(sd);
    }
    
    
    
    forInnerClassDefOnly(that);
  }
  
  
  protected void handleInnerInterfaceDef(InnerInterfaceDef that, Data data, String name) {

    forInnerInterfaceDefDoFirst(that);
    if (prune(that)) return;
    
    that.getMav().visit(this);
    that.getName().visit(this);
        
    
    assert that.getTypeParameters().length == 0;

    for (int i = 0; i < that.getInterfaces().length; i++) that.getInterfaces()[i].visit(this);
    
    SymbolData sd = defineInnerSymbolData(that, name, data);
    if (sd != null) { 
      that.getBody().visit(new InterfaceBodyIntermediateVisitor(sd, 
                                                                _file, 
                                                                _package,
                                                                _importedFiles, 
                                                                _importedPackages,
                                                                _classNamesInThisFile, 
                                                                continuations));
    }
    
    forInnerInterfaceDefOnly(that);
  }
  
  
  public Void forModifiersAndVisibilityDoFirst(ModifiersAndVisibility that) {
    String[] modifiersAndVisibility = that.getModifiers();
    StringBuffer sb = new StringBuffer();
    String temp;
    int count = 0;    
    for(int i = 0; i < modifiersAndVisibility.length; i++) {
      temp = modifiersAndVisibility[i];
      if (! temp.equals("final") && ! temp.equals("abstract") && ! temp.equals("public") && ! temp.equals("private") && 
          ! temp.equals("protected") && ! temp.equals("static")) {
        sb.append(" \"" + temp + "\"");
        count++;
      }
    }
    
    temp = "The keyword";
    if (sb.length() > 0) {
      if (count > 1)  temp = temp + "s";
      _addAndIgnoreError(temp + sb.toString() + " cannot be used at the Intermediate level", that);
      return null;
    }
    return super.forModifiersAndVisibilityDoFirst(that);
  }
  







  
  public Void forInnerClassDefDoFirst(InnerClassDef that) {

    return null;
  }
  
  
    public Void forInnerInterfaceDefDoFirst(InnerInterfaceDef that) {

    return null;
  }
  





  





  





  





  





  





  





  





  
  
  public Void forSynchronizedStatementDoFirst(SynchronizedStatement that) {
    _addError("Synchronized statements cannot be used at the Intermediate level", that);
    return null;
  }
  





  










  
  
  public Void forTypeParameterDoFirst(TypeParameter that) {
    _addError("Type Parameters cannot be used at the Intermediate level", that);
    return null;
  }
  












  





  





  





  
  
  
  private boolean _isClassInCurrentFile(String className) {
    Iterator<String> iter = _classNamesInThisFile.iterator();
    while (iter.hasNext()) {
      String s = iter.next();
      if (s.equals(className) || s.endsWith("." + className)) {
        return true;
      }
    }
    return false;   
  }  
  
  
  public Void forClassDef(ClassDef that) {    
    forClassDefDoFirst(that);
    if (prune(that)) return null;
    
    boolean isTestCase = false;  
    String className = getQualifiedClassName(that.getName().getText());
    SymbolData sd = defineSymbolData(that, className);
    
    if (sd != null) {
    
      String superName = that.getSuperclass().getName();
      if (superName.equals("TestCase") || superName.equals("junit.framework.TestCase")) {
        isTestCase = true;
        if (! _importedPackages.contains("junit.framework") && ! _importedFiles.contains("junit.framework.TestCase")) {

          _importedFiles.addLast("junit.framework.TestCase");
          sd.setHasAutoGeneratedJunitImport(true);
        }
        
        getSymbolData("junit.framework.TestCase", that.getSourceInfo(), true, false, true, false); 

      }
    }
    
    that.getMav().visit(this);
    that.getName().visit(this);
    that.getSuperclass().visit(this);
    
    
    if (isTestCase) sd.addModifier("public");
    
    
    for (int i = 0; i < that.getTypeParameters().length; i++) that.getTypeParameters()[i].visit(this);
    
    for (int i = 0; i < that.getInterfaces().length; i++) that.getInterfaces()[i].visit(this);
    
    if (sd != null) {
      that.getBody().visit(new ClassBodyIntermediateVisitor(sd, _file, _package, _importedFiles, _importedPackages,
                                                            _classNamesInThisFile, continuations));
      createAccessors(sd, _file);
      createToString(sd);
      createHashCode(sd);
      createEquals(sd);
    }
    forClassDefOnly(that);
    _classesToBeParsed.remove(className);
    return null;
  }
  
  
  public Void forInterfaceDef(InterfaceDef that) {
    forInterfaceDefDoFirst(that);
    if (prune(that)) return null;
    String className = getQualifiedClassName(that.getName().getText());
    
    
    assert that.getTypeParameters().length == 0;
    
    for (int i = 0; i < that.getInterfaces().length; i++) that.getInterfaces()[i].visit(this);

    SymbolData sd = defineSymbolData(that, className);
    if (sd != null) {
      sd.setInterface(true);
      that.getBody().visit(new InterfaceBodyIntermediateVisitor(sd, _file, _package, _importedFiles, _importedPackages, 
                                                                _classNamesInThisFile, continuations));
    }
    that.getMav().visit(this);
    that.getName().visit(this);
    forInterfaceDefOnly(that);
    _classesToBeParsed.remove(className);
    return null;
  }
  
  
  public Void forShiftAssignmentExpressionDoFirst(ShiftAssignmentExpression that) {
    _addAndIgnoreError("Shift assignment operators cannot be used at any language level", that);
    return null;
  }
  public Void forBitwiseAssignmentExpressionDoFirst(BitwiseAssignmentExpression that) {
    _addAndIgnoreError("Bitwise operators cannot be used at any language level", that);
    return null;
  }
  public Void forBitwiseBinaryExpressionDoFirst(BitwiseBinaryExpression that) {
    _addAndIgnoreError("Bitwise binary expressions cannot be used at any language level", that);
    return null;
  }
  public Void forBitwiseOrExpressionDoFirst(BitwiseOrExpression that) {
    _addAndIgnoreError("Bitwise or expressions cannot be used at any language level." + 
                       "  Perhaps you meant to compare two values using regular or (||)", that);
    return null;
  }
  public Void forBitwiseXorExpressionDoFirst(BitwiseXorExpression that) {
    _addAndIgnoreError("Bitwise xor expressions cannot be used at any language level", that);
    return null;
  }
  public Void forBitwiseAndExpressionDoFirst(BitwiseAndExpression that) {
    _addAndIgnoreError("Bitwise and expressions cannot be used at any language level." + 
                       "  Perhaps you meant to compare two values using regular and (&&)", that);
    return null;
  }
  public Void forBitwiseNotExpressionDoFirst(BitwiseNotExpression that) {
    _addAndIgnoreError("Bitwise not expressions cannot be used at any language level." + 
                       "  Perhaps you meant to negate this value using regular not (!)", that);
    return null;
  }
  public Void forShiftBinaryExpressionDoFirst(ShiftBinaryExpression that) {
    _addAndIgnoreError("Bit shifting operators cannot be used at any language level", that);
    return null;
  }
  public Void forBitwiseNotExpressionDoFirst(ShiftBinaryExpression that) {
    _addAndIgnoreError("Bitwise operators cannot be used at any language level", that);
    return null;
  }
  























  
  
  protected VariableData[] llVariableDeclaration2VariableData(VariableDeclaration vd, Data enclosingData) {
    return super._variableDeclaration2VariableData(vd, enclosingData);
  }
  
  
  public void anonymousClassInstantiationHelper(AnonymousClassInstantiation that, Data enclosing, SymbolData superC) {
    that.getArguments().visit(this); 
    
    
    SymbolData sd = new SymbolData(getQualifiedClassName(enclosing.getSymbolData().getName()) + "$" + 
                                   enclosing.getSymbolData().preincrementAnonymousInnerClassNum());
    enclosing.addInnerClass(sd);
    sd.setOuterData(enclosing);
    sd.setSuperClass(superC); 
    sd.setPackage(_package);
    
    createToString(sd);
    createHashCode(sd);
    createEquals(sd);
    
    
    
    that.getBody().visit(new ClassBodyIntermediateVisitor(sd, _file, _package, _importedFiles, _importedPackages, 
                                                          _classNamesInThisFile, continuations));
  }
  
  
  public void simpleAnonymousClassInstantiationHelper(SimpleAnonymousClassInstantiation that, Data data) {
    forSimpleAnonymousClassInstantiationDoFirst(that);
    if (prune(that)) return;
    
    
    SymbolData superC = getSymbolData(that.getType().getName(), that.getSourceInfo());
    
    anonymousClassInstantiationHelper(that, data, superC);
    
    forSimpleAnonymousClassInstantiationOnly(that);
  }
  
  
  public void complexAnonymousClassInstantiationHelper(ComplexAnonymousClassInstantiation that, Data data) {
    forComplexAnonymousClassInstantiationDoFirst(that);
    if (prune(that)) return;
    
    
    that.getEnclosing().visit(this);
    
    
    
    
    
    anonymousClassInstantiationHelper(that, data, null);
    
    
    forComplexAnonymousClassInstantiationOnly(that);
  }
  
  
  
  public static class IntermediateVisitorTest extends TestCase {
    
    private IntermediateVisitor _iv;
    
    private SymbolData _sd1;
    private SymbolData _sd2;
    private SymbolData _sd3;
    private SymbolData _sd4;
    private SymbolData _sd5;
    private SymbolData _sd6;
    private ModifiersAndVisibility _publicMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] { "public" });
    private ModifiersAndVisibility _protectedMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] { "protected" });
    private ModifiersAndVisibility _privateMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] { "private" });
    private ModifiersAndVisibility _packageMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    private ModifiersAndVisibility _abstractMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract"});
    private ModifiersAndVisibility _privateAbstractMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract", "private"});  
    private ModifiersAndVisibility _staticMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"static"});
    private ModifiersAndVisibility _finalMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final"});
    
    public IntermediateVisitorTest() { this(""); }
    public IntermediateVisitorTest(String name) {
      super(name);



    }
    
    public void setUp() {
      
      errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();
      LanguageLevelConverter._newSDs.clear();
      LanguageLevelConverter.OPT = new Options(JavaVersion.JAVA_5, IterUtil.make(new File("lib/buildlib/junit.jar")));
      visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, SourceFile>>();      
      _hierarchy = new Hashtable<String, TypeDefBase>();
      
      _iv = new IntermediateVisitor(new File(""),
                                    errors,
                                    continuations, 
                                    new LinkedList<Pair<LanguageLevelVisitor, SourceFile>>());
      LanguageLevelConverter.OPT = new Options(JavaVersion.JAVA_5, IterUtil.make(new File("lib/buildlib/junit.jar")));
      _iv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();
      _iv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      _iv._resetNonStaticFields();
      _iv._importedPackages.addFirst("java.lang");
      _errorAdded = false;
      _sd1 = new SymbolData("i.like.monkey");
      _sd2 = new SymbolData("i.like.giraffe");
      _sd3 = new SymbolData("zebra");
      _sd4 = new SymbolData("u.like.emu");
      _sd5 = new SymbolData("");
      _sd6 = new SymbolData("cebu");
    }
    
    public void testForModifiersAndVisibilityDoFirst() {
      
      
      _iv.forModifiersAndVisibilityDoFirst(_abstractMav);
      _iv.forModifiersAndVisibilityDoFirst(_publicMav);
      _iv.forModifiersAndVisibilityDoFirst(_privateMav);
      _iv.forModifiersAndVisibilityDoFirst(_protectedMav);
      _iv.forModifiersAndVisibilityDoFirst(_staticMav);
      
      ModifiersAndVisibility mavs = new ModifiersAndVisibility(SourceInfo.NO_INFO, 
                                                               new String[] {"private", "static"});
      _iv.forModifiersAndVisibilityDoFirst(mavs);
      assertEquals("there should still be 0 errors", 0, errors.size());
      
      
      
      _iv.forModifiersAndVisibilityDoFirst(_finalMav);



      
      ModifiersAndVisibility mavs2 = new ModifiersAndVisibility(SourceInfo.NO_INFO, 
                                                                new String[] {"private", "final"});
      
      _iv.forModifiersAndVisibilityDoFirst(mavs2);
      assertEquals("There should still be 0 errors", 0, errors.size());
      
      ModifiersAndVisibility mavs3 = new ModifiersAndVisibility(SourceInfo.NO_INFO, 
                                                                new String[] {"final", "native"});
      
      _iv.forModifiersAndVisibilityDoFirst(mavs3);
      assertEquals("There should now be 1 errors", 1, errors.size());
      assertEquals("The error message should be correct for 1 bad modifier:",
                   "The keyword \"native\" cannot be used at the Intermediate level", 
                   errors.get(0).getFirst());
    }
    
    public void testForClassDefDoFirst() {
      
      ClassDef cd0 = new ClassDef(SourceInfo.NO_INFO, _publicMav, new Word(SourceInfo.NO_INFO, "Lisa"),
                                  new TypeParameter[0], 
                                  new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), 
                                  new ReferenceType[0], 
                                  new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      _iv.forClassDefDoFirst(cd0);
      assertEquals("should be no errors", 0, errors.size());
      
      
      ClassDef cd1 = new ClassDef(SourceInfo.NO_INFO, _publicMav, 
                                  new Word(SourceInfo.NO_INFO, "Test"), new TypeParameter[0], JExprParser.NO_TYPE,
                                  new ReferenceType[0], new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      _iv.forClassDefDoFirst(cd1);
      assertEquals("there should still be 0 errors", 0, errors.size());
      
      
      ClassDef cd2 = 
        new ClassDef(SourceInfo.NO_INFO, _publicMav, 
                     new Word(SourceInfo.NO_INFO, "Test"), new TypeParameter[0],
                     new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), 
                     new ReferenceType[] {new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0])}, 
                     new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      
      _iv.forClassDefDoFirst(cd2);
      assertEquals("there should still be 0 errors", 0, errors.size());
    }
    
    public void testForFormalParameterDoFirst() {
      PrimitiveType pt = new PrimitiveType(SourceInfo.NO_INFO, "int");
      Word w = new Word(SourceInfo.NO_INFO, "param");
      UninitializedVariableDeclarator uvd = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, pt, w);
      
      
      FormalParameter fp = new FormalParameter(SourceInfo.NO_INFO, uvd, false);
      _iv.forFormalParameterDoFirst(fp);
      assertEquals("should be no errors", 0, errors.size());
      
      
      FormalParameter fp2 = new FormalParameter(SourceInfo.NO_INFO, uvd, true);  
      _iv.forFormalParameterDoFirst(fp2);
      assertEquals("should be no errors", 0, errors.size());
    }
    
    public void test_NotAllowed() {
      SourceInfo noInfo = SourceInfo.NO_INFO;
      Word w = new Word(SourceInfo.NO_INFO, "word");
      TypeParameter[] tps = new TypeParameter[0];
      ReferenceType[] rts = new ReferenceType[0];
      BracedBody emptyBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      ClassOrInterfaceType superClass = new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]);
      FormalParameter[] fps = new FormalParameter[0];
      CompoundWord cw = new CompoundWord(noInfo, new Word[0]);
      Statement stmt = new EmptyStatement(noInfo);
      Expression e = new NullLiteral(noInfo);
      Block b = new Block(noInfo, emptyBody);
      
      TypeVariable tv = new TypeVariable(noInfo, "name");
      
      InnerInterfaceDef ii = new InnerInterfaceDef(noInfo, _publicMav, w, tps, rts, emptyBody);
      InnerClassDef ic = new InnerClassDef(noInfo, _publicMav, w, tps, superClass, rts, emptyBody);
      
      StaticInitializer si = new StaticInitializer(noInfo, b);
      LabeledStatement ls = new LabeledStatement(noInfo, new Word(noInfo, "label"), stmt);
      SwitchStatement ss = new SwitchStatement(noInfo, e, new SwitchCase[0]);
      WhileStatement ws = new WhileStatement(noInfo, e, stmt);
      DoStatement ds = new DoStatement(noInfo, stmt, e);
      ForStatement fs = new ForStatement(noInfo, new UnparenthesizedExpressionList(noInfo, new Expression[] {e}), 
                                         e, new UnparenthesizedExpressionList(noInfo, new Expression[] {e}),
                                         stmt);
      BreakStatement bs = new UnlabeledBreakStatement(noInfo);
      ContinueStatement cs = new UnlabeledContinueStatement(noInfo);
      SynchronizedStatement syncs = new SynchronizedStatement(noInfo, e, b);
      TypeParameter tp = new TypeParameter(noInfo, tv, superClass);
      ArrayInitializer ai = new ArrayInitializer(noInfo, new VariableInitializerI[0]);
      ArrayType at = new ArrayType(noInfo, "myName", tv);
      VoidReturn vr = new VoidReturn(noInfo, "string");
      ConditionalExpression ce = new ConditionalExpression(noInfo, e, e, e);
      
      BracedBody hasBitOperator = 
        new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] { 
        new ExpressionStatement(SourceInfo.NO_INFO, 
                                new BitwiseOrAssignmentExpression(SourceInfo.NO_INFO, 
                                                                  new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                                          new Word(SourceInfo.NO_INFO, 
                                                                                                   "i")), 
                                                                  new IntegerLiteral(SourceInfo.NO_INFO, 5)))});
      
      TryCatchStatement tcs = new NormalTryCatchStatement(noInfo, b, new CatchBlock[0]);
      
      si.visit(_iv);
      assertEquals("After visiting static initializer, errors should still be 0", 0, errors.size());
      
      ii.visit(_iv);
      assertEquals("After visiting inner interface, errors should still be 0", 0, errors.size());
      
      ic.visit(_iv);
      assertEquals("After visiting inner class, errors should still be 0", 0, errors.size());
      
      ls.visit(_iv);
      assertEquals("After visiting labeled statment, errors should still be 0", 0, errors.size());
      
      ss.visit(_iv);
      assertEquals("After visiting switch statment, errors should still be 0", 0, errors.size());
      
      ws.visit(_iv);
      assertEquals("After visiting while statment, errors should still be 0", 0, errors.size());
      
      ds.visit(_iv);
      assertEquals("After visiting do statment, errors should still be 0", 0, errors.size());
      
      fs.visit(_iv);
      assertEquals("After visiting for statment, errors should still be 0", 0, errors.size());
      
      bs.visit(_iv);
      assertEquals("After visiting break statment, errors should still be 0", 0, errors.size());
      
      cs.visit(_iv);
      assertEquals("After visiting continue statment, errors should still be 0", 0, errors.size());
      
      syncs.visit(_iv);
      assertEquals("After visiting synchronized statment, errors should now be 1", 1, errors.size());
      assertEquals("SynchronizedStatement is not allowed", 
                   "Synchronized statements cannot be used at the Intermediate level", 
                   errors.getLast().getFirst());
      
      tp.visit(_iv);
      assertEquals("After visiting type parameter, errors should now be 2", 2, errors.size());
      assertEquals("TypeParameters is not allowed", 
                   "Type Parameters cannot be used at the Intermediate level", 
                   errors.getLast().getFirst());
    }
    
    public void testForPrimitiveTypeDoFirst() {
      
      SourceInfo noInfo = SourceInfo.NO_INFO;
      
      
      PrimitiveType i = new PrimitiveType(noInfo, "int");
      PrimitiveType c = new PrimitiveType(noInfo, "char");
      PrimitiveType d = new PrimitiveType(noInfo, "double");
      PrimitiveType b = new PrimitiveType(noInfo, "boolean");
      
      i.visit(_iv);
      assertEquals("After visiting int, errors should still be 0", 0, errors.size());
      
      c.visit(_iv);
      assertEquals("After visiting char, errors should still be 0", 0, errors.size());
      
      d.visit(_iv);
      assertEquals("After visiting double, errors should still be 0", 0, errors.size());
      
      b.visit(_iv);
      assertEquals("After visiting boolean, errors should still be 0", 0, errors.size());
      
      
      
      PrimitiveType byt = new PrimitiveType(noInfo, "byte");
      PrimitiveType s = new PrimitiveType(noInfo, "short");
      PrimitiveType l = new PrimitiveType(noInfo, "long");
      PrimitiveType f = new PrimitiveType(noInfo, "float");
      
      byt.visit(_iv);
      assertEquals("After visiting byte, errors should be 0", 0, errors.size());
      
      s.visit(_iv);
      assertEquals("After visiting short, errors should be 0", 0, errors.size());
      
      l.visit(_iv);
      assertEquals("After visiting long, errors should be 0", 0, errors.size());
      
      f.visit(_iv);
      assertEquals("After visiting float, errors should be 0", 0, errors.size());
    }
    
    public void test_isClassInCurrentFile() {
      assertFalse("class not in file should return false", _iv._isClassInCurrentFile("NotInFile"));
      _iv._classNamesInThisFile.addLast("package.MyClass");
      assertTrue("full class name in file should return true", _iv._isClassInCurrentFile("package.MyClass"));
      assertTrue("unqualified class name in file should return true", _iv._isClassInCurrentFile("MyClass"));
      
      assertFalse("partial name in file, not same class, should return false", _iv._isClassInCurrentFile("Class"));
      
    }
    
    public void testCreateConstructor() {
      SymbolData sd = new SymbolData("ClassName", _publicMav, new TypeParameter[0], null, new LinkedList<SymbolData>(), null);
      VariableData v1 = new VariableData("i", _publicMav, SymbolData.INT_TYPE, false, sd);
      VariableData v2 = new VariableData("j", _publicMav, SymbolData.CHAR_TYPE, false, sd);
      VariableData v3 = new VariableData("var", _publicMav, SymbolData.DOUBLE_TYPE, false, sd);
      sd.addVar(v1);
      sd.addVar(v2);
      sd.setSuperClass(_sd1);
      
      MethodData md = new MethodData("ClassName", _publicMav, new TypeParameter[0], sd, 
                                     sd.getVars().toArray(new VariableData[0]), 
                                     new String[0], 
                                     sd,
                                     null);
      md.addVars(md.getParams());
      _iv.createConstructor(sd);
      
      assertEquals("sd should have 1 method: its own constructor", md, sd.getMethods().getFirst());
      
      
      v1 = new VariableData("i", _publicMav, SymbolData.INT_TYPE, true, sd);
      v2 = new VariableData("j", _publicMav, SymbolData.CHAR_TYPE, true, sd);
      
      


      
      
      
      
      SymbolData subSd = new SymbolData("Subclass",_publicMav, new TypeParameter[0], null, new LinkedList<SymbolData>(), null);
      subSd.addVar(v3);
      subSd.setSuperClass(sd);
      
      VariableData v1Param = new VariableData("super_i", _packageMav, SymbolData.INT_TYPE, true, null);
      VariableData v2Param = new VariableData("super_j", _packageMav, SymbolData.CHAR_TYPE, true, null);
      VariableData[] vars = {v1Param, v2Param, v3};
      MethodData md2 = new MethodData("Subclass", _publicMav, new TypeParameter[0], subSd,
                                      vars, new String[0], subSd, null);
      md2.addVars(md2.getParams());
      
      _iv.createConstructor(subSd);
      v1Param.setEnclosingData(subSd.getMethods().getFirst());
      v2Param.setEnclosingData(subSd.getMethods().getFirst());
      assertEquals("subSd should have 1 method: its own constructor.", md2, subSd.getMethods().getFirst());
    }
    
    public void test_getFieldAccessorName() {
      
      assertEquals("Should correctly convert from lower case to upper case", "name", _iv.getFieldAccessorName("name"));
    }
    
    public void testCreateToString() {
      SymbolData sd = new SymbolData("ClassName", _publicMav, new TypeParameter[0], null, new LinkedList<SymbolData>(), null);
      
      MethodData md = new MethodData("toString", _publicMav,
                                     new TypeParameter[0],
                                     _iv.getSymbolData("String", _iv._makeSourceInfo("java.lang.String")), 
                                     new VariableData[0],
                                     new String[0], 
                                     sd,
                                     null); 
      
      _iv.createToString(sd);
      assertEquals("sd should have 1 method: toString", md, sd.getMethods().getFirst());
    }
    
    public void testCreateHashCode() {
      SymbolData sd = new SymbolData("ClassName", _publicMav, new TypeParameter[0], null, new LinkedList<SymbolData>(), null);      
      MethodData md = new MethodData("hashCode",
                                     _publicMav, 
                                     new TypeParameter[0], 
                                     SymbolData.INT_TYPE, 
                                     new VariableData[0],
                                     new String[0], 
                                     sd,
                                     null);
      _iv.createHashCode(sd);
      assertEquals("sd should have 1 method: hashCode()", md, sd.getMethods().getFirst());
    }
    
    public void testCreateEquals() {
      SymbolData sd = new SymbolData("ClassName", _publicMav, new TypeParameter[0], null, new LinkedList<SymbolData>(), null);
      MethodData md = new MethodData("equals",
                                     _publicMav, 
                                     new TypeParameter[0], 
                                     SymbolData.BOOLEAN_TYPE, 
                                     new VariableData[] { new VariableData(_iv.getSymbolData("Object", _iv._makeSourceInfo("java.lang.Object"))) },
                                     new String[0], 
                                     sd,
                                     null);
      
      
      _iv.createEquals(sd);
      md.getParams()[0].setEnclosingData(sd.getMethods().getFirst());                               
      assertEquals("sd should have 1 method: equals()", md, sd.getMethods().getFirst());
    }
    
    public void testForClassDef() {
      
      _iv._package = "myPackage";
      ClassDef cd0 = new ClassDef(SourceInfo.NO_INFO, _packageMav, new Word(SourceInfo.NO_INFO, "Lisa"),
                                  new TypeParameter[0], 
                                  new ClassOrInterfaceType(SourceInfo.NO_INFO, "Object", new Type[0]), new ReferenceType[0], 
                                  new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0])); 
      
      cd0.visit(_iv);
      assertEquals("There should be no errors", 0, errors.size());
      assertTrue("Should have resolved java.lang.Object", 
                 LanguageLevelConverter.symbolTable.containsKey("java.lang.Object"));
      assertFalse("Should not be a continuation", 
                  LanguageLevelConverter.symbolTable.get("java.lang.Object").isContinuation());
      SymbolData sd = LanguageLevelConverter.symbolTable.get("myPackage.Lisa");
      assertTrue("Lisa should be in _newSDs", LanguageLevelConverter._newSDs.containsKey(sd));
      MethodData md2 = 
        new MethodData("equals",
                       _publicMav, 
                       new TypeParameter[0], 
                       SymbolData.BOOLEAN_TYPE, 
                       new VariableData[] { new VariableData(_iv.getSymbolData("Object", 
                                                                               _iv._makeSourceInfo("java.lang.Object"))) },
                       new String[0], 
                       sd,
                       null);
      
      md2.getParams()[0].setEnclosingData(sd.getMethods().getLast());  
      
      assertEquals("sd's last method should be equals()", md2, sd.getMethods().getLast());
      assertEquals("sd's package should be correct", "myPackage", sd.getPackage());
      
      
      _iv._package = "";
      ClassDef cd1 = new ClassDef(SourceInfo.NO_INFO, _abstractMav, new Word(SourceInfo.NO_INFO, "Bart"),
                                  new TypeParameter[0], new ClassOrInterfaceType(SourceInfo.NO_INFO, "System", new Type[0]), new ReferenceType[0], 
                                  new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      cd1.visit(_iv);
      assertEquals("There should be no errors", 0, errors.size());
      assertTrue("Should have resolved java.lang.System", LanguageLevelConverter.symbolTable.containsKey("java.lang.System"));
      assertFalse("Should not be a continuation", LanguageLevelConverter.symbolTable.get("java.lang.System").isContinuation());
      sd = LanguageLevelConverter.symbolTable.get("Bart");
      
      assertEquals("There should be 3 methods", 3, sd.getMethods().size());
      
      
      
      ConcreteMethodDef cmd = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                                    _packageMav, 
                                                    new TypeParameter[0], 
                                                    new VoidReturn(SourceInfo.NO_INFO, "void"), 
                                                    new Word(SourceInfo.NO_INFO, "testMethodName"),
                                                    new FormalParameter[0],
                                                    new ReferenceType[0], 
                                                    new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      ClassDef cd3 = new ClassDef(SourceInfo.NO_INFO, _abstractMav, new Word(SourceInfo.NO_INFO, "TestSuper2"),
                                  new TypeParameter[0], new ClassOrInterfaceType(SourceInfo.NO_INFO, "TestCase", new Type[0]), new ReferenceType[0], 
                                  new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {cmd}));
      
      _iv._file=new File("TestSuper2.dj0");
      _iv._importedFiles.addLast("junit.framework.TestCase");
      LanguageLevelConverter.symbolTable.put("junit.framework.TestCase", new SymbolData("junit.framework.TestCase"));
      cd3.visit(_iv);
      assertEquals("There should still just be no errors", 0, errors.size());
      assertNotNull("Should have looked up TestSuper2", LanguageLevelConverter.symbolTable.get("TestSuper2"));
      
      
      
      ConcreteMethodDef cmd2 = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                                     _packageMav, 
                                                     new TypeParameter[0], 
                                                     new VoidReturn(SourceInfo.NO_INFO, "void"), 
                                                     new Word(SourceInfo.NO_INFO, "uhOh"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0], 
                                                     new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      ClassDef cd4 = new ClassDef(SourceInfo.NO_INFO, _abstractMav, new Word(SourceInfo.NO_INFO, "TestVoidNoTestMethod"),
                                  new TypeParameter[0], new ClassOrInterfaceType(SourceInfo.NO_INFO, "junit.framework.TestCase", new Type[0]), new ReferenceType[0], 
                                  new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {cmd2}));
      
      
      
      _iv._file=new File("TestVoidNoTestMethod.dj0");
      cd4.visit(_iv);
      
      assertEquals("There should still be 0 errors", 0, errors.size());
      _iv._importedFiles.remove("junit.framework.TestCase");
      
    }
    
    public void testForInterfaceDef() {
      AbstractMethodDef amd = new AbstractMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], new PrimitiveType(SourceInfo.NO_INFO, "int"),
                                                    new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[0], new ReferenceType[0]);
      AbstractMethodDef amd2 = new AbstractMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], new PrimitiveType(SourceInfo.NO_INFO, "int"),
                                                     new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[0], new ReferenceType[0]);
      InterfaceDef id = new InterfaceDef(SourceInfo.NO_INFO, _publicMav, new Word(SourceInfo.NO_INFO, "id"), new TypeParameter[0], new ReferenceType[0], 
                                         new BracedBody(SourceInfo.NO_INFO, 
                                                        new BodyItemI[] {amd}));
      InterfaceDef id2 = new InterfaceDef(SourceInfo.NO_INFO, _publicMav, new Word(SourceInfo.NO_INFO, "id2"), new TypeParameter[0], new ReferenceType[] {new ClassOrInterfaceType(SourceInfo.NO_INFO, "id", new Type[0])}, 
                                          new BracedBody(SourceInfo.NO_INFO, 
                                                         new BodyItemI[] {amd2}));
      SymbolData sd = new SymbolData("id", _publicMav, new TypeParameter[0], new LinkedList<SymbolData>(), null);
      sd.setIsContinuation(true);
      MethodData md = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE, new VariableData[0], new String[0], sd, amd);
      
      LinkedList<SymbolData> interfaces = new LinkedList<SymbolData>();
      interfaces.addLast(sd);
      SymbolData sd2 = new SymbolData("id2", _publicMav, new TypeParameter[0], interfaces, null);
      sd2.setIsContinuation(true);
      MethodData md2 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE, new VariableData[0], new String[0], sd2, amd2);
      
      LanguageLevelConverter.symbolTable.put("id", sd);
      LanguageLevelConverter.symbolTable.put("id2", sd2);
      
      id.visit(_iv);
      id2.visit(_iv);
      
      assertEquals("Should be no errors", 0, errors.size());
      assertEquals("Should return the same symbol datas: id", sd, LanguageLevelConverter.symbolTable.get("id"));
      assertEquals("Should return the same symbol datas:id2 ", sd2, LanguageLevelConverter.symbolTable.get("id2"));
    }
    
    
    
    public void testCreateMethodData() {
      
      MethodDef mdef = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                             _privateAbstractMav, 
                                             new TypeParameter[0], 
                                             new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                             new Word(SourceInfo.NO_INFO, "methodName"),
                                             new FormalParameter[0],
                                             new ReferenceType[0]); 
      
      MethodData mdata = new MethodData("methodName", _privateAbstractMav, new TypeParameter[0], SymbolData.INT_TYPE, 
                                        new VariableData[0], 
                                        new String[0],
                                        _sd1,
                                        null);
      assertEquals("Should return the correct MethodData", mdata, _iv.createMethodData(mdef, _sd1));
      assertEquals("There should be one errors.", 1, errors.size());

      
      
      mdef = 
        new AbstractMethodDef(SourceInfo.NO_INFO, 
                              _abstractMav, 
                              new TypeParameter[] { new TypeParameter(SourceInfo.NO_INFO,
                                                                      new TypeVariable(SourceInfo.NO_INFO, "T"),
                                                                      new TypeVariable(SourceInfo.NO_INFO, "U"))},
                              new VoidReturn(SourceInfo.NO_INFO, "void"), 
                              new Word(SourceInfo.NO_INFO, "methodName"),
                              new FormalParameter[] {
                                new FormalParameter(SourceInfo.NO_INFO, 
                                                    new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                                                                        new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                                                        new Word (SourceInfo.NO_INFO, "field1")),
                                                    false
                                                   ),
                                  new FormalParameter(SourceInfo.NO_INFO, 
                                                      new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                                                                          new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                                                          new Word (SourceInfo.NO_INFO, "field1")),
                                                      false
                                                     )},
                              new ReferenceType[] { new TypeVariable(SourceInfo.NO_INFO, "X") }
                              );
      mdata = 
        new MethodData("methodName", 
                       _abstractMav, 
                       new TypeParameter[] { new TypeParameter(SourceInfo.NO_INFO,
                                                               new TypeVariable(SourceInfo.NO_INFO, "T"),
                                                               new TypeVariable(SourceInfo.NO_INFO, "U"))}, 
                       SymbolData.VOID_TYPE, 
                       new VariableData[] { new VariableData("field1", _finalMav, SymbolData.DOUBLE_TYPE, true, null),
                         new VariableData("field1", _finalMav, SymbolData.INT_TYPE, true, null) }, 
                       new String[] { "X" },
                       _sd1,
                       null);
      
      
      MethodData result = _iv.createMethodData(mdef, _sd1);
      mdata.getParams()[0].setEnclosingData(result);
      mdata.getParams()[1].setEnclosingData(result);
      
      mdata.addVars(new VariableData[] { new VariableData("field1", _finalMav, SymbolData.DOUBLE_TYPE, true, result) });                                                          
      assertEquals("Should return the correct MethodData", mdata, result);
      assertEquals("There should be 2 errors.", 2, errors.size());
      
      
      assertEquals("The second error message should be correct.", "You cannot have two method parameters with the same name", errors.get(1).getFirst());
    }
    
    public void testSimpleAnonymousClassInstantiationHelper() {
      SimpleAnonymousClassInstantiation basic = 
        new SimpleAnonymousClassInstantiation(SourceInfo.NO_INFO, 
                                              new ClassOrInterfaceType(SourceInfo.NO_INFO, "Object", new Type[0]), 
                                              new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]),
                                              new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      _iv._package = "i.like";
      _iv.simpleAnonymousClassInstantiationHelper(basic, _sd1);
      assertEquals("There should be no errors", 0, errors.size());
      SymbolData obj = LanguageLevelConverter.symbolTable.get("java.lang.Object");
      assertNotNull("Object should be in the symbol table", obj);
      assertEquals("sd1 should have one inner class", 1, _sd1.getInnerClasses().size());
      SymbolData inner = _sd1.getInnerClasses().get(0);
      assertEquals("The inner class should have the proper name", "i.like.monkey$1", inner.getName());
      assertEquals("The inner class should have proper outer data", _sd1, inner.getOuterData());
      assertEquals("The inner class should have proper super class", obj, inner.getSuperClass());
      assertEquals("The inner class should have the right package", "i.like", inner.getPackage());
      assertEquals("The inner class should have 3 methods", 3, inner.getMethods().size());
    }
    
    
    public void testComplexAnonymousClassInstantiationHelper() {
      ComplexAnonymousClassInstantiation basic = new ComplexAnonymousClassInstantiation(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "java.lang.Object")),
                                                                                        new ClassOrInterfaceType(SourceInfo.NO_INFO, "Inner", new Type[0]), 
                                                                                        new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]),
                                                                                        new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      _iv._package = "i.like";
      _iv.complexAnonymousClassInstantiationHelper(basic, _sd1);
      assertEquals("There should be no errors", 0, errors.size());
      SymbolData obj = LanguageLevelConverter.symbolTable.get("java.lang.Object");
      assertNotNull("Object should be in the symbol table", obj);
      SymbolData objInner = LanguageLevelConverter.symbolTable.get("java.lang.Object.Inner");
      assertEquals("sd1 should have one inner class", 1, _sd1.getInnerClasses().size());
      SymbolData inner = _sd1.getInnerClasses().get(0);
      assertEquals("The inner class should have the proper name", "i.like.monkey$1", inner.getName());
      assertEquals("The inner class should have proper outer data", _sd1, inner.getOuterData());
      assertEquals("The inner class should have null as its super class", null, inner.getSuperClass());
      assertEquals("The inner class should have the right package", "i.like", inner.getPackage());
      assertEquals("The inner class should have 3 methods", 3, inner.getMethods().size());
    }
    
    public void testForVariableDeclaration() {
      
      
      SimpleAnonymousClassInstantiation basic = new SimpleAnonymousClassInstantiation(SourceInfo.NO_INFO, new ClassOrInterfaceType(SourceInfo.NO_INFO, "Object", new Type[0]), 
                                                                                      new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]),
                                                                                      new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      
      
      VariableDeclarator[] d1 = {new InitializedVariableDeclarator(SourceInfo.NO_INFO, new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), new Word(SourceInfo.NO_INFO, "b"), basic)};
      VariableDeclaration vd1 = new VariableDeclaration(SourceInfo.NO_INFO,_publicMav, d1); 
      
      ClassBodyIntermediateVisitor cbiv = 
        new ClassBodyIntermediateVisitor(_sd1, 
                                         _iv._file, 
                                         _iv._package,
                                         _iv._importedFiles, 
                                         _iv._importedPackages, 
                                         _iv._classNamesInThisFile, 
                                         _iv.continuations);
      
      vd1.visit(cbiv);
      assertEquals("Should be 1 inner class of _sd1", 1, _sd1.getInnerClasses().size());
      
    }
    
    public void testForPackageStatementDoFirst() {
      PackageStatement ps = new PackageStatement(SourceInfo.NO_INFO, new CompoundWord(SourceInfo.NO_INFO, new Word[0]));
      ps.visit(_iv);
      assertEquals("Should be no errors", 0, errors.size());

    }
  }
}