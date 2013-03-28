

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.*;
import edu.rice.cs.javalanglevels.util.*;
import java.util.*;
import java.io.*;

import junit.framework.TestCase;


public class BodyBodyIntermediateVisitor extends IntermediateVisitor {

  
  private BodyData _bodyData;
  
  
  public BodyBodyIntermediateVisitor(BodyData bodyData,
                                     File file, 
                                     String packageName, 
                                     LinkedList<String> importedFiles, 
                                     LinkedList<String> importedPackages,
                                     LinkedList<String> classDefsInThisFile, 
                                     Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations,
                                     LinkedList<String> innerClassesToBeParsed) {
    super(file, 
          packageName, 
          importedFiles, 
          importedPackages, 
          classDefsInThisFile, 
          continuations);
    _bodyData = bodyData;
    _innerClassesToBeParsed = innerClassesToBeParsed;
  }
  
  
  public Void forMethodDefDoFirst(MethodDef that) {
    _addError("Methods definitions cannot appear within the body of another method or block.", that);
    return null;
  }
  
  
  public Void forInstanceInitializer(InstanceInitializer that) {
    return forBlock(that.getCode());
  }

 
  public Void forBlock(Block that) {
    forBlockDoFirst(that);
    if (prune(that)) return null;
    BlockData bd = new BlockData(_bodyData);
    _bodyData.addBlock(bd);
    that.getStatements().visit(new BodyBodyIntermediateVisitor(bd, _file, _package, _importedFiles, _importedPackages,
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
    
    b.getStatements().visit(new BodyBodyIntermediateVisitor(bd, _file, _package, _importedFiles, _importedPackages, 
                                                            _classNamesInThisFile, continuations, 
                                                            _innerClassesToBeParsed));
    forBlockOnly(b);
    return forCatchBlockOnly(that);
  }
  
  
  public Void forVariableDeclarationOnly(VariableDeclaration that) {
    if (! _bodyData.addFinalVars(_variableDeclaration2VariableData(that, _bodyData))) {

      _addAndIgnoreError("You cannot have two variables with the same name.", that);
    }
    return null;
  }
  
  
  public Void forTryCatchStatementDoFirst(TryCatchStatement that) { return null;  }
  
  
  public Void forInnerClassDef(InnerClassDef that) {

    handleInnerClassDef(that, _bodyData, getQualifiedClassName(_bodyData.getSymbolData().getName()) + "." + 
                        _bodyData.getSymbolData().preincrementLocalClassNum() + that.getName().getText());
    return null;
  }
  
   
  public Void forInnerInterfaceDef(InnerInterfaceDef that) {
    handleInnerInterfaceDef(that, _bodyData, getQualifiedClassName(_bodyData.getSymbolData().getName()) + "." + 
                        _bodyData.getSymbolData().preincrementLocalClassNum() + that.getName().getText());
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
  
  
  
  public Void forThisReferenceDoFirst(ThisReference that) {
    if (isConstructor(_bodyData)) {
      _addAndIgnoreError("You cannot reference the field 'this' inside a constructor at the Intermediate Level", that);
    }
    return null;
  }

  
  protected VariableData[] _variableDeclaration2VariableData(VariableDeclaration vd, Data enclosingData) {
    VariableData[] vds = llVariableDeclaration2VariableData(vd, enclosingData);
    for (int i = 0; i < vds.length; i++) {
      if (vds[i].getMav().getModifiers().length > 0) {
        StringBuilder s = new StringBuilder("the keyword(s) ");
        String[] modifiers = vds[i].getMav().getModifiers();
        for (String m: modifiers) { if (! m.equals("final")) s.append("\"" + m + "\" "); }
        _addAndIgnoreError("You cannot use " + s + "to declare a local variable", vd);
      }
      vds[i].setFinal();
      vds[i].setIsLocalVariable(true);
    }

    return vds;
  }
  






  
  
  public static class BodyBodyIntermediateVisitorTest extends TestCase {
    
    private BodyBodyIntermediateVisitor _bbv;
    
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
    
    
    public BodyBodyIntermediateVisitorTest() { this(""); }
    
    public BodyBodyIntermediateVisitorTest(String name) { super(name); }
    
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
      _bbv = 
        new BodyBodyIntermediateVisitor(_md1, new File(""), "", new LinkedList<String>(), new LinkedList<String>(), 
                                        new LinkedList<String>(), 
                                        new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>(),
                                        new LinkedList<String>());
      _bbv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();
      _bbv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      _bbv._resetNonStaticFields();
      _bbv._importedPackages.addFirst("java.lang");
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
      cmd.visit(_bbv);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct.", 
                   "Methods definitions cannot appear within the body of another method or block.",
                   errors.get(0).getFirst());
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
      VariableData vd1 = new VariableData("field1", _finalMav, SymbolData.DOUBLE_TYPE, false, _bbv._bodyData);
      VariableData vd2 = new VariableData("field2", _finalMav, SymbolData.BOOLEAN_TYPE, false, _bbv._bodyData);
      vdecl.visit(_bbv);
      assertEquals("There should not be any errors.", 0, errors.size());
      assertTrue("field1 was added.", _md1.getVars().contains(vd1));
      assertTrue("field2 was added.", _md1.getVars().contains(vd2));
      
      
      VariableDeclaration vdecl2 = new VariableDeclaration(SourceInfo.NO_INFO,
                                                        _packageMav,
                                                        new VariableDeclarator[] {
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                            new Word (SourceInfo.NO_INFO, "field3")),
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                            new Word (SourceInfo.NO_INFO, "field3"))});
      VariableData vd3 = new VariableData("field3", _finalMav, SymbolData.DOUBLE_TYPE, false, _bbv._bodyData);
      vdecl2.visit(_bbv);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct", "You cannot have two variables with the same name.", 
                   errors.get(0).getFirst());
      assertTrue("field3 was added.", _md1.getVars().contains(vd3));
    }
    
















    
    public void testForTryCatchStatement() {
      
      BracedBody emptyBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      Block b = new Block(SourceInfo.NO_INFO, emptyBody);

      NormalTryCatchStatement ntcs = new NormalTryCatchStatement(SourceInfo.NO_INFO, b, new CatchBlock[0]);
      TryCatchFinallyStatement tcfs = new TryCatchFinallyStatement(SourceInfo.NO_INFO, b, new CatchBlock[0], b);
      ntcs.visit(_bbv);
      tcfs.visit(_bbv);
      assertEquals("After visiting both NormalTryCatchStatement and TryCatchFinallyStatement, there should be no " 
                     + "errors", 0, errors.size());
      
      
      BracedBody errorBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {
        new ExpressionStatement(SourceInfo.NO_INFO, 
                                new BitwiseOrExpression(SourceInfo.NO_INFO, 
                                                        new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                                new Word(SourceInfo.NO_INFO, "i")), 
                                                        new IntegerLiteral(SourceInfo.NO_INFO, 10)))});
      Block errorBlock = new Block(SourceInfo.NO_INFO, errorBody);
      
      ntcs = new NormalTryCatchStatement(SourceInfo.NO_INFO, errorBlock, new CatchBlock[0]);
      ntcs.visit(_bbv);
      assertEquals("Should be one error", 1, errors.size());
      assertEquals("Error message should be correct", 
                   "Bitwise or expressions cannot be used at any language level.  " 
                     + "Perhaps you meant to compare two values using regular or (||)", 
                   errors.getLast().getFirst());
      
      
      UninitializedVariableDeclarator uvd = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, new PrimitiveType(SourceInfo.NO_INFO, "int"), new Word(SourceInfo.NO_INFO, "i"));
      FormalParameter fp = new FormalParameter(SourceInfo.NO_INFO, uvd, false);

      tcfs = new TryCatchFinallyStatement(SourceInfo.NO_INFO, b, new CatchBlock[] {
        new CatchBlock(SourceInfo.NO_INFO, fp, errorBlock)}, b);
        
     tcfs.visit(_bbv);
     assertEquals("Should be two errors", 2, errors.size());
     assertEquals("Error message should be correct", "Bitwise or expressions cannot be used at any language level.  Perhaps you meant to compare two values using regular or (||)", errors.getLast().getFirst());
    }
    
    public void testForThisReferenceDoFirst() {
      SimpleThisReference str = new SimpleThisReference(SourceInfo.NO_INFO);
      ComplexThisReference ctr = new ComplexThisReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "field")));

      
      _bbv._bodyData = _md1;
      str.visit(_bbv);
      ctr.visit(_bbv);
      assertEquals("Should be no errors", 0, errors.size());
           
      
      
      MethodData constr = new MethodData("monkey", _publicMav, new TypeParameter[0], _sd1, 
                                   new VariableData[0], 
                                   new String[0],
                                   _sd1,
                                   null);
      _bbv._bodyData = constr;
      str.visit(_bbv);
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "You cannot reference the field 'this' inside a constructor at the Intermediate Level", errors.getLast().getFirst());
      
      ctr.visit(_bbv);
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "You cannot reference the field 'this' inside a constructor at the Intermediate Level", errors.getLast().getFirst());
      
    }
    
  }
}