

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class ActionBracePlusTest extends IndentRulesTestCase {
  private String _text, _aligned;
  
  private IndentRuleAction _action;
  
  public void testNoSuffix() throws BadLocationException {
    _action = new ActionBracePlus(0);
    
    
    
    _text = 
      "method(\n" + 
      ")\n";

    _aligned = 
      "method(\n" + 
      "      )\n";
 
    _setDocText(_text);
    _action.testIndentLine(_doc, 0, Indenter.IndentReason.OTHER); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    _action.testIndentLine(_doc, 7, Indenter.IndentReason.OTHER); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    _action.testIndentLine(_doc, 8, Indenter.IndentReason.OTHER); 
    assertEquals("Line aligned to open paren.", _aligned.length(), _doc.getLength());
    assertEquals("Line aligned to open paren.", _aligned, _doc.getText());
  }
  

  public void testSpaceSuffix() throws BadLocationException
  {
    _action = new ActionBracePlus(1);
    
    
    
    _text = 
     "var = method(arg1,\n" + 
     "  arg2, arg3) + 4;";

    _aligned = 
     "var = method(arg1,\n" + 
     "             arg2, arg3) + 4;";
 
    _setDocText(_text);
    _action.testIndentLine(_doc, 0, Indenter.IndentReason.OTHER); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    _action.testIndentLine(_doc, 18, Indenter.IndentReason.OTHER); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    _action.testIndentLine(_doc, 20, Indenter.IndentReason.OTHER); 
    assertEquals("Line aligned to open paren.", _aligned.length(), _doc.getLength());
    assertEquals("Line aligned to open paren.", _aligned, _doc.getText());
    
    
     
    _text =
     "boolean method(\n" + 
     "int[] a, String b)\n" + 
     "{}";
    _aligned = 
     "boolean method(\n" + 
     "               int[] a, String b)\n" + 
     "{}";

    _setDocText(_text);
    _action.testIndentLine(_doc, 0, Indenter.IndentReason.OTHER); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    _action.testIndentLine(_doc, 15, Indenter.IndentReason.OTHER); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    _action.testIndentLine(_doc, 16, Indenter.IndentReason.OTHER); 
    assertEquals("Line aligned to open paren.", _aligned.length(), _doc.getLength());
    assertEquals("Line aligned to open paren.", _aligned, _doc.getText());
 
    
 
    _text =
     "boolean method(\n" + 
     "int[] a,\n" + 
     "               String b)\n" + 
     "{}";
    _aligned = 
     "boolean method(\n" + 
     "               int[] a,\n" + 
     "               String b)\n" + 
     "{}";

    _setDocText(_text);
    _action.testIndentLine(_doc, 0, Indenter.IndentReason.OTHER); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    _action.testIndentLine(_doc, 15, Indenter.IndentReason.OTHER); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    _action.testIndentLine(_doc, 20, Indenter.IndentReason.OTHER); 
    assertEquals("Line aligned to open paren.", _aligned, _doc.getText());
 
    

    _text =
     "array[\n" + 
     "              new Listener() {\n" + 
     "           method() {\n" + 
     "           }\n" + 
     "      }]";
    _aligned =
     "array[\n" + 
     "      new Listener() {\n" + 
     "           method() {\n" + 
     "           }\n" + 
     "      }]";

    _setDocText(_text);
    _action.testIndentLine(_doc, 0, Indenter.IndentReason.OTHER); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    _action.testIndentLine(_doc, 6, Indenter.IndentReason.OTHER); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    _action.testIndentLine(_doc, 10, Indenter.IndentReason.OTHER); 
    assertEquals("Line aligned to open bracket.", _aligned, _doc.getText()); 

  }
  
  public void testLargeSuffix() throws BadLocationException
  {
    _action = new ActionBracePlus(3);
    
    
    
    _text = 
     "var = method(foo.\n" + 
     "  bar(), arg3) + 4;";

    _aligned = 
     "var = method(foo.\n" + 
     "               bar(), arg3) + 4;";
 
    _setDocText(_text);
    _action.testIndentLine(_doc, 0, Indenter.IndentReason.OTHER); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    _action.testIndentLine(_doc, 17, Indenter.IndentReason.OTHER); 
    assertEquals("START has no brace.", _text.length(), _doc.getLength());
    _action.testIndentLine(_doc, 25, Indenter.IndentReason.OTHER); 
    assertEquals("Line aligned to open paren.", _aligned.length(), _doc.getLength());
    assertEquals("Line aligned to open paren.", _aligned, _doc.getText());
  }
  
  public void testComment() throws BadLocationException
  {
    _action = new ActionBracePlus(3);
    
    
    
    _text = 
      "foo(i,\n" + 
      "    j.\n" +
      "bar().\n" +
      "// bar();\n" +
      "baz(),\n" +
      "    k);";

    _aligned = 
      "foo(i,\n" + 
      "    j.\n" +
      "      bar().\n" +
      "      // bar();\n" +
      "      baz(),\n" +
      "    k);";
 
    _setDocText(_text);
    _action.testIndentLine(_doc, 14, Indenter.IndentReason.OTHER); 
    _action.testIndentLine(_doc, 27, Indenter.IndentReason.OTHER); 
    _action.testIndentLine(_doc, 43, Indenter.IndentReason.OTHER); 
    assertEquals("Lines aligned plus one level.",
                 _aligned, _doc.getText());
    
    _action.testIndentLine(_doc, 54, Indenter.IndentReason.OTHER); 
    assertEquals("Cursor after baz().", _aligned, _doc.getText());
  }
}
