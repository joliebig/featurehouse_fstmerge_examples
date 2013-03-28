

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import junit.framework.*;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.definitions.reducedmodel.BraceException;


public final class BraceTest extends DrJavaTestCase implements ReducedModelStates {
  protected Brace rparen;
  protected Brace lparen;

  
  public void setUp() throws Exception {
    super.setUp();
    lparen = Brace.MakeBrace("(", FREE);
    rparen = Brace.MakeBrace(")", FREE);
  }

  
  public static Test suite() {
    return  new TestSuite(BraceTest.class);
  }

  
  public void testMakeBraceSuccess() {
    Brace brace = Brace.MakeBrace("{", FREE);
    assertEquals("{", brace.getType());
    assertEquals(1, brace.getSize());
  }

  
  public void testMakeBraceFailure() {
    try {
      Brace.MakeBrace("k", FREE);
    } catch (BraceException e) {
      assertEquals("Invalid brace type \"k\"", e.getMessage());
    }
  }

  
  public void testGetType() {
    assertEquals("(", lparen.getType());
    assertEquals(")", rparen.getType());
  }

  
  public void testIsShadowed() {
    assertTrue("#0.0", !lparen.isShadowed());
    lparen.setState(INSIDE_DOUBLE_QUOTE);
    assertEquals("#0.0.1", INSIDE_DOUBLE_QUOTE, lparen.getState());
    assertTrue("#0.1", lparen.isShadowed());
    rparen.setState(INSIDE_BLOCK_COMMENT);
    assertTrue("#0.2", rparen.isShadowed());
    rparen.setState(FREE);
    assertTrue("#0.3", !rparen.isShadowed());
  }

  
  public void testIsQuoted() {
    assertTrue("#0.0", !lparen.isQuoted());
    lparen.setState(INSIDE_DOUBLE_QUOTE);
    assertTrue("#0.1", lparen.isQuoted());
    lparen.setState(INSIDE_BLOCK_COMMENT);
    assertTrue("#0.2", !lparen.isQuoted());
  }

  
  public void testIsCommented() {
    assertTrue("#0.0", !lparen.isCommented());
    lparen.setState(INSIDE_BLOCK_COMMENT);
    assertTrue("#0.1", lparen.isCommented());
    lparen.setState(INSIDE_DOUBLE_QUOTE);
    assertTrue("#0.2", !lparen.isCommented());
  }

  
  public void testToString() {
    assertEquals("Brace<(>", lparen.toString());
    assertEquals("Brace<)>", rparen.toString());
  }

  
  public void testFlip() {
    lparen.flip();
    rparen.flip();
    assertEquals("(", rparen.getType());
    assertEquals(")", lparen.getType());
  }

  
  public void testOpenClosed() {
    assertTrue(lparen.isOpen());
    assertTrue(rparen.isClosed());
  }

  
  public void testIsMatch() {
    Brace bracket = Brace.MakeBrace("]", FREE);
    Brace dummy = Brace.MakeBrace("", FREE);
    assertTrue(lparen.isMatch(rparen));
    assertTrue(!lparen.isMatch(bracket));
    assertTrue(!lparen.isMatch(dummy));
    assertTrue(!dummy.isMatch(lparen));
  }
  
  public void testSetTypeFalse() {
    try{
     lparen.setType("a");
     fail("Expected BraceException");
    }catch(BraceException b){};
  }
  
  public void testIsSlashStar(){
    assertEquals(false, lparen.isSlash());
    Brace slash = Brace.MakeBrace("/",FREE);
    assertEquals(true, slash.isSlash());
    assertEquals(false, lparen.isStar());
    Brace star = Brace.MakeBrace("*",FREE);
    assertEquals(true, star.isStar());
  }
  
  public void testGrowFail() {
    try{
     lparen.grow(5);
     fail("Expected BraceException");
    }catch(BraceException b){};
  }
  
  public void testShrinkFail() {
    try{
     lparen.shrink(5);
     fail("Expected BraceException");
    }catch(BraceException b){};
  }
}



