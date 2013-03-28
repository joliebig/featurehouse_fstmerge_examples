
package genj.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

public class ServiceLookup {
  
  private static Logger LOG = Logger.getLogger("genj.util");

  public static <X> List<X> lookup(Class<X> service) {
    List<X> result = new ArrayList<X>();
    Iterator<X> it = ServiceRegistry.lookupProviders(service);
    while (it.hasNext()) try {
      result.add(it.next());
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "Error retrieving service for "+service, t);
    }
    return result;
  }
  
}
