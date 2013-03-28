
package genj.gedcom;

import genj.gedcom.time.Delta;
import junit.framework.TestCase;


public class PropertyAgeTest extends TestCase {
  
  
  public void testAges() {
    
    test( "35y 11m 2d", 2, 11, 35);
    test( "0d", 0, 0, 0);
    test( "14y 2m 0d", 0, 2, 14);
    test( "0d", 0, 0, 0);
    test( "13y", 0, 0, 13);
    test( "10m", 0, 10, 0);
    test( "24d", 24, 0, 0);
    
  }
  
  private void test(String expecting, int d, int m, int y) {
    
    assertEquals( expecting, new Delta(d,m,y).getValue());
  }

  
} 
