

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.util.Vector;



public final class VectorOptionTest extends DrJavaTestCase {
  private VectorOption<Integer> _ivo;
  private VectorOption<Boolean> _bvo;

  public void setUp() throws Exception {
    super.setUp();
    
    _ivo = new VectorOption<Integer>("whatever", new IntegerOption("", null), (Vector<Integer>) null);
    _bvo = new VectorOption<Boolean>("everwhat", new BooleanOption("", null), (Vector<Boolean>) null);
  }

  public void testGetName() {
    assertEquals("whatever", _ivo.getName());
    assertEquals("everwhat", _bvo.getName());
  }

  public void testParse() {
    assertTrue(_ivo.parse("[]").isEmpty());
    assertTrue(_bvo.parse("[]").isEmpty());

    try { _ivo.parse("[,]"); fail("Comma at beginning."); } 
    catch (OptionParseException e) { }
    
    try { _ivo.parse("[11"); fail("Missing footer."); } 
    catch (OptionParseException e) { }
    try { _ivo.parse("[11,]"); fail("Comma w/o following list element."); } 
    catch (OptionParseException e) { }
    
    try { _ivo.parse("11]"); fail("Missing header."); } 
    catch (OptionParseException e) { }
    
    try { _ivo.parse("[11,,22]"); fail("Missing list element."); } 
    catch (OptionParseException e) { }
    
    try { _ivo.parse("{11,22}"); fail("Illegal header and footer."); } 
    catch (OptionParseException e) { }
    
    try { _ivo.parse("[11;22]"); fail("Illegal delimiter."); } 
    catch (OptionParseException e) { }

    Vector<Boolean> bv = _bvo.parse("[true]");

    assertEquals(1, bv.size());
    assertEquals(Boolean.TRUE, bv.get(0));

    bv = _bvo.parse("[true,false,true,true]");

    assertEquals(4, bv.size());
    assertEquals(Boolean.TRUE,  bv.get(0));
    assertEquals(Boolean.FALSE, bv.get(1));
    assertEquals(Boolean.TRUE,  bv.get(2));
    assertEquals(Boolean.TRUE,  bv.get(3));

    try { _bvo.parse("[11]"); fail("Number instead of boolean."); } 
    catch (OptionParseException e) { }
  }

  public void testFormat() {
    Vector<Integer> iv = new Vector<Integer>();
    assertEquals("[]", _ivo.format(iv));

    iv.add(new Integer(-33));
    assertEquals("[-33]", _ivo.format(iv));

    iv.add(new Integer(2));
    assertEquals("[-33,2]", _ivo.format(iv));

    iv.add(new Integer(0));
    assertEquals("[-33,2,0]", _ivo.format(iv));
  }
}
