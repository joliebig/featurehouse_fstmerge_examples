
package genj.view;

import java.awt.Component;



public interface ContextProvider {

  
  public ViewContext getContext();
  
  
  public class Lookup {
    
    private ViewContext context;
    private ContextProvider provider;
    
    public Lookup(Component component) {
      
      while (component != null) {
        
        if (component instanceof ContextProvider) {
          context = ((ContextProvider) component).getContext();
          if (context != null) {
            provider = (ContextProvider)component;
            break;
          }
        }
        
        component = component.getParent();
      }
    }
    
    public ViewContext getContext() {
      return context;
    }
    
    public ContextProvider getProvider() {
      return provider;
    }
  }
  

} 
