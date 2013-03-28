

package edu.rice.cs.util.sexp;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.io.*;
import java.util.List;


public class SExpParserTest extends DrJavaTestCase {
  
  
  private File _fillTempFile(String fname, String text) {
    File f = null;
    try {
      f = File.createTempFile(fname, null).getCanonicalFile();
      FileWriter fw = new FileWriter(f);
      fw.write(text, 0, text.length());
      fw.close();
    }
    catch (IOException e) {
      throw new RuntimeException("IOException thrown while writing to temp file");
    }
    return f;
  }
  
  
  public void testDifferentInputs() throws SExpParseException, IOException{
    String text = "()";
    File f = _fillTempFile("temp1",text);
    char[] ca = new char[text.length()];
    text.getChars(0, text.length(), ca, 0);
    Reader r = new CharArrayReader(ca);
    
    SExp sa1 = SExpParser.parse(text).get(0);
    SExp sa2 = SExpParser.parse(f).get(0);
    SExp sa3 = SExpParser.parse(r).get(0);
    
    SExp ans = Empty.ONLY;
    
    assertEquals("the 1st parse wasn't right", ans, sa1);
    assertEquals("the 2nd parse wasn't right", ans, sa2);
    assertEquals("the 3rd parse wasn't right", ans, sa3);
  }
  
  
  public void testParseMultiple() throws SExpParseException{
    String text = "(abcdefg)(hijklmnop)";
    List<? extends SExp> exps = SExpParser.parse(text);
    SExp exp1 = exps.get(0);
    SExp exp2 = exps.get(1);
    
    
    
    final SExpVisitor<String> innerVisitor = new SExpVisitor<String>() {
      private String _failMe(String who) {
        fail("The inside was " +  who  + " but should have been text");
        return "";
      }
      public String forEmpty(Empty e){ return _failMe("an empty list"); }
      public String forCons(Cons c){ return _failMe("an empty list"); }
      public String forBoolAtom(BoolAtom b){ return _failMe("a boolean"); }
      public String forNumberAtom(NumberAtom n) { return _failMe("a number"); }
      public String forTextAtom(TextAtom t) { return t.getText(); }
    };
    
    final SExpVisitor<String> outerVisitor = new SExpVisitor<String>() {
      private String _failMe(String who) {
        fail("The top-level was " +  who  + " but should have been a cons");
        return "";
      }
      public String forEmpty(Empty e){ return _failMe("an empty list"); }
      public String forCons(Cons c){ return c.getFirst().accept(innerVisitor); }
      public String forBoolAtom(BoolAtom b){ return _failMe("a boolean"); }
      public String forNumberAtom(NumberAtom n) { return _failMe("a number"); }
      public String forTextAtom(TextAtom t) { return _failMe("text"); }
    };
    
    assertEquals("wrong text in 1st s-expression", "abcdefg",  exp1.accept(outerVisitor));
    assertEquals("wrong text in 2nd s-expression", "hijklmnop",exp2.accept(outerVisitor));
  }
  
  public void testTopLevel() throws SExpParseException {
    
    String text = "true";
    try {
      SExpParser.parse(text).get(0);
      fail("Didn't throw a parse exception");
    }catch(SExpParseException e) {
      assertEquals("Incorrect exception message", 
                   "A top-level s-expression must be a list. " + 
                   "Invalid start of list: true",
                   e.getMessage());
    }
    text = "123 ((help) me)";
    try {
      SExpParser.parse(text).get(0);
      fail("Didn't throw a parse exception");
    }catch(SExpParseException e) {
      assertEquals("Incorrect exception message", 
                   "A top-level s-expression must be a list. " + 
                   "Invalid start of list: 123",
                   e.getMessage());
    }
    text = "[help me]"; 
    try {
      SExpParser.parse(text).get(0);
      fail("Didn't throw a parse exception");
    }catch(SExpParseException e) {
      assertEquals("Incorrect exception message", 
                   "A top-level s-expression must be a list. " + 
                   "Invalid start of list: [help",
                   e.getMessage());
    }
  }
  
  public void testInvalidLowerLevel() {
    
    String text = "(abcdefg";
    try {
      SExpParser.parse(text).get(0);
      fail("Didn't throw a parse exception");
    }catch(SExpParseException e) {
      assertEquals("Incorrect exception message", 
                   "Unexpected <EOF> at line 1",
                   e.getMessage());
    }
    
    text = "(ab\ncdefg";
    try {
      SExpParser.parse(text).get(0);
      fail("Didn't throw a parse exception");
    }catch(SExpParseException e) {
      assertEquals("Incorrect exception message", 
                   "Unexpected <EOF> at line 2",
                   e.getMessage());
    }
    
    text = "(ab\ncdefg))";
    try {
      SExpParser.parse(text).get(0);
      fail("Didn't throw a parse exception");
    }catch(SExpParseException e) {
      assertEquals("Incorrect exception message", 
                   "A top-level s-expression must be a list. " + 
                   "Invalid start of list: )",
                   e.getMessage());
    }
    
    text = "(\")";  
    try {
      SExpParser.parse(text).get(0);
      fail("Didn't throw a parse exception");
    }catch(SExpParseException e) {
      assertEquals("Incorrect exception message", 
                   "Unexpected <EOF> at line 1",
                   e.getMessage());
    }
    
    
    text = "(;)";  
    try {
      SExpParser.parse(text).get(0);
      fail("Didn't throw a parse exception");
    }catch(SExpParseException e) {
      assertEquals("Incorrect exception message", 
                   "Unexpected <EOF> at line 1",
                   e.getMessage());
    }
  }
  
  public void testCorrectParse() throws SExpParseException {
    String n = "\n";
    String text = 
      "; this is a comment line                      " + n +
      "; this is another comment line                " + n +
      "(Source                                       " + n +
      "  (/sexp/Atom.java)                           " + n +
      "  (/sexp/Cons.java)                           " + n +
      "  (/sexp/Empty.java)                          " + n +
      "  (/sexp/Lexer.java)                          " + n +
      "  (/sexp/SExp.java)                           " + n +
      "  (/sexp/SExpParser.java)                     " + n +
      "  (/sexp/SExpVisitor.java)                    " + n +
      "  (/sexp/Tokens.java)                         " + n +
      ")                                             " + n +
      "; This is the build directory.  Absolute path " + n +
      "(BuildDir \"/home/javaplt/drjava/built\")     " + n +
      "(MainFile \"/sexp/SExpParser.java\")          " + n +
      "(Included                                     " + n +
      ")";
    
    List<SEList> res = SExpParser.parse(text);
    assertEquals("Should have four trees in forest", 4, res.size());
  }
}
