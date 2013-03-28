

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.util.Vector;



public final class VectorOptionTest extends DrJavaTestCase {
  private VectorOption<String> _svo;
  private VectorOption<Integer> _ivo;
  private VectorOption<Boolean> _bvo;

  public void setUp() throws Exception {
    super.setUp();
    
    _svo = new VectorOption<String>("whatever", new StringOption("", null), (Vector<String>) null);
    _ivo = new VectorOption<Integer>("something", new IntegerOption("", null), (Vector<Integer>) null);
    _bvo = new VectorOption<Boolean>("everwhat", new BooleanOption("", null), (Vector<Boolean>) null);
  }

  public void testGetName() {
    assertEquals("whatever", _svo.getName());
    assertEquals("everwhat", _bvo.getName());
  }

  public void testParse() {
    assertTrue(_svo.parse("").isEmpty());
    assertTrue(_bvo.parse("").isEmpty());
    
    Vector<String> v = _svo.parse("[]");
    assertEquals(1, v.size());
    assertEquals("", v.get(0));
    
    v = _svo.parse("[x]");
    assertEquals(1, v.size());
    assertEquals("x", v.get(0));

    v = _svo.parse("[||]");
    assertEquals(1, v.size());
    assertEquals("|", v.get(0));
    
    v = _svo.parse("[|,]");
    assertEquals(1, v.size());
    assertEquals(",", v.get(0));
    
    v = _svo.parse("[|,,]");
    assertEquals(2, v.size());
    assertEquals(",", v.get(0));
    assertEquals("", v.get(1));

    v = _svo.parse("[,]");
    assertEquals(2, v.size());
    assertEquals("", v.get(0));
    assertEquals("", v.get(1));
    
    try { _svo.parse("[|x]"); fail("Pipe not in front of another pipe or delimiter."); } 
    catch (OptionParseException e) { }
    
    try { _svo.parse("[11"); fail("Missing footer."); } 
    catch (OptionParseException e) { }
    
    v = _svo.parse("[11,]");
    assertEquals(2, v.size());
    assertEquals("11", v.get(0));
    assertEquals("", v.get(1));
    
    try { _svo.parse("11]"); fail("Missing header."); } 
    catch (OptionParseException e) { }
    
    v = _svo.parse("[11,,22]");
    assertEquals(3, v.size());
    assertEquals("11", v.get(0));
    assertEquals("", v.get(1));
    assertEquals("22", v.get(2));
    
    v = _svo.parse("[11,|,,22]");
    assertEquals(3, v.size());
    assertEquals("11", v.get(0));
    assertEquals(",", v.get(1));
    assertEquals("22", v.get(2));
    
    v = _svo.parse("[11,abc|,def,22]");
    assertEquals(3, v.size());
    assertEquals("11", v.get(0));
    assertEquals("abc,def", v.get(1));
    assertEquals("22", v.get(2));

    v = _svo.parse("[11,||,22]");
    assertEquals(3, v.size());
    assertEquals("11", v.get(0));
    assertEquals("|", v.get(1));
    assertEquals("22", v.get(2));

    
    
    v = _svo.parse("{11,22}");
    assertEquals(1, v.size());
    assertEquals("{11,22}", v.get(0));    
    
    
    try { _ivo.parse("{11,22}"); fail("Should not have parsed this as singleton list."); } 
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
    
    try { _bvo.parse("[true;false]"); fail("Illegal delimiter."); } 
    catch (OptionParseException e) { }
  }

  public void testFormat() {
    Vector<String> sv = new Vector<String>();
    assertEquals("", _svo.format(sv));

    sv.add("");
    assertEquals("[]", _svo.format(sv));

    sv.add("-33");
    assertEquals("[,-33]", _svo.format(sv));

    sv.add("2");
    assertEquals("[,-33,2]", _svo.format(sv));

    sv.add("");
    assertEquals("[,-33,2,]", _svo.format(sv));

    sv.add(",");
    assertEquals("[,-33,2,,|,]", _svo.format(sv));

    sv.add("0");
    assertEquals("[,-33,2,,|,,0]", _svo.format(sv));
  }
}
