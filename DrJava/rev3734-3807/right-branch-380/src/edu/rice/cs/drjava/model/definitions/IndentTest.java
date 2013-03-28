

package  edu.rice.cs.drjava.model.definitions;

import  junit.framework.*;
import  javax.swing.text.BadLocationException;





import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.DJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.definitions.indent.*;
import edu.rice.cs.drjava.model.GlobalEventNotifier;

import edu.rice.cs.util.swing.Utilities;


public final class IndentTest extends DrJavaTestCase {
  protected DefinitionsDocument doc;

  static String noBrace = IndentInfo.noBrace;
  static String openSquiggly = IndentInfo.openSquiggly;
  static String openParen = IndentInfo.openParen;
  static String openBracket = IndentInfo.openBracket;
  private Integer indentLevel = new Integer(2);
  private GlobalEventNotifier _notifier;

  
  public IndentTest(String name) { super(name); }

  
  public void setUp() throws Exception {
    super.setUp();
    DrJava.getConfig().resetToDefaults();
    _notifier = new GlobalEventNotifier();
    doc = new DefinitionsDocument(_notifier);
    DrJava.getConfig().setSetting(OptionConstants.INDENT_LEVEL,indentLevel);
  }
  
  
  public static Test suite() { return  new TestSuite(IndentTest.class); }

  
  public void testIndentComments() throws BadLocationException {
    String text =
      "  foo();\n" +
      "   // foo\n" +
      "/**\n" +
      "\n" +
      "* Comment\n" +
      "    * More comment\n" +
      "code;\n" +
      "* More comment\n" +
      "\n" +
      "*/\n" +
      "\n";

    String indented =
      "  foo();\n" +     
      "  // foo\n" +     
      "  /**\n" +     
      "   * \n" +     
      "   * Comment\n" +     
      "   * More comment\n" +     
      "   code;\n" +     
      "   * More comment\n" +     
      "   * \n" +     
      "   */\n" +     
      "  \n";     

    doc.insertString(0, text, null);
    _assertContents(text, doc);
    doc.indentLines(9, doc.getLength());
    _assertContents(indented, doc);
  }

  
  public void testMultiLineStarInsertFirstLine() throws BadLocationException {
    String text =
      "/**\n" +
      "comments here blah blah\n" +
      " */";

    String noStarAdded =
      "/**\n" +
      " comments here blah blah\n" +
      " */";

    String starAdded =
      "/**\n" +
      " * comments here blah blah\n" +
      " */";

    doc.insertString(0, text, null);
    _assertContents(text, doc);
    doc.gotoLine(2);
    
    doc._indentLine(Indenter.OTHER);
    _assertContents(noStarAdded, doc);
    
    doc._indentLine(Indenter.ENTER_KEY_PRESS);
    _assertContents(starAdded, doc);
  }

  
  public void testMultiLineStarInsertLaterLine() throws BadLocationException {

    String text =
      "/**\n" +
      " * other comments\n" +
      "comments here blah blah\n" +
      " */";

    String noStarAdded =
      "/**\n" +
      " * other comments\n" +
      " comments here blah blah\n" +
      " */";

    String starAdded =
      "/**\n" +
      " * other comments\n" +
      " * comments here blah blah\n" +
      " */";

    doc.insertString(0, text, null);
    _assertContents(text, doc);
    doc.gotoLine(3);
    
    doc._indentLine(Indenter.OTHER);
    _assertContents(noStarAdded, doc);
    
    doc._indentLine(Indenter.ENTER_KEY_PRESS);
    _assertContents(starAdded, doc);
  }

  
  public void testIndentParenPhrases() throws BadLocationException {
    String text =
      "foo(i,\n" +
      "j.\n" +
      "bar().\n" +
      "// foo();\n" +
      "baz(),\n" +
      "cond1 ||\n" +
      "cond2);\n" +
      "i = myArray[x *\n" +
      "y.\n" +
      "foo() +\n" +
      "z\n" +
      "];\n";

    String indented =
      "foo(i,\n" +
      "    j.\n" +     
      "      bar().\n" +     
      "// foo();\n" +     
      "      baz(),\n" +     
      "    cond1 ||\n" +     
      "    cond2);\n" +     
      "i = myArray[x *\n" +     
      "            y.\n" +     
      "              foo() +\n" +     
      "            z\n" +     
      "              ];\n";     

    doc.insertString(0, text, null);
    _assertContents(text, doc);
    doc.indentLines(0, doc.getLength());
    _assertContents(indented, doc);
  }

  
 public void testIndentBraces() throws BadLocationException {
   String text =
     "{\n" +
     "class Foo\n" +
     "extends F {\n" +
     "int i;   \n" +
     "void foo() {\n" +
     "if (true) {\n" +
     "bar();\n" +
     "}\n" +
     "}\n" +
     "/* comment */ }\n" +
     "class Bar {\n" +
     "/* comment\n" +
     "*/ }\n" +
     "int i;\n" +
     "}\n";

   String indented =
     "{\n" +
     "  class Foo\n" +     
     "    extends F {\n" +     
     "    int i;   \n" +     
     "    void foo() {\n" +     
     "      if (true) {\n" +     
     "        bar();\n" +     
     "      }\n" +     
     "    }\n" +     
     "  /* comment */ }\n" +     
     "  class Bar {\n" +     
     "    /* comment\n" +     
     "     */ }\n" +      
     "  int i;\n" +     
     "}\n";


   doc.insertString(0, text, null);
   _assertContents(text, doc);
   doc.indentLines(0, doc.getLength());
   _assertContents(indented, doc);
 }

  
 public void testIndentArray() throws BadLocationException {
   String text =
     "int[2][] a ={\n" +
     "{\n"  +
     "1,\n" +
     "2,\n" +
     "3},\n" +
     "{\n" +
     "4,\n" +
     "5}\n" +
     "};\n";

   String indented =
     "int[2][] a ={\n" +
     "  {\n"  +
     "    1,\n" +
     "    2,\n" +
     "    3},\n" +
     "  {\n" +
     "    4,\n" +
     "    5}\n" +
     "};\n";




   doc.insertString(0, text, null);
   _assertContents(text, doc);
   doc.indentLines(0, doc.getLength());
   _assertContents(indented, doc);
 }

  
  public void testIndentCommonCases() throws BadLocationException {
    String text =
      "int x;\n" +
      "      int y;\n" +
      "  class Foo\n" +
      "     extends F\n" +
      " {\n" +
      "   }";

    String indented =
      "int x;\n" +
      "int y;\n" +
      "class Foo\n" +
      "  extends F\n" +
      "{\n" +
      "}";

    doc.insertString(0, text, null);
    _assertContents(text, doc);
    doc.indentLines(0, doc.getLength());
    _assertContents(indented, doc);
  }

  
  public void testIndentSwitch() throws BadLocationException {
    String text =
      "switch (x) {\n" +
      "case 1:\n" +
      "foo();\n" +
      "break;\n" +
      "case 2: case 3:\n" +
      "case 4: case 5:\n" +
      "bar();\n" +
      "break;\n" +
      "}\n";

    String indented =
      "switch (x) {\n" +
      "  case 1:\n" +     
      "    foo();\n" +     
      "    break;\n" +     
      "  case 2: case 3:\n" +     
      "  case 4: case 5:\n" +     
      "    bar();\n" +     
      "    break;\n" +     
      "}\n";     


    doc.insertString(0, text, null);
    _assertContents(text, doc);
    doc.indentLines(0, doc.getLength());
    _assertContents(indented, doc);
  }

  
  public void testIndentTernary() throws BadLocationException {
    String text =
      "test1 = x ? y : z;\n" +
      "test2 = x ? y :\n" +
      "z;\n" +
      "foo();\n" +
      "test3 =\n" +
      "x ?\n" +
      "y :\n" +
      "z;\n" +
      "bar();\n" +
      "test4 = (x ?\n" +
      "y :\n" +
      "z);\n";

    String indented =
      "test1 = x ? y : z;\n" +     
      "test2 = x ? y :\n" +     
      "  z;\n" +     
      "foo();\n" +     
      "test3 =\n" +     
      "  x ?\n" +     
      "  y :\n" +     
      "  z;\n" +     
      "bar();\n" +     
      "test4 = (x ?\n" +     
      "           y :\n" +     
      "           z);\n";     


    doc.insertString(0, text, null);
    _assertContents(text, doc);
    doc.indentLines(0, doc.getLength());
    _assertContents(indented, doc);
  }

  
  public void testIndentInfoSquiggly() throws BadLocationException {
    
    BraceReduction rm = doc.getReduced();
    IndentInfo ii = rm.getIndentInformation();
    _assertIndentInfo(ii, noBrace, -1, -1, -1);
    
    doc.insertString(0, "\n", null);
    _assertContents("\n", doc);
    ii = rm.getIndentInformation();
    _assertIndentInfo(ii, noBrace, -1, -1, 0);
    
    doc.insertString(0, "{\n\n", null);
    
    _assertContents("{\n\n\n", doc);
    ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openSquiggly, -1, 3, 0);
    
    doc.insertString(3, "{\n\n", null);
    
    _assertContents("{\n\n{\n\n\n", doc);
    ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openSquiggly, 3, 3, 0);
    
    doc.insertString(6, "  {\n\n", null);
    
    _assertContents("{\n\n{\n\n  {\n\n\n", doc);
    ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openSquiggly, 5, 3, 0);
  }

  
  public void testIndentInfoParen() throws BadLocationException {
    
    BraceReduction rm = doc.getReduced();
    doc.insertString(0, "\n(\n", null);
    IndentInfo ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openParen, 2, 2, 0);
    
    doc.insertString(1, "  helo ", null);
    doc.move(2);
    
    _assertContents("\n  helo (\n", doc);
    ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openParen, 9, 2, 0);
    
    doc.move(-1);
    doc.insertString(9, " (", null);
    doc.move(1);
    
    _assertContents("\n  helo ( (\n", doc);
    ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openParen, 11, 2, 0);
  }

  
  public void testIndentInfoBracket() throws BadLocationException {
    
    BraceReduction rm = doc.getReduced();
    doc.insertString(0, "\n[\n", null);
    IndentInfo ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openBracket, 2, 2, 0);
    
    doc.insertString(1, "  helo ", null);
    doc.move(2);
    
    _assertContents("\n  helo [\n", doc);
    ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openBracket, 9, 2, 0);
    
    doc.move(-1);
    doc.insertString(9, " [", null);
    doc.move(1);
    
    _assertContents("\n  helo [ [\n", doc);
    ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openBracket, 11, 2, 0);
  }

  
  public void testIndentInfoPrevNewline () throws BadLocationException {
    BraceReduction rm = doc.getReduced();
    doc.insertString(0, "{\n  {\nhello", null);
    
    IndentInfo ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openSquiggly, 9, 7, 5);
  }

  
  public void testEndOfBlockComment () throws BadLocationException {
    doc.insertString(0, "\n{\n  hello;\n /*\n hello\n */", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("\n{\n  hello;\n /*\n hello\n */", doc);
  }

  
  public void testAfterBlockComment () throws BadLocationException {
    doc.insertString(0, "\n{\n  hello;\n  /*\n  hello\n  */\nhello", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("\n{\n  hello;\n  /*\n  hello\n  */\n  hello", doc);
  }

  
  public void testAfterBlockComment3 () throws BadLocationException {
    doc.insertString(0, "\n{\n  hello;\n  /*\n  hello\n  grr*/\nhello", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("\n{\n  hello;\n  /*\n  hello\n  grr*/\n  hello", doc);
  }

  
  public void testAfterBlockComment4 () throws BadLocationException {
    doc.insertString(0, "\n{\n  hello;\n /*\n  hello\n */ hello", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("\n{\n  hello;\n /*\n  hello\n  */ hello", doc);
  }

  
  public void testAfterBlockComment2 () throws BadLocationException {
    doc.insertString(0, "\n{\n  hello;\n  /*\n  hello\n  */ (\nhello", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("\n{\n  hello;\n  /*\n  hello\n  */ (\n      hello", doc);
  }

  
  public void testIndentInfoBlockComments () throws BadLocationException {
    BraceReduction rm = doc.getReduced();
    doc.insertString(0, "(\n /*\n*\n", null);
    
    rm.move(-1);
    IndentInfo ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openParen, -1, 7, 1);
  }

  
  public void testIndentInfoBlockComments2 () throws BadLocationException {
    BraceReduction rm = doc.getReduced();
    doc.insertString(0, "\n(\n /*\n*\n", null);
    
    rm.move(-1);
    IndentInfo ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openParen, 7, 7, 1);
  }

  
  public void testIndentInfoBlockComments3 () throws BadLocationException {
    BraceReduction rm = doc.getReduced();
    doc.insertString(0, "{\n  /*\n*\n", null);
    
    rm.move(-1);
    IndentInfo ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openSquiggly, -1, 8, 1);
  }

  
  public void testIndentInfoBlockComments4 () throws BadLocationException {
    BraceReduction rm = doc.getReduced();
    doc.insertString(0, "\n{\n  /*\n*\n", null);
    
    rm.move(-1);
    IndentInfo ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openSquiggly, 8, 8, 1);
  }

  
  public void testSkippingBraces () throws BadLocationException {
    BraceReduction rm = doc.getReduced();
    doc.insertString(0, "\n{\n   { ()}\n}", null);
    IndentInfo ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openSquiggly, 12, 12, 1);
  }

  
  public void testSkippingComments () throws BadLocationException {
    
    BraceReduction rm = doc.getReduced();
    doc.insertString(0, "\n{\n   //{ ()\n}", null);
    IndentInfo ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openSquiggly, 13, 13, 1);
  }

  
  public void testSkippingCommentsBraceAtBeginning () throws BadLocationException {
    
    BraceReduction rm = doc.getReduced();
    doc.insertString(0, "{\n   //{ ()}{", null);
    IndentInfo ii = rm.getIndentInformation();
    _assertIndentInfo(ii, openSquiggly, -1, 13, 11);
  }

  
  public void testNothingToIndentOn () throws BadLocationException {
    
    BraceReduction rm = doc.getReduced();
    doc.insertString(0, "   //{ ()}{", null);
    IndentInfo ii = rm.getIndentInformation();
    _assertIndentInfo(ii, noBrace, -1, -1, -1);
  }

  
  public void testStartSimple () throws BadLocationException {
    
    doc.insertString(0, "abcde", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("abcde", doc);
  }

  
  public void testStartSpaceIndent () throws BadLocationException {
    
    doc.insertString(0, "  abcde", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("abcde", doc);
  }

  
  public void testStartBrace () throws BadLocationException {
    
    doc.insertString(0, "public class temp \n {", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("public class temp \n{", doc);
  }

  
  public void testEndBrace () throws BadLocationException {
    
    doc.insertString(0, "public class temp \n{ \n  }", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("public class temp \n{ \n}", doc);
  }

  
  public void testInsideClass () throws BadLocationException {
    
    doc.insertString(0, "public class temp \n{ \ntext here", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("public class temp \n{ \n  text here", doc);
  }

  
  public void testInsideClassWithBraceSets () throws BadLocationException {
    
    doc.insertString(0, "public class temp \n{  ()\ntext here", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("public class temp \n{  ()\n  text here", doc);
  }

  
  public void testIgnoreBraceOnSameLine () throws BadLocationException {
    
    doc.insertString(0, "public class temp \n{  ()\n{text here", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("public class temp \n{  ()\n  {text here", doc);
  }

  

  
  public void testWeird () throws BadLocationException {
    
    doc.insertString(0, "hello\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("hello\n  ", doc);
  }

  
  public void testWierd2 () throws BadLocationException {
    
    doc.insertString(0, "hello", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("hello", doc);
  }

  
  public void testMotion () throws BadLocationException {
    
    doc.insertString(0, "hes{\n{abcde", null);
    doc.insertString(11, "\n{", null);
    
    doc.move(-8);
    
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    
    _assertContents("hes{\n  {abcde\n{", doc);
  }

  
  public void testNextCharIsNewline () throws BadLocationException {
    
    doc.insertString(0, "hes{\n{abcde", null);
    doc.insertString(11, "\n{", null);
    
    doc.move(-2);
    
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    
    _assertContents("hes{\n  {abcde\n{", doc);
  }

  
  public void testFor () throws BadLocationException {
    
    doc.insertString(0, "for(;;)\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("for(;;)\n  ", doc);
  }

  
  public void testFor2 () throws BadLocationException {
    
    doc.insertString(0, "{\n  for(;;)\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("{\n  for(;;)\n    ", doc);
  }

  
  public void testOpenParen () throws BadLocationException {
    
    doc.insertString(0, "hello(\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("hello(\n      ", doc);
  }

  
  public void testPrintString () throws BadLocationException {
    
    doc.insertString(0, "Sys.out(\"hello\"\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("Sys.out(\"hello\"\n          ", doc);
  }

  
  public void testOpenBracket () throws BadLocationException {
    
    doc.insertString(0, "hello[\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("hello[\n      ", doc);
  }

  
  public void testSquigglyAlignment () throws BadLocationException {
    
    doc.insertString(0, "{\n  }", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("{\n}", doc);
  }

  
  public void testSpaceBrace () throws BadLocationException {
    
    doc.insertString(0, "   {\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("   {\n     ", doc);
  }

  

  

  
  public void testEnter () throws BadLocationException {
    
    doc.insertString(0, "\n\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("\n\n", doc);
  }

  
  public void testEnter2 () throws BadLocationException {
    
    doc.insertString(0, "\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("\n", doc);
  }

  
  public void testNotRecognizeComments () throws BadLocationException {
    
    doc.insertString(0, "\nhello //bal;\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("\nhello //bal;\n  ", doc);
  }

  
  public void testNotRecognizeComments2 () throws BadLocationException {
    
    doc.insertString(0, "\nhello; /*bal*/\n ", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("\nhello; /*bal*/\n", doc);
  }

  
  public void testBlockIndent () throws BadLocationException {
    
    doc.insertString(0, "hello\n{\n{\n  {", null);
    doc.indentLines(8, 13);
    _assertContents("hello\n{\n  {\n    {", doc);
  }

  
  public void testBlockIndent2 () throws BadLocationException {
    doc.insertString(0, "  x;\n  y;\n", null);
    doc.indentLines(0, doc.getLength());
    _assertContents("x;\ny;\n", doc);
  }

  
  public void testIndentInsideCommentBlock () throws BadLocationException {
    doc.insertString(0, "hello\n{\n/*{\n{\n*/\nhehe", null);
    doc.indentLines(0, 21);
    _assertContents("hello\n{\n  /*{\n   {\n   */\n  hehe", doc);
  }

  
  public void testSecondLineProblem () throws BadLocationException {
    
    doc.insertString(0, "\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("\n", doc);
  }

  
  public void testSecondLineProblem2 () throws BadLocationException {
    
    doc.insertString(0, "a\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("a\n  ", doc);
  }

  
  public void testSmallFileProblem () throws BadLocationException {
    
    doc.insertString(0, "\n\n", null);
    doc.indentLines(doc.getCurrentLocation(), doc.getCurrentLocation());
    _assertContents("\n\n", doc);
  }

  
  public void testAnonymousInnerClass() throws BadLocationException {
    String text =
      "addWindowListener(new WindowAdapter() {\n" +
      "public void windowClosing(WindowEvent e) {\n" +
      "dispose();\n" +
      "}\n" +
      "void x() {\n" +
      "\n" +
      "}\n" +
      "\n" +
      "}\n" +
      ");\n";
    String indented =
      "addWindowListener(new WindowAdapter() {\n" +
      "  public void windowClosing(WindowEvent e) {\n" +
      "    dispose();\n" +
      "  }\n" +
      "  void x() {\n" +
      "    \n" +
      "  }\n" +
      "  \n" +
      "}\n" +
      ");\n";


    doc.insertString(0, text, null);
    _assertContents(text, doc);
    doc.indentLines(0, doc.getLength());
    _assertContents(indented, doc);
  }


















































































  public void testLiveUpdateOfIndentLevel() throws BadLocationException {

    String text =
      "int[2][] a ={\n" +
      "{\n"  +
      "1,\n" +
      "2,\n" +
      "3},\n" +
      "{\n" +
      "4,\n" +
      "5}\n" +
      "};\n";

    String indentedBefore =
      "int[2][] a ={\n" +
      "  {\n"  +
      "    1,\n" +
      "    2,\n" +
      "    3},\n" +
      "  {\n" +
      "    4,\n" +
      "    5}\n" +
      "};\n";

    String indentedAfter =
      "int[2][] a ={\n" +
      "        {\n" +
      "                1,\n" +
      "                2,\n" +
      "                3},\n" +
      "        {\n" +
      "                4,\n" +
      "                5}\n" +
      "};\n";

    doc.insertString(0, text, null);

    _assertContents(text, doc);
    doc.indentLines(0, doc.getLength());
    _assertContents(indentedBefore, doc);
    DrJava.getConfig().setSetting(OptionConstants.INDENT_LEVEL, new Integer(8));
    
    Utilities.clearEventQueue();
    doc.indentLines(0, doc.getLength());
    _assertContents(indentedAfter, doc);
  }

  
  public void testNestedIfInSwitch() throws BadLocationException {
    String text =
      "switch(cond) {\n" +
      "case 1:\n" +
      "object.doStuff();\n" +
      "if(object.hasDoneStuff()) {\n" +
      "thingy.doOtherStuff();\n" +
      "lion.roar(\"raaargh\");\n" +
      "}\n" +
      "break;\n" +
      "}\n";

    String indented =
      "switch(cond) {\n" +
      "  case 1:\n" +
      "    object.doStuff();\n" +
      "    if(object.hasDoneStuff()) {\n" +
      "      thingy.doOtherStuff();\n" +
      "      lion.roar(\"raaargh\");\n" +
      "    }\n" +
      "    break;\n" +
      "}\n";

    doc.insertString(0, text, null);
    _assertContents(text, doc);
    doc.indentLines(0, doc.getLength());
    _assertContents(indented, doc);
  }





























  
  public void testIndentingCorrectLine() throws BadLocationException {
    String test1 = 
      "class A {\n" +
      "  int a = 5;\n" +
      "     }";
    
    String test1Correct =
      "class A {\n" +
      "  int a = 5;\n" +
      "}";
    
    String test2 = 
      "     {\n" +
      "  int a = 5;\n" +
      "  }\n";
    
    String test2Correct =
      "{\n" +
      "  int a = 5;\n" +
      "  }\n";
    
    doc.insertString(0, test1, null);
    _assertContents(test1, doc);
    doc.setCurrentLocation(20);
    doc.indentLines(20,20);


    _assertContents(test1, doc);
    
    doc = new DefinitionsDocument(_notifier);
    
    doc.insertString(0, test1, null);
    _assertContents(test1, doc);
    doc.indentLines(28,28);
    _assertContents(test1Correct, doc);
    
    doc = new DefinitionsDocument(_notifier);
    
    doc.insertString(0, test2, null);
    _assertContents(test2, doc);
    doc.setCurrentLocation(5);
    doc.indentLines(5,5);
    _assertContents(test2Correct, doc);
  }

  

  private void _assertContents(String expected, DJDocument document) throws BadLocationException {
    assertEquals("document contents", expected, document.getText());
  }

  private void _assertIndentInfo(IndentInfo ii, String braceType, int distToNewline, int distToBrace, int distToPrevNewline) {
    assertEquals("indent info: brace type", braceType, ii.braceType);
    assertEquals("indent info: dist to new line", distToNewline, ii.distToNewline);
    assertEquals("indent info: dist to brace", distToBrace, ii.distToBrace);
    assertEquals("indent info: dist to prev new line", distToPrevNewline, ii.distToPrevNewline);
  }


































































}
