
package genj.gedcom;

import junit.framework.TestCase;


public class PropertyNameTest extends TestCase {

  
  public void testValues() {     
    
    PropertyName name = new PropertyName();

    String 
    	first = "Nils",
    	last  = "Meier",
    	suff  = "jr.",
    	value; 

    
    name.setName(first, last, suff);
    testName(name, first, last, suff, first+" /"+last+"/ "+suff);
    
    
    name.setName(first, "", "");
    testName(name, first, "", "", first);
    
    
    name.setName("", last, "");
    testName(name, "", last, "", "/"+last+"/");
    
    
    name.setName("", "", suff);
    testName(name, "", "", suff, "// "+suff);
    
    
  }
  
  private void testName(PropertyName name, String first, String last, String suffix, String value) {
    assertEquals("expected first "+first, first, name.getFirstName());
    assertEquals("expected last "+last, last, name.getLastName());
    assertEquals("expected "+suffix, suffix, name.getSuffix());
    assertEquals("expected "+value, value, name.getValue());
  }
  
} 
