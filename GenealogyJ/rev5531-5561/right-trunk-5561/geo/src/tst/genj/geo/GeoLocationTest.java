
package genj.geo;

import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.util.Origin;

import java.util.Locale;

import junit.framework.TestCase;


public class GeoLocationTest extends TestCase {

  private Indi indi;
  
  
  protected void setUp() throws Exception {

    
    Gedcom gedcom = new Gedcom(Origin.create("file://foo.ged"));
    
    
    indi = (Indi)gedcom.createEntity(Gedcom.INDI);
    
  }
  
  
  public void testParsing() {

    Locale.setDefault(Locale.GERMAN);
    
    
    GeoLocation[] locations = {
      locate(place("Timaru, Neuseeland")),
      locate(place("Timaru, New Zealand")),
      locate(addr("Timaru", "New Zealand")),
      locate(addr("Timaru", "Neuseeland")),
    };
    
    for (int l = 1; l < locations.length; l++) {
      assertEquals(locations[l-1], locations[l]);
    }
    
    
    GeoLocation other = locate(place("Timaru"));
    for (int l = 0; l < locations.length; l++) {
      
      assertFalse(other.equals((Object)locations[l])); 
    }
    
    
    assertNull("don't consider iso country codes", locate(place("Washington, IL")).getCountry());
    assertEquals("consider country names", "il", locate(place("Washington, Israel")).getCountry().getCode());
    

    
  }
  
  private GeoLocation locate(Property prop) {
    return new GeoLocation(prop);
  }

  private Property addr(String city, String country) {
    
    Property event = indi.addProperty("EVEN", "");
    
    Property addr = event.addProperty("ADDR", "");
    addr.addProperty("CITY", city);
    addr.addProperty("CTRY", country);
    
    return event;
  }
    
  private Property place(String value) {
    Property event = indi.addProperty("EVEN", "");
    event.addProperty("PLAC", value);
    return event;
  }

}
