

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.*;
import java.util.*;
import java.io.*;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.*;

import junit.framework.TestCase;



public class FullJavaVisitor extends LanguageLevelVisitor {
  
  
  public FullJavaVisitor(File file, 
                         String packageName, 
                         LinkedList<String> importedFiles, 
                         LinkedList<String> importedPackages,
                         LinkedList<String> classNamesInThisFile, 
                         Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations) {
    super(file, packageName, importedFiles, importedPackages, classNamesInThisFile, continuations);
  }
  
  
  public FullJavaVisitor(File file) {
    this(file, 
         new LinkedList<Pair<String, JExpressionIF>>(),
         new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>(), 
         new LinkedList<Pair<LanguageLevelVisitor, SourceFile>>());
  }
  
  
  public FullJavaVisitor(File file, 
                         LinkedList<Pair<String, JExpressionIF>> errors, 
                         Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations,
                         LinkedList<Pair<LanguageLevelVisitor, SourceFile>> visitedFiles) {
    super(file, 
          "", 
          new LinkedList<String>(), 
          new LinkedList<String>(), 
          new LinkedList<String>(), 
          continuations);
    this.errors = errors;
    this.visitedFiles= visitedFiles;
    _hierarchy = new Hashtable<String, TypeDefBase>();
  }

  
  public void createConstructor(SymbolData sd) {
    SymbolData superSd = sd.getSuperClass();
    
    
    if (sd.isContinuation()) return;
    
    String name = getUnqualifiedClassName(sd.getName());
    
    
    boolean hasOtherConstructor = sd.hasMethod(name);
    if (hasOtherConstructor) {
          LanguageLevelConverter._newSDs.remove(sd); 
          System.err.println(sd + " removed from _newSDs.  _newSDs = " + LanguageLevelConverter._newSDs);
          return;
    }
    
    
    MethodData md = MethodData.make(name,
                                   new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}), 
                                   new TypeParameter[0], 
                                   sd, 
                                   new VariableData[0], 
                                   new String[0], 
                                   sd,
                                   null);

    addGeneratedMethod(sd, md);
    LanguageLevelConverter._newSDs.remove(sd); 
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
      that.getBody().visit(new ClassBodyFullJavaVisitor(sd, "", _file, _package, _importedFiles,
                                                        _importedPackages, _classNamesInThisFile, continuations));
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
      that.getBody().visit(new InterfaceBodyFullJavaVisitor(sd, _file, _package, _importedFiles, _importedPackages,
                                                            _classNamesInThisFile, continuations));
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
      if (!(temp.equals("abstract") || temp.equals("public") || temp.equals("private") || temp.equals("protected") || 
            temp.equals("static") || temp.equals("final"))) {
        sb.append(" \"" + temp + "\"");
        count++;
      }
    }
    
    return super.forModifiersAndVisibilityDoFirst(that);
  }

  
  public Void forSwitchStatementDoFirst(SwitchStatement that) { return super.forSwitchStatementDoFirst(that); }
  
  
  public Void forInstanceInitializerDoFirst(InstanceInitializer that) { return null; }
  
  
  public Void forStaticInitializerDoFirst(StaticInitializer that) { return null; }
  
  
  public Void forLabeledStatementDoFirst(LabeledStatement that) { return null; }

  
  public Void forLabeledBreakStatementDoFirst(LabeledBreakStatement that) { return null; }
 
  
  public Void forLabeledContinueStatementDoFirst(LabeledContinueStatement that) { return null; }

  
  public Void forSynchronizedStatementDoFirst(SynchronizedStatement that) { return null; }

  
  public Void forTypeParameterDoFirst(TypeParameter that) { return null; }

  
  public Void forConditionalExpressionDoFirst(ConditionalExpression that) { return null; }

  
  public Void forInstanceofExpressionDoFirst(InstanceofExpression that) { return null; }

  
  public Void forPrimitiveTypeDoFirst(PrimitiveType that) { return super.forPrimitiveTypeDoFirst(that); }

  
  public Void forTryCatchStatementDoFirst(TryCatchStatement that) {
    return super.forTryCatchStatementDoFirst(that);
  }
  
  
  private boolean _isClassInCurrentFile(String className) {
    Iterator<String> iter = _classNamesInThisFile.iterator();
    while (iter.hasNext()) {
      String s = iter.next();
      if (s.equals(className) || s.endsWith("." + className)) return true;
    }
    return false;   
  }
  
  
  protected VariableData[] formalParameters2VariableData(FormalParameter[] fps, Data d) {
    VariableData[] varData = new VariableData[fps.length];
    VariableDeclarator vd;
    String[] mav;
    
    
    
    if (d instanceof MethodData && d.hasModifier("static"))
      mav = new String[] {"final", "static"};
    else
      mav = new String[] {"final"};
    
    for (int i = 0; i < varData.length; i++) {
      vd = fps[i].getDeclarator();
      String name = vd.getName().getText();
      SymbolData type = getSymbolData(vd.getType().getName(), vd.getType().getSourceInfo());
      
      if (type == null) {
        
        type = d.getInnerClassOrInterface(vd.getType().getName());
      }
      
      if (type == null) {
        
        String typeName = d.getSymbolData().getName() + "." + vd.getType().getName();
        type = new SymbolData(typeName);
        d.getSymbolData().addInnerClass(type);
        type.setOuterData(d.getSymbolData());

        continuations.put(typeName, new Pair<SourceInfo, LanguageLevelVisitor>(vd.getType().getSourceInfo(), this));
      }
      
      varData[i] = 
        new VariableData(name, new ModifiersAndVisibility(SourceInfo.NO_INFO, mav), type, true, d);
      varData[i].gotValue();

    }
    return varData;
  }
  
  
  public Void forClassDef(ClassDef that) {    
    forClassDefDoFirst(that);
    if (prune(that)) return null;

    String className = getQualifiedClassName(that.getName().getText());
    SymbolData sd = defineSymbolData(that, className); 
    
    that.getMav().visit(this);
    that.getName().visit(this);
    that.getSuperclass().visit(this);  
    
    
    for (int i = 0; i < that.getTypeParameters().length; i++) that.getTypeParameters()[i].visit(this);
    
    for (int i = 0; i < that.getInterfaces().length; i++) that.getInterfaces()[i].visit(this);
    
    if (sd != null) {
      that.getBody().visit(new ClassBodyFullJavaVisitor(sd, className, _file, _package, _importedFiles, 
                                                        _importedPackages, _classNamesInThisFile, continuations));
    }
    forClassDefOnly(that);
    _classesToBeParsed.remove(className);
    return null;
  }
  
  
  public Void forBracedBodyDoFirst(BracedBody that) {
    for (BodyItemI bi: that.getStatements()) {
      if (bi instanceof TypeDefBase) {
        TypeDefBase type = (TypeDefBase) bi;
        String rawClassName = type.getName().getText();
        _log.log("Adding " + rawClassName + " to _innerClassesToBeParsed inside " + that + "\n");

        _innerClassesToBeParsed.add(_enclosingClassName + "." + rawClassName);
      }
    }

    return super.forBracedBodyDoFirst(that);
  }

  
  protected void createToString(SymbolData sd) { return; }

  
  protected void createHashCode(SymbolData sd) { return; }
    
  
  protected void createEquals(SymbolData sd) { return; }    
   
  
  public Void forInterfaceDef(InterfaceDef that) {
    forInterfaceDefDoFirst(that);
    if (prune(that)) return null;

    String className = getQualifiedClassName(that.getName().getText());

    SymbolData sd = defineSymbolData(that, className);
    
    that.getMav().visit(this);
    that.getName().visit(this); 
    
    
    assert that.getTypeParameters().length == 0;
    
    for (int i = 0; i < that.getInterfaces().length; i++) that.getInterfaces()[i].visit(this);
    
    if (sd != null) {
      sd.setInterface(true);
      that.getBody().visit(new InterfaceBodyFullJavaVisitor(sd, _file, _package, _importedFiles, _importedPackages,
                                                            _classNamesInThisFile, continuations));
    }
    
    forInterfaceDefOnly(that);
    _classesToBeParsed.remove(className);
    return null;
  }

  
  public void anonymousClassInstantiationHelper(AnonymousClassInstantiation that, Data enclosing, SymbolData superC) {
    that.getArguments().visit(this); 
    String anonName = getQualifiedClassName(enclosing.getSymbolData().getName()) + "$" + 
      enclosing.getSymbolData().preincrementAnonymousInnerClassNum();
    
    SymbolData sd = new SymbolData(anonName);
    enclosing.addInnerClass(sd);
    sd.setOuterData(enclosing);
    
    if (superC != null && ! superC.isInterface()) {
      sd.setSuperClass(superC); 
    }
    sd.setPackage(_package);
    
    
    that.getBody().visit(new ClassBodyFullJavaVisitor(sd, anonName, _file, _package, _importedFiles, _importedPackages, 
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
  
    
  public Void forArrayType(ArrayType that) {
    forArrayTypeDoFirst(that);
    if (prune(that)) return null;
    getSymbolData(that.getName(), that.getSourceInfo());
    return null;
  }
  
  
  public static class FullJavaVisitorTest extends TestCase {
    
    
    private FullJavaVisitor _fv;
    
    private SymbolData _sd1;
    private SymbolData _sd2;
    private SymbolData _sd3;
    private SymbolData _sd4;
    private SymbolData _sd5;
    private SymbolData _sd6;
    private ModifiersAndVisibility _publicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _protectedMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});
    private ModifiersAndVisibility _privateMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"private"});
    private ModifiersAndVisibility _packageMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    private ModifiersAndVisibility _abstractMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract"});
    private ModifiersAndVisibility _staticMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"static"});
    private ModifiersAndVisibility _finalMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final"});
    private ModifiersAndVisibility _volatileMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[]{"volatile"});
    
    public FullJavaVisitorTest() { this(""); }
    public FullJavaVisitorTest(String name) { super(name); }
    
    public void setUp() {
      errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();
      LanguageLevelConverter._newSDs.clear();
      LanguageLevelConverter.OPT = new Options(JavaVersion.JAVA_5, IterUtil.make(new File("lib/buildlib/junit.jar")));
      visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, edu.rice.cs.javalanglevels.tree.SourceFile>>();      
      _hierarchy = new Hashtable<String, TypeDefBase>();
      _fv = new FullJavaVisitor(new File(""), 
                                errors,
                                continuations, 
                                new LinkedList<Pair<LanguageLevelVisitor, SourceFile>>());
      _fv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();
      _fv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      _fv._resetNonStaticFields();
      _fv._importedPackages.addFirst("java.lang");
      _errorAdded = false;
      _sd1 = new SymbolData("i.like.monkey");
      _sd2 = new SymbolData("i.like.giraffe");
      _sd3 = new SymbolData("zebra");
      _sd4 = new SymbolData("u.like.emu");
      _sd5 = new SymbolData("");
      _sd6 = new SymbolData("cebu");
    }
    
    public void testForModifiersAndVisibilityDoFirst() {
      
      
      _fv.forModifiersAndVisibilityDoFirst(_abstractMav);
      _fv.forModifiersAndVisibilityDoFirst(_publicMav);
      _fv.forModifiersAndVisibilityDoFirst(_privateMav);
      _fv.forModifiersAndVisibilityDoFirst(_protectedMav);
      _fv.forModifiersAndVisibilityDoFirst(_staticMav);
      _fv.forModifiersAndVisibilityDoFirst(_finalMav);
      
      ModifiersAndVisibility mavs = new ModifiersAndVisibility(SourceInfo.NO_INFO, 
                                                               new String[] {"private", "static"});
       _fv.forModifiersAndVisibilityDoFirst(mavs);
      assertEquals("there should still be 0 errors", 0, errors.size());

      
      
      _fv.forModifiersAndVisibilityDoFirst(_volatileMav);
      assertEquals("there should now be no errors", 0, errors.size());




      ModifiersAndVisibility mavs2 = new ModifiersAndVisibility(SourceInfo.NO_INFO, 
                                                                new String[] {"final", "volatile"});
     
      _fv.forModifiersAndVisibilityDoFirst(mavs2);
      assertEquals("There should now be 1 error", 1, errors.size());
      assertEquals("The error message should be correct for 1 bad, 1 good modifier:", 
                   "Illegal combination of modifiers. Can't use final and volatile together.", 
                   errors.get(0).getFirst());

      ModifiersAndVisibility mavs3 = new ModifiersAndVisibility(SourceInfo.NO_INFO, 
                                                                new String[] {"synchronized", "native"});
     
      _fv.forModifiersAndVisibilityDoFirst(mavs3);
      assertEquals("There should now be 1 errors", 1, errors.size());



    }
    
    public void testForClassDefDoFirst() {
      
      ClassDef cd0 = new ClassDef(SourceInfo.NO_INFO, _publicMav,
                                  new Word(SourceInfo.NO_INFO, "Lisa"),
                                  new TypeParameter[0], 
                                  new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), 
                                  new ReferenceType[0], 
                                  new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      _fv.forClassDefDoFirst(cd0);
      assertEquals("should be no errors", 0, errors.size());
      
      
      ClassDef cd1 = new ClassDef(SourceInfo.NO_INFO, _publicMav, 
                                  new Word(SourceInfo.NO_INFO, "Test"), new TypeParameter[0], JExprParser.NO_TYPE,
                                  new ReferenceType[0], new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));

      _fv.forClassDefDoFirst(cd1);
      assertEquals("there should still be 0 errors", 0, errors.size());
       
      
      ReferenceType rt2 = new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]);
      ClassDef cd2 = new ClassDef(SourceInfo.NO_INFO, _publicMav, 
                                   new Word(SourceInfo.NO_INFO, "Test"), new TypeParameter[0], 
                                   new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]),
                                   new ReferenceType[] { rt2 }, 
                                   new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));

                                 
      _fv.forClassDefDoFirst(cd2);
      assertEquals("there should still be 0 errors", 0, errors.size());
    }
    
    public void testForFormalParameterDoFirst() {
      PrimitiveType pt = new PrimitiveType(SourceInfo.NO_INFO, "int");
      Word w = new Word(SourceInfo.NO_INFO, "param");
      UninitializedVariableDeclarator uvd = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, pt, w);
      
      
      FormalParameter fp = new FormalParameter(SourceInfo.NO_INFO, uvd, false);
      _fv.forFormalParameterDoFirst(fp);
      assertEquals("should be no errors", 0, errors.size());
      
      
      FormalParameter fp2 = new FormalParameter(SourceInfo.NO_INFO, uvd, true);  
      _fv.forFormalParameterDoFirst(fp2);
      assertEquals("should still be no errors", 0, errors.size());
    }
    
    public void test_NotAllowed() {
      SourceInfo noInfo = SourceInfo.NO_INFO;
      Word w = new Word(SourceInfo.NO_INFO, "word");
      TypeParameter[] tps = new TypeParameter[0];
      ReferenceType[] rts = new ReferenceType[0];
      BracedBody emptyBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      UnbracedBody emptyUnbracedBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      ClassOrInterfaceType superClass = new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]);
      FormalParameter[] fps = new FormalParameter[0];
      CompoundWord cw = new CompoundWord(noInfo, new Word[0]);
      Statement stmt = new EmptyStatement(noInfo);
      Expression e = new EmptyExpression(noInfo);
      Block b = new Block(noInfo, emptyBody);
      
      TypeVariable tv = new TypeVariable(noInfo, "name");
      
      InnerInterfaceDef ii = new InnerInterfaceDef(noInfo, _publicMav, w, tps, rts, emptyBody);
      InnerClassDef ic = new InnerClassDef(noInfo, _publicMav, w, tps, superClass, rts, emptyBody);
      
      StaticInitializer si = new StaticInitializer(noInfo, b);
      LabeledStatement ls = new LabeledStatement(noInfo, new Word(noInfo, "label"), stmt);

      LabeledBreakStatement bs = new LabeledBreakStatement(noInfo, new Word(noInfo, "myLabel"));
      LabeledContinueStatement cs = new LabeledContinueStatement(noInfo, new Word(noInfo, "yourLabel"));
      SimpleNameReference snr = new SimpleNameReference(noInfo, w);
      SynchronizedStatement syncs = new SynchronizedStatement(noInfo, snr, b);
      TypeParameter tp = new TypeParameter(noInfo, tv, superClass);
      ConditionalExpression ce = new ConditionalExpression(noInfo, snr, snr, snr);
      
      TryCatchStatement tcs = new NormalTryCatchStatement(noInfo, b, new CatchBlock[0]);
      SwitchCase defaultSc = new DefaultCase(SourceInfo.NO_INFO, emptyUnbracedBody);
      SwitchStatement ssBadDefault = 
        new SwitchStatement(noInfo, new IntegerLiteral(SourceInfo.NO_INFO, 5), new SwitchCase[]{defaultSc, defaultSc});
     
     si.visit(_fv);
     assertEquals("There should be 0 errors", 0, errors.size());

     ls.visit(_fv);
     assertEquals("There should be 0 errors", 0, errors.size());

     bs.visit(_fv);
     assertEquals("There should be 0 errors", 0, errors.size());

     cs.visit(_fv);
     assertEquals("There should be 0 errors", 0, errors.size());
     
     syncs.visit(_fv);
     assertEquals("There should be 0 errors", 0, errors.size());
    
     tp.visit(_fv);
     assertEquals("There should be 0 errors", 0, errors.size());

     ce.visit(_fv);
     assertEquals("There should be 0 errors", 0, errors.size());
     
     tcs.visit(_fv);
     assertEquals("There should be 0 errors", 0, errors.size());
     
     ssBadDefault.visit(_fv);
     assertEquals("There should be 0 errors", 0, errors.size());
     }
    
    public void testForPrimitiveTypeDoFirst() {
      
      SourceInfo noInfo = SourceInfo.NO_INFO;
      
      
      PrimitiveType i = new PrimitiveType(noInfo, "int");
      PrimitiveType c = new PrimitiveType(noInfo, "char");
      PrimitiveType d = new PrimitiveType(noInfo, "double");
      PrimitiveType b = new PrimitiveType(noInfo, "boolean");
      
      i.visit(_fv);
      assertEquals("After visiting int, errors should still be 0", 0, errors.size());
      
      c.visit(_fv);
      assertEquals("After visiting char, errors should still be 0", 0, errors.size());
      
      d.visit(_fv);
      assertEquals("After visiting double, errors should still be 0", 0, errors.size());
      
      b.visit(_fv);
      assertEquals("After visiting boolean, errors should still be 0", 0, errors.size());
      
      
      
      PrimitiveType byt = new PrimitiveType(noInfo, "byte");
      PrimitiveType s = new PrimitiveType(noInfo, "short");
      PrimitiveType l = new PrimitiveType(noInfo, "long");
      PrimitiveType f = new PrimitiveType(noInfo, "float");
      
      byt.visit(_fv);
      assertEquals("After visiting byte, errors should be 0", 0, errors.size());
      
      s.visit(_fv);
      assertEquals("After visiting short, errors should be 0", 0, errors.size());
      
      l.visit(_fv);
      assertEquals("After visiting long, errors should be 0", 0, errors.size());
      
      f.visit(_fv);
      assertEquals("After visiting float, errors should be 0", 0, errors.size());
    }
    
    public void testForArrayType() {
      LanguageLevelConverter.symbolTable.put("name", new SymbolData("name"));
      ArrayInitializer ai = new ArrayInitializer(SourceInfo.NO_INFO, new VariableInitializerI[0]);
      TypeVariable tv = new TypeVariable(SourceInfo.NO_INFO, "name");
      ArrayType at = new ArrayType(SourceInfo.NO_INFO, "name[]", tv);
      
      at.visit(_fv);
      assertEquals("There should be no errors", 0, errors.size());
      SymbolData sd = LanguageLevelConverter.symbolTable.get("name[]");
      assertNotNull("sd should not be null", sd);
      ArrayData ad = (ArrayData) sd;
      assertEquals("ad should have an inner sd of name name:", "name", ad.getElementType().getName());
      
      ai = new ArrayInitializer(SourceInfo.NO_INFO, new VariableInitializerI[0]);
      tv = new TypeVariable(SourceInfo.NO_INFO, "String");
      at = new ArrayType(SourceInfo.NO_INFO, "String[]", tv);
      
      VariableDeclarator vd = 
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, at, new Word(SourceInfo.NO_INFO, "myArray"));
      VariableDeclaration vdecl = 
        new VariableDeclaration(SourceInfo.NO_INFO, _publicMav, new VariableDeclarator[] {vd});
      vdecl.visit(_fv);
      SymbolData bob = LanguageLevelConverter.symbolTable.get("java.lang.String[]");
      assertNotNull("bob should not be null", bob);
      
      
      tv = new TypeVariable(SourceInfo.NO_INFO, "Object");
      at = new ArrayType(SourceInfo.NO_INFO, "Object[]", tv);
      ArrayType at2 = new ArrayType(SourceInfo.NO_INFO, "Object[][]", at);

      at2.visit(_fv);
      assertEquals("There should be no errors", 0, errors.size());
      assertNotNull("Object should be in the symbolTable", LanguageLevelConverter.symbolTable.get("java.lang.Object"));
      assertNotNull("Object[] should be in the symbolTable", 
                    LanguageLevelConverter.symbolTable.get("java.lang.Object[]"));
      assertNotNull("Object[][] should be in the symbolTable", 
                    LanguageLevelConverter.symbolTable.get("java.lang.Object[][]"));
    }
    
    public void xtestCreateConstructor() {
      SymbolData sd = 
        new SymbolData("ClassName", _publicMav, new TypeParameter[0], null, new LinkedList<SymbolData>(), null);
      VariableData v1 = new VariableData("i", _publicMav, SymbolData.INT_TYPE, false, sd);
      VariableData v2 = new VariableData("j", _publicMav, SymbolData.CHAR_TYPE, false, sd);
      VariableData v3 = new VariableData("var", _publicMav, SymbolData.DOUBLE_TYPE, false, sd);
      sd.addVar(v1);
      sd.addVar(v2);
      sd.setSuperClass(_sd1);
      
      MethodData md = new MethodData("ClassName", _publicMav, new TypeParameter[0], sd, 
                                   new VariableData[0], 
                                   new String[0], 
                                   sd,
                                   null);
      md.addVars(md.getParams());
      _fv.createConstructor(sd);
      
      assertEquals("sd should have 1 method: its own constructor", md, sd.getMethods().getFirst());
      
      
      
      v1 = new VariableData("i", _publicMav, SymbolData.INT_TYPE, true, sd);
      v2 = new VariableData("j", _publicMav, SymbolData.CHAR_TYPE, true, sd);

      
      
      SymbolData subSd = 
        new SymbolData("Subclass",_publicMav, new TypeParameter[0], null, new LinkedList<SymbolData>(), null);
      subSd.addVar(v3);
      subSd.setSuperClass(sd);

      
      VariableData v1Param = new VariableData("super_i", _packageMav, SymbolData.INT_TYPE, true, null);
      VariableData v2Param = new VariableData("super_j", _packageMav, SymbolData.CHAR_TYPE, true, null);
      VariableData[] vars = {v1Param, v2Param, v3};
      MethodData md2 = new MethodData("Subclass", _publicMav, new TypeParameter[0], subSd,
                                      new VariableData[0], new String[0], subSd, null);
      md2.addVars(md2.getParams());
                
      _fv.createConstructor(subSd);
      v1Param.setEnclosingData(subSd.getMethods().getFirst());
      v2Param.setEnclosingData(subSd.getMethods().getFirst());
      assertEquals("subSd should have 1 method: its own constructor.", md2, subSd.getMethods().getFirst());
    }
    
    public void xtest_getFieldAccessorName() {
      
      assertEquals("Should correctly convert from lower case to upper case", "name", _fv.getFieldAccessorName("name"));
    }
    
    
    public void testCreateToString() {
      SymbolData sd = 
        new SymbolData("ClassName", _publicMav, new TypeParameter[0], null, new LinkedList<SymbolData>(), null);
      _fv.createToString(sd);
      
      assertEquals("sd should have no methods", 0, sd.getMethods().size());
    }
    
    public void testCreateHashCode() {
      SymbolData sd = 
        new SymbolData("ClassName", _publicMav, new TypeParameter[0], null, new LinkedList<SymbolData>(), null);      
      _fv.createHashCode(sd);
      
      assertEquals("sd should have 0 methods", 0, sd.getMethods().size());
    }
    
    public void testCreateEquals() {
      SymbolData sd = 
        new SymbolData("ClassName", _publicMav, new TypeParameter[0], null, new LinkedList<SymbolData>(), null);
      _fv.createEquals(sd);
      
      assertEquals("sd should have 0 methods", 0, sd.getMethods().size());
    }
    
    public void testForClassDef() {
      
      _fv._package = "myPackage";
      ClassDef cd0 = 
        new ClassDef(SourceInfo.NO_INFO, _packageMav, 
                     new Word(SourceInfo.NO_INFO, "Lisa"),
                     new TypeParameter[0], 
                     new ClassOrInterfaceType(SourceInfo.NO_INFO, "Object", new Type[0]), 
                     new ReferenceType[0], 
                     new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0])); 
      
      
      cd0.visit(_fv);
      assertEquals("There should be no errors", 0, errors.size());
      assertTrue("Should have resolved java.lang.Object", 
                 LanguageLevelConverter.symbolTable.containsKey("java.lang.Object"));
      assertFalse("Should not be a continuation", 
                  LanguageLevelConverter.symbolTable.get("java.lang.Object").isContinuation());
      SymbolData sd = LanguageLevelConverter.symbolTable.get("myPackage.Lisa");
      assertTrue("Lisa should be in _newSDs", LanguageLevelConverter._newSDs.containsKey(sd));
      assertEquals("sd should have no methods", 0, sd.getMethods().size());
      assertEquals("sd's package should be correct", "myPackage", sd.getPackage());
      
      
      _fv._package = "";
      ClassDef cd1 = 
        new ClassDef(SourceInfo.NO_INFO, _abstractMav, new Word(SourceInfo.NO_INFO, "Bart"),
                     new TypeParameter[0], 
                     new ClassOrInterfaceType(SourceInfo.NO_INFO, "System", new Type[0]), 
                     new ReferenceType[0], 
                     new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      cd1.visit(_fv);
      assertEquals("There should be no errors", 0, errors.size());
      assertTrue("Should have resolved java.lang.System", 
                 LanguageLevelConverter.symbolTable.containsKey("java.lang.System"));
      assertFalse("Should not be a continuation", 
                  LanguageLevelConverter.symbolTable.get("java.lang.System").isContinuation());
      sd = LanguageLevelConverter.symbolTable.get("Bart");
      
      assertEquals("There should be 0 methods", 0, sd.getMethods().size()); 
      
      
      ConcreteMethodDef cmd = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                                    _packageMav, 
                                                    new TypeParameter[0], 
                                                    new VoidReturn(SourceInfo.NO_INFO, "void"), 
                                                    new Word(SourceInfo.NO_INFO, "testMethodName"),
                                                    new FormalParameter[0],
                                                    new ReferenceType[0], 
                                                    new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));

      ClassDef cd3 = 
        new ClassDef(SourceInfo.NO_INFO, 
                     _abstractMav, 
                     new Word(SourceInfo.NO_INFO, "TestSuper2"),
                     new TypeParameter[0], new ClassOrInterfaceType(SourceInfo.NO_INFO, "TestCase", new Type[0]), 
                     new ReferenceType[0], 
                     new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {cmd}));


      _fv._file=new File("TestSuper2.dj2");
      _fv._importedFiles.addLast("junit.framework.TestCase");
      LanguageLevelConverter.symbolTable.put("junit.framework.TestCase", new SymbolData("junit.framework.TestCase"));
      cd3.visit(_fv);
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

      ClassDef cd4 = 
        new ClassDef(SourceInfo.NO_INFO, 
                     _abstractMav, 
                     new Word(SourceInfo.NO_INFO, "TestVoidNoTestMethod"),
                     new TypeParameter[0], 
                     new ClassOrInterfaceType(SourceInfo.NO_INFO,"junit.framework.TestCase", new Type[0]), 
                     new ReferenceType[0], 
                     new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] { cmd2 }));

      _fv._file=new File("TestVoidNoTestMethod.dj2");
      cd4.visit(_fv);

      assertEquals("There should still be 0 errors", 0, errors.size());
      _fv._importedFiles.remove("junit.framework.TestCase");  
    }
    
    public void testForInterfaceDef() {
      AbstractMethodDef amd = 
        new AbstractMethodDef(SourceInfo.NO_INFO, 
                              _publicMav, 
                              new TypeParameter[0], 
                              new PrimitiveType(SourceInfo.NO_INFO, "int"),
                              new Word(SourceInfo.NO_INFO, "myMethod"), 
                              new FormalParameter[0], 
                              new ReferenceType[0]);
      AbstractMethodDef amd2 = 
        new AbstractMethodDef(SourceInfo.NO_INFO, 
                              _publicMav, 
                              new TypeParameter[0], 
                              new PrimitiveType(SourceInfo.NO_INFO, "int"),
                              new Word(SourceInfo.NO_INFO, "myMethod"), 
                              new FormalParameter[0], 
                              new ReferenceType[0]);
      InterfaceDef id = 
        new InterfaceDef(SourceInfo.NO_INFO, 
                         _publicMav, 
                         new Word(SourceInfo.NO_INFO, "id"), 
                         new TypeParameter[0], 
                         new ReferenceType[0], 
                         new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] { amd }));
      InterfaceDef id2 = 
        new InterfaceDef(SourceInfo.NO_INFO, 
                         _publicMav, 
                         new Word(SourceInfo.NO_INFO, "id2"), 
                         new TypeParameter[0], 
                         new ReferenceType[] { new ClassOrInterfaceType(SourceInfo.NO_INFO, "id", new Type[0]) }, 
                         new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] { amd2 }));
      SymbolData sd = new SymbolData("id", _publicMav, new TypeParameter[0], new LinkedList<SymbolData>(), null);
      sd.setIsContinuation(true);
      MethodData md = 
        new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE, new VariableData[0], 
                       new String[0], sd, amd);

      LinkedList<SymbolData> interfaces = new LinkedList<SymbolData>();
      interfaces.addLast(sd);
      SymbolData sd2 = new SymbolData("id2", _publicMav, new TypeParameter[0], interfaces, null);
      sd2.setIsContinuation(true);
      MethodData md2 = 
        new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE, new VariableData[0], 
                       new String[0], sd2, amd2);
      
      LanguageLevelConverter.symbolTable.put("id", sd);
      LanguageLevelConverter.symbolTable.put("id2", sd2);

      id.visit(_fv);
      id2.visit(_fv);
      assertEquals("Should be no errors", 0, errors.size());
      assertEquals("Should return the same symbol datas: id", sd, LanguageLevelConverter.symbolTable.get("id"));
      assertEquals("Should return the same symbol datas:id2 ", sd2, LanguageLevelConverter.symbolTable.get("id2"));
    }
    
    public void testHandleInnerClassDef() {      
      SymbolData obj = new SymbolData("java.lang.Object");
      LanguageLevelConverter.symbolTable.put("java.lang.Object", obj);
      InnerClassDef cd1 = 
        new InnerClassDef(SourceInfo.NO_INFO, 
                          _packageMav, 
                          new Word(SourceInfo.NO_INFO, "Bart"),
                          new TypeParameter[0], 
                          new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), 
                          new ReferenceType[0], 
                          new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      InnerClassDef cd0 = 
        new InnerClassDef(SourceInfo.NO_INFO, 
                          _packageMav, 
                          new Word(SourceInfo.NO_INFO, "Lisa"),
                          new TypeParameter[0], 
                          new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), 
                          new ReferenceType[0], 
                          new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {cd1}));

      SymbolData outerData = new SymbolData("i.eat.potato");
      SymbolData sd0 = 
        new SymbolData(outerData.getName() + "$Lisa", _packageMav, new TypeParameter[0], obj, 
                       new LinkedList<SymbolData>(), null); 
      SymbolData sd1 = 
        new SymbolData(outerData.getName() + "$Lisa$Bart", _packageMav, new TypeParameter[0], obj, 
                       new LinkedList<SymbolData>(), null); 
      
      outerData.addInnerClass(sd0);
      sd0.setOuterData(outerData);

      sd0.addInnerClass(sd1);
      sd1.setOuterData(sd0);

      sd0.setIsContinuation(true);
      sd1.setIsContinuation(true);
      
            
      LanguageLevelConverter.symbolTable.put(outerData.getName() + "$Lisa", sd0);

      _fv.handleInnerClassDef(cd0, outerData, outerData.getName() + "$Lisa");

      SymbolData sd = outerData.getInnerClassOrInterface("Lisa");
      assertEquals("There should be no errors", 0, errors.size());
      assertEquals("This symbolData should now have sd0 as an inner class", sd0, sd);
      assertEquals("sd0 should have the correct outer data", outerData, sd0.getOuterData());
      assertEquals("sd1 should have the correct outer data", sd0, sd1.getOuterData());
      assertEquals("Sd should now have sd1 as an inner class", sd1, sd.getInnerClassOrInterface("Bart"));
      
      
      assertEquals("Lisa should have 0 methods", 0, sd0.getMethods().size());
    }
    
    public void xtestHandleInnerInterfaceDef() {
      SymbolData obj = new SymbolData("java.lang.Object");
      LanguageLevelConverter.symbolTable.put("java.lang.Object", obj);
      InnerInterfaceDef cd1 = 
        new InnerInterfaceDef(SourceInfo.NO_INFO, _packageMav, new Word(SourceInfo.NO_INFO, "Bart"),
                              new TypeParameter[0], new ReferenceType[0], 
                              new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      InnerInterfaceDef cd0 = 
        new InnerInterfaceDef(SourceInfo.NO_INFO, _packageMav, new Word(SourceInfo.NO_INFO, "Lisa"),
                              new TypeParameter[0], new ReferenceType[0], 
                              new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] { cd1 }));

      SymbolData outerData = new SymbolData("i.drink.vanilla.coke");

      
      SymbolData sd0 = 
        new SymbolData(outerData.getName() + "$Lisa", _packageMav, new TypeParameter[0], new LinkedList<SymbolData>(), 
                       null); 
      SymbolData sd1 = 
        new SymbolData(outerData.getName() + "$Lisa$Bart", _packageMav, new TypeParameter[0], 
                       new LinkedList<SymbolData>(), null);
      sd0.addInnerInterface(sd1);
      sd0.setIsContinuation(true);
      sd1.setIsContinuation(true);

      outerData.addInnerInterface(sd0);
      sd0.setOuterData(outerData);

      sd0.addInnerInterface(sd1);
      sd1.setOuterData(sd0);

      sd0.setIsContinuation(true);
      sd1.setIsContinuation(true);

      
      _fv.handleInnerInterfaceDef(cd0, outerData, outerData.getName() + "$Lisa");

      SymbolData sd = outerData.getInnerClassOrInterface("Lisa");
      
      assertEquals("There should be no errors", 0, errors.size());
      assertEquals("This symbolData should now have sd0 as an inner interface", sd0, sd);
      assertEquals("sd0 should have the correct outer data", outerData, sd0.getOuterData());
      assertEquals("sd1 should have the correct outer data", sd0, sd1.getOuterData());
      assertEquals("Sd should now have sd1 as an inner interface", sd1, sd.getInnerClassOrInterface("Bart"));
      assertTrue("Lisa should be an interface", sd0.isInterface());
      assertTrue("Bart should be an interface", sd1.isInterface());
    }
    
    
    public void testCreateMethodData() {
      
      MethodDef mdef = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                                    _volatileMav, 
                                                    new TypeParameter[0], 
                                                    new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                    new Word(SourceInfo.NO_INFO, "methodName"),
                                                    new FormalParameter[0],
                                                    new ReferenceType[0], 
                                                    new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      MethodData mdata = new MethodData("methodName", _volatileMav, new TypeParameter[0], SymbolData.INT_TYPE, 
                                   new VariableData[0], 
                                   new String[0],
                                   _sd1,
                                   null);
      assertEquals("Should return the correct MethodData", mdata, _fv.createMethodData(mdef, _sd1));
      assertEquals("There should be no errors", 0, errors.size());

      mdef = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                                    _finalMav, 
                                                    new TypeParameter[0], 
                                                    new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                    new Word(SourceInfo.NO_INFO, "methodName2"),
                                                    new FormalParameter[0],
                                                    new ReferenceType[0], 
                                                    new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      mdata = new MethodData("methodName2", _finalMav, new TypeParameter[0], SymbolData.INT_TYPE, 
                                   new VariableData[0], 
                                   new String[0],
                                   _sd1,
                                   null);
      assertEquals("Should return the correct MethodData", mdata, _fv.createMethodData(mdef, _sd1));
      assertEquals("There should still be no errors", 0, errors.size());

      
      
      FormalParameter fp1 =
        new FormalParameter(SourceInfo.NO_INFO, 
                            new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                                                new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                                new Word (SourceInfo.NO_INFO, "field1")),
                            false);
      FormalParameter fp2 =
        new FormalParameter(SourceInfo.NO_INFO, 
                            new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                                                new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                                new Word (SourceInfo.NO_INFO, "field1")),
                            false);
      mdef =
        new AbstractMethodDef(SourceInfo.NO_INFO, 
                              _abstractMav, 
                              new TypeParameter[] { new TypeParameter(SourceInfo.NO_INFO,
                                                                      new TypeVariable(SourceInfo.NO_INFO, "T"),
                                                                      new TypeVariable(SourceInfo.NO_INFO, "U"))},
                              new VoidReturn(SourceInfo.NO_INFO, "void"), 
                              new Word(SourceInfo.NO_INFO, "methodName"),
                              new FormalParameter[] {fp1, fp2},
                              new ReferenceType[] { new TypeVariable(SourceInfo.NO_INFO, "X") });
      VariableData[] vardatas =
        new VariableData[] { new VariableData("field1", _finalMav, SymbolData.DOUBLE_TYPE, true, null),
                             new VariableData("field1", _finalMav, SymbolData.INT_TYPE, true, null) };
      mdata = new MethodData("methodName", 
                             _abstractMav, 
                             new TypeParameter[] { new TypeParameter(SourceInfo.NO_INFO,
                                                                     new TypeVariable(SourceInfo.NO_INFO, "T"),
                                                                     new TypeVariable(SourceInfo.NO_INFO, "U"))}, 
                             SymbolData.VOID_TYPE, 
                             vardatas, 
                             new String[] { "X" },
                             _sd1,
                             null);
      
      mdata.getParams()[0].setEnclosingData(mdata);
      mdata.getParams()[1].setEnclosingData(mdata);
      
      MethodData expectedMethod = _fv.createMethodData(mdef, _sd1);
      
      
      mdata.getParams()[0].setEnclosingData(expectedMethod);
      mdata.getParams()[1].setEnclosingData(expectedMethod);
      VariableData[] vd = new VariableData[] { 
        new VariableData("f,ield1", _finalMav, SymbolData.DOUBLE_TYPE, true, expectedMethod)
      };
      mdata.addVars(vd);  

      assertEquals("There should be 1 error", 1, errors.size());
      assertEquals("The first error message should be correct.", 
                   "You cannot have two method parameters with the same name", errors.get(0).getFirst());
    }
    
    public void xtestSimpleAnonymousClassInstantiationHelper() {
      SimpleAnonymousClassInstantiation basic = 
        new SimpleAnonymousClassInstantiation(SourceInfo.NO_INFO, 
                                              new ClassOrInterfaceType(SourceInfo.NO_INFO, "Object", new Type[0]), 
                                              new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]),
                                              new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      

     _fv._package = "i.like";
     _fv.simpleAnonymousClassInstantiationHelper(basic, _sd1);
     assertEquals("There should be no errors", 0, errors.size());
     SymbolData obj = LanguageLevelConverter.symbolTable.get("java.lang.Object");
     assertNotNull("Object should be in the symbol table", obj);
     assertEquals("sd1 should have one inner class", 1, _sd1.getInnerClasses().size());
     SymbolData inner = _sd1.getInnerClasses().get(0);
     assertEquals("The inner class should have the proper name", "i.like.monkey$1", inner.getName());
     assertEquals("The inner class should have proper outer data", _sd1, inner.getOuterData());
     assertEquals("The inner class should have proper super class", obj, inner.getSuperClass());
     assertEquals("The inner class should have the right package", "i.like", inner.getPackage());
     assertEquals("The inner class should have 0 methods", 0, inner.getMethods().size());
    }

    
    public void testComplexAnonymousClassInstantiationHelper() {
      ComplexAnonymousClassInstantiation basic = 
        new ComplexAnonymousClassInstantiation(SourceInfo.NO_INFO, 
                                               new SimpleNameReference(SourceInfo.NO_INFO,
                                                                       new Word(SourceInfo.NO_INFO, "java.lang.Object")),
                                               new ClassOrInterfaceType(SourceInfo.NO_INFO, "Inner", new Type[0]), 
                                               new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]),
                                               new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));

     _fv._package = "i.like";
     _fv.complexAnonymousClassInstantiationHelper(basic, _sd1);
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
     assertEquals("The inner class should have 0 methods", 0, inner.getMethods().size());
    }

    public void testForVariableDeclaration() {
      
      
      SimpleAnonymousClassInstantiation basic = 
        new SimpleAnonymousClassInstantiation(SourceInfo.NO_INFO, 
                                              new ClassOrInterfaceType(SourceInfo.NO_INFO, "Object", new Type[0]), 
                                              new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]),
                                              new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
     VariableDeclarator[] d1 = {
       new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                         new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), 
                                         new Word(SourceInfo.NO_INFO, "b"), basic)
     };
     VariableDeclaration vd1 = new VariableDeclaration(SourceInfo.NO_INFO,_publicMav, d1); 
     
     ClassBodyFullJavaVisitor cbav = 
       new ClassBodyFullJavaVisitor(_sd1, "", _fv._file, _fv._package, _fv._importedFiles, _fv._importedPackages, 
                                    _fv._classNamesInThisFile, _fv.continuations);
     vd1.visit(cbav);
     assertEquals("Should be 1 inner class of _sd1", 1, _sd1.getInnerClasses().size());
     
    }
    
    public void testDummy() { }
  }
}