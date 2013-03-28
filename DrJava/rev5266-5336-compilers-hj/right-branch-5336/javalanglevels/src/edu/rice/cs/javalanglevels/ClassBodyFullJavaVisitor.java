

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.*;
import edu.rice.cs.javalanglevels.util.*;
import java.util.*;
import java.io.*;

import junit.framework.TestCase;


public class ClassBodyFullJavaVisitor extends FullJavaVisitor {
  
  
  private SymbolData _enclosingData;
  
  
  public ClassBodyFullJavaVisitor(SymbolData sd, 
                                  String className,
                                  File file, 
                                  String packageName,
                                  LinkedList<String> importedFiles, 
                                  LinkedList<String> importedPackages,
                                  LinkedList<String> classDefsInThisFile, 
                                  Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations) {
    super(file, packageName, importedFiles, importedPackages, classDefsInThisFile, continuations);
    _enclosingClassName = className;
    _enclosingData = sd;
  }

  
  public Void forStatementDoFirst(Statement that) { return null; }
  
  
  
  
  public Void forAbstractMethodDefDoFirst(AbstractMethodDef that) {



    if (! _enclosingData.isInterface() && ! _enclosingData.hasModifier("abstract")) { 
      _addError("Abstract methods can only be declared in abstract classes", that);
    }
    return super.forAbstractMethodDefDoFirst(that);
  }

  
  public Void forInstanceInitializerDoFirst(InstanceInitializer that) {
    _addError("This open brace must mark the beginning of a method or class body", that);
    return null;
  }
  
  
  public Void forVariableDeclarationOnly(VariableDeclaration that) {
    VariableData[] vds = _variableDeclaration2VariableData(that, _enclosingData);

    



    if (! _enclosingData.addVars(vds)) {
      _addAndIgnoreError("You cannot have two fields with the same name.  Either you already have a field by that " 
                           + "name in this class, or one of your superclasses or interfaces has a field by that name", 
                         that);
    }
    return null;
  }
  
  
  public Void forConcreteMethodDef(ConcreteMethodDef that) {
    forConcreteMethodDefDoFirst(that);
    if (prune(that)) return null;
    
    MethodData md = createMethodData(that, _enclosingData);
    String className = getUnqualifiedClassName(_enclosingData.getName());
    
    if (className.equals(md.getName())) {
      _addAndIgnoreError("Only constructors can have the same name as the class they appear in, and constructors do not have an explicit return type",
                         that);
    }
    else {
      _enclosingData.addMethod(md);
    }
    that.getBody().visit(new BodyBodyFullJavaVisitor(md, _file, _package, _importedFiles, _importedPackages, 
                                                     _classNamesInThisFile, continuations, _innerClassesToBeParsed));
    return null;
  }

  
  public Void forAbstractMethodDef(AbstractMethodDef that) {
    forAbstractMethodDefDoFirst(that);
    if (prune(that)) return null;

    MethodData md = createMethodData(that, _enclosingData);
    String className = getUnqualifiedClassName(_enclosingData.getName());
    if (className.equals(md.getName())) {
      _addAndIgnoreError("Only constructors can have the same name as the class they appear in, and constructors do "
                           + "not have an explicit return type",
                         that);
    }
    else _enclosingData.addMethod(md);
    return null;
  }
  
  
  public Void forInnerInterfaceDef(InnerInterfaceDef that) {
    handleInnerInterfaceDef(that, _enclosingData, 
                            getQualifiedClassName(_enclosingData.getName()) + "." + that.getName().getText());
    return null;
  }
  
  
  public Void forInnerClassDef(InnerClassDef that) {
    handleInnerClassDef(that, _enclosingData, getQualifiedClassName(_enclosingData.getName()) + "." + that.getName().getText());
    return null;
  }

  
  public Void forConstructorDef(ConstructorDef that) {
    forConstructorDefDoFirst(that);
    if (prune(that)) return null;
    
    that.getMav().visit(this);
    String name = getUnqualifiedClassName(that.getName().getText());
    if ((that.getName().getText().indexOf(".") != -1 && !that.getName().getText().equals(_enclosingData.getName())) || !name.equals(getUnqualifiedClassName(_enclosingData.getName()))) {
      _addAndIgnoreError("The constructor return type and class name must match", that);
    }

    
    String[] throwStrings = referenceType2String(that.getThrows());
    
    SymbolData returnType = _enclosingData;
    MethodData md = MethodData.make(name, that.getMav(), new TypeParameter[0], returnType, 
                                   new VariableData[0], throwStrings, _enclosingData, that);

    _checkError(); 
    
    VariableData[] vds = formalParameters2VariableData(that.getParameters(), md);
    if (! _checkError()) {  
      md.setParams(vds);
      if (!md.addVars(vds)) {
        _addAndIgnoreError("You cannot have two method parameters with the same name", that);
      }
    }
    
    _enclosingData.addMethod(md);
    that.getStatements().visit(new BodyBodyFullJavaVisitor(md, _file, _package, _importedFiles, _importedPackages,
                                                           _classNamesInThisFile, continuations, 
                                                           _innerClassesToBeParsed));

    
    _enclosingData.incrementConstructorCount();
    return null;
  }
  
  
  public Void forComplexAnonymousClassInstantiation(ComplexAnonymousClassInstantiation that) {
    complexAnonymousClassInstantiationHelper(that, _enclosingData);
    return null;
  }

  
  public Void forSimpleAnonymousClassInstantiation(SimpleAnonymousClassInstantiation that) {
    simpleAnonymousClassInstantiationHelper(that, _enclosingData);
    return null;
  }
   
  
  public Void forModifiersAndVisibilityDoFirst(ModifiersAndVisibility that) {
    String[] modifiers = that.getModifiers();


    if (Utilities.isAbstract(modifiers) && Utilities.isStatic(modifiers))  _badModifiers("static", "abstract", that);
    return super.forModifiersAndVisibilityDoFirst(that);
  }
  
  
  public static class ClassBodyFullJavaVisitorTest extends TestCase {
    
    private ClassBodyFullJavaVisitor _cbfjv;
    
    private SymbolData _sd1;
    private ModifiersAndVisibility _publicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _protectedMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});
    private ModifiersAndVisibility _privateMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"private"});
    private ModifiersAndVisibility _packageMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    private ModifiersAndVisibility _abstractMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract"});
    private ModifiersAndVisibility _finalMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final"});
    private ModifiersAndVisibility _staticMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"static"});
    private ModifiersAndVisibility _abstractStaticMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract", "static"});
    private ModifiersAndVisibility _finalStaticMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"static", "final"});
    
    
    public ClassBodyFullJavaVisitorTest() { this(""); }
    public ClassBodyFullJavaVisitorTest(String name) { super(name); }
    
    public void setUp() {
      _sd1 = new SymbolData("i.like.monkey");

      errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();
      LanguageLevelConverter._newSDs.clear();
      visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, edu.rice.cs.javalanglevels.tree.SourceFile>>();      
      _hierarchy = new Hashtable<String, TypeDefBase>();
      _cbfjv = new ClassBodyFullJavaVisitor(_sd1, 
                                           "", 
                                           new File(""), 
                                           "", 
                                           new LinkedList<String>(), 
                                           new LinkedList<String>(), 
                                           new LinkedList<String>(), 
                                           new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      _cbfjv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();
      _cbfjv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      _cbfjv._resetNonStaticFields();
      _cbfjv._importedPackages.addFirst("java.lang");

      _errorAdded = false;
    }
    
    public void testForConcreteMethodDefDoFirst() {
      
      ConcreteMethodDef cmd = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                                    _publicMav, 
                                                    new TypeParameter[0], 
                                                    new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                    new Word(SourceInfo.NO_INFO, "methodName1"),
                                                    new FormalParameter[0],
                                                    new ReferenceType[0], 
                                                    new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      cmd.visit(_cbfjv);
      assertEquals("There should not be any errors", 0, errors.size());
      
      
      ConcreteMethodDef cmd2 = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                                     _abstractMav, 
                                                     new TypeParameter[0], 
                                                     new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                     new Word(SourceInfo.NO_INFO, "methodName2"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0], 
                                                     new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      cmd2.visit(_cbfjv);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", 
                   "Methods that have a braced body cannot be declared \"abstract\"", 
                   errors.get(0).getFirst());
      
      
      ConcreteMethodDef cmd3 = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                                     _staticMav, 
                                                     new TypeParameter[0], 
                                                     new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                     new Word(SourceInfo.NO_INFO, "methodName2"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0], 
                                                     new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      cmd3.visit(_cbfjv);
      assertEquals("There should still be one error", 1, errors.size());
      
      
    }
    
    public void testForAbstractMethodDefDoFirst() {
      
      AbstractMethodDef amd = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                                    _abstractMav, 
                                                    new TypeParameter[0], 
                                                    new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                    new Word(SourceInfo.NO_INFO, "methodName"),
                                                    new FormalParameter[0],
                                                    new ReferenceType[0]);
      amd.visit(_cbfjv);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct.", "Abstract methods can only be declared in abstract classes", 
                   errors.get(0).getFirst());
      
      
      _cbfjv._enclosingData.setMav(_abstractMav);
      AbstractMethodDef amd2 = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                                     _abstractMav, 
                                                     new TypeParameter[0], 
                                                     new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                     new Word(SourceInfo.NO_INFO, "methodName"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0]);
      amd2.visit(_cbfjv);
      assertEquals("There should still be one error", 1, errors.size());
      
      
      AbstractMethodDef amd3 = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                                     _abstractStaticMav, 
                                                     new TypeParameter[0], 
                                                     new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                     new Word(SourceInfo.NO_INFO, "methodName2"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0]);
      amd3.visit(_cbfjv);
      assertEquals("There should be two errors", 2, errors.size());
      assertEquals("The error message should be correct.", 
                   "Illegal combination of modifiers. Can't use static and abstract together.", 
                   errors.get(1).getFirst());
    }

    public void testForInstanceInitializerDoFirst() {
      InstanceInitializer ii = new InstanceInitializer(SourceInfo.NO_INFO, 
                                                       new Block(SourceInfo.NO_INFO, 
                                                                 new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0])));
      ii.visit(_cbfjv);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct.", 
                   "This open brace must mark the beginning of a method or class body", errors.get(0).getFirst());
    }
    
    
    
    public void testForVariableDeclarationOnly() {
      
      VariableDeclaration vdecl = new VariableDeclaration(SourceInfo.NO_INFO,
                                                       _packageMav,
                                                       new VariableDeclarator[] {
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                               new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                               new Word (SourceInfo.NO_INFO, "field1")),
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                               new PrimitiveType(SourceInfo.NO_INFO, "boolean"), 
                               new Word (SourceInfo.NO_INFO, "field2"))});
      VariableData vd1 = new VariableData("field1", _packageMav, SymbolData.DOUBLE_TYPE, true, _cbfjv._enclosingData);
      VariableData vd2 = new VariableData("field2", _packageMav, SymbolData.BOOLEAN_TYPE, true, _cbfjv._enclosingData);
      vdecl.visit(_cbfjv);
      assertEquals("There should not be any errors.", 0, errors.size());
      assertTrue("field1 was added.", _sd1.getVars().contains(vd1));
      assertTrue("field2 was added.", _sd1.getVars().contains(vd2));
      
      
      VariableDeclaration vdecl2 = 
        new VariableDeclaration(SourceInfo.NO_INFO,
                                _packageMav,
                                new VariableDeclarator[] {
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                            new Word(SourceInfo.NO_INFO, "field3")),
          new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                              new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                              new Word(SourceInfo.NO_INFO, "field3"))});
      VariableData vd3 = new VariableData("field3", _packageMav, SymbolData.DOUBLE_TYPE, true, _cbfjv._enclosingData);
      vdecl2.visit(_cbfjv);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct", 
                   "You cannot have two fields with the same name.  Either you already have a field by that name in " 
                     + "this class, or one of your superclasses or interfaces has a field by that name", 
                   errors.get(0).getFirst());
      




      assertTrue("field3 was added.", _sd1.getVars().contains(vd3));
      
      
      VariableDeclaration vdecl3 = new VariableDeclaration(SourceInfo.NO_INFO,
                                                           _staticMav,
                                                           new VariableDeclarator[] {
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                            new Word (SourceInfo.NO_INFO, "field4"))});
      
      VariableData vd4 = new VariableData("field4", _staticMav, SymbolData.DOUBLE_TYPE, true, _cbfjv._enclosingData);
      vdecl3.visit(_cbfjv);

      assertEquals("There should still be one error", 1, errors.size());



      assertTrue("field4 was added.", _sd1.getVars().contains(vd4));
           
      
      VariableDeclaration vdecl5 = new VariableDeclaration(SourceInfo.NO_INFO,
                                                           _publicMav,
                                                           new VariableDeclarator[] {
        new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                          new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                          new Word(SourceInfo.NO_INFO, "field5"), 
                                          new DoubleLiteral(SourceInfo.NO_INFO, 2.4))});
      vdecl5.visit(_cbfjv);
      VariableData vd5 = new VariableData("field5", _publicMav, SymbolData.DOUBLE_TYPE, true, _cbfjv._enclosingData);
      vd5.setHasInitializer(true);
      assertEquals("There should still be one error", 1, errors.size());
      assertTrue("Field 5 was added.", _sd1.getVars().contains(vd5));
      



















      
    }
    
    public void xtestFormalParameters2VariableData() {
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
      VariableData vd1 = new VariableData("field1", _finalMav, SymbolData.DOUBLE_TYPE, true, _cbfjv._enclosingData);
      VariableData vd2 = new VariableData("field2", _finalMav, SymbolData.BOOLEAN_TYPE, true, _cbfjv._enclosingData);
      VariableData[] vds = _cbfjv.formalParameters2VariableData(fps, _cbfjv._enclosingData);
      assertEquals("There should not be any errors.", 0, errors.size());
      assertEquals("vd1 should be the first entry in vds.", vd1, vds[0]);
      assertEquals("vd2 should be the second entry in vds.", vd2, vds[1]);
    }
    

    

    
    public void xtestForConcreteMethodDef() {
      
      MethodDef mdef = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                             _packageMav, 
                                             new TypeParameter[0], 
                                             new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                             new Word(SourceInfo.NO_INFO, "methodName"),
                                             new FormalParameter[0],
                                             new ReferenceType[0], 
                                             new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      mdef.visit(_cbfjv);
      assertEquals("There should not be any errors.", 0, errors.size());
      
      mdef = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                             _packageMav, 
                                             new TypeParameter[0], 
                                             new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                             new Word(SourceInfo.NO_INFO, "monkey"),
                                             new FormalParameter[0],
                                             new ReferenceType[0], 
                                             new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      mdef.visit(_cbfjv);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct.", 
                   "Only constructors can have the same name as the class they appear in, and constructors do not have an explicit return type",
                   errors.get(0).getFirst());
    }
    
    public void xtestForAbstractMethodDef() {
      
      MethodDef mdef = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                             _abstractMav, 
                                             new TypeParameter[0], 
                                             new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                             new Word(SourceInfo.NO_INFO, "methodName"),
                                             new FormalParameter[0],
                                             new ReferenceType[0]);
      _cbfjv._enclosingData.setMav(_abstractMav);
      mdef.visit(_cbfjv);
      assertEquals("There should not be any errors.", 0, errors.size());
      
      mdef = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                             _abstractMav, 
                                             new TypeParameter[0], 
                                             new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                             new Word(SourceInfo.NO_INFO, "monkey"),
                                             new FormalParameter[0],
                                             new ReferenceType[0]);
      mdef.visit(_cbfjv);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct.", 
                   "Only constructors can have the same name as the class they appear in, and constructors do not have an explicit return type",
                   errors.get(0).getFirst());
    }
    
   
    public void xtestForInitializedVariableDeclaratorDoFirst() {
      InitializedVariableDeclarator ivd = new InitializedVariableDeclarator(SourceInfo.NO_INFO,
                                                                            new PrimitiveType(SourceInfo.NO_INFO, "int"),
                                                                            new Word(SourceInfo.NO_INFO, "i"),
                                                                            new IntegerLiteral(SourceInfo.NO_INFO, 2));
      
      ivd.visit(_cbfjv);
      
      assertEquals("There should be no errors now", 0, errors.size());
    }

    public void xtestForInnerClassDef() {
      SymbolData obj = new SymbolData("java.lang.Object");
      LanguageLevelConverter.symbolTable.put("java.lang.Object", obj);
      InnerClassDef cd1 = new InnerClassDef(SourceInfo.NO_INFO, _packageMav, new Word(SourceInfo.NO_INFO, "Bart"),
                                       new TypeParameter[0], new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), new ReferenceType[0], 
                                       new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      InnerClassDef cd0 = new InnerClassDef(SourceInfo.NO_INFO, _packageMav, new Word(SourceInfo.NO_INFO, "Lisa"),
                                       new TypeParameter[0], new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), new ReferenceType[0], 
                                            new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {cd1}));

      
      SymbolData sd0 = new SymbolData(_cbfjv._enclosingData.getName() + "$Lisa", _packageMav, new TypeParameter[0], obj, new LinkedList<SymbolData>(), null); 
      _cbfjv._enclosingData.addInnerClass(sd0);
      sd0.setOuterData(_cbfjv._enclosingData);
      SymbolData sd1 = new SymbolData(_cbfjv._enclosingData.getName() + "$Lisa$Bart", _packageMav, new TypeParameter[0], obj, new LinkedList<SymbolData>(), null); 
      sd0.addInnerClass(sd1);
      sd1.setOuterData(sd0);

      sd0.setIsContinuation(true);
      sd1.setIsContinuation(true);

      
            
      LanguageLevelConverter.symbolTable.put(_cbfjv._enclosingData.getName() + "$Lisa", sd0);


      cd0.visit(_cbfjv);



      
      SymbolData sd = _cbfjv._enclosingData.getInnerClassOrInterface("Lisa");
      assertEquals("There should be no errors", 0, errors.size());
      assertEquals("This symbolData should now have sd0 as an inner class", sd0, sd);
      assertEquals("sd0 should have the correct outer data", _cbfjv._enclosingData, sd0.getOuterData());
      assertEquals("sd1 should have the correct outer data", sd0, sd1.getOuterData());
      assertEquals("Sd should now have sd1 as an inner class", sd1, sd.getInnerClassOrInterface("Bart"));
      
      
      assertEquals("Lisa should have 0 methods", 0, sd0.getMethods().size());
           
    }
    
    public void xtestForInnerInterfaceDef() {
      SymbolData obj = new SymbolData("java.lang.Object");
      LanguageLevelConverter.symbolTable.put("java.lang.Object", obj);
      InnerInterfaceDef cd1 = new InnerInterfaceDef(SourceInfo.NO_INFO, _packageMav, new Word(SourceInfo.NO_INFO, "Bart"),
                                       new TypeParameter[0], new ReferenceType[0], 
                                       new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      InnerInterfaceDef cd0 = new InnerInterfaceDef(SourceInfo.NO_INFO, _packageMav, new Word(SourceInfo.NO_INFO, "Lisa"),
                                       new TypeParameter[0], new ReferenceType[0], 
                                            new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {cd1}));
      
      SymbolData sd0 = new SymbolData(_cbfjv._enclosingData.getName() + "$Lisa", _packageMav, new TypeParameter[0], new LinkedList<SymbolData>(), null); 
      SymbolData sd1 = new SymbolData(_cbfjv._enclosingData.getName() + "$Lisa$Bart", _packageMav, new TypeParameter[0], new LinkedList<SymbolData>(), null);
      sd0.addInnerInterface(sd1);
      sd0.setIsContinuation(true);
      sd1.setIsContinuation(true);
      
      _cbfjv._enclosingData.addInnerInterface(sd0);
      sd0.setOuterData(_cbfjv._enclosingData);

      sd0.addInnerInterface(sd1);
      sd1.setOuterData(sd0);





      cd0.visit(_cbfjv);

      SymbolData sd = _cbfjv._enclosingData.getInnerClassOrInterface("Lisa");

      assertEquals("There should be no errors", 0, errors.size());
      assertEquals("This symbolData should now have sd0 as an inner interface", sd0, sd);
      assertEquals("sd0 should have the correct outer data", _cbfjv._enclosingData, sd0.getOuterData());
      assertEquals("sd1 should have the correct outer data", sd0, sd1.getOuterData());
      assertEquals("Sd should now have sd1 as an inner interface", sd1, sd.getInnerClassOrInterface("Bart"));
      assertTrue("Lisa should be an interface", sd0.isInterface());
      assertTrue("Bart should be an interface", sd1.isInterface());

    
    }
    
    public void xtestForConstructorDef() {
      
      ConstructorDef cd = new ConstructorDef(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "MyClass"), _publicMav, new FormalParameter[0], new ReferenceType[0], 
                                             new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      
      _cbfjv._enclosingData = new SymbolData("NotRightName");
      cd.visit(_cbfjv);
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "The constructor return type and class name must match", errors.getLast().getFirst());
      
      
      _cbfjv._enclosingData = new SymbolData("MyClass");
      
      MethodData constructor = new MethodData("MyClass", _publicMav, new TypeParameter[0], _cbfjv._enclosingData, 
                                              new VariableData[0], 
                                              new String[0], 
                                              _cbfjv._enclosingData,
                                              null);
      
      
      cd.visit(_cbfjv);
      
      
      assertEquals("Should still be 1 error", 1, errors.size());
      assertEquals("SymbolData should have 1 method", 1, _cbfjv._enclosingData.getMethods().size());
      assertTrue("SymbolData's constructor should be correct", _cbfjv._enclosingData.getMethods().contains(constructor));
      
      
      FormalParameter fp = new FormalParameter(SourceInfo.NO_INFO, new UninitializedVariableDeclarator(SourceInfo.NO_INFO, new PrimitiveType(SourceInfo.NO_INFO, "int"), new Word(SourceInfo.NO_INFO, "i")), false);
      ReferenceType rt = new TypeVariable(SourceInfo.NO_INFO, "MyMadeUpException");
      ConstructorDef cd2 = new ConstructorDef(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "MyClass"), _publicMav, new FormalParameter[] {fp}, new ReferenceType[] {rt}, 
                                             new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      VariableData vd = new VariableData("i", _finalMav, SymbolData.INT_TYPE, true, null);
      MethodData constructor2 = new MethodData("MyClass", _publicMav, new TypeParameter[0], _cbfjv._enclosingData, 
                                               new VariableData[] {vd}, 
                                               new String[] {"MyMadeUpException"}, 
                                              _cbfjv._enclosingData,
                                              null);
                                              

                                              
      constructor2.addVar(vd);
      cd2.visit(_cbfjv);
      vd.setEnclosingData(_cbfjv._enclosingData.getMethods().getLast());                                        
      assertEquals("Should still be 1 error", 1, errors.size());
      assertEquals("SymbolData should have 2 methods", 2, _cbfjv._enclosingData.getMethods().size());
      
      assertTrue("SymbolData should have new constructor", _cbfjv._enclosingData.getMethods().contains(constructor2));
      
                                              
      
      FormalParameter fp2 = new FormalParameter(SourceInfo.NO_INFO, new UninitializedVariableDeclarator(SourceInfo.NO_INFO, new PrimitiveType(SourceInfo.NO_INFO, "double"), new Word(SourceInfo.NO_INFO, "i")), false);
      
      ConstructorDef cd3 = new ConstructorDef(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "MyClass"), _publicMav, new FormalParameter[] {fp, fp2}, new ReferenceType[] {rt}, 
                                             new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      cd3.visit(_cbfjv);
      
      assertEquals("Should now be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct","You cannot have two method parameters with the same name" , errors.getLast().getFirst());
      
      
      _cbfjv._enclosingData.setName("package.MyClass2");
      ConstructorDef cd4 = new ConstructorDef(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "different.MyClass2"), _publicMav, new FormalParameter[0], new ReferenceType[0], 
                                             new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      cd4.visit(_cbfjv);

      assertEquals("There should now be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "The constructor return type and class name must match", errors.getLast().getFirst());
    }
    public void testDummy() { }
  }
}