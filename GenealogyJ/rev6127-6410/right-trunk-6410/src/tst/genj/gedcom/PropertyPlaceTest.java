
package genj.gedcom;

import genj.util.Origin;
import junit.framework.TestCase;


public class PropertyPlaceTest extends TestCase {
  
  private Gedcom gedcom;
  
  
  protected void setUp() throws Exception {

    
    gedcom = new Gedcom(Origin.create("file://foo.ged"));

    
  }
  
  
  private Indi createIndi() {
    
    Indi indi = null;
    
    try {
      
      indi = (Indi)gedcom.createEntity("INDI");
      
      indi.addDefaultProperties();
    } catch (GedcomException e) {
      fail(e.getMessage());
    }
    
    
    return indi;
  }
  
  
  public void testJurisdictiosn() {
    
    Indi indi = createIndi();
    Property birt = indi.addProperty("BIRT", "");
    
    test(birt, "Rendsburg, Schleswig Holstein, Deutschland", "Rendsburg", "Rendsburg", "Deutschland");
    test(birt, "Rendsburg", "Rendsburg", "Rendsburg", null);

    test(birt, "Backyard, The Hood, Rendsburg, Schleswig Holstein, Deutschland", "Backyard", "Backyard", "Rendsburg");
    gedcom.setPlaceFormat("backyard,neighbourhood,city,world");
    test(birt, "Backyard, The Hood, Rendsburg, Schleswig Holstein, Deutschland", "Rendsburg", "Backyard", "Rendsburg");
    
  }
  
  
  private void test(Property event, String value, String city, String first, String third) {
    
    PropertyPlace plac = (PropertyPlace)event.addProperty("PLAC", value);
    
    assertEquals(city, plac.getCity());
    assertEquals(first, plac.getFirstAvailableJurisdiction());
    assertEquals(third, plac.getJurisdiction(2));
    
  }
  
} 
