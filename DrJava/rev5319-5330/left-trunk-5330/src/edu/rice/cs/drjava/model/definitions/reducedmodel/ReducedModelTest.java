

package edu.rice.cs.drjava.model.definitions.reducedmodel;


public final class ReducedModelTest extends BraceReductionTestCase implements ReducedModelStates {
  
  public void testBalanceBackward() {
    assertEquals("#0.0", -1, model0.balanceBackward());
    model0 = setUpExample();
    
    
    
    
    
    
    assertEquals("#1.0", 36, model0.balanceBackward());
    assertFalse(model0._rmb.openBraceImmediatelyLeft());
    assertTrue(model0._rmb.closedBraceImmediatelyLeft());
    model0.move(-2);
    assertEquals("#2.0", -1, model0.balanceBackward());
    model0.move(-14);
    assertEquals("#3.0", -1, model0.balanceBackward());
    model0.move(-10);
    assertEquals("#4.0", 4, model0.balanceBackward());
    model0.move(-10);
    assertEquals("#5.0", -1, model0.balanceBackward());
    model1.insertChar(')');
    assertEquals("#6.0", -1, model1.balanceBackward());
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertTrue(model1._rmb.closedBraceImmediatelyLeft());
    model1.move(-1);
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    model1.insertChar('{');
    assertTrue(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    model1.move(1);
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertTrue(model1._rmb.closedBraceImmediatelyLeft());
    assertEquals("#7.0", -1, model1.balanceBackward());
  }
  
  
  public void testInsertGap() {
    insertGap(model1, 4);
    model1.move(-4);
    
    assertTrue("#0.0", model1.currentToken().isGap());
    assertEquals("#0.2", 4, model1.currentToken().getSize());
    model1.move(4);
    
    insertGap(model2, 5);
    model2.move(-5);
    
    assertTrue("#1.0", model2.currentToken().isGap());
    assertEquals("#1.2", 5, model2.currentToken().getSize());
  }

  
  public void testInsertGapBeforeGap() {
    insertGap(model1, 3);
    assertTrue("#0.0.0", model1.atEnd());
    model1.move(-3);
    insertGap(model1, 3);
    
    assertTrue("#0.0", model1.currentToken().isGap());
    assertEquals("#0.1", 3, model1.absOffset());
    assertEquals("#0.2", 6, model1.currentToken().getSize());
    model1.move(-3);
    insertGap(model1, 2);
    assertTrue("#1.0", model1.currentToken().isGap());
    assertEquals("#1.1", 2, model1.absOffset());
    assertEquals("#1.2", 8, model1.currentToken().getSize());
  }

  
  public void testInsertGapAfterGap() {
    insertGap(model1, 3);
    assertTrue("#0.0", model1.atEnd());
    model1.move(-3);
    assertTrue("#0.1", model1.currentToken().isGap());
    assertEquals("#0.2", 3, model1.currentToken().getSize());
    insertGap(model1, 4);
    assertTrue("#1.1", model1.currentToken().isGap());
    assertEquals("#1.2", 7, model1.currentToken().getSize());
  }

  
  public void testInsertGapInsideGap() {
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    insertGap(model1, 3);
    assertTrue("#0.0", model1.atEnd());
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    model1.move(-3);
    assertTrue("#0.1", model1.currentToken().isGap());
    assertEquals("#0.2", 3, model1.currentToken().getSize());
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    insertGap(model1, 3);
    assertTrue("#1.1", model1.currentToken().isGap());
    assertEquals("#1.2", 6, model1.currentToken().getSize());
    assertEquals("#1.3", 3, model1.absOffset());
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    insertGap(model1, 4);
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    assertTrue("#1.1", model1.currentToken().isGap());
    assertEquals("#1.2", 10, model1.currentToken().getSize());
    assertEquals("#1.3", 7, model1._offset);
  }

  
  public void testInsertBraceAtStartAndEnd() {
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    model1.insertChar('(');
    assertTrue("#0.0", model1.atEnd());
    assertTrue(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    model1.move(-1);
    assertEquals("#0.1", "(", model1.currentToken().getType());
    assertEquals("#0.2", 1, model1.currentToken().getSize());
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    assertFalse(model2._rmb.openBraceImmediatelyLeft());
    assertFalse(model2._rmb.closedBraceImmediatelyLeft());
    model2.insertChar(')');
    assertTrue("#1.0", model2.atEnd());
    assertTrue(model2._rmb.closedBraceImmediatelyLeft());
    assertFalse(model2._rmb.openBraceImmediatelyLeft());
    model2.move(-1);
    assertEquals("#1.1", ")", model2.currentToken().getType());
    assertEquals("#1.2", 1, model2.currentToken().getSize());
    assertFalse(model2._rmb.openBraceImmediatelyLeft());
    assertFalse(model2._rmb.closedBraceImmediatelyLeft());
  }

  
  public void testInsertBraceInsideGap() {
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    insertGap(model1, 4);
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    model1.move(-4);
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    insertGap(model1, 3);
    assertEquals("#0.0", 3, model1.absOffset());
    assertEquals("#0.1", 7, model1.currentToken().getSize());
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    model1.insertChar('{');
    assertTrue(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
    assertEquals("#1.0", 4, model1.absOffset());
    assertEquals("#1.1", 4, model1.currentToken().getSize());
    assertTrue("#1.2", model1.currentToken().isGap());
    model1.move(-1);
    assertEquals("#2.0", 1, model1.currentToken().getSize());
    assertEquals("#2.1", "{", model1.currentToken().getType());
    model1.move(-3);
    assertEquals("#3.0", 0, model1.absOffset());
    assertEquals("#3.1", 3, model1.currentToken().getSize());
    assertTrue("#3.2", model1.currentToken().isGap());
    assertFalse(model1._rmb.openBraceImmediatelyLeft());
    assertFalse(model1._rmb.closedBraceImmediatelyLeft());
  }

  
  public void testInsertBrace() {
    model1.insertChar('{');
    assertTrue("#0.0", model1.atEnd());
    model1.move(-1);
    assertEquals("#1.0", 1, model1.currentToken().getSize());
    assertEquals("#1.1", "{", model1.currentToken().getType());
    model1.insertChar('(');
    model1.insertChar('[');
    assertEquals("#2.0", 1, model1.currentToken().getSize());
    assertEquals("#2.1", "{", model1.currentToken().getType());
    model1.move(-1);
    assertEquals("#3.0", 1, model1.currentToken().getSize());
    assertEquals("#3.1", "[", model1.currentToken().getType());
    model1.move(-1);
    assertEquals("#3.0", 1, model1.currentToken().getSize());
    assertEquals("#3.1", "(", model1.currentToken().getType());
  }

  
  public void testInsertBraceAndBreakLineComment() {
    model1.insertChar('/');
    model1.insertChar('/');
    model1.move(-1);
    assertEquals("#0.0", 2, model1.currentToken().getSize());
    
    model1.insertChar('{');
    assertEquals("#1.0", "/", model1.currentToken().getType());
    assertEquals("#1.1", 1, model1.currentToken().getSize());
    model1.move(-1);
    assertEquals("#2.0", "{", model1.currentToken().getType());
    assertEquals("#2.1", 1, model1.currentToken().getSize());
    model1.move(-1);
    assertEquals("#3.0", "/", model1.currentToken().getType());
    assertEquals("#3.1", 1, model1.currentToken().getSize());
  }

  
  public void testInsertBraceAndBreakBlockCommentStart() {
    model1.insertChar('/');
    model1.insertChar('*');
    model1.move(-2);
    assertEquals("#0.0", 2, model1.currentToken().getSize());
    model1.move(1);
    model1.insertChar('{');
    assertEquals("#1.0", "*", model1.currentToken().getType());
    assertEquals("#1.1", 1, model1.currentToken().getSize());
    model1.move(-1);
    assertEquals("#2.0", "{", model1.currentToken().getType());
    assertEquals("#2.1", 1, model1.currentToken().getSize());
    model1.move(-1);
    assertEquals("#3.0", "/", model1.currentToken().getType());
    assertEquals("#3.1", 1, model1.currentToken().getSize());
  }

  
  public void testInsertMultipleBraces() {
    model1.insertChar('/');
    model1.insertChar('*');
    model1.insertChar('{');
    model1.move(-1);
    
    assertEquals("#0.0", INSIDE_BLOCK_COMMENT, model1.getStateAtCurrent());
    model1.move(-2);
    assertEquals("#0.1", FREE, model1.currentToken().getState());
    model1.move(3);
    model1.insertChar('*');
    model1.insertChar('/');
    
    model1.move(-2);
    assertEquals("#1.0", FREE, model1.currentToken().getState());
    model1.move(1);
    model1.insertChar('{');
    model1.move(0);
    
    model1.move(-1);
    assertEquals("#2.0", INSIDE_BLOCK_COMMENT, model1.currentToken().getState());
    assertEquals("#2.1", INSIDE_BLOCK_COMMENT, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#2.2", "/", model1.currentToken().getType());
  }

  
  public void testComplexBraceInsertion() {
    model1.insertChar('\n');
    model1.insertChar('\n');
    model1.move(-1);
    
    assertEquals("#0.0", false, model1.atEnd());
    model1.insertChar('{');
    model1.insertChar('\n');
    model1.insertChar('\n');
    model1.insertChar('}');
    model1.move(-2);
    
    assertEquals("#0.1", FREE, model1.currentToken().getState());
    model1.insertChar('{');
    model1.insertChar('{');
    model1.insertChar('}');
    model1.insertChar('}');
    
    assertEquals("#1.0", 4, model1.balanceBackward());
    model1.move(-1);
    assertEquals("#1.1", 2, model1.balanceBackward());
  }

  
  public void testCrazyCase1() {
    model1.insertChar('/');
    insertGap(model1, 4);
    model1.insertChar('*');
    model1.insertChar('/');
    
    model1.move(-1);
    assertEquals("#0.0", "/", model1.currentToken().getType());
    model1.move(-1);
    assertEquals("#0.1", "*", model1.currentToken().getType());
    
    model1.move(1);
    model1.insertChar('/');
    
    assertEquals("#1.0", 2, model1.currentToken().getSize());
    model1.move(-2);
    
    assertEquals("#1.0", "*", model1.currentToken().getType());
    model1.move(-4);
    model1.insertChar('*');
    
    model1.move(-2);
    assertEquals("#2.0", "/*", model1.currentToken().getType());
    assertEquals("#2.1", FREE, model1.currentToken().getState());
    model1.move(6);
    
    assertEquals("#2.2", "*/", model1.currentToken().getType());
    assertEquals("#2.3", FREE, model1.currentToken().getState());
    assertEquals("#2.4", INSIDE_BLOCK_COMMENT, model1.getStateAtCurrent());
    
  }

  
  public void testCrazyCase2() {
    model1.insertChar('/');
    insertGap(model1, 4);
    model1.move(-2);
    model1.insertChar('/');
    model1.move(0);
    model1.move(-3);
    
    assertEquals("#0.0", 2, model1.currentToken().getSize());
    assertEquals("#0.3", FREE, model1.getStateAtCurrent());
    model1.move(2);
    assertEquals("#0.2", 1, model1.currentToken().getSize());
    assertEquals("#0.1", "/", model1.currentToken().getType());
    model1.move(-2);
    model1.insertChar('/');
    model1.move(-2);
    assertEquals("#1.1", "//", model1.currentToken().getType());
    assertEquals("#1.3", FREE, model1.currentToken().getState());
    model1.move(2);
    
    assertEquals("#1.2", INSIDE_LINE_COMMENT, model1.getStateAtCurrent());
    assertEquals("1.4", INSIDE_LINE_COMMENT, model1.currentToken().getState());
    model1.move(2);
    assertEquals("1.5", INSIDE_LINE_COMMENT, model1.currentToken().getState());
    model1.move(-2);
  }

  
  public void testLineCommentBreakCrazy() {
    model1.insertChar('/');
    model1.insertChar('/');
    insertGap(model1, 4);
    model1.move(-2);
    model1.insertChar('/');
    
    
    model1.move(-4);
    model1.insertChar('/');
    model1.move(0);
    
    model1.move(-2);
    assertEquals("#2.0", "//", model1.currentToken().getType());
    assertEquals("#2.3", FREE, model1.currentToken().getState());
    model1.move(2);
    assertEquals("#2.1", "/", model1.currentToken().getType());
    assertEquals("#2.2", INSIDE_LINE_COMMENT, model1.getStateAtCurrent());
    assertEquals("2.4", INSIDE_LINE_COMMENT, model1.currentToken().getState());
    model1.move(1);
    assertEquals("2.5", INSIDE_LINE_COMMENT, model1.currentToken().getState());
    
    model1.move(-2);
    model1.insertChar('*');                     
    model1.move(0);
    
    model1.move(-2);
    assertEquals("#3.0", "/*", model1.currentToken().getType());
    assertEquals("#3.3", FREE, model1.currentToken().getState());
    model1.move(2);
    assertEquals("#3.1", "/", model1.currentToken().getType());
    assertEquals("#3.3", INSIDE_BLOCK_COMMENT, model1.getStateAtCurrent());
    assertEquals("3.4", INSIDE_BLOCK_COMMENT, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#3.2", "/", model1.currentToken().getType());
    assertEquals("3.5", INSIDE_BLOCK_COMMENT, model1.currentToken().getState());
  }

  
  public void testBreakBlockCommentWithStar() {
    
    model1.insertChar('/');
    model1.insertChar('*');
    model1.insertChar('/');
    model1.insertChar('/');
    insertGap(model1, 2);
    model1.insertChar('/');
    insertGap(model1, 2);
    
    model1.move(-8);
    model1.insertChar('*');
    
    model1.move(-2);
    assertEquals("#4.0", "/*", model1.currentToken().getType());
    assertEquals("#4.3", FREE, model1.currentToken().getState());
    model1.move(2);
    assertEquals("#4.1", "*/", model1.currentToken().getType());
    assertEquals("#4.3", INSIDE_BLOCK_COMMENT, model1.getStateAtCurrent());
    assertEquals("4.4", FREE, model1.currentToken().getState());
    model1.move(2);
    assertEquals("#4.2", "/", model1.currentToken().getType());
    assertEquals("4.5", FREE, model1.currentToken().getState());
  }

  
  public void testBreakCloseBlockCommentWithStar() {
    model1.insertChar('/');
    model1.insertChar('*');
    model1.insertChar('*');
    model1.insertChar('/');
    model1.insertChar('/');
    insertGap(model1, 2);
    model1.insertChar('/');
    insertGap(model1, 2);
    model1.move(-7);
    insertGap(model1, 3);
    
    model1.move(-3);
    assertEquals("#5.0", true, model1.currentToken().isGap());
    assertEquals("#5.4", INSIDE_BLOCK_COMMENT, model1.currentToken().getState());
    model1.move(3);
    assertEquals("#5.1", INSIDE_BLOCK_COMMENT, model1.getStateAtCurrent());
    assertEquals("#5.2", "/", model1.currentToken().getType());
    assertEquals("5.5", INSIDE_BLOCK_COMMENT, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#5.3", "/", model1.currentToken().getType());
    assertEquals("5.6", INSIDE_BLOCK_COMMENT, model1.currentToken().getState());
  }

  
  public void testBasicBlockComment() {
    model1.insertChar('/');
    model1.insertChar('*');
    model1.insertChar('*');
    model1.insertChar('/');
    model1.move(-4);
    assertEquals("0.1", FREE, model1.currentToken().getState());
    assertEquals("0.2", "/*", model1.currentToken().getType());
    model1.move(2);
    assertEquals("0.3", FREE, model1.currentToken().getState());
    assertEquals("0.4", "*/", model1.currentToken().getType());
    model1.insertChar('/');
    model1.move(-1);
    assertEquals("1.1", INSIDE_BLOCK_COMMENT, model1.currentToken().getState());
    assertEquals("1.3", "/", model1.currentToken().getType());
    model1.move(1);
    assertEquals("1.0", FREE, model1.currentToken().getState());
    assertEquals("1.2", "*/", model1.currentToken().getType());
  }

  
  public void testInsertBlockInsideBlockComment() {
    model1.insertChar('/');
    model1.insertChar('*');
    model1.insertChar('/');
    model1.insertChar('*');
    model1.insertChar('/');
    
    model1.move(-2);
    model1.insertChar('*');
    
    model1.move(-1);
    assertEquals("1.1", INSIDE_BLOCK_COMMENT, model1.currentToken().getState());
    assertEquals("1.3", "*", model1.currentToken().getType());
    model1.move(1);
    assertEquals("1.0", FREE, model1.currentToken().getState());
    assertEquals("1.2", "*/", model1.currentToken().getType());
  }

  
  public void testInsertBlockCommentEnd() { 
    model1.insertChar('*');
    model1.insertChar('/');
    model1.move(-1);
    assertEquals("#3.0", "/", model1.currentToken().getType());
    assertEquals("#3.1", 1, model1.currentToken().getSize());
  }

  
  public void testGetStateAtCurrent() {
    assertEquals("#0.0", FREE, model1.getStateAtCurrent());
    assertEquals("#0.1", FREE, model1.getStateAtCurrent());
    model1.insertChar('(');
    model1.move(-1);
    assertEquals("#1.0", FREE, model1.currentToken().getState());
    model1.move(1);
    model1.insertChar('/');
    model1.insertChar('/');
    model1.move(-2);
    assertEquals("#2.0", FREE, model1.currentToken().getState());
    model1.move(2);
    assertEquals("#2.1", INSIDE_LINE_COMMENT, model1.getStateAtCurrent());
    
    model1.move(-3);
    model1.insertChar('/');
    model1.insertChar('/');
    
    model1.move(-2);
    assertEquals("#3.0", FREE, model1.currentToken().getState());
    model1.move(2);
    assertEquals("#3.1", INSIDE_LINE_COMMENT, model1.getStateAtCurrent());
    assertEquals("#3.2", INSIDE_LINE_COMMENT, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#3.3", INSIDE_LINE_COMMENT, model1.currentToken().getState());
    assertEquals("#3.4", "/", model1.currentToken().getType());
    model1.move(1);
    assertEquals("#4.1", INSIDE_LINE_COMMENT, model1.currentToken().getState());
    assertEquals("#4.2", "/", model1.currentToken().getType());
  }

  
  public void testQuotesSimple() {
    model1.insertChar('\"');
    model1.insertChar('\"');
    model1.move(-2);
    assertEquals("#0.0", "\"", model1.currentToken().getType());
    assertEquals("#0.3", FREE, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#0.1", "\"", model1.currentToken().getType());
    assertEquals("#0.2", FREE, model1.currentToken().getState());
    assertEquals("#0.4", INSIDE_DOUBLE_QUOTE, model1.getStateAtCurrent());
  }

  
  public void testQuotesWithGap() {
    model1.insertChar('\"');
    model1.insertChar('\"');
    model1.move(-2);
    assertEquals("#0.1", "\"", model1.currentToken().getType());
    assertEquals("#0.3", FREE, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#0.0", "\"", model1.currentToken().getType());
    assertEquals("#0.2", FREE, model1.currentToken().getState());
    assertEquals("#0.4", INSIDE_DOUBLE_QUOTE, model1.getStateAtCurrent());
    insertGap(model1, 4);
    
    model1.move(-4);
    assertEquals("#1.1", true, model1.currentToken().isGap());
    assertEquals("#1.3", INSIDE_DOUBLE_QUOTE, model1.currentToken().getState());
    model1.move(4);
    assertEquals("#1.0", "\"", model1.currentToken().getType());
    assertEquals("#1.2", FREE, model1.currentToken().getState());
    assertEquals("#1.4", INSIDE_DOUBLE_QUOTE, model1.getStateAtCurrent());
    model1.move(-2);
    model1.insertChar('/');
    
    model1.move(-1);
    assertEquals("#2.1", "/", model1.currentToken().getType());
    model1.move(1);
    assertEquals("#2.0", true, model1.currentToken().isGap());
    assertEquals("#2.4", INSIDE_DOUBLE_QUOTE, model1.currentToken().getState());
    assertEquals("#2.6", INSIDE_DOUBLE_QUOTE, model1.getStateAtCurrent());
    model1.move(2);
    assertEquals("#2.2", "\"", model1.currentToken().getType());
    assertEquals("#2.3", FREE, model1.currentToken().getState());
  }

  
  public void testInsertQuoteToQuoteBlock() {
    model1.insertChar('\"');
    insertGap(model1, 2);
    model1.insertChar('/');
    insertGap(model1, 2);
    model1.insertChar('\"');
    model1.move(-3);
    model1.insertChar('\"');
    
    model1.move(-1);
    assertEquals("#3.1", "\"", model1.currentToken().getType());
    assertEquals("#3.5", FREE, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#3.0", true, model1.currentToken().isGap());
    assertEquals("#3.4", FREE, model1.currentToken().getState());
    assertEquals("#3.6", FREE, model1.getStateAtCurrent());
    model1.move(2);
    assertEquals("#3.2", "\"", model1.currentToken().getType());
    assertEquals("#3.3", FREE, model1.currentToken().getState());
    
    model1.move(-6);
    assertEquals("#4.1", true, model1.currentToken().isGap());
    assertEquals("#4.5", INSIDE_DOUBLE_QUOTE, model1.currentToken().getState());
    model1.move(2);
    assertEquals("#4.0", "/", model1.currentToken().getType());
    assertEquals("#4.4", INSIDE_DOUBLE_QUOTE, model1.currentToken().getState());
    assertEquals("#4.6", INSIDE_DOUBLE_QUOTE, model1.getStateAtCurrent());
    model1.move(1);
    assertEquals("#4.2", "\"", model1.currentToken().getType());
    assertEquals("#4.3", FREE, model1.currentToken().getState());
    model1.move(-1);
    
    
    model1.insertChar('\n');
    
    model1.move(-1);
    assertEquals("#5.5", FREE, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#5.4", FREE, model1.currentToken().getState());
    assertEquals("#5.6", FREE, model1.getStateAtCurrent());
    model1.move(1);
    assertEquals("#5.3", FREE, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#5.7", INSIDE_DOUBLE_QUOTE, model1.currentToken().getState());
    assertEquals("#5.8", true, model1.currentToken().isGap());
  }

  
  public void testQuoteBreaksComment() {
    model1.insertChar('/');
    model1.insertChar('*');
    model1.insertChar('*');
    model1.insertChar('/');
    model1.move(-2);
    model1.insertChar('\"');
    model1.move(-1);
    
    model1.move(-2);
    assertEquals("#1.1", FREE, model1.currentToken().getState());
    model1.move(2);
    assertEquals("#1.1", INSIDE_BLOCK_COMMENT, model1.getStateAtCurrent());
    assertEquals("#1.2", INSIDE_BLOCK_COMMENT, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#1.2", FREE, model1.currentToken().getState());
    model1.move(-3);
    
    model1.insertChar('\"');
    model1.move(-1);
    assertEquals("#2.2", FREE, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#2.0", INSIDE_DOUBLE_QUOTE, model1.getStateAtCurrent());
    assertEquals("#2.1", INSIDE_DOUBLE_QUOTE, model1.currentToken().getState());
    assertEquals("#2.3", "/", model1.currentToken().getType());
    model1.move(1);
    assertEquals("#2.4", "*", model1.currentToken().getType());
    
    model1.move(2);
    
    assertEquals("#5.0", FREE, model1.getStateAtCurrent());
    assertEquals("#5.1", FREE, model1.currentToken().getState());
    assertEquals("#5.3", "*", model1.currentToken().getType());
    model1.move(1);
    assertEquals("#5.4", "/", model1.currentToken().getType());
    assertEquals("#5.5", FREE, model1.currentToken().getState());
  }

  
  public void testQuoteBreakComment2() {
    model1.insertChar('/');
    model1.insertChar('*');
    model1.insertChar('*');
    model1.insertChar('/');
    model1.move(-4);
    assertEquals("#0.0", "/*", model1.currentToken().getType());
    model1.move(2);
    assertEquals("#0.1", "*/", model1.currentToken().getType());
    model1.move(-2);
    
    model1.insertChar('\"');
    model1.move(-1);
    assertEquals("#1.0", FREE, model1.currentToken().getState());
    assertEquals("#1.4", "\"", model1.currentToken().getType());
    model1.move(1);
    assertEquals("#1.1", INSIDE_DOUBLE_QUOTE, model1.getStateAtCurrent());
    assertEquals("#1.4", "/", model1.currentToken().getType());
    assertEquals("#1.2", INSIDE_DOUBLE_QUOTE, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#1.3", INSIDE_DOUBLE_QUOTE, model1.currentToken().getState());
    assertEquals("#1.4", "*", model1.currentToken().getType());
  }

  
  public void testInsertNewlineEndLineComment() {
    model1.insertChar('/');
    model1.insertChar('/');
    insertGap(model1, 5);
    model1.move(-2);
    model1.insertChar('\n');
    
    model1.move(-1);
    assertEquals("#0.2", "\n", model1.currentToken().getType());
    assertEquals("#0.4", FREE, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#0.0", FREE, model1.getStateAtCurrent());
    assertTrue("#0.1", model1.currentToken().isGap());
    assertEquals("#0.3", 2, model1.currentToken().getSize());
    assertEquals("#0.5", FREE, model1.currentToken().getState());
  }

  
  public void testInsertNewlineEndQuote() {
    model1.insertChar('\"');
    insertGap(model1, 5);
    model1.move(-2);
    model1.insertChar('\n');
    
    model1.move(-1);
    assertEquals("#0.4", FREE, model1.currentToken().getState());
    assertEquals("#0.2", "\n", model1.currentToken().getType());
    model1.move(1);
    assertEquals("#0.0", FREE, model1.getStateAtCurrent());
    assertTrue("#0.1", model1.currentToken().isGap());
    assertEquals("#0.3", 2, model1.currentToken().getSize());
    assertEquals("#0.5", FREE, model1.currentToken().getState());
  }

  
  public void testInsertNewlineChainReaction() {
    model1.insertChar('/');
    model1.insertChar('/');
    model1.insertChar('/');
    model1.insertChar('*');
    
    model1.move(-1);
    
    model1.move(-1);
    assertEquals("#0.2", "/", model1.currentToken().getType());
    assertEquals("#0.3", INSIDE_LINE_COMMENT, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#0.0", INSIDE_LINE_COMMENT, model1.getStateAtCurrent());
    assertEquals("#0.1", "*", model1.currentToken().getType());
    assertEquals("#0.4", INSIDE_LINE_COMMENT, model1.currentToken().getState());
    model1.move(1);
    model1.insertChar('\n');
    model1.insertChar('\"');
    model1.insertChar('*');
    model1.insertChar('/');
    model1.move(-1);
    
    
    assertEquals("#1.0", INSIDE_DOUBLE_QUOTE, model1.getStateAtCurrent());
    assertEquals("#1.1", "/", model1.currentToken().getType());
    assertEquals("#1.4", INSIDE_DOUBLE_QUOTE, model1.currentToken().getState());
    model1.move(-5);
    assertEquals("#2.1", "/", model1.currentToken().getType());
    model1.insertChar('\n');
    
    
    
    assertEquals("#3.0", FREE, model1.getStateAtCurrent());
    assertEquals("#3.4", FREE, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#3.1", "/*", model1.currentToken().getType());
    
    
    
    model1.move(2);
    assertEquals("#4.0", INSIDE_BLOCK_COMMENT, model1.getStateAtCurrent());
    assertEquals("#4.1", "\"", model1.currentToken().getType());
    assertEquals("#4.4", INSIDE_BLOCK_COMMENT, model1.currentToken().getState());
    model1.move(1);
    assertEquals("#4.6", "*/", model1.currentToken().getType());
  }

  
  public void testMoveWithinToken() {
    insertGap(model1, 10);
    assertTrue("#0.0", model1.atEnd());
    assertEquals("#0.1", 10, model1.absOffset());
    model1.move(-5);
    assertTrue("#1.0", model1.currentToken().isGap());
    assertEquals("#1.1", 5, model1.absOffset());
    model1.move(2);
    assertTrue("#2.0", model1.currentToken().isGap());
    assertEquals("#2.1", 7, model1.absOffset());
    model1.move(-4);
    assertTrue("#3.0", model1.currentToken().isGap());
    assertEquals("#3.1", 3, model1.absOffset());
    model1.move(-3);
    assertTrue("#4.0", model1.currentToken().isGap());
    assertEquals("#4.1", 0, model1.absOffset());
    model1.move(10);
    assertTrue("#5.0", model1.atEnd());
    assertEquals("#5.1", 10, model1.absOffset());
  }

  
  public void testMoveOnEmpty() {
    model1.move(0);
    assertTrue("#0.0", model1.atStart());
    try {
      model1.move(-1);
      assertTrue("#0.1", false);
    } catch (Exception e) { }
    try {
      model1.move(1);
      assertTrue("#0.2", false);
    } catch (Exception e) { }
  }

  
  public void testMove0StaysPut() {
    model0.insertChar('/');
    assertEquals("#1", 1, model0.absOffset());
    model0.move(0);
    assertEquals("#2", 1, model0.absOffset());
    model0.insertChar('/');
    assertEquals("#3", 2, model0.absOffset());
    model0.move(0);
    assertEquals("#4", 2, model0.absOffset());
    model0.move(-1);
    assertEquals("#5", 1, model0.absOffset());
    model0.move(0);
    assertEquals("#6", 1, model0.absOffset());
  }

  
  public void testInsideComment() {
    assertEquals("#0.0", FREE, model0.getStateAtCurrent());
    model0.insertChar('/');
    model0.insertChar('*');
    assertEquals("#0.1", INSIDE_BLOCK_COMMENT, model0.getStateAtCurrent());
    model1.insertChar('/');
    model1.insertChar('/');
    assertEquals("#0.2", INSIDE_LINE_COMMENT, model1.getStateAtCurrent());
    model1.insertChar('(');
    assertEquals("#0.3", INSIDE_LINE_COMMENT, model1.getStateAtCurrent());
    model1.insertChar('\n');
    assertEquals("#0.4", FREE, model1.getStateAtCurrent());
    model0.insertChar('*');
    model0.insertChar('/');
    assertEquals("#0.4", FREE, model0.getStateAtCurrent());
  }

  
  public void testInsideString() {
    assertEquals("#0.0", FREE, model0.getStateAtCurrent());
    model0.insertChar('\"');
    assertEquals("#0.1", INSIDE_DOUBLE_QUOTE, model0.getStateAtCurrent());
    model1.insertChar('\"');
    assertEquals("#0.2", INSIDE_DOUBLE_QUOTE, model1.getStateAtCurrent());
    model1.insertChar('(');
    assertEquals("#0.3", INSIDE_DOUBLE_QUOTE, model1.getStateAtCurrent());
    model1.insertChar('\"');
    assertEquals("#0.4", FREE, model1.getStateAtCurrent());
  }

  
  public void testInsertBraces() {
    assertEquals("#0.0", 0, model0.absOffset());
    model0.insertChar('/');
    
    assertEquals("#1.0", FREE, model0.getStateAtCurrent());
    model0.insertChar('*');
    
    assertEquals("#2.0", INSIDE_BLOCK_COMMENT, model0.getStateAtCurrent());
    assertEquals("#2.1", 2, model0.absOffset());
    model0.move(-1);
    
    assertEquals("#3.0", 1, model0.absOffset());
    model0.insertChar('(');
    
    assertEquals("#4.0", FREE, model0.getStateAtCurrent());
    model0.move(-1);
    
    model0.delete(1);
    
    model0.move(1);
    
    assertEquals("#5.0", INSIDE_BLOCK_COMMENT, model0.getStateAtCurrent());
    model0.insertChar('*');
    
    assertEquals("#6.0", INSIDE_BLOCK_COMMENT, model0.getStateAtCurrent());
    model0.insertChar('/');
    
    assertEquals("#7.0", 4, model0.absOffset());
    assertEquals("#7.1", FREE, model0.getStateAtCurrent());
    model0.move(-2);
    
    assertEquals("#8.0", INSIDE_BLOCK_COMMENT, model0.getStateAtCurrent());
    assertEquals("#8.1", 2, model0.absOffset());
    model0.insertChar('(');
    assertEquals("#9.0", INSIDE_BLOCK_COMMENT, model0.getStateAtCurrent());
    
    model0.move(1);
    
    assertEquals("#10.0", INSIDE_BLOCK_COMMENT, model0.getStateAtCurrent());
    model0.move(-2);
    
    assertEquals("#11.0", INSIDE_BLOCK_COMMENT, model0.getStateAtCurrent());
    model0.move(1);
    
    
    assertEquals("#12.0", INSIDE_BLOCK_COMMENT, model0.getStateAtCurrent());
    assertEquals("#12.1", 3, model0.absOffset());
    insertGap(model0, 4);
    
    model0.move(-2);
    
    assertEquals("#13.0", 5, model0.absOffset());
    model0.insertChar(')');
    
    assertEquals("#14.0", 6, model0.absOffset());
    
    model0.move(-1);
    
    assertEquals("#12.0", INSIDE_BLOCK_COMMENT, model0.getStateAtCurrent());
  }

  
  public void testInsertGap2() {
    model0.insertChar('/');
    model0.insertChar('*');
    insertGap(model0, 5);
    assertEquals("#0.0", 7, model0.absOffset());
    model0.insertChar('(');
    model0.move(-1);
    insertGap(model0, 3);
    assertEquals("#1.0", 10, model0.absOffset());
  }

  
  public void testMove() {
    model0.insertChar('(');
    insertGap(model0, 5);
    model0.insertChar(')');
    model0.insertChar('\n');
    insertGap(model0, 2);
    model0.insertChar('{');
    model0.insertChar('}');
    








    assertEquals("#0.2", 12, model0.absOffset());
    model0.move(-2);
    assertEquals("#0.3", 10, model0.absOffset());
    model0.move(-8);
    assertEquals("#0.4", 2, model0.absOffset());
    model0.move(3);
    assertEquals("#0.5", 5, model0.absOffset());
    model0.move(4);
    assertEquals("#0.6", 9, model0.absOffset());
  }

  
  protected ReducedModelControl setUpExample() {
    ReducedModelControl model = new ReducedModelControl();
    model.insertChar('{');
    model.insertChar('\n');
    insertGap(model, 3);
    model.insertChar('\n');
    model.insertChar('(');
    insertGap(model, 2);
    model.insertChar(')');
    model.insertChar('\n');
    insertGap(model, 3);
    model.insertChar('/');
    model.insertChar('/');
    insertGap(model, 3);
    model.insertChar('\n');
    model.insertChar('\"');
    insertGap(model, 1);
    model.insertChar('{');
    insertGap(model, 1);
    model.insertChar('\"');
    model.insertChar('/');
    model.insertChar('*');
    insertGap(model, 1);
    model.insertChar('(');
    insertGap(model, 1);
    model.insertChar(')');
    insertGap(model, 1);
    model.insertChar('*');
    model.insertChar('/');
    model.insertChar('\n');
    model.insertChar('}');
    
    
    
    
    
    
    return  model;
  }

  
  public void testBalanceForward() {
    assertEquals("#0.0", -1, model0.balanceForward());
    model0 = setUpExample();
    assertEquals("#1.0", -1, model0.balanceForward());
    model0.move(-1);

    assertEquals("#2.0", -1, model0.balanceForward());
    model0.move(-34);

    assertEquals("#3.0", 35, model0.balanceForward());
    model0.move(1);

    assertEquals("#4.0", -1, model0.balanceForward());
    model0.move(5);

    assertEquals("#5.0", 3, model0.balanceForward());
    model0.move(27);

    assertEquals("#6.0", -1, model0.balanceForward());
    model0.move(-20);

    assertEquals("#7.0", -1, model0.balanceForward());
    model1.insertChar('(');
    model1.move(-1);

    assertEquals("#8.0", -1, model1.balanceForward());
    model1.move(1);
    model1.insertChar('}');
    model1.move(-1);

    assertEquals("#9.0", -1, model1.balanceForward());
  }
}



;

