

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.*;
import java.util.*;
import java.io.*;

import junit.framework.TestCase;




public class BodyBodyFullJavaVisitor extends FullJavaVisitor {

  
  private BodyData _bodyData;
  
  
  public BodyBodyFullJavaVisitor(BodyData bodyData,
                                 File file, 
                                 String packageName,
                                 LinkedList<String> importedFiles, 
                                 LinkedList<String> importedPackages, 
                                 LinkedList<String> classDefsInThisFile, 
                                 Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations,
                                 LinkedList<String> innerClassesToBeParsed) {
    super(file, packageName, importedFiles, importedPackages, classDefsInThisFile, continuations);
    _bodyData = bodyData;
    _innerClassesToBeParsed = innerClassesToBeParsed;
  }
  
  
  public Void forMethodDefDoFirst(MethodDef that) { return null; }
  
  
  public Void forInstanceInitializer(InstanceInitializer that) { return forBlock(that.getCode());  }

  
  public Void forBlock(Block that) {
    forBlockDoFirst(that);
    if (prune(that)) return null;
    BlockData bd = new BlockData(_bodyData);
    _bodyData.addBlock(bd);
    that.getStatements().visit(new BodyBodyFullJavaVisitor(bd, _file, _package, _importedFiles, _importedPackages, 
                                                           _classNamesInThisFile, continuations, 
                                                           _innerClassesToBeParsed));
    return forBlockOnly(that);
  }
  
  
  public Void forCatchBlock(CatchBlock that) {
    forCatchBlockDoFirst(that);
    if (prune(that)) return null;
    
    Block b = that.getBlock();
    forBlockDoFirst(b);
    if (prune(b)) return null;
    BlockData bd = new BlockData(_bodyData);
    _bodyData.addBlock(bd);
    
    VariableData exceptionVar = formalParameters2VariableData(new FormalParameter[]{ that.getException() }, bd)[0];
    if (prune(that.getException())) return null;
    bd.addVar(exceptionVar);
    
    b.getStatements().visit(new BodyBodyFullJavaVisitor(bd, _file, _package, _importedFiles, _importedPackages, 
                                                        _classNamesInThisFile, continuations, 
                                                        _innerClassesToBeParsed));
    forBlockOnly(b);
    return forCatchBlockOnly(that);
  }
  
  
  public Void forVariableDeclarationOnly(VariableDeclaration that) {
    if (! _bodyData.addVars(_variableDeclaration2VariableData(that, _bodyData))) {


    }
    return null;
  }
  
  
  public Void forTryCatchStatementDoFirst(TryCatchStatement that) { return null; }

  
  public Void forInnerClassDef(InnerClassDef that) {
    
    handleInnerClassDef(that, _bodyData, getQualifiedClassName(_bodyData.getSymbolData().getName()) + "."
                          + _bodyData.getSymbolData().preincrementLocalClassNum() + that.getName().getText());
    return null;
  }
  
  
  public Void forInnerInterfaceDef(InnerInterfaceDef that) {
    
    handleInnerInterfaceDef(that, _bodyData, getQualifiedClassName(_bodyData.getSymbolData().getName()) + "."
                              + _bodyData.getSymbolData().preincrementLocalClassNum() + that.getName().getText());
    return null;
  }
  
  
  public Void forComplexAnonymousClassInstantiation(ComplexAnonymousClassInstantiation that) {
    complexAnonymousClassInstantiationHelper(that, _bodyData);
    return null;
  }

  
  public Void forSimpleAnonymousClassInstantiation(SimpleAnonymousClassInstantiation that) {
    simpleAnonymousClassInstantiationHelper(that, _bodyData);
    return null;
  }

  
  public static class BodyBodyFullJavaVisitorTest extends TestCase {
    
    private BodyBodyFullJavaVisitor _bfv;
    
    private SymbolData _sd1;
    private MethodData _md1;
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
    
    
    public BodyBodyFullJavaVisitorTest() { this(""); }
    
    public BodyBodyFullJavaVisitorTest(String name) { super(name); }
    
    public void setUp() {
      _sd1 = new SymbolData("i.like.monkey");
      _md1 = new MethodData("methodName", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE, 
                                   new VariableData[0], 
                                   new String[0],
                                   _sd1,
                                   null);

      errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();
      LanguageLevelConverter._newSDs.clear();
      visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, edu.rice.cs.javalanglevels.tree.SourceFile>>();      
      _hierarchy = new Hashtable<String, TypeDefBase>();
      _bfv = new BodyBodyFullJavaVisitor(_md1, 
                                         new File(""), 
                                         "", 
                                         new LinkedList<String>(), 
                                         new LinkedList<String>(), 
                                         new LinkedList<String>(), 
                                         new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>(),
                                         new LinkedList<String>());
      _bfv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();
      _bfv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      _bfv._resetNonStaticFields();
      _bfv._importedPackages.addFirst("java.lang");
      _errorAdded = false;
    }
    
    public void testForMethodDefDoFirst() {
      ConcreteMethodDef cmd = new ConcreteMethodDef(SourceInfo.NO_INFO, 
                                                    _packageMav, 
                                                    new TypeParameter[0], 
                                                    new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                    new Word(SourceInfo.NO_INFO, "methodName"),
                                                    new FormalParameter[0],
                                                    new ReferenceType[0], 
                                                    new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      cmd.visit(_bfv);
      assertEquals("There should be no errors", 0, errors.size());  



    }
    
    
    
    public void testForVariableDeclarationOnly() {
      
      VariableDeclarator[] vdecs = new VariableDeclarator[] {
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                            new Word (SourceInfo.NO_INFO, "field1")),
          new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                              new PrimitiveType(SourceInfo.NO_INFO, "boolean"), 
                                              new Word (SourceInfo.NO_INFO, "field2"))};
      VariableDeclaration vdecl = new VariableDeclaration(SourceInfo.NO_INFO, _packageMav, vdecs);
      
      VariableData vd1 = new VariableData("field1", _packageMav, SymbolData.DOUBLE_TYPE, false, _bfv._bodyData);
      VariableData vd2 = new VariableData("field2", _packageMav, SymbolData.BOOLEAN_TYPE, false, _bfv._bodyData);
      vdecl.visit(_bfv);
      assertEquals("There should not be any errors.", 0, errors.size());
      LinkedList<VariableData> vars = _md1.getVars();






      assertTrue("field1 was added.", vars.contains(vd1));
      assertTrue("field2 was added.", vars.contains(vd2));
      
      
      VariableDeclaration vdecl2 = new VariableDeclaration(SourceInfo.NO_INFO,
                                                        _packageMav,
                                                        new VariableDeclarator[] {
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                            new Word (SourceInfo.NO_INFO, "field3")),
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                            new Word (SourceInfo.NO_INFO, "field3"))});
      VariableData vd3 = new VariableData("field3", _packageMav, SymbolData.DOUBLE_TYPE, false, _bfv._bodyData);
      vdecl2.visit(_bfv);
      assertEquals("There should still be no errors.", 0, errors.size());
      
      



      assertTrue("field3 was added.", _md1.getVars().contains(vd3));
    }
    
    public void testForTryCatchStatement() {
      
      BracedBody emptyBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      Block b = new Block(SourceInfo.NO_INFO, emptyBody);

      NormalTryCatchStatement ntcs = new NormalTryCatchStatement(SourceInfo.NO_INFO, b, new CatchBlock[0]);
      TryCatchFinallyStatement tcfs = new TryCatchFinallyStatement(SourceInfo.NO_INFO, b, new CatchBlock[0], b);
      ntcs.visit(_bfv);
      tcfs.visit(_bfv);
      assertEquals("After visiting NormalTryCatchStatement and TryCatchFinallyStatement, there should be no errors", 
                   0, errors.size());
      
      
      BracedBody errorBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {
        new ExpressionStatement(SourceInfo.NO_INFO, 
                                new BitwiseOrExpression(SourceInfo.NO_INFO, 
                                                        new IntegerLiteral(SourceInfo.NO_INFO, 1), 
                                                        new IntegerLiteral(SourceInfo.NO_INFO, 2)))});
      Block errorBlock = new Block(SourceInfo.NO_INFO, errorBody);
      
      ntcs = new NormalTryCatchStatement(SourceInfo.NO_INFO, errorBlock, new CatchBlock[0]);
      ntcs.visit(_bfv);

      assertEquals("Should be no errors", 0, errors.size());  
      
      
      UninitializedVariableDeclarator uvd = 
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                            new Word(SourceInfo.NO_INFO, "i"));
      FormalParameter fp = new FormalParameter(SourceInfo.NO_INFO, uvd, false);

      tcfs = new TryCatchFinallyStatement(SourceInfo.NO_INFO, b, new CatchBlock[] {
        new CatchBlock(SourceInfo.NO_INFO, fp, errorBlock)}, b);
        
     tcfs.visit(_bfv);

     assertEquals("Should be no errors", 0, errors.size());  
    }
    
     public void testForInnerClassDef() {
     
      
      SymbolData obj = new SymbolData("java.lang.Object");
      LanguageLevelConverter.symbolTable.put("java.lang.Object", obj);
      InnerClassDef cd0 = 
        new InnerClassDef(SourceInfo.NO_INFO, 
                          _packageMav, 
                          new Word(SourceInfo.NO_INFO, "Rod"),
                          new TypeParameter[0], 
                          new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), 
                          new ReferenceType[0], 
                          new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      cd0.visit(_bfv);
      assertEquals("There should be no errors", 0, errors.size());
      SymbolData innerClass = _bfv._bodyData.getInnerClassOrInterface("Rod");
      assertNotNull("Should have a inner class named Rod", innerClass);
           
      
      InnerClassDef cd1 = 
        new InnerClassDef(SourceInfo.NO_INFO, 
                          _publicMav, 
                          new Word(SourceInfo.NO_INFO, "Todd"),
                          new TypeParameter[0], 
                          new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), 
                          new ReferenceType[0], 
                          new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      cd1.visit(_bfv);
      assertEquals("There should be no errors", 0, errors.size());  
    }
    
     public void testForInnerInterfaceDef() {       
       
       InnerInterfaceDef iid = 
         new InnerInterfaceDef(SourceInfo.NO_INFO, 
                               _packageMav, 
                               new Word(SourceInfo.NO_INFO, "Broken"),
                               new TypeParameter[0], 
                               new ReferenceType[0], 
                               new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
       iid.visit(_bfv);
       assertEquals("There should be no errors", 0, errors.size());
       
       
       SymbolData obj = new SymbolData("java.lang.Object");
       LanguageLevelConverter.symbolTable.put("java.lang.Object", obj);
       InnerInterfaceDef id0 = 
         new InnerInterfaceDef(SourceInfo.NO_INFO, 
                               _packageMav, 
                               new Word(SourceInfo.NO_INFO, "RodInterface"),
                               new TypeParameter[0], 
                               new ReferenceType[0], 
                               new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
       id0.visit(_bfv);
       assertEquals("There should be no errors", 0, errors.size());
       SymbolData innerInterface = _bfv._bodyData.getInnerClassOrInterface("RodInterface");
       assertNotNull("Should have a inner interface named RodInterface", innerInterface);
       
       
      InnerInterfaceDef id1 = 
        new InnerInterfaceDef(SourceInfo.NO_INFO, 
                          _publicMav, 
                          new Word(SourceInfo.NO_INFO, "Todd"),
                          new TypeParameter[0], 
                          new ReferenceType[0], 
                          new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      id1.visit(_bfv);
      assertEquals("There should be no errors", 0, errors.size());  
     }
     public void testDummy() { }
  }
}