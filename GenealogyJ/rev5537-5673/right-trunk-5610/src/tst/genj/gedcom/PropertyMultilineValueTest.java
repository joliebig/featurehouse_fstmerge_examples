
package genj.gedcom;

import junit.framework.TestCase;


public class PropertyMultilineValueTest extends TestCase {

  private final static TagPath MLPATH = new TagPath("INDI:NOTE");
  
  private final static String 
    CONT = "CONT",
    CONC = "CONC";

  
  private PropertyMultilineValue mle;
  
  
  private MultiLineProperty.Iterator it;

  
  protected void setUp() throws Exception {
    
    
    Options.getInstance().setValueLineBreak(40);
    
    
    mle = new PropertyMultilineValue();
  }
  
  
  public void testDisplayValue() {
    
    
    
    
    
    
    mle.setValue("one\ntwo\nthree");
    assertEquals("wrong display value", ((Property)mle).getDisplayValue(), mle.getValue());
  }
  
  
  public void testSimpleIterator() {

    
    iterator("abcde");

    
    assertLine(0, null, "abcde");    
    assertNoNext();
    
    
  }

  
  public void testMultiline() {

    
    iterator("abcde\nfghij");

    
    assertLine(0, null, "abcde");    
    assertNext();
    assertLine(1, CONT, "fghij");    
    assertNoNext();

    
  }

  
  public void testValueLineBreak() {

    
    iterator("0123456789012345678901234567890123456789xyz");

    
    assertLine(0, null, "0123456789012345678901234567890123456789");    
    assertNext();
    assertLine(1, CONC, "xyz");    
    assertNoNext();

    
  }

  
  public void testMultilineSpaces() {

    
    iterator("\nabcde\n\n  fg  \nhi\n\n");

    
    assertLine(0, null, "");    
    assertNext();
    assertLine(1, CONT, "abcde");    
    assertNext();
    assertLine(1, CONT, "");    
    assertNext();
    assertLine(1, CONT, "  fg  ");    
    assertNext();
    assertLine(1, CONT, "hi");    
    assertNext();
    assertLine(1, CONT, "");    
    assertNoNext();

    
  }
  
  
  public void testSpaceAtLineBreak() {

    
    iterator("123 567 901 345 789 123 567 901 345 789 xxx");

    
    assertLine(0, null, "123 567 901 345 789 123 567 901 345 78");    
    assertNext();
    assertLine(1, CONC, "9 xxx");    
    assertNoNext();

    
    iterator("1                                       xxx");

    
    assertLine(0, null, "1");    
    assertNext();
    assertLine(1, CONC, "                                       x");
    assertNext();
    assertLine(1, CONC, "xx");    
    assertNoNext();
  }

  
  private void iterator(String value) {

    
    mle.setValue(value);

    
    it = mle.getLineIterator();
    
  }

    
  private void assertLine(int indent, String tag, String value) {

    assertEquals("wrong indent",  indent, it.getIndent());
    if (tag!=null)
      assertEquals("wrong tag"   ,  tag   , it.getTag   ());
    assertEquals("wrong value" ,  value , it.getValue ());
    
  }
  
    
  private void assertNext() {

    assertTrue  ("no next"  , it.next());
    
  }
  
    
  private void assertNoNext() {

    assertFalse("next"  , it.next());
    
  }
  

} 
