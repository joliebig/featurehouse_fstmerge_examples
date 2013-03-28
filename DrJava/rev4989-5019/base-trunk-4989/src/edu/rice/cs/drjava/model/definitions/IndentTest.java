

package  edu.rice.cs.drjava.model.definitions;

import  junit.framework.*;
import  javax.swing.text.BadLocationException;





import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.DJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.BraceInfo;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.definitions.indent.*;
import edu.rice.cs.drjava.model.GlobalEventNotifier;


import edu.rice.cs.util.swing.Utilities;

import static edu.rice.cs.drjava.model.definitions.reducedmodel.BraceInfo.*;


public final class IndentTest extends DrJavaTestCase {
  protected DefinitionsDocument _doc;
  
  private Integer indentLevel = Integer.valueOf(2);
  private GlobalEventNotifier _notifier;
  
  
  public IndentTest(String name) { super(name); }
  
  
  public void setUp() throws Exception {
    super.setUp();
    DrJava.getConfig().resetToDefaults();
    _notifier = new GlobalEventNotifier();
    _doc = new DefinitionsDocument(_notifier);
    setConfigSetting(OptionConstants.INDENT_LEVEL, indentLevel);
  }
  
  
  public static Test suite() { return  new TestSuite(IndentTest.class); }
  
  
  private void safeIndentLine(final Indenter.IndentReason reason) {
    Utilities.invokeAndWait(new Runnable() { public void run() { _doc._indentLine(reason); } });
  }
 
  
  private void safeIndentLines(final int startSel, final int endSel) {
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        _doc.indentLines(startSel, endSel); 
      } 
    });
  }
  
  
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
    
    _doc.insertString(0, text, null);
    _assertContents(text, _doc);
    safeIndentLines(9, _doc.getLength());
    _assertContents(indented, _doc);
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
    
    _doc.insertString(0, text, null);
    _assertContents(text, _doc);
    _doc.gotoLine(2);
    
    safeIndentLine(Indenter.IndentReason.OTHER);
    _assertContents(noStarAdded, _doc);
    
    safeIndentLine(Indenter.IndentReason.ENTER_KEY_PRESS);
    _assertContents(starAdded, _doc);
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
    
    _doc.insertString(0, text, null);
    _assertContents(text, _doc);
    _doc.gotoLine(3);
    
    safeIndentLine(Indenter.IndentReason.OTHER);
    _assertContents(noStarAdded, _doc);
    
    safeIndentLine(Indenter.IndentReason.ENTER_KEY_PRESS);
    _assertContents(starAdded, _doc);
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
    
    _doc.insertString(0, text, null);
    _assertContents(text, _doc);
    safeIndentLines(0, _doc.getLength());
    _assertContents(indented, _doc);
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
    
    
    _doc.insertString(0, text, null);
    _assertContents(text, _doc);
    safeIndentLines(0, _doc.getLength());
    _assertContents(indented, _doc);
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
    
    _doc.insertString(0, text, null);
    _assertContents(text, _doc);
    safeIndentLines(0, _doc.getLength());
    _assertContents(indented, _doc);
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
    
    _doc.insertString(0, text, null);
    _assertContents(text, _doc);
    safeIndentLines(0, _doc.getLength());
    _assertContents(indented, _doc);
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
    
    
    _doc.insertString(0, text, null);
    _assertContents(text, _doc);
    safeIndentLines(0, _doc.getLength());
    _assertContents(indented, _doc);
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
    
    
    _doc.insertString(0, text, null);
    _assertContents(text, _doc);
    safeIndentLines(0, _doc.getLength());
    _assertContents(indented, _doc);
  }
  
  
  public void testIndentInfoCurly() throws BadLocationException {
    
    _assertLineBraceInfo(-1, NONE);
    _assertBraceInfo(-1, NONE);
    
    _doc.insertString(0, "\n", null);
    _assertContents("\n", _doc);
    _assertLineBraceInfo(-1, NONE);
    _assertBraceInfo(-1, NONE);
    
    _doc.insertString(0, "{\n\n", null);
    
    _assertContents("{\n\n\n", _doc);
    _assertLineBraceInfo(3, OPEN_CURLY);
    _assertBraceInfo(3, OPEN_CURLY);
    
    _doc.insertString(3, "{\n\n", null);
    
    _assertContents("{\n\n{\n\n\n", _doc);
    _assertLineBraceInfo(3, OPEN_CURLY);
    _assertBraceInfo(3, OPEN_CURLY);
    
    _doc.insertString(6, "  {\n\n", null);
    
    _assertContents("{\n\n{\n\n  {\n\n\n", _doc);
    _assertLineBraceInfo(3, OPEN_CURLY);
    _assertBraceInfo(3, OPEN_CURLY);
  }
  
  
  public void testIndentInfoParen() throws BadLocationException {
    
    _doc.insertString(0, "\n(\n", null);
    _assertLineBraceInfo(2, OPEN_PAREN);
    _assertBraceInfo(2, OPEN_PAREN);
    
    _doc.insertString(1, "  helo ", null);
    _doc.move(2);
    
    _assertContents("\n  helo (\n", _doc);
    _assertLineBraceInfo(2, OPEN_PAREN);
    _assertBraceInfo(2, OPEN_PAREN);
    
    _doc.move(-1);
    _doc.insertString(9, " (", null);
    _doc.move(1);
    
    _assertContents("\n  helo ( (\n", _doc);
    _assertLineBraceInfo(2, OPEN_PAREN);
    _assertBraceInfo(2, OPEN_PAREN);
  }
  
  
  public void testIndentInfoBracket() throws BadLocationException {
    
    _doc.insertString(0, "\n[\n", null);
    _assertLineBraceInfo(2, OPEN_BRACKET);
    _assertBraceInfo(2, OPEN_BRACKET);
    
    _doc.insertString(1, "  helo ", null);
    _doc.move(2);
    
    _assertContents("\n  helo [\n", _doc);
    _assertLineBraceInfo(2, OPEN_BRACKET);
    _assertBraceInfo(2, OPEN_BRACKET);
    
    _doc.move(-1);
    _doc.insertString(9, " [", null);
    _doc.move(1);
    
    _assertContents("\n  helo [ [\n", _doc);
    _assertLineBraceInfo(2, OPEN_BRACKET);
    _assertBraceInfo(2, OPEN_BRACKET);
  }
  
  
  public void testIndentInfoPrevNewline () throws BadLocationException {

    _doc.insertString(0, "{\n  {\nhello", null);

    


    _assertLineBraceInfo(2, OPEN_CURLY);
    _assertBraceInfo(7, OPEN_CURLY);
  }
  
  
  public void testEndOfBlockComment () throws BadLocationException {
    _doc.insertString(0, "\n{\n  hello;\n /*\n hello\n */", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("\n{\n  hello;\n /*\n hello\n */", _doc);
  }
  
  
  public void testAfterBlockComment () throws BadLocationException {
    _doc.insertString(0, "\n{\n  hello;\n  /*\n  hello\n  */\nhello", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("\n{\n  hello;\n  /*\n  hello\n  */\n  hello", _doc);
  }
  
  
  public void testAfterBlockComment3 () throws BadLocationException {
    _doc.insertString(0, "\n{\n  hello;\n  /*\n  hello\n  grr*/\nhello", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("\n{\n  hello;\n  /*\n  hello\n  grr*/\n  hello", _doc);
  }
  
  
  public void testAfterBlockComment4 () throws BadLocationException {
    _doc.insertString(0, "\n{\n  hello;\n /*\n  hello\n */ hello", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("\n{\n  hello;\n /*\n  hello\n  */ hello", _doc);
  }
  
  
  public void testAfterBlockComment2 () throws BadLocationException {
    _doc.insertString(0, "\n{\n  hello;\n  /*\n  hello\n  */ (\nhello", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("\n{\n  hello;\n  /*\n  hello\n  */ (\n      hello", _doc);
  }
  











  











  











  






















































  
  
  public void testStartSimple () throws BadLocationException {
    
    _doc.insertString(0, "abcde", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("abcde", _doc);
  }
  
  
  public void testStartSpaceIndent () throws BadLocationException {
    
    _doc.insertString(0, "  abcde", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("abcde", _doc);
  }
  
  
  public void testStartBrace () throws BadLocationException {
    
    _doc.insertString(0, "public class temp \n {", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("public class temp \n{", _doc);
  }
  
  
  public void testEndBrace () throws BadLocationException {
    
    _doc.insertString(0, "public class temp \n{ \n  }", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("public class temp \n{ \n}", _doc);
  }
  
  
  public void testInsideClass () throws BadLocationException {
    
    _doc.insertString(0, "public class temp \n{ \ntext here", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("public class temp \n{ \n  text here", _doc);
  }
  
  
  public void testInsideClassWithBraceSets () throws BadLocationException {
    
    _doc.insertString(0, "public class temp \n{  ()\ntext here", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("public class temp \n{  ()\n  text here", _doc);
  }
  
  
  public void testIgnoreBraceOnSameLine () throws BadLocationException {
    
    _doc.insertString(0, "public class temp \n{  ()\n{text here", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("public class temp \n{  ()\n  {text here", _doc);
  }
  








  
  
  public void testWeird () throws BadLocationException {
    
    _doc.insertString(0, "hello\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("hello\n  ", _doc);
  }
  
  
  public void testWierd2 () throws BadLocationException {
    
    _doc.insertString(0, "hello", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("hello", _doc);
  }
  
  
  public void testMotion () throws BadLocationException {
    
    _doc.insertString(0, "hes{\n{abcde", null);
    _doc.insertString(11, "\n{", null);
    
    _doc.move(-8);
    
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    
    _assertContents("hes{\n  {abcde\n{", _doc);
  }
  
  
  public void testNextCharIsNewline () throws BadLocationException {
    
    _doc.insertString(0, "hes{\n{abcde", null);
    _doc.insertString(11, "\n{", null);
    
    _doc.move(-2);
    
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    
    _assertContents("hes{\n  {abcde\n{", _doc);
  }
  
  
  public void testFor () throws BadLocationException {
    
    _doc.insertString(0, "for(;;)\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("for(;;)\n  ", _doc);
  }
  
  
  public void testFor2 () throws BadLocationException {
    
    _doc.insertString(0, "{\n  for(;;)\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("{\n  for(;;)\n    ", _doc);
  }
  
  
  public void testOpenParen () throws BadLocationException {
    
    _doc.insertString(0, "hello(\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("hello(\n      ", _doc);
  }
  
  
  public void testPrintString () throws BadLocationException {
    
    _doc.insertString(0, "Sys.out(\"hello\"\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("Sys.out(\"hello\"\n          ", _doc);
  }
  
  
  public void testOpenBracket () throws BadLocationException {
    
    _doc.insertString(0, "hello[\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("hello[\n      ", _doc);
  }
  
  
  public void testCurlyAlignment () throws BadLocationException {
    
    _doc.insertString(0, "{\n  }", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("{\n}", _doc);
  }
  
  
  public void testSpaceBrace () throws BadLocationException {
    
    _doc.insertString(0, "   {\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("   {\n     ", _doc);
  }
  
  
  
  
  
  
  public void testEnter () throws BadLocationException {
    
    _doc.insertString(0, "\n\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("\n\n", _doc);
  }
  
  
  public void testEnter2 () throws BadLocationException {
    
    _doc.insertString(0, "\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("\n", _doc);
  }
  
  
  public void testNotRecognizeComments () throws BadLocationException {
    
    _doc.insertString(0, "\nhello //bal;\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("\nhello //bal;\n  ", _doc);
  }
  
  
  public void testNotRecognizeComments2 () throws BadLocationException {
    
    _doc.insertString(0, "\nhello; /*bal*/\n ", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("\nhello; /*bal*/\n", _doc);
  }
  
  
  public void testBlockIndent () throws BadLocationException {
    
    _doc.insertString(0, "hello\n{\n{\n  {", null);
    safeIndentLines(8, 13);
    _assertContents("hello\n{\n  {\n    {", _doc);
  }
  
  
  public void testBlockIndent2 () throws BadLocationException {
    _doc.insertString(0, "  x;\n  y;\n", null);
    safeIndentLines(0, _doc.getLength());
    _assertContents("x;\ny;\n", _doc);
  }
  
  
  public void testIndentInsideCommentBlock () throws BadLocationException {
    _doc.insertString(0, "hello\n{\n/*{\n{\n*/\nhehe", null);
    safeIndentLines(0, 21);
    _assertContents("hello\n{\n  /*{\n   {\n   */\n  hehe", _doc);
  }
  
  
  public void testSecondLineProblem () throws BadLocationException {
    
    _doc.insertString(0, "\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("\n", _doc);
  }
  
  
  public void testSecondLineProblem2 () throws BadLocationException {
    
    _doc.insertString(0, "a\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("a\n  ", _doc);
  }
  
  
  public void testSmallFileProblem () throws BadLocationException {
    
    _doc.insertString(0, "\n\n", null);
    safeIndentLines(_doc.getCurrentLocation(), _doc.getCurrentLocation());
    _assertContents("\n\n", _doc);
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
      ");\n" +
      "foo.bar();\n";
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
      ");\n" +
      "foo.bar();\n";
    
    
    _doc.insertString(0, text, null);
    _assertContents(text, _doc);

    
    safeIndentLines(0, _doc.getLength());


    _assertContents(indented, _doc);

  }
  
  
  public void testParenthesizedAnonymousInnerClass() throws BadLocationException {
    String text = "addActionListener(new ActionListener() {\n" +
      "public void actionPerformed(ActionEvent e) {\n" +
        "config.setSetting(LANGUAGE_LEVEL, DrJavaRoot.FULL_JAVA);\n" +
      "}});\n" +
      "group.add(rbMenuItem);\n";
    String indented = "addActionListener(new ActionListener() {\n" +
      "  public void actionPerformed(ActionEvent e) {\n" +
       "    config.setSetting(LANGUAGE_LEVEL, DrJavaRoot.FULL_JAVA);\n" +
      "  }});\n" +
      "group.add(rbMenuItem);\n";
    _doc.insertString(0, text, null);
    _assertContents(text, _doc);


    safeIndentLines(0, _doc.getLength());


    _assertContents(indented, _doc);
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
    
    setDocText(_doc, text);
    
    _assertContents(text, _doc);
    safeIndentLines(0, _doc.getLength());
    Utilities.clearEventQueue();
    Utilities.clearEventQueue();
    _assertContents(indentedBefore, _doc);

    setConfigSetting(OptionConstants.INDENT_LEVEL, 8);
    
    Utilities.clearEventQueue();
    Utilities.clearEventQueue();


    safeIndentLines(0, _doc.getLength());
        
    Utilities.clearEventQueue();
    Utilities.clearEventQueue();

    _assertContents(indentedAfter, _doc);
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
    
    _doc.insertString(0, text, null);
    _assertContents(text, _doc);
    safeIndentLines(0, _doc.getLength());
    _assertContents(indented, _doc);
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
    
    _doc.insertString(0, test1, null);
    _assertContents(test1, _doc);
    _doc.setCurrentLocation(20);
    safeIndentLines(20,20);
    _assertContents(test1, _doc);
    
    _doc = new DefinitionsDocument(_notifier);
    
    _doc.insertString(0, test1, null);
    _assertContents(test1, _doc);
    safeIndentLines(28,28);



    _assertContents(test1Correct, _doc);
    
    _doc = new DefinitionsDocument(_notifier);
    
    _doc.insertString(0, test2, null);
    _assertContents(test2, _doc);
    _doc.setCurrentLocation(5);
    safeIndentLines(5,5);
    _assertContents(test2Correct, _doc);
  }
  
  
  public void testAnnotationsAfterOpenCurly() throws BadLocationException {
    String textToIndent =
      "@Annotation\n" +
      "public class TestClass {\n" +
      "public TestClass() {}\n" +
      "\n" +
      "@Annotation(WithParens)\n" +
      "private int _classField = 42;\n" +
      "\n" +
      "@Override\n" +
      "public String toString() {\n" +
      "@LocalVariableAnnotation\n" +
      "String msg = \"hello\";\n" +
      "return msg;\n" +
      "}\n" +
      "\n" +
      "public int methodAfterAnnotation() {\n" +
      "return 0;\n" +
      "}\n" +
      "}\n" +
      "\n";
    String textIndented = 
      "@Annotation\n" +
      "public class TestClass {\n" +
      "  public TestClass() {}\n" +
      "  \n" +
      "  @Annotation(WithParens)\n" +
      "  private int _classField = 42;\n" +
      "  \n" +
      "  @Override\n" +
      "  public String toString() {\n" +
      "    @LocalVariableAnnotation\n" +
      "    String msg = \"hello\";\n" +
      "    return msg;\n" +
      "  }\n" +
      "  \n" +
      "  public int methodAfterAnnotation() {\n" +
      "    return 0;\n" +
      "  }\n" +
      "}\n" +
      "\n";
    
    _doc.insertString(0, textToIndent, null);
    _assertContents(textToIndent, _doc);
    safeIndentLines(0, _doc.getLength());
    _assertContents(textIndented, _doc);
  }
  
  
  public void testAnnotationsAfterDefinition() throws BadLocationException {
    String textToIndent =
      "@Annotation\n" +
      "public class TestClass {\n" +
      "public TestClass() {}\n" +
      "\n" +
      "private int _classField = 0;\n" +
      "\n" +
      "@Annotation(WithParens)\n" +
      "private int _classField2 = 42;\n" +
      "\n" +
      "@Override\n" +
      "public String toString() {\n" +
      "@LocalVariableAnnotation\n" +
      "String msg = \"hello\";\n" +
      "return msg;\n" +
      "}\n" +
      "\n" +
      "public int methodAfterAnnotation() {\n" +
      "return 0;\n" +
      "}\n" +
      "}\n";
    String textIndented = 
      "@Annotation\n" +
      "public class TestClass {\n" +
      "  public TestClass() {}\n" +
      "  \n" +
      "  private int _classField = 0;\n" +
      "  \n" +
      "  @Annotation(WithParens)\n" +
      "  private int _classField2 = 42;\n" +
      "  \n" +
      "  @Override\n" +
      "  public String toString() {\n" +
      "    @LocalVariableAnnotation\n" +
      "    String msg = \"hello\";\n" +
      "    return msg;\n" +
      "  }\n" +
      "  \n" +
      "  public int methodAfterAnnotation() {\n" +
      "    return 0;\n" +
      "  }\n" +
      "}\n";
    
    _doc.insertString(0, textToIndent, null);
    _assertContents(textToIndent, _doc);
    safeIndentLines(0, _doc.getLength());
    _assertContents(textIndented, _doc);
  }
  



































  
  private void _assertContents(String expected, DJDocument document) throws BadLocationException {
    assertEquals("document contents", expected, document.getText());
  }
  
  private void _assertLineBraceInfo(int distance, String braceType) {
    BraceInfo info = _doc._getLineEnclosingBrace();

    assertEquals("line brace info: brace distance", distance, info.distance());
    assertEquals("line brace info: brace type", braceType, info.braceType());
  }
  
  private void _assertBraceInfo(int distance, String braceType) {
    BraceInfo info = _doc._getEnclosingBrace();
    assertEquals("line brace info: brace distance", distance, info.distance());
    assertEquals("line brace info: brace type", braceType, info.braceType());
  }







  






















  
























  
  
  public void testNoParameters() throws BadLocationException {
    
    
    String _text =
      "method(\n"+
      ")\n";
    
    String _aligned =
      "method(\n"+
      ")\n";
    
    _doc.insertString(0, _text, null);
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    safeIndentLines(0, 7); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    
    safeIndentLines(0, _doc.getLength()); 
    


    _assertContents(_aligned, _doc);
    assertEquals("Line aligned to open paren.", _aligned.length(), _doc.getLength());
  }
  
  






















  
  






















  
  






















  
  






















  
}
