



package edu.rice.cs.drjava.model.definitions.indent;


public class ActionStartPrevLinePlusTest extends IndentRulesTestCase {
  
  
  private IndentRuleAction makeAction(String suffix) {
    return new ActionStartPrevLinePlus(suffix);
  }

  public void testLeaveBe() throws javax.swing.text.BadLocationException {
    _setDocText("foo\nbar");
    _doc.setCurrentLocation(4);
    makeAction("").indentLine(_doc, Indenter.OTHER);
    assertEquals(7, _doc.getLength());
    assertEquals("foo\nbar", _doc.getText());
  }
  public void testLeaveBeMidLine() throws javax.swing.text.BadLocationException {
    _setDocText("foo\nbar");
    _doc.setCurrentLocation(6);
    makeAction("").indentLine(_doc, Indenter.OTHER);
    assertEquals(7, _doc.getLength());
    assertEquals("foo\nbar", _doc.getText());
  }
  public void testAddSpaces() throws javax.swing.text.BadLocationException {
    _setDocText("foo\nbar");
    _doc.setCurrentLocation(4);
    makeAction("   ").indentLine(_doc, Indenter.OTHER);  
    assertEquals(10, _doc.getLength());
    assertEquals("foo\n   bar", _doc.getText());
  }
  public void testAddSpacesMidLine() throws javax.swing.text.BadLocationException {
    _setDocText("foo\nbar");
    _doc.setCurrentLocation(6);
    makeAction("   ").indentLine(_doc, Indenter.OTHER);  
    assertEquals(10, _doc.getLength());
    assertEquals("foo\n   bar", _doc.getText());
  }
  public void testBothIndented() throws javax.swing.text.BadLocationException {
    _setDocText("  foo\n  bar");
    _doc.setCurrentLocation(9);
    makeAction("").indentLine(_doc, Indenter.OTHER);
    assertEquals(11, _doc.getLength());
    assertEquals("  foo\n  bar", _doc.getText());
  }
  public void testBothIndentedAddSpaces() throws javax.swing.text.BadLocationException {
    _setDocText("  foo\n  bar");
    _doc.setCurrentLocation(9);
    makeAction("   ").indentLine(_doc, Indenter.OTHER);
    assertEquals(14, _doc.getLength());
    assertEquals("  foo\n     bar", _doc.getText());
  }
  public void testBothIndentedAddStuff() throws javax.swing.text.BadLocationException {
    _setDocText("  foo\n  bar");
    _doc.setCurrentLocation(9);
    makeAction("abc").indentLine(_doc, Indenter.OTHER);
    assertEquals(14, _doc.getLength());
    assertEquals("  foo\n  abcbar", _doc.getText());
  }
  public void testSecondLineMisindented() throws javax.swing.text.BadLocationException {
    _setDocText("  foo\n bar");
    _doc.setCurrentLocation(9);
    makeAction("abc").indentLine(_doc, Indenter.OTHER);
    assertEquals(14, _doc.getLength());
    assertEquals("  foo\n  abcbar", _doc.getText());
  }
  public void testLeavesOtherLinesAlone() throws javax.swing.text.BadLocationException {
    _setDocText("foo\nbar\nblah");
    _doc.setCurrentLocation(10);
    makeAction("   ").indentLine(_doc, Indenter.OTHER);  
    assertEquals(15, _doc.getLength());
    assertEquals("foo\nbar\n   blah", _doc.getText());
  }
  public void testOtherLinesIndented() throws javax.swing.text.BadLocationException {
    _setDocText(" foo\n  bar\n   blah");
    _doc.setCurrentLocation(15);
    makeAction("   ").indentLine(_doc, Indenter.OTHER);  
    assertEquals(20, _doc.getLength());
    assertEquals(" foo\n  bar\n     blah", _doc.getText());
  }
}
