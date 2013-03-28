
package edu.rice.cs.util.sexp;

import junit.framework.TestCase;


public class TokensTest extends TestCase {
  
  static WordToken tok1 = new WordToken("this");
  static QuotedTextToken tok2 = new QuotedTextToken("this");
  static SExpToken tok3 = new SExpToken("this");
  static NumberToken tok4 = new NumberToken(7);
  static NumberToken tok5 = new NumberToken(12);
  
  public void testEquals() {
    
    assertEquals("\\ token equals test", BackSlashToken.ONLY, BackSlashToken.ONLY);
    assertFalse("\\ token not equals test", BackSlashToken.ONLY.equals(new SExpToken("\\")));
    
    assertEquals("( token equals test", LeftParenToken.ONLY, LeftParenToken.ONLY);
    assertFalse("\\ token not equals test", LeftParenToken.ONLY.equals(new SExpToken("(")));
    
    assertEquals(") token equals test", RightParenToken.ONLY, RightParenToken.ONLY);
    assertFalse("\\ token not equals test", RightParenToken.ONLY.equals(new SExpToken(")")));
    
    assertEquals("FALSE token equals test", BooleanToken.FALSE, BooleanToken.FALSE);
    assertFalse("FALSE token not equals test", BooleanToken.FALSE.equals(new SExpToken("FALSE")));
    
    assertEquals("TRUE token equals test", BooleanToken.TRUE, BooleanToken.TRUE);
    assertFalse("TRUE token not equals test", BooleanToken.TRUE.equals(new SExpToken("TRUE")));
    
    assertEquals("WordToken equals test", tok1, tok1);
    assertFalse("WordToken not equals test 1", tok1.equals(tok2));
    assertFalse("WordToken not equals test 2", tok1.equals(tok3));
    
    assertEquals("QuotedTextToken equals test", tok2, tok2);
    assertFalse("QuotedTextToken not equals test 1", tok2.equals(tok1));
    assertFalse("QuotedTextToken not equals test 2", tok2.equals(tok3));
    
    assertEquals("NumberToken equals test", tok4, tok4);
    assertFalse("NumberToken not equals test 1", tok4.equals(tok5));
    assertFalse("NumberToken not equals test 2", tok4.equals(tok3));
  }
}
    