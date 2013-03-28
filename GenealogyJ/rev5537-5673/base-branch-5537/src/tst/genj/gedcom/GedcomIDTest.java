
package genj.gedcom;

import junit.framework.TestCase;


public class GedcomIDTest extends TestCase {
  
  
  public void testIDs() throws GedcomException {
    
    Gedcom gedcom = new Gedcom();
    
    
    assertID("I1", gedcom.createEntity(Gedcom.INDI, "I1"));
    
    
    assertID("I2", gedcom.createEntity(Gedcom.INDI));
    
    
    assertID("I4", gedcom.createEntity(Gedcom.INDI, "I4"));
    
    
    Options.getInstance().isFillGapsInIDs = true;
    assertID("I3", gedcom.createEntity(Gedcom.INDI));
    
    
    assertID("I000005", gedcom.createEntity(Gedcom.INDI, "I000005"));
    
    
    assertID("I6", gedcom.createEntity(Gedcom.INDI));
    
    
    assertID("I006", gedcom.createEntity(Gedcom.INDI, "I006"));
    
    
    try {
      gedcom.createEntity(Gedcom.INDI, "I006");
      fail("duplicate ID not caught!");
    } catch (GedcomException e) {
    }
      
    
  }
  
  private void assertID(String id, Entity e) {
    assertEquals(id, e.getId());
  }
  
} 
