
package genj.io;

import genj.gedcom.Entity;
import genj.util.Resources;


public class GedcomEncodingException extends GedcomIOException {
  
  private final static Resources RESOURCES = Resources.get("genj.io");
  
  
  public GedcomEncodingException(Entity entity, String encoding) {
      super(RESOURCES.getString("write.error.cantencode", new Object[]{ entity, encoding } ), 0);
  }

  
  public GedcomEncodingException(String message) {
      super(message, 0);
  }

}
