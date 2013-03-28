
package edu.rice.cs.util.sexp;

import junit.framework.TestCase;


public class TokensTest extends TestCase {
  
  static Tokens.WordToken tok1 = new Tokens.WordToken("this");
  static Tokens.QuotedTextToken tok2 = new Tokens.QuotedTextToken("this");
  static Tokens.SExpToken tok3 = new Tokens.SExpToken("this");
  static Tokens.NumberToken tok4 = new Tokens.NumberToken(7);
  static Tokens.NumberToken tok5 = new Tokens.NumberToken(12);
  
  public void testEquals() {
    
    assertEquals("\\ token equals test", Tokens.BackSlashToken.ONLY, Tokens.BackSlashToken.ONLY);
    assertFalse("\\ token not equals test", Tokens.BackSlashToken.ONLY.equals(new Tokens.SExpToken("\\")));
    
    assertEquals("( token equals test", Tokens.LeftParenToken.ONLY, Tokens.LeftParenToken.ONLY);
    assertFalse("\\ token not equals test", Tokens.LeftParenToken.ONLY.equals(new Tokens.SExpToken("(")));
    
    assertEquals(") token equals test", Tokens.RightParenToken.ONLY, Tokens.RightParenToken.ONLY);
    assertFalse("\\ token not equals test", Tokens.RightParenToken.ONLY.equals(new Tokens.SExpToken(")")));
    
    assertEquals("FALSE token equals test", Tokens.BooleanToken.FALSE, Tokens.BooleanToken.FALSE);
    assertFalse("FALSE token not equals test", Tokens.BooleanToken.FALSE.equals(new Tokens.SExpToken("FALSE")));
    
    assertEquals("TRUE token equals test", Tokens.BooleanToken.TRUE, Tokens.BooleanToken.TRUE);
    assertFalse("TRUE token not equals test", Tokens.BooleanToken.TRUE.equals(new Tokens.SExpToken("TRUE")));
    
    assertEquals("Tokens.WordToken equals test", tok1, tok1);
    assertFalse("Tokens.WordToken not equals test 1", tok1.equals(tok2));
    assertFalse("Tokens.WordToken not equals test 2", tok1.equals(tok3));
    
    assertEquals("Tokens.QuotedTextToken equals test", tok2, tok2);
    assertFalse("Tokens.QuotedTextToken not equals test 1", tok2.equals(tok1));
    assertFalse("Tokens.QuotedTextToken not equals test 2", tok2.equals(tok3));
    
    assertEquals("Tokens.NumberToken equals test", tok4, tok4);
    assertFalse("Tokens.NumberToken not equals test 1", tok4.equals(tok5));
    assertFalse("Tokens.NumberToken not equals test 2", tok4.equals(tok3));
  }
}
    