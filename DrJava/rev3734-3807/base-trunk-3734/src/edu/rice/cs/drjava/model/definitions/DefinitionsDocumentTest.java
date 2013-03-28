

package edu.rice.cs.drjava.model.definitions;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.GlobalEventNotifier;
import edu.rice.cs.drjava.model.definitions.reducedmodel.BraceReduction;
import edu.rice.cs.drjava.model.definitions.reducedmodel.HighlightStatus;
import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelStates;
import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedToken;
import junit.framework.Test;
import junit.framework.TestSuite;

import javax.swing.text.BadLocationException;
import java.util.Vector;


public final class DefinitionsDocumentTest extends DrJavaTestCase implements ReducedModelStates {
  private DefinitionsDocument _defModel;
  private GlobalEventNotifier _notifier;

  
  public DefinitionsDocumentTest(String name) {
    super(name);
  }

  
  protected void setUp() throws Exception {
    super.setUp();
    _notifier = new GlobalEventNotifier();
    _defModel = new DefinitionsDocument(_notifier);
    DrJava.getConfig().resetToDefaults();
  }

  
  public static Test suite() {
    return  new TestSuite(DefinitionsDocumentTest.class);
  }

  
  public void testInsertToDoc() throws BadLocationException {
    _defModel.insertString(0, "a/*bc */\"\\{}()", null);
    assertEquals("#0.0", _defModel.getText(0, 8), "a/*bc */");
    assertEquals("#0.1", 14, _defModel.getCurrentLocation());
    _defModel.insertString(0, "Start:", null);
    assertEquals("#1.0", _defModel.getText(0, 14), "Start:a/*bc */");
    assertEquals("#1.1", 6, _defModel.getCurrentLocation());
    
    
    BraceReduction rm = _defModel.getReduced();
    assertEquals("2.1", FREE, rm.getStateAtCurrent());
    rm.move(2);
    
    
    assertEquals("2.3", "/*", rm.currentToken().getType());
    rm.move(2);
    
    
    assertEquals("2.4", true, rm.currentToken().isGap());
    assertEquals("2.5", ReducedToken.INSIDE_BLOCK_COMMENT, rm.currentToken().getState());
    rm.move(2);
    
    
    assertEquals("2.6", "*/", rm.currentToken().getType());
    rm.move(2);
    
    
    assertEquals("2.7", "\"", rm.currentToken().getType());
    rm.move(1);
    
    
    assertEquals("2.8", "\\", rm.currentToken().getType());
    rm.move(1);
    
    
    assertEquals("2.9", "{", rm.currentToken().getType());
    rm.move(1);
    
    
    assertEquals("2.91", "}", rm.currentToken().getType());
    rm.move(1);
    
    
    assertEquals("2.92", "(", rm.currentToken().getType());
    rm.move(1);
    
    
    assertEquals("2.93", ")", rm.currentToken().getType());
  }

  
  public void testInsertStarIntoStarSlash() throws BadLocationException {
    BraceReduction rm = _defModel.getReduced();
    _defModel.insertString(0, "/**/", null);
    
    _defModel.insertString(3, "*", null);
    _defModel.move(-4);
    assertEquals("1", "/*", rm.currentToken().getType());
    assertEquals("2", ReducedToken.FREE, rm.currentToken().getState());
    rm.move(2);
    assertEquals("3", "*", rm.currentToken().getType());
    assertEquals("4", ReducedToken.INSIDE_BLOCK_COMMENT, rm.currentToken().getState());
    rm.move(1);
    assertEquals("5", "*/", rm.currentToken().getType());
    assertEquals("6", ReducedToken.FREE, rm.currentToken().getState());
  }

  
  public void testInsertSlashIntoStarSlash() throws BadLocationException {
    BraceReduction rm = _defModel.getReduced();
    _defModel.insertString(0, "/**/", null);
    
    _defModel.insertString(3, "/", null);
    _defModel.move(-4);
    assertEquals("1", "/*", rm.currentToken().getType());
    assertEquals("2", ReducedToken.FREE, rm.currentToken().getState());
    rm.move(2);
    assertEquals("3", "*/", rm.currentToken().getType());
    assertEquals("4", ReducedToken.FREE, rm.currentToken().getState());
    rm.move(2);
    assertEquals("5", "/", rm.currentToken().getType());
    assertEquals("6", ReducedToken.FREE, rm.currentToken().getState());
  }

  
  public void testInsertStarIntoSlashStar() throws BadLocationException {
    BraceReduction rm = _defModel.getReduced();
    _defModel.insertString(0, "/**/", null);
    
    _defModel.insertString(1, "*", null);
    _defModel.move(-2);
    assertEquals("1", "/*", rm.currentToken().getType());
    assertEquals("2", ReducedToken.FREE, rm.currentToken().getState());
    rm.move(2);
    assertEquals("3", "*", rm.currentToken().getType());
    assertEquals("4", ReducedToken.INSIDE_BLOCK_COMMENT, rm.currentToken().getState());
    rm.move(1);
    assertEquals("5", "*/", rm.currentToken().getType());
    assertEquals("6", ReducedToken.FREE, rm.currentToken().getState());
  }

  
  public void testDeleteDoc() throws BadLocationException {
    _defModel.insertString(0, "a/*bc */", null);
    _defModel.remove(3, 3);
    assertEquals("#0.0", "a/**/", _defModel.getText(0, 5));
    assertEquals("#0.1", 3, _defModel.getCurrentLocation());
    BraceReduction rm = _defModel.getReduced();
    assertEquals("1.0", "*/", rm.currentToken().getType());
    
    
    rm.move(-2);
    assertEquals("1.2", "/*", rm.currentToken().getType());
    rm.move(2);
    assertEquals("1.3", ReducedToken.INSIDE_BLOCK_COMMENT, rm.getStateAtCurrent());
  }

  
  private void _checkHighlightStatusConsistent(Vector<HighlightStatus> v,
                                               int start,
                                               int end)
  {
    
    int walk = start;
    for (int i = 0; i < v.size(); i++) {
      assertEquals("Item #" + i + "in highlight vector starts at right place",
                   walk,
                   v.get(i).getLocation());
      
      assertTrue("Item #" + i + " in highlight vector has positive length",
                 v.get(i).getLength() > 0);

      walk += v.get(i).getLength();
    }
    assertEquals("Location after walking highlight vector",
                 end,
                 walk);
  }

  
  public void testHighlightKeywords1() throws BadLocationException {
    Vector<HighlightStatus> v;
    final String s = "public class Foo {\n" +
      "  private int _x = 0;\n" +
        "}";
    _defModel.insertString(_defModel.getLength(), s, null);
    v = _defModel.getHighlightStatus(0, _defModel.getLength());
    _checkHighlightStatusConsistent(v, 0, _defModel.getLength());
    
    assertEquals("vector length", 12, v.size());
    assertEquals(HighlightStatus.KEYWORD, v.get(0).getState());
    assertEquals(HighlightStatus.NORMAL, v.get(1).getState());
    assertEquals(HighlightStatus.KEYWORD, v.get(2).getState());
    assertEquals(HighlightStatus.NORMAL, v.get(3).getState());
    assertEquals(HighlightStatus.TYPE, v.get(4).getState());
    assertEquals(HighlightStatus.NORMAL, v.get(5).getState());

    assertEquals(HighlightStatus.KEYWORD, v.get(6).getState());
    assertEquals(HighlightStatus.NORMAL, v.get(7).getState());
    assertEquals(HighlightStatus.TYPE, v.get(8).getState());
    assertEquals(HighlightStatus.NORMAL, v.get(9).getState());
    assertEquals(HighlightStatus.NUMBER, v.get(10).getState());
    assertEquals(HighlightStatus.NORMAL, v.get(11).getState());
  }

  
  public void testHighlightKeywords2() throws BadLocationException {
    Vector<HighlightStatus> v;
    final String s = "int y";
    _defModel.insertString(_defModel.getLength(), s, null);
    
    v = _defModel.getHighlightStatus(0, _defModel.getLength());
    _checkHighlightStatusConsistent(v, 0, _defModel.getLength());
    

    assertEquals("vector length", 2, v.size());
    assertEquals(HighlightStatus.TYPE, v.get(0).getState());
    assertEquals(HighlightStatus.NORMAL, v.get(1).getState());
    
    v = _defModel.getHighlightStatus(0, 2);
    _checkHighlightStatusConsistent(v, 0, 2);
    assertEquals("vector length", 1, v.size());
    assertEquals(0, v.get(0).getLocation());
    assertEquals(2, v.get(0).getLength());
  }

  
  public void testGotoLine1() throws BadLocationException {
    final String s = "a\n";
    _defModel.insertString(0, s, null);
    _defModel.gotoLine(2);
    assertEquals("#0.0", 2, _defModel.getCurrentLocation());
  }

  
  public void testGotoLine2() throws BadLocationException {
    final String s = "abcd\n";
    _defModel.insertString(0, s, null);
    _defModel.gotoLine(2);
    assertEquals("#0.0", 5, _defModel.getCurrentLocation());
  }

  
  public void testGotoLine3() throws BadLocationException {
    final String s = "a\nb\nc\n";
    _defModel.insertString(0, s, null);
    _defModel.gotoLine(4);
    assertEquals("#0.0", 6, _defModel.getCurrentLocation());
  }

  
  public void testGotoLine4() throws BadLocationException {
    final String s = "a\nb\nc\n";
    _defModel.insertString(0, s, null);
    _defModel.gotoLine(8);
    assertEquals("#0.0", 6, _defModel.getCurrentLocation());
  }

  
  public void testGotoLine5() {
    _defModel.gotoLine(1);
    assertEquals("#0.0", 0, _defModel.getCurrentLocation());
  }

  
  public void testGotoLine6() {
    _defModel.gotoLine(4);
    assertEquals("#0.0", 0, _defModel.getCurrentLocation());
  }

  
  public void testGotoLine7() throws BadLocationException {
    final String s = "11111\n2222\n33333\n44444";
    _defModel.insertString(0, s, null);
    _defModel.gotoLine(3);
    assertEquals("#0.0", 11, _defModel.getCurrentLocation());
  }

  
  public void testGetColumn1() throws BadLocationException {
    final String s = "1234567890";
    assertEquals("#0.0", 0, _defModel.getCurrentCol());
    _defModel.insertString(0, s, null);
    assertEquals("#0.1", 10, _defModel.getCurrentCol());
    _defModel.gotoLine(0);
    assertEquals("#0.2", 0, _defModel.getCurrentCol());
  }


  
  public void testGetColumn2() throws BadLocationException {
    final String s = "1234567890\n1234\n12345";
    _defModel.insertString(0, s, null);
    assertEquals("#0.0", 5, _defModel.getCurrentCol() );
  }

  
  public void testGetLine1() throws BadLocationException {
    final String s = "a\n";
    _defModel.insertString(0, s, null);
    _defModel.setCurrentLocation(2);
    assertEquals("#0.0", 2, _defModel.getCurrentLine());
  }

  
  public void testGetLine2() throws BadLocationException {
    final String s = "abcd\n";
    _defModel.insertString(0, s, null);
    _defModel.setCurrentLocation(2);
    assertEquals("#0.0", 1, _defModel.getCurrentLine());
    _defModel.gotoLine(2);
    assertEquals("#0.1", 2, _defModel.getCurrentLine());
  }

  
  public void testGetLine3() throws BadLocationException {
    final String s = "a\nb\nc\n";
    _defModel.insertString(0, s, null);
    _defModel.setCurrentLocation(6);
    assertEquals("#0.0", 4, _defModel.getCurrentLine());
  }

  
  public void testGetLine4() throws BadLocationException {
    final String s = "a\nb\nc\n";
    _defModel.insertString(0, s, null);
    _defModel.gotoLine(8);
    assertEquals("#0.0", 4, _defModel.getCurrentLine());
  }

  
  public void testGetLine5() {
    _defModel.setCurrentLocation(0);
    assertEquals("#0.0", 1, _defModel.getCurrentLine());
  }

  
  public void testGetLine6() {
    _defModel.gotoLine(4);
    assertEquals("#0.0", 1, _defModel.getCurrentLine());
  }

  
  public void testGetLine7() throws BadLocationException {
    final String s = "12345\n7890\n2345\n789";
    _defModel.insertString(0, s, null);
    _defModel.setCurrentLocation(12);
    assertEquals("#0.0", 3, _defModel.getCurrentLine());
    _defModel.move(-5);
    assertEquals("#0.1", 2, _defModel.getCurrentLine());
    _defModel.setCurrentLocation(19);
    assertEquals("#0.2", 4, _defModel.getCurrentLine());
  }

  
  public void testGetLineDeleteText() throws BadLocationException{
    final String s = "123456789\n123456789\n123456789\n123456789\n";
    _defModel.insertString(0,s,null);
    _defModel.setCurrentLocation(35);
    assertEquals("Before delete", 4, _defModel.getCurrentLine() );
    _defModel.remove(0,30);
    _defModel.setCurrentLocation(5);
    assertEquals("After delete", 1, _defModel.getCurrentLine() );
  }

  
  public void testGetLineDeleteText2() throws BadLocationException {
    final String s = "123456789\n123456789\n123456789\n123456789\n";
    _defModel.insertString(0,s,null);
    _defModel.setCurrentLocation(35);
    assertEquals("Before delete", 4, _defModel.getCurrentLine());
    _defModel.remove(18,7);
    assertEquals("After delete", 2, _defModel.getCurrentLine());
  }

  
  public void testRemoveTabs1() {
    _defModel.setIndent(1);
    String test = "\t this \t\tis a \t\t\t\t\ttest\t\t";
    String result = _defModel._removeTabs(test);
    assertEquals( "  this   is a      test  ", result);
  }

  
  public void testRemoveTabs2() {
   String input =
    "\ttoken = nextToken(); // read trailing parenthesis\n" +
    "\tif (token != ')')\n" +
    "\t  throw new ParseException(\"wrong number of arguments to |\");\n";

   String expected =
    " token = nextToken(); // read trailing parenthesis\n" +
    " if (token != ')')\n" +
    "   throw new ParseException(\"wrong number of arguments to |\");\n";

    int count = 5000;
    StringBuffer bigIn = new StringBuffer(input.length() * count);
    StringBuffer bigExp = new StringBuffer(expected.length() * count);
    for (int i = 0; i < count; i++) {
      bigIn.append(input);
      bigExp.append(expected);
    }

    String result = _defModel._removeTabs(bigIn.toString());
    assertEquals(bigExp.toString(), result);
  }

  
  public void testTabRemovalOnInsertString2() throws BadLocationException {
   String[] inputs = {
      "\ttoken = nextToken(); // read trailing parenthesis\n",
      "\tif (token != ')')\n",
      "\t  throw new ParseException(\"wrong number of arguments to |\");\n",
    };

   String expected =
    " token = nextToken(); // read trailing parenthesis\n" +
    " if (token != ')')\n" +
    "   throw new ParseException(\"wrong number of arguments to |\");\n";

    for (int i = 0; i < inputs.length; i++) {
      _defModel.insertString(_defModel.getLength(), inputs[i], null);
    }

    assertEquals(expected, _getAllText());
  }

  
  public void testTabRemovalOnInsertString() throws BadLocationException {
    _defModel.setIndent(1);
    _defModel.insertString(0, " \t yet \t\tanother\ttest\t", null);
    String result = _defModel.getText();

    if (_defModel.tabsRemoved()) {
      assertEquals("   yet   another test ", result);
    }
    else { 
      assertEquals(" \t yet \t\tanother\ttest\t", result);
    }
  }

  
  public void testPackageNameEmpty() throws InvalidPackageException {
    assertEquals("Package name for empty document", "", _defModel.getPackageName());
  }

  
  public void testPackageNameSimple()
    throws Exception
  {
    final String[] comments = {
      "/* package very.bad; */",
      "// package terribly.wrong;"
    };

    final String[] packages = {"edu", "edu.rice", "edu.rice.cs.drjava" };

    for (int i = 0; i < packages.length; i++) {
      String curPack = packages[i];

      for (int j = 0; j < comments.length; j++) {
        String curComment = comments[j];

        setUp();
        _defModel.insertString(0,
                              curComment + "\n\n" +
                                "package " + curPack +
                                ";\nclass Foo { int x; }\n",
                              null);

        assertEquals("Package name for document with comment " + curComment,
                     curPack,
                     _defModel.getPackageName());
      }
    }
  }

  
  public void testPackageNameWeird1()
    throws BadLocationException, InvalidPackageException
  {
    String weird = "package edu . rice\n./*comment!*/cs.drjava;";
    String normal = "edu.rice.cs.drjava";
    _defModel.insertString(0, weird, null);

    assertEquals("Package name for weird: '" + weird + "'",
                 normal,
                 _defModel.getPackageName());
  }

  
  public void testPackageNameWeird2()
    throws BadLocationException, InvalidPackageException
  {
    String weird = "package edu . rice //comment!\n.cs.drjava;";
    String normal = "edu.rice.cs.drjava";
    _defModel.insertString(0, weird, null);

    assertEquals("Package name for weird: '" + weird + "'",
                 normal,
                 _defModel.getPackageName());
  }

  
  public void testGetPackageNameWithPackageStatementAfterImport()
    throws BadLocationException, InvalidPackageException
  {
    String text = "import java.util.*;\npackage junk;\nclass Foo {}";
    _defModel.insertString(0, text, null);
    assertEquals("Package name for text with package statement after import",
                 "",
                 _defModel.getPackageName());
  }

  private String _getAllText() throws BadLocationException {
    return _defModel.getText();
  }
  
  public void testTopLevelClassName()
    throws BadLocationException, ClassNameNotFoundException
  {
    String weird = "package edu . rice\n./*comment!*/cs.drjava; class MyClass<T> implements O{";
    String result = "MyClass";
    _defModel.insertString(0, weird, null);

    assertEquals("class name for weird: '" + weird + "'",
                 result,
                 _defModel.getFirstTopLevelClassName());
  }

 
  public void testTopLevelInterfaceName()
    throws BadLocationException, ClassNameNotFoundException
  {
    String weird = "package edu . rice\n./*comment!*/cs.drjava; \n" +
      " interface thisInterface { \n" +
      " class MyClass {";
    String result = "thisInterface";
    _defModel.insertString(0, weird, null);

    assertEquals("class name for interface: '" + weird + "'",
                 result,
                 _defModel.getFirstTopLevelClassName());
  }

 
  public void testTopLevelClassNameWComments()
    throws BadLocationException, ClassNameNotFoundException
  {
    String weird = "package edu . rice\n./*comment!*/cs.drjava; \n" +
      "/* class Y */ \n" +
      " /* class Foo \n" +
      " * class Bar \n" +
      " interface Baz \n" +
      " */ \n" +
      "//class Blah\n" +
      "class MyClass {";

    String result = "MyClass";
    _defModel.insertString(0, weird, null);

    assertEquals("class name for class: '" + weird + "'",
                 result,
                 _defModel.getFirstTopLevelClassName());
  }

  
  public void testTopLevelClassNameNoSpace()
    throws BadLocationException
  {
    String c = "class";
    _defModel.insertString(0, c, null);
    try {
      _defModel.getFirstTopLevelClassName();
      fail("Should not have found a class name");
    }
    catch (ClassNameNotFoundException e) {
      
    }
  }

  
  public void testTopLevelClassNameWithClassloaderImport()
    throws BadLocationException, ClassNameNotFoundException
  {
    String weird = "import classloader.class; class MyClass {";
    String result = "MyClass";
    _defModel.insertString(0, weird, null);

    assertEquals("class name for weird: '" + weird + "'",
                 result,
                 _defModel.getFirstTopLevelClassName());
  }

  
  public void testTopLevelClassNameMisleading()
    throws BadLocationException, ClassNameNotFoundException
  {
    String weird = "package edu . rice\n./*comment!*/cs.drjava; \n" +
      " {class X} \n" +
      " interface thisInterface { \n" +
      " class MyInnerClass {";
    String result = "thisInterface";
    _defModel.insertString(0, weird, null);

    assertEquals("class name for interface: '" + weird + "'",
                 result,
                 _defModel.getFirstTopLevelClassName());
  }

  
  public void testTopLevelInterfaceNameMisleading()
    throws BadLocationException, ClassNameNotFoundException
  {
    String weird = "package edu . rice\n./*comment!*/cs.drjava; \n" +
      " {interface X} " +
      " \"class Foo\"" +
      " class MyClass {";
    String result = "MyClass";
    _defModel.insertString(0, weird, null);

    assertEquals("class name for user interface: '" + weird + "'",
                 result,
                 _defModel.getFirstTopLevelClassName());
  }

  
  public void testTopLevelInterfaceNameMisleading2()
    throws BadLocationException, ClassNameNotFoundException
  {
    String weird = "package edu . rice\n./*interface comment!*/cs.drjava; \n" +
      " {interface X<T>} " +
      " \"class interface Foo\"" +
      " class MyClass extends Foo<T> {";
    String result = "MyClass";
    _defModel.insertString(0, weird, null);

    assertEquals("class name for user interface: '" + weird + "'",
                 result,
                 _defModel.getFirstTopLevelClassName());
  }

  
  public void testTopLevelInterfaceNameBeforeClassName()
    throws BadLocationException, ClassNameNotFoundException
  {
    String weird = "package edu . rice\n./*comment!*/cs.drjava; \n" +
      " interface thisInterface { \n" +
      "  } \n" +
      " class thatClass {\n" +
      "  }";
    String result = "thisInterface";
    _defModel.insertString(0, weird, null);

    assertEquals("interface should have been chosen, rather than the class: '" + weird + "'",
                 result,
                 _defModel.getFirstTopLevelClassName());
  }

  
  public void testTopLevelClassNameWithDelimiters()
    throws BadLocationException, ClassNameNotFoundException
  {
    String weird1 = "package edu . rice\n./*comment!*/cs.drjava; \n" +
       " class MyClass<T> {";
    String result1 = "MyClass";
    _defModel.insertString(0, weird1, null);

    assertEquals("generics should be removed: '" + weird1 + "'",
                 result1,
                 _defModel.getFirstTopLevelClassName());

    String weird2 = "package edu . rice\n./*comment!*/cs.drjava; \n" +
       " class My_Class {";
    String result2 = "My_Class";
    _defModel.insertString(0, weird2, null);

    assertEquals("underscores should remain: '" + weird1 + "'",
                 result2,
                 _defModel.getFirstTopLevelClassName());
  }

  
  public void testTopLevelEnclosingClassName()
    throws BadLocationException, ClassNameNotFoundException
  {
    String classes =
      "import foo;\n" +  
      "class C1 {\n" +  
      "  void foo() { int a; }\n" +  
      "  class C2 { int x;\n" +  
      "    int y;\n" +  
      "    class C3 {}\n" +  
      "  } int b;\n" +  
      "}\n" +  
      "class C4 {\n" +  
      "  class C5 {\n" +  
      "    void bar() { int c; } class C6 {}\n" +  
      "  }\n" +  
      "} class C7 {}";  

    _defModel.insertString(0, classes, null);

    
    try {
      String result = _defModel.getEnclosingTopLevelClassName(3);
      fail("no enclosing class should be found at start");
    }
    catch (ClassNameNotFoundException cnnfe) {
      
    }

    
    try {
      _defModel.getEnclosingTopLevelClassName(15);
      fail("no enclosing class should be found before open brace");
    }
    catch (ClassNameNotFoundException cnnfe) {
      
    }

    try {
      String result = _defModel.getEnclosingTopLevelClassName(186);
      fail("no enclosing class should be found at end of file");
    }
    catch (ClassNameNotFoundException cnnfe) {
      
    }

    assertEquals("top level class name after first open brace", "C1",
                 _defModel.getEnclosingTopLevelClassName(22));
    assertEquals("top level class name inside C1", "C1",
                 _defModel.getEnclosingTopLevelClassName(26));
    assertEquals("top level class name inside method of C1", "C1",
                 _defModel.getEnclosingTopLevelClassName(42));
    assertEquals("top level class name on C2's brace", "C1",
                 _defModel.getEnclosingTopLevelClassName(58));
    assertEquals("top level class name after C2's brace", "C1",
                 _defModel.getEnclosingTopLevelClassName(59));
    assertEquals("top level class name inside C2", "C1",
                 _defModel.getEnclosingTopLevelClassName(68));
    assertEquals("top level class name inside C3", "C1",
                 _defModel.getEnclosingTopLevelClassName(92));
    assertEquals("top level class name after C3's close brace", "C1",
                 _defModel.getEnclosingTopLevelClassName(93));
    assertEquals("top level class name after C2's close brace", "C1",
                 _defModel.getEnclosingTopLevelClassName(100));

    
    try {
      _defModel.getEnclosingTopLevelClassName(107);
      fail("no enclosing class should be found between classes");
    }
    catch (ClassNameNotFoundException cnnfe) {
      
    }

    assertEquals("class name inside C4", "C4",
                 _defModel.getEnclosingTopLevelClassName(122));
    assertEquals("class name inside C5", "C4",
                 _defModel.getEnclosingTopLevelClassName(135));
    assertEquals("class name inside C6", "C4",
                 _defModel.getEnclosingTopLevelClassName(167));
    assertEquals("class name inside C7", "C7",
                 _defModel.getEnclosingTopLevelClassName(185));

    
    try {
      String result = _defModel.getEnclosingTopLevelClassName(186);
      fail("no enclosing class should be found at end");
    }
    catch (ClassNameNotFoundException cnnfe) {
      
    }
  }

  
  public void testQualifiedClassNameWithPackage()
    throws BadLocationException, ClassNameNotFoundException
  {
    String classes =
      "package foo;\n" +  
      "class C1 {}\n" +  
      "class C2 {}";  
    _defModel.insertString(0, classes, null);

    assertEquals("qualified class name without pos", "foo.C1",
                 _defModel.getQualifiedClassName());
    assertEquals("enclosing class name in C1", "C1",
                 _defModel.getEnclosingTopLevelClassName(23));
    assertEquals("qualified class name with pos in C1", "foo.C1",
                 _defModel.getQualifiedClassName(23));
    assertEquals("qualified class name with pos in C2", "foo.C2",
                 _defModel.getQualifiedClassName(35));

    
    try {
      _defModel.getQualifiedClassName(15);
      fail("no qualified class name should be found outside classes");
    }
    catch (ClassNameNotFoundException cnnfe) {
      
    }
  }

  
  public void testQualifiedClassNameWithoutPackage()
    throws BadLocationException, ClassNameNotFoundException
  {
    String classes =
      "class C1 {}\n" +  
      "class C2 {}";  
    _defModel.insertString(0, classes, null);

    assertEquals("qualified class name without pos", "C1",
                 _defModel.getQualifiedClassName());
    assertEquals("qualified class name with pos in C1", "C1",
                 _defModel.getQualifiedClassName(10));
    assertEquals("qualified class name with pos in C2", "C2",
                 _defModel.getQualifiedClassName(22));

    
    try {
      _defModel.getQualifiedClassName(15);
      fail("no qualified class name should be found outside classes");
    }
    catch (ClassNameNotFoundException cnnfe) {
      
    }
  }

  

  
  public void testUndoAndRedoAfterMultipleLineIndent() throws BadLocationException {  
    String text =
      "public class stuff {\n" +
      "private int _int;\n" +
      "private Bar _bar;\n" +
      "public void foo() {\n" +
      "_bar.baz(_int);\n" +
      "}\n" +
      "}\n";

    String indented =
      "public class stuff {\n" +
      "  private int _int;\n" +
      "  private Bar _bar;\n" +
      "  public void foo() {\n" +
      "    _bar.baz(_int);\n" +
      "  }\n" +
      "}\n";

    _defModel.addUndoableEditListener(_defModel.getUndoManager());
    DrJava.getConfig().setSetting(OptionConstants.INDENT_LEVEL,new Integer(2));
    _defModel.insertString(0,text,null);
    assertEquals("insertion",text, _defModel.getText()); 
    
    _defModel.getUndoManager().startCompoundEdit();
    _defModel.indentLines(0,_defModel.getLength());
    assertEquals("indenting",indented, _defModel.getText());
    _defModel.getUndoManager().undo();
    assertEquals("undo",text, _defModel.getText());
    _defModel.getUndoManager().redo();
    assertEquals("redo",indented, _defModel.getText());
  }

  
  public void testUndoAndRedoAfterMultipleLineCommentAndUncomment()
    throws BadLocationException {
    String text =
      "public class stuff {\n" +
      "  private int _int;\n" +
      "  private Bar _bar;\n" +
      "  public void foo() {\n" +
      "    _bar.baz(_int);\n" +
      "  }\n" +
      "}\n";

    String commented =
      "//public class stuff {\n" +
      "//  private int _int;\n" +
      "//  private Bar _bar;\n" +
      "//  public void foo() {\n" +
      "//    _bar.baz(_int);\n" +
      "//  }\n" +
      "//}\n";

    _defModel.addUndoableEditListener(_defModel.getUndoManager());
    DrJava.getConfig().setSetting(OptionConstants.INDENT_LEVEL,new Integer(2));
    _defModel.insertString(0,text,null);
    assertEquals("insertion",text, _defModel.getText());

    _defModel.getUndoManager().startCompoundEdit();
    _defModel.commentLines(0,_defModel.getLength());
    assertEquals("commenting",commented, _defModel.getText());
    _defModel.getUndoManager().undo();
    assertEquals("undo commenting",text, _defModel.getText());
    _defModel.getUndoManager().redo();
    assertEquals("redo commenting",commented, _defModel.getText());

    _defModel.getUndoManager().startCompoundEdit();
    _defModel.uncommentLines(0,_defModel.getLength());
    assertEquals("uncommenting",text, _defModel.getText());
    _defModel.getUndoManager().undo();
    assertEquals("undo uncommenting",commented, _defModel.getText());
    _defModel.getUndoManager().redo();
    assertEquals("redo uncommenting",text, _defModel.getText());
  }

  
  public void testCompoundUndoManager() throws BadLocationException {
    String text =
      "public class foo {\n" +
      "int bar;\n" +
      "}";

    String indented =
      "public class foo {\n" +
      "  int bar;\n" +
      "}";
    CompoundUndoManager undoManager = _defModel.getUndoManager();

    _defModel.addUndoableEditListener(undoManager);
    DrJava.getConfig().setSetting(OptionConstants.INDENT_LEVEL,new Integer(2));

    

    
    int key = undoManager.startCompoundEdit();
    assertEquals("Should have returned the correct key.", 0, key);

    
    _defModel.insertString(0, text, null);
    assertEquals("Should have inserted the text properly.", text,
                 _defModel.getText());

    
    undoManager.startCompoundEdit();
    _defModel.indentLines(0, _defModel.getLength());
    assertEquals("Should have indented correctly.", indented,
                 _defModel.getText());

    undoManager.undo();
    assertEquals("Should have undone correctly.", "",
                 _defModel.getText());

    

    String commented =
      "//public class foo {\n" +
      "//  int bar;\n" +
      "//}";

    
    key = _defModel.getUndoManager().startCompoundEdit();
    assertEquals("Should have returned the correct key.", 2, key);

    
    _defModel.insertString(0, text, null);
    assertEquals("Should have inserted the text properly.", text,
                 _defModel.getText());

    
    _defModel.indentLines(0, _defModel.getLength());
    assertEquals("Should have indented correctly.", indented,
                 _defModel.getText());

    undoManager.startCompoundEdit();
    _defModel.commentLines(0, _defModel.getLength());
    assertEquals("Should have commented correctly.", commented,
                 _defModel.getText());

    
    _defModel.getUndoManager().undo();
    assertEquals("Should have undone the commenting.", indented,
                 _defModel.getText());

    
    _defModel.getUndoManager().undo();
    assertEquals("Should have undone the indenting and inserting.", "",
                 _defModel.getText());

    

    
    key = _defModel.getUndoManager().startCompoundEdit();
    assertEquals("Should have returned the correct key.", 4, key);

    
    _defModel.insertString(0, text, null);
    assertEquals("Should have inserted the text properly.", text,
                 _defModel.getText());

    
    _defModel.indentLines(0, _defModel.getLength());
    assertEquals("Should have indented correctly.", indented,
                 _defModel.getText());


















    
    try {
      _defModel.getUndoManager().endCompoundEdit(key + 2);

    }
    catch (IllegalStateException e) {
      assertEquals("Should have printed the correct error message.",
                   "Improperly nested compound edits.", e.getMessage());
    }

    
    undoManager.startCompoundEdit();
    _defModel.indentLines(0, _defModel.getLength());
    assertEquals("Should have indented correctly.", indented,
                 _defModel.getText());

    
    
    
    

    








    

    _defModel.getUndoManager().undo();
    assertEquals("Should have undone the indenting and inserting.", "",
                 _defModel.getText());
  }

  
  public void testUndoOrRedoSetsUnmodifiedState() throws BadLocationException {
    _defModel.addUndoableEditListener(_defModel.getUndoManager());
    _defModel.insertString(0, "This is text", null);
    assertTrue("Document should be modified.", _defModel.isModifiedSinceSave());
    _defModel.getUndoManager().undo();
    _defModel.updateModifiedSinceSave();
    assertFalse("Document should no longer be modified after undo.", _defModel.isModifiedSinceSave());
    _defModel.insertString(0, "This is text", null);
    _defModel.resetModification();
    assertFalse("Document should not be modified after \"save\".", _defModel.isModifiedSinceSave());
    _defModel.getUndoManager().undo();
    _defModel.updateModifiedSinceSave();
    assertTrue("Document should be modified after undo.", _defModel.isModifiedSinceSave());
    _defModel.getUndoManager().redo();
    _defModel.updateModifiedSinceSave();
    assertFalse("Document should no longer be modified after redo.", _defModel.isModifiedSinceSave());
  }
}
