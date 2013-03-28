

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;
import java.io.*;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.*;

import junit.framework.TestCase;


public class TryCatchBodyTypeChecker extends BodyTypeChecker {


  
  public TryCatchBodyTypeChecker(BodyData bodyData, File file, String packageName, LinkedList<String> importedFiles, LinkedList<String> importedPackages, LinkedList<VariableData> vars, LinkedList<Pair<SymbolData, JExpression>> thrown) {
    super(bodyData, file, packageName, importedFiles, importedPackages, vars, thrown);
  }
 
  
  
  protected BodyTypeChecker createANewInstanceOfMe(BodyData bodyData, File file, String pakage, LinkedList<String> importedFiles, LinkedList<String> importedPackages, LinkedList<VariableData> vars, LinkedList<Pair<SymbolData, JExpression>> thrown) {
    return new TryCatchBodyTypeChecker(bodyData, file, pakage, importedFiles, importedPackages, vars, thrown);
  }
  
  
  public TypeData forBracedBody(BracedBody that) {
    final TypeData[] items_result = makeArrayOfRetType(that.getStatements().length);
    for (int i = 0; i < that.getStatements().length; i++) {
      items_result[i] = that.getStatements()[i].visit(this);
    }
    return forBracedBodyOnly(that, items_result);
  }
  
  
  protected void compareThrownAndCaught(TryCatchStatement that, SymbolData[] caught_array, 
                                        LinkedList<Pair<SymbolData, JExpression>> thrown) {
    LinkedList<Pair<SymbolData, JExpression>> copyOfThrown = new LinkedList<Pair<SymbolData, JExpression>>();
    for (Pair<SymbolData, JExpression> p : thrown) {
      copyOfThrown.addLast(p);
    }
    
    for (Pair<SymbolData, JExpression> p : copyOfThrown) {
      SymbolData sd = p.getFirst();
      
      for (SymbolData currCaughtSD : caught_array) {
        if (sd.isSubClassOf(currCaughtSD) || (!isUncaughtCheckedException(sd, new NullLiteral(SourceInfo.NO_INFO)))) {
          thrown.remove(p);
        }
      }
    }
    makeSureCaughtStuffWasThrown(that, caught_array, copyOfThrown);
  }
  
   
  public static class TryCatchBodyTypeCheckerTest extends TestCase {
    
    private TryCatchBodyTypeChecker _tcbtc;
    
    private BodyData _bd1;
    private BodyData _bd2;
    
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
    
    
    public TryCatchBodyTypeCheckerTest() {
      this("");
    }
    public TryCatchBodyTypeCheckerTest(String name) {
      super(name);
    }
    
    public void setUp() {
      _sd1 = new SymbolData("i.like.monkey");
      _sd2 = new SymbolData("i.like.giraffe");
      _sd3 = new SymbolData("zebra");
      _sd4 = new SymbolData("u.like.emu");
      _sd5 = new SymbolData("");
      _sd6 = new SymbolData("cebu");

      _bd1 = new MethodData("monkey", 
                            _packageMav, 
                            new TypeParameter[0], 
                            _sd1, 
                            new VariableData[] { new VariableData("i", _publicMav, SymbolData.INT_TYPE, true, null), new VariableData(SymbolData.BOOLEAN_TYPE) },
                            new String[0],
                            _sd1,
                            null); 
      ((MethodData) _bd1).getParams()[0].setEnclosingData(_bd1);
      ((MethodData) _bd1).getParams()[1].setEnclosingData(_bd1);
                            
      errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();
      _bd1.addEnclosingData(_sd1);
      _bd1.addFinalVars(((MethodData)_bd1).getParams());
      _tcbtc = new TryCatchBodyTypeChecker(_bd1, new File(""), "", new LinkedList<String>(), new LinkedList<String>(), new LinkedList<VariableData>(), new LinkedList<Pair<SymbolData, JExpression>>());
      LanguageLevelConverter.OPT = new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make());
      _tcbtc._importedPackages.addFirst("java.lang");
    }
    
    
    public void testCreateANewInstanceOfMe() {
      
      BodyTypeChecker btc = _tcbtc.createANewInstanceOfMe(_tcbtc._bodyData, _tcbtc._file, _tcbtc._package, _tcbtc._importedFiles, _tcbtc._importedPackages, _tcbtc._vars, _tcbtc._thrown);
      assertTrue("Should be an instance of ConstructorBodyTypeChecker", btc instanceof TryCatchBodyTypeChecker);
    }
    
    public void testForBracedBody() {
      
      BracedBody bb = new BracedBody(SourceInfo.NO_INFO, 
                                     new BodyItemI[] { 
        new ThrowStatement(SourceInfo.NO_INFO, 
                           new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, 
                                         new ClassOrInterfaceType(SourceInfo.NO_INFO, 
                                                                 "java.util.prefs.BackingStoreException", 
                                                                 new Type[0]), 
                                                             new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new StringLiteral(SourceInfo.NO_INFO, "arg")})))});
      
      LanguageLevelVisitor llv = 
        new LanguageLevelVisitor(new File(""), 
                                 "",
                                 new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      llv.errors = new LinkedList<Pair<String, JExpressionIF>>();
      llv._errorAdded=false;
      LanguageLevelConverter.symbolTable.clear();
      LanguageLevelConverter._newSDs.clear();
      llv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      llv.visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, edu.rice.cs.javalanglevels.tree.SourceFile>>();      
      llv._hierarchy = new Hashtable<String, TypeDefBase>();
      llv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();

      SymbolData e = llv.getSymbolData("java.util.prefs.BackingStoreException", SourceInfo.NO_INFO, true);
      
      bb.visit(_tcbtc);
      assertEquals("There should be no errors because it's ok to have uncaught exceptions in this visitor", 
                   0, 
                   errors.size());
    }
    
    public void testCompareThrownAndCaught() {
      BracedBody emptyBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      Block b = new Block(SourceInfo.NO_INFO, emptyBody);

      PrimitiveType intt = new PrimitiveType(SourceInfo.NO_INFO, "int");
      UninitializedVariableDeclarator uvd = 
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "i"));
      FormalParameter param = 
        new FormalParameter(SourceInfo.NO_INFO, 
                            new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "j")), 
                            false);

      NormalTryCatchStatement ntcs = new NormalTryCatchStatement(SourceInfo.NO_INFO, b, new CatchBlock[] {new CatchBlock(SourceInfo.NO_INFO,  param, b)});

      SymbolData javaLangThrowable = _tcbtc.getSymbolData("java.lang.Throwable", ntcs, false, true); 
      _tcbtc.symbolTable.put("java.lang.Throwable", javaLangThrowable);
      SymbolData exception = new SymbolData("my.crazy.exception");
      exception.setSuperClass(javaLangThrowable);
      SymbolData exception2 = new SymbolData("A&M.beat.Rice.in.BaseballException");
      exception2.setSuperClass(javaLangThrowable);
      SymbolData exception3 = new SymbolData("aegilha");
      exception3.setSuperClass(exception2);
      SymbolData[] caught_array = new SymbolData[] { exception, exception2 };
      LinkedList<Pair<SymbolData, JExpression>> thrown = new LinkedList<Pair<SymbolData, JExpression>>();
      thrown.addLast(new Pair<SymbolData, JExpression>(exception, ntcs));
      thrown.addLast(new Pair<SymbolData, JExpression>(exception2, ntcs));
      thrown.addLast(new Pair<SymbolData, JExpression>(exception3, ntcs));
      
      _tcbtc.compareThrownAndCaught(ntcs, caught_array, thrown);

      assertTrue("Thrown should have no elements", thrown.isEmpty());

      
      _tcbtc.compareThrownAndCaught(ntcs, new SymbolData[] {exception2}, thrown);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "The exception A&M.beat.Rice.in.BaseballException is never thrown in the body of the corresponding try block", errors.get(0).getFirst());

    }
  }
}