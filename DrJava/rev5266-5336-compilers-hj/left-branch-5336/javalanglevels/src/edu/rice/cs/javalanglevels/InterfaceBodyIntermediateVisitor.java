

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.*;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.IterUtil;
import java.util.*;
import java.io.*;

import junit.framework.TestCase;


public class InterfaceBodyIntermediateVisitor extends IntermediateVisitor {
  
  
  private SymbolData _symbolData;
  
  
  public InterfaceBodyIntermediateVisitor(SymbolData sd, 
                                          File file, 
                                          String packageName, 
                                          LinkedList<String> importedFiles, 
                                          LinkedList<String> importedPackages, 
                                          LinkedList<String> classDefsInThisFile, 
                                          Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations) {
    super(file, packageName, importedFiles, importedPackages, classDefsInThisFile, continuations);
    _symbolData = sd;
  }
  
  
  public Void forStatementDoFirst(Statement that) {
    _addError("Statements cannot appear outside of method bodies", that);
    return null;
  }
  
  
  public Void forConcreteMethodDefDoFirst(ConcreteMethodDef that) {
    _addError("You cannot have concrete methods definitions in interfaces", that);
    return null;
  }

  
  public Void forInstanceInitializerDoFirst(InstanceInitializer that) {
    _addError("This open brace must mark the beginning of an interface body", that);
    return null;
  }
  
  
  public Void forVariableDeclarationDoFirst(VariableDeclaration that) {
    _addError("You cannot have fields in interfaces at the Intermediate level", that);
    return null;
  }
  
  
  public Void forSuperReferenceDoFirst(SuperReference that) {
    _addAndIgnoreError("The field 'super' does not exist in interfaces.  Only classes have a 'super' field", that);
    return null;
  }
  
  
  public Void forThisReferenceDoFirst(ThisReference that) {
    _addAndIgnoreError("The field 'this' does not exist in interfaces.  Only classes have a 'this' field.", that);
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
  
  
  public Void forConstructorDefDoFirst(ConstructorDef that) {
    _addAndIgnoreError("Constructor definitions cannot appear in interfaces", that);
    return null;
  }
  
  
  protected VariableData[] _variableDeclaration2VariableData(VariableDeclaration vd, Data enclosingData) {
    VariableData[] vds = super._variableDeclaration2VariableData(vd, enclosingData);
    for (int i = 0; i < vds.length; i++) {
      vds[i].setFinalAndStatic();
    }
    return vds;
  }
  
  
  public Void forComplexAnonymousClassInstantiation(ComplexAnonymousClassInstantiation that) {
    complexAnonymousClassInstantiationHelper(that, _symbolData);
    return null;
  }

  
  public Void forSimpleAnonymousClassInstantiation(SimpleAnonymousClassInstantiation that) {
    simpleAnonymousClassInstantiationHelper(that, _symbolData);
    return null;
  }
  
  
  public static class InterfaceBodyIntermediateVisitorTest extends TestCase {
    
    private InterfaceBodyIntermediateVisitor _ibiv;
    
    private SymbolData _sd1;
    private ModifiersAndVisibility _publicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _protectedMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});
    private ModifiersAndVisibility _privateMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"private"});
    private ModifiersAndVisibility _packageMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    private ModifiersAndVisibility _abstractMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract"});
    private ModifiersAndVisibility _finalMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final"});
    
    
    public InterfaceBodyIntermediateVisitorTest() {
      this("");
    }
    public InterfaceBodyIntermediateVisitorTest(String name) {
      super(name);
    }
    
    public void setUp() {
      _sd1 = new SymbolData("i.like.monkey");

      errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();
      LanguageLevelConverter._newSDs.clear();
      LanguageLevelConverter.OPT = new Options(JavaVersion.JAVA_5, IterUtil.make(new File("lib/buildlib/junit.jar")));
      visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, edu.rice.cs.javalanglevels.tree.SourceFile>>();      
      _hierarchy = new Hashtable<String, TypeDefBase>();
      _ibiv = 
        new InterfaceBodyIntermediateVisitor(_sd1, 
                                             new File(""), 
                                             "", 
                                             new LinkedList<String>(), 
                                             new LinkedList<String>(), new LinkedList<String>(), 
                                             new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      _ibiv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();
      _ibiv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      _ibiv._resetNonStaticFields();
      _ibiv._importedPackages.addFirst("java.lang");
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
      cmd.visit(_ibiv);
      assertEquals("There should not be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "You cannot have concrete methods definitions in interfaces", errors.getLast().getFirst());
      
    }
    
    public void testForAbstractMethodDefDoFirst() {
      
      _ibiv._symbolData.setMav(_abstractMav);
      AbstractMethodDef amd2 = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                                     _abstractMav, 
                                                     new TypeParameter[0], 
                                                     new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                     new Word(SourceInfo.NO_INFO, "methodName"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0]);
      amd2.visit(_ibiv);
      assertEquals("There should be no errors", 0, errors.size());
      assertTrue("The method def should be public", _ibiv._symbolData.getMethods().get(0).hasModifier("public"));

    }

    public void testForInstanceInitializerDoFirst() {
      InstanceInitializer ii = new InstanceInitializer(SourceInfo.NO_INFO, 
                                                       new Block(SourceInfo.NO_INFO, 
                                                                 new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0])));
      ii.visit(_ibiv);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct.", "This open brace must mark the beginning of an interface body", errors.get(0).getFirst());    
    }

    public void testForSimpleThisReferenceDoFirst() {
     SimpleThisReference tl = new SimpleThisReference(SourceInfo.NO_INFO);
     tl.visit(_ibiv);
     assertEquals("There should be one error", 1, errors.size());
     assertEquals("The error message should be correct", "The field 'this' does not exist in interfaces.  Only classes have a 'this' field.", errors.get(0).getFirst());
    }
    
    
    public void testForComplexThisReferenceDoFirst() {
     ComplexThisReference tl = new ComplexThisReference(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO));
     tl.visit(_ibiv);
     assertEquals("There should be one error", 1, errors.size());
     assertEquals("The error message should be correct", "The field 'this' does not exist in interfaces.  Only classes have a 'this' field.", errors.get(0).getFirst());

    }
    
    public void testForSimpleSuperReferenceDoFirst() {
      SimpleSuperReference sr = new SimpleSuperReference(SourceInfo.NO_INFO);
      sr.visit(_ibiv);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "The field 'super' does not exist in interfaces.  Only classes have a 'super' field", errors.get(0).getFirst());
    }

    public void testForComplexSuperReferenceDoFirst() {
      ComplexSuperReference cr = new ComplexSuperReference(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO));
      cr.visit(_ibiv);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "The field 'super' does not exist in interfaces.  Only classes have a 'super' field", errors.get(0).getFirst());
    }

    
    public void testForVariableDeclarationDoFirst() {
      
      VariableDeclaration vdecl = new VariableDeclaration(SourceInfo.NO_INFO,
                                                       _packageMav,
                                                       new VariableDeclarator[] {
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                               new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                               new Word (SourceInfo.NO_INFO, "field1")),
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                               new PrimitiveType(SourceInfo.NO_INFO, "boolean"), 
                               new Word (SourceInfo.NO_INFO, "field2"))});
      vdecl.visit(_ibiv);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "You cannot have fields in interfaces at the Intermediate level", errors.getLast().getFirst());
    }
    
    public void testForAbstractMethodDef() {
      
      MethodDef mdef = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                             _abstractMav, 
                                             new TypeParameter[0], 
                                             new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                             new Word(SourceInfo.NO_INFO, "methodName"),
                                             new FormalParameter[0],
                                             new ReferenceType[0]);
      _ibiv._symbolData.setMav(_abstractMav);
      mdef.visit(_ibiv);
      assertEquals("There should not be any errors.", 0, errors.size());
      
      
      mdef = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                             _abstractMav, 
                                             new TypeParameter[0], 
                                             new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                             new Word(SourceInfo.NO_INFO, "monkey"),
                                             new FormalParameter[0],
                                             new ReferenceType[0]);
      mdef.visit(_ibiv);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct.", 
                   "Only constructors can have the same name as the class they appear in, and constructors cannot appear in interfaces.",
                   errors.get(0).getFirst());
      
      
      
      AbstractMethodDef amd3 = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                                     _publicMav, 
                                                     new TypeParameter[0], 
                                                     new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                     new Word(SourceInfo.NO_INFO, "methodName2"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0]);
      amd3.visit(_ibiv);
      assertEquals("There should still be one error", 1, errors.size());
      assertTrue("The method def should be public", _ibiv._symbolData.getMethods().get(1).hasModifier("public"));

      
      AbstractMethodDef amd4 = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                                     _privateMav, 
                                                     new TypeParameter[0], 
                                                     new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                     new Word(SourceInfo.NO_INFO, "methodName3"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0]);
      amd4.visit(_ibiv);
      assertEquals("There should be two errors", 2, errors.size());
      assertEquals("The error message should be correct","Interface methods cannot be made private.  They must be public." , errors.get(1).getFirst());
    
      
      AbstractMethodDef amd5 = new AbstractMethodDef(SourceInfo.NO_INFO, 
                                                     _protectedMav, 
                                                     new TypeParameter[0], 
                                                     new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                                     new Word(SourceInfo.NO_INFO, "methodName4"),
                                                     new FormalParameter[0],
                                                     new ReferenceType[0]);
      amd5.visit(_ibiv);
      assertEquals("There should be three errors", 3, errors.size());
      assertEquals("The error message should be correct","Interface methods cannot be made protected.  They must be public." , errors.get(2).getFirst());
    }
    
    
    public void testForConstructorDef() {
     
      ConstructorDef cd = new ConstructorDef(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "MyClass"), _publicMav, new FormalParameter[0], new ReferenceType[0], 
                                             new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      
      cd.visit(_ibiv);
      assertEquals("There should now be one error", 1, errors.size());
      assertEquals("The error message should be correct", "Constructor definitions cannot appear in interfaces", errors.get(0).getFirst());
      
    }
  }
}
