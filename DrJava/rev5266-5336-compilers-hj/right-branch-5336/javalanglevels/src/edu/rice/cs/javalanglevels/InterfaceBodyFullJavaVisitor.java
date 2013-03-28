

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.*;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.IterUtil;
import java.util.*;
import java.io.*;

import junit.framework.TestCase;


public class InterfaceBodyFullJavaVisitor extends FullJavaVisitor {
  
  
  private SymbolData _symbolData;
  
  
  public InterfaceBodyFullJavaVisitor(SymbolData sd, 
                                      File file, 
                                      String packageName, 
                                      LinkedList<String> importedFiles, 
                                      LinkedList<String> importedPackages, 
                                      LinkedList<String> classDefsInThisFile,
                                      Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations) {
    super(file, packageName, importedFiles, importedPackages, classDefsInThisFile, continuations);
    _symbolData = sd;
  }
  
  
  public Void forStatementDoFirst(Statement that) { return null; }
  
    
  
  public Void forConcreteMethodDefDoFirst(ConcreteMethodDef that) {
    _addError("You cannot have concrete methods definitions in interfaces", that);
    return null;
  }

  
  
  public Void forInstanceInitializerDoFirst(InstanceInitializer that) {
    _addError("This open brace must mark the beginning of an interface body", that);
    return null;
  }
  
  
  public Void forVariableDeclarationOnly(VariableDeclaration that) { return null; }

  
  public Void forThisReferenceDoFirst(ThisReference that) {
    _addAndIgnoreError("The field 'this' does not exist in interfaces.  Only classes have a 'this' field.", that);
    return null;
  }

  
  public Void forSuperReferenceDoFirst(SuperReference that) {
    _addAndIgnoreError("The field 'super' does not exist in interfaces.  Only classes have a 'super' field", that);
    return null;
  }

    public Void forAbstractMethodDef(AbstractMethodDef that) {
    forAbstractMethodDefDoFirst(that);
    if (_checkError()) return null;
    
    MethodData md = createMethodData(that, _symbolData);
    
    
    if (md.hasModifier("private")) {
      _addAndIgnoreError("Interface methods cannot be made private.  They must be public.", that.getMav());
    }
    if (md.hasModifier("protected")) {
      _addAndIgnoreError("Interface methods cannot be made protected.  They must be public.", that.getMav());
    }
    
 
    md.addModifier("public");
    md.addModifier("abstract"); 
    String className = getUnqualifiedClassName(_symbolData.getName());
    if (className.equals(md.getName())) {
      _addAndIgnoreError("Only constructors can have the same name as the class they appear in, " + 
                         "and constructors cannot appear in interfaces.", that);
    }
    else _symbolData.addMethod(md);
    return null;
  }
  
  
  public Void forInnerInterfaceDef(InnerInterfaceDef that) {
    handleInnerInterfaceDef(that, _symbolData, getQualifiedClassName(_symbolData.getName()) + "." + that.getName().getText());
    return null;
  }
  
  
  public Void forInnerClassDef(InnerClassDef that) {
    handleInnerClassDef(that, _symbolData, getQualifiedClassName(_symbolData.getName()) + "." + that.getName().getText());
    return null;
  }
  
  
  public Void forConstructorDefDoFirst(ConstructorDef that) {
    _addAndIgnoreError("Constructor definitions cannot appear in interfaces", that);
    return null;
  }

  
  public Void forComplexAnonymousClassInstantiation(ComplexAnonymousClassInstantiation that) {
    complexAnonymousClassInstantiationHelper(that, _symbolData);
    return null;
  }

  
  public Void forSimpleAnonymousClassInstantiation(SimpleAnonymousClassInstantiation that) {

    simpleAnonymousClassInstantiationHelper(that, _symbolData);
    return null;
  }

  
  public static class InterfaceBodyFullJavaVisitorTest extends TestCase {
    
    private InterfaceBodyFullJavaVisitor _ibfv;
    
    private SymbolData _sd1;
    private ModifiersAndVisibility _publicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _protectedMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});
    private ModifiersAndVisibility _privateMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"private"});
    private ModifiersAndVisibility _packageMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    private ModifiersAndVisibility _abstractMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract"});
    private ModifiersAndVisibility _finalMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final"});
    
    public InterfaceBodyFullJavaVisitorTest() { this(""); }
    public InterfaceBodyFullJavaVisitorTest(String name) { super(name);  }
    
    public void setUp() {
      _sd1 = new SymbolData("i.like.monkey");

      errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();
      LanguageLevelConverter._newSDs.clear();
      LanguageLevelConverter.OPT = new Options(JavaVersion.JAVA_5, IterUtil.make(new File("lib/buildlib/junit.jar")));
      visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, edu.rice.cs.javalanglevels.tree.SourceFile>>();      
      _hierarchy = new Hashtable<String, TypeDefBase>();
      _ibfv = new InterfaceBodyFullJavaVisitor(_sd1, 
                                               new File(""), 
                                               "", 
                                               new LinkedList<String>(), 
                                               new LinkedList<String>(), 
                                               new LinkedList<String>(), 
                                               new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      _ibfv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();
      _ibfv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      _ibfv._resetNonStaticFields();
      _ibfv._importedPackages.addFirst("java.lang");
      _errorAdded = false;
    }
    
    public void testForConcreteMethodDefDoFirst() {
      
      ConcreteMethodDef cmd = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                                    _publicMav, 
                                                    new TypeParameter[0], 
                                                    new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                    new Word(SourceInfo.NO_INFO, "methodName"),
                                                    new FormalParameter[0],
                                                    new ReferenceType[0], 
                                                    new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      cmd.visit(_ibfv);
      assertEquals("There should not be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "You cannot have concrete methods definitions in interfaces", 
                   errors.getLast().getFirst());
      
    }
    
    public void testForAbstractMethodDefDoFirst() {
      
      _ibfv._symbolData.setMav(_abstractMav);
      AbstractMethodDef amd2 = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                                     _abstractMav, 
                                                     new TypeParameter[0], 
                                                     new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                     new Word(SourceInfo.NO_INFO, "methodName"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0]);
      amd2.visit(_ibfv);
      assertEquals("There should be no errors", 0, errors.size());
      assertTrue("The method def should be public", 
                 _ibfv._symbolData.getMethods().get(0).hasModifier("public"));

    }

    public void testForInstanceInitializerDoFirst() {
      InstanceInitializer ii = 
        new InstanceInitializer(SourceInfo.NO_INFO, 
                                new Block(SourceInfo.NO_INFO, 
                                          new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0])));
      ii.visit(_ibfv);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct.", 
                   "This open brace must mark the beginning of an interface body", 
                   errors.get(0).getFirst());    
    }

    public void testForSimpleThisReferenceDoFirst() {
     SimpleThisReference tl = new SimpleThisReference(SourceInfo.NO_INFO);
     tl.visit(_ibfv);
     assertEquals("There should be one error", 1, errors.size());
     assertEquals("The error message should be correct", 
                  "The field 'this' does not exist in interfaces.  Only classes have a 'this' field.", 
                  errors.get(0).getFirst());
    }
    
    public void testForComplexThisReferenceDoFirst() {
     ComplexThisReference tl = new ComplexThisReference(SourceInfo.NO_INFO, 
                                                        new NullLiteral(SourceInfo.NO_INFO));
     tl.visit(_ibfv);
     assertEquals("There should be one error", 1, errors.size());
     assertEquals("The error message should be correct", 
                  "The field 'this' does not exist in interfaces.  Only classes have a 'this' field.", 
                  errors.get(0).getFirst());

    }
    
    public void testForSimpleSuperReferenceDoFirst() {
     SimpleSuperReference sr = new SimpleSuperReference(SourceInfo.NO_INFO);
     sr.visit(_ibfv);
     assertEquals("There should be one error", 1, errors.size());
     assertEquals("The error message should be correct", 
                  "The field 'super' does not exist in interfaces.  Only classes have a 'super' field", 
                  errors.get(0).getFirst());
    }
    
    public void testForComplexSuperReferenceDoFirst() {
      ComplexSuperReference cr = new ComplexSuperReference(SourceInfo.NO_INFO, 
                                                           new NullLiteral(SourceInfo.NO_INFO));
      cr.visit(_ibfv);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", 
                   "The field 'super' does not exist in interfaces.  Only classes have a 'super' field", 
                   errors.get(0).getFirst());
    }
    
    public void xtestForVariableDeclarationDoFirst() {
      
      VariableDeclaration vdecl0 = new VariableDeclaration(SourceInfo.NO_INFO,
                                                       _packageMav,
                                                       new VariableDeclarator[] {
        new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                          new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                          new Word (SourceInfo.NO_INFO, "field0"), 
                                          new DoubleLiteral(SourceInfo.NO_INFO, 2.345))});

      vdecl0.visit(_ibfv);
      assertEquals("There should be no errors", 0, errors.size());
                                                           
      
      
      VariableDeclaration vdecl = new VariableDeclaration(SourceInfo.NO_INFO,
                                                       _packageMav,
                                                       new VariableDeclarator[] {
        new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                               new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                               new Word (SourceInfo.NO_INFO, "field1"),
                               new DoubleLiteral(SourceInfo.NO_INFO, 2.45)),
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                               new PrimitiveType(SourceInfo.NO_INFO, "boolean"), 
                               new Word (SourceInfo.NO_INFO, "field2"))});
      vdecl.visit(_ibfv);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", 
                   "All fields in interfaces must be assigned a value when they are declared", 
                   errors.getLast().getFirst());
    }
    
    public void testForAbstractMethodDef() {
      
      MethodDef mdef = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                             _abstractMav, 
                                             new TypeParameter[0], 
                                             new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                             new Word(SourceInfo.NO_INFO, "methodName"),
                                             new FormalParameter[0],
                                             new ReferenceType[0]);
      _ibfv._symbolData.setMav(_abstractMav);
      mdef.visit(_ibfv);
      assertEquals("There should not be any errors.", 0, errors.size());
      
      
      mdef = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                             _abstractMav, 
                                             new TypeParameter[0], 
                                             new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                             new Word(SourceInfo.NO_INFO, "monkey"),
                                             new FormalParameter[0],
                                             new ReferenceType[0]);
      mdef.visit(_ibfv);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct.", 
                   "Only constructors can have the same name as the class they appear in, and constructors cannot " 
                     + "appear in interfaces.",
                   errors.get(0).getFirst());
      
      
      AbstractMethodDef amd3 = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                                     _publicMav, 
                                                     new TypeParameter[0], 
                                                     new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                     new Word(SourceInfo.NO_INFO, "methodName2"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0]);
      amd3.visit(_ibfv);
      assertEquals("There should still be one error", 1, errors.size());
      assertTrue("The method def should be public", _ibfv._symbolData.getMethods().get(1).hasModifier("public"));

      
      AbstractMethodDef amd4 = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                                     _privateMav, 
                                                     new TypeParameter[0], 
                                                     new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                     new Word(SourceInfo.NO_INFO, "methodName3"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0]);
      amd4.visit(_ibfv);
      assertEquals("There should be two errors", 2, errors.size());
      assertEquals("The error message should be correct", 
                   "Interface methods cannot be made private.  They must be public." , 
                   errors.get(1).getFirst());
    
      
      AbstractMethodDef amd5 = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                                     _protectedMav, 
                                                     new TypeParameter[0], 
                                                     new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                     new Word(SourceInfo.NO_INFO, "methodName4"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0]);
      amd5.visit(_ibfv);
      assertEquals("There should be three errors", 3, errors.size());
      assertEquals("The error message should be correct",
                   "Interface methods cannot be made protected.  They must be public." , 
                   errors.get(2).getFirst());
    }
    

    public void testForInnerClassDef() {
      _ibfv._symbolData = new SymbolData("MyInterface");
      _ibfv._symbolData.setInterface(true);
      
      SymbolData obj = new SymbolData("java.lang.Object");
      LanguageLevelConverter._newSDs.clear();
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
     
      SymbolData sd0 = new SymbolData(_ibfv._symbolData.getName() + "$Lisa", _packageMav, new TypeParameter[0], obj, 
                                      new LinkedList<SymbolData>(), null); 
      SymbolData sd1 = 
        new SymbolData(_ibfv._symbolData.getName() + "$Lisa$Bart", _packageMav, new TypeParameter[0], obj, 
                       new LinkedList<SymbolData>(), null); 
      
      _ibfv._symbolData.addInnerClass(sd0);
      sd0.setOuterData(_ibfv._symbolData);

      sd0.addInnerClass(sd1);
      sd1.setOuterData(sd0);

      sd0.setIsContinuation(true);
      sd1.setIsContinuation(true);
      LanguageLevelConverter._newSDs.clear();
      LanguageLevelConverter.symbolTable.put(_ibfv._symbolData.getName() + "$Lisa", sd0);

      cd0.visit(_ibfv);

      SymbolData sd = _ibfv._symbolData.getInnerClassOrInterface("Lisa");
      
      assertEquals("This symbolData should now have sd0 as an inner class", sd0, sd);
      assertEquals("sd0 should have the correct outer data", _ibfv._symbolData, sd0.getOuterData());
      assertFalse("sd0 should be a class", sd0.isInterface());
      assertFalse("sd1 should be a class", sd1.isInterface());
      assertTrue("Top symbol data should be an interface", _ibfv._symbolData.isInterface());
      assertEquals("sd1 should have the correct outer data", sd0, sd1.getOuterData());
      assertEquals("Sd should now have sd1 as an inner class", sd1, sd.getInnerClassOrInterface("Bart"));
      
      assertEquals("Lisa should have 0 methods", 0, sd0.getMethods().size());
    }
    
    public void testForInnerInterfaceDef() {
      _ibfv._symbolData = new SymbolData("MyInterface");
      _ibfv._symbolData.setInterface(true);

      
      SymbolData obj = new SymbolData("java.lang.Object");
      LanguageLevelConverter._newSDs.clear();
      LanguageLevelConverter.symbolTable.put("java.lang.Object", obj);
      InnerInterfaceDef cd1 = 
        new InnerInterfaceDef(SourceInfo.NO_INFO, _packageMav, new Word(SourceInfo.NO_INFO, "Bart"),
                              new TypeParameter[0], new ReferenceType[0], 
                              new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      InnerInterfaceDef cd0 = 
        new InnerInterfaceDef(SourceInfo.NO_INFO, _packageMav, new Word(SourceInfo.NO_INFO, "Lisa"),
                              new TypeParameter[0], new ReferenceType[0], 
                              new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {cd1}));
      
      SymbolData sd0 = new SymbolData(_ibfv._symbolData.getName() + "$Lisa", _packageMav, new TypeParameter[0], 
                                      new LinkedList<SymbolData>(), null); 
      SymbolData sd1 = new SymbolData(_ibfv._symbolData.getName() + "$Lisa$Bart", _packageMav, new TypeParameter[0], 
                                      new LinkedList<SymbolData>(), null);
      sd0.addInnerInterface(sd1);

      
      _ibfv._symbolData.addInnerInterface(sd0);
      sd0.setOuterData(_ibfv._symbolData);

      sd0.addInnerInterface(sd1);
      sd1.setOuterData(sd0);

      
      sd0.setIsContinuation(true);
      sd1.setIsContinuation(true);
      LanguageLevelConverter._newSDs.clear();
      LanguageLevelConverter.symbolTable.put(_ibfv._symbolData.getName() + "$Lisa", sd0);
      LanguageLevelConverter._newSDs.clear();
      LanguageLevelConverter.symbolTable.put(_ibfv._symbolData.getName() + "$Lisa$Bart", sd1);

      cd0.visit(_ibfv);

      SymbolData sd = _ibfv._symbolData.getInnerClassOrInterface("Lisa");
      
      assertEquals("This symbolData should now have sd0 as an inner interface", sd0, sd);
      assertEquals("sd0 should have the correct outer data", _ibfv._symbolData, sd0.getOuterData());
      assertEquals("sd1 should have the correct outer data", sd0, sd1.getOuterData());
      assertEquals("Sd should now have sd1 as an inner interface", sd1, sd.getInnerClassOrInterface("Bart"));
      assertTrue("Lisa should be an interface", sd0.isInterface());
      assertTrue("Bart should be an interface", sd1.isInterface());
      assertTrue("The outer data should be an interface", _ibfv._symbolData.isInterface());      
    }

    public void testForConstructorDef() {
     
      ConstructorDef cd =
        new ConstructorDef(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "MyClass"),
                           _publicMav, new FormalParameter[0], new ReferenceType[0], 
                            new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));

      
      cd.visit(_ibfv);
      assertEquals("There should now be one error", 1, errors.size());
      assertEquals("The error message should be correct", "Constructor definitions cannot appear in interfaces", 
                   errors.get(0).getFirst());
      
    }
    public void testDummy() { }
  }
}