
package genj.option;

import genj.util.Registry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.spi.ServiceRegistry;


public abstract class OptionProvider {

  
  private static List options;
  
  
  private static String[] PROVIDERS;

  
  public abstract List getOptions();

  
  public static void persistAll(Registry registry) {
    
    registry = new Registry(registry, "options");
  
    
    Iterator it = getAllOptions(null).iterator();
    while (it.hasNext()) try {
      ((Option)it.next()).persist(registry);
    } catch (Throwable t) {
    }
    
    
    
  }
  
  
  public static void setOptionProviders(String[] providers) {
    PROVIDERS = providers;
  }
  
  
  public static List getAllOptions() {  
    return getAllOptions(null);
  }
  public static List getAllOptions(Registry restoreFrom) {  

    
    if (options!=null)
      return options;    
  
    
    options = new ArrayList(32);
    if (restoreFrom!=null) 
      restoreFrom = new Registry(restoreFrom, "options");
  
    
    Iterator providers = lookupProviders();
    while (providers.hasNext()) {
      
      
      OptionProvider provider = (OptionProvider)providers.next();
      
      
      List os = provider.getOptions();
      options.addAll(os);
      
      
      if (restoreFrom!=null) {
        for (Iterator it=os.iterator(); it.hasNext(); ) {
          try {
            Option option = (Option)it.next();
            option.restore(restoreFrom);
          } catch (Throwable t) {
            t.printStackTrace();
          }
        }
      }
      
    }
    
    
    return options;
  }
  
  
  private static Iterator lookupProviders() {
    
    
    if (PROVIDERS!=null) {
        List result = new ArrayList(32);
        for (int i=0;i<PROVIDERS.length;i++) { 
          try {
            result.add((OptionProvider)Class.forName(PROVIDERS[i]).newInstance());
          } catch (Throwable t) {
            t.printStackTrace(System.err);
          }
        }
        return result.iterator();
    }
    
    
    return ServiceRegistry.lookupProviders(OptionProvider.class);
  }

} 
