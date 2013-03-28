
package genj.geo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PostTest {

  
  public static void main(String[] args) {
    try {
      new PostTest().testWebservice();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
  
  
  public void testWebservice() throws Throwable {
    
    
    Logger.getLogger("").setLevel(Level.OFF);

    GeoLocation[]  locs = {
      new GeoLocation("Lohmar", null, null),
      new GeoLocation("Siegburg", "Nordrhein-Westfalen", null),
      new GeoLocation("Siegburg", "Rhein-Sieg-Kreis", null).addJurisdiction("Nordrhein-Westfalen"),
      new GeoLocation("Köln", null, Country.get("de")),
      new GeoLocation("Rendsburg", null, null),
      new GeoLocation("Celle", null, null),
      new GeoLocation("Celle", "Niedersachsen", Country.get("de")),
      new GeoLocation("Hambu*", null, null)
    };
    
    GeoService service = GeoService.getInstance();
    
    int i=0;
    for (Iterator rows = service.webservice(GeoService.URL, Arrays.asList(locs), true).iterator(); rows.hasNext(); i++) {
      System.out.println("---"+locs[i]+"---");
      for (Iterator hits = ((Collection)rows.next()).iterator(); hits.hasNext(); )
        System.out.println(hits.next());
    }

    
  }

}
