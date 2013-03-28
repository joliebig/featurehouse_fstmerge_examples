
package genj.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SafeProxy {

  
  public static<T> T harden(final T implementation) {
    return harden(implementation, Logger.getAnonymousLogger());
  }
  
  
  @SuppressWarnings("unchecked")
  public static<T> T harden(final T implementation, Logger logger) {
    
    
    if (logger==null||implementation==null)
      throw new IllegalArgumentException("implementation|logger==null");

    
    List<Class<?>> interfaces = new ArrayList<Class<?>>();
    Class c = implementation.getClass();
    while (c!=null) {
      for (Class<?> i : c.getInterfaces()) 
        if (Modifier.isPublic(i.getModifiers())&&!interfaces.contains(i)) 
          interfaces.add(i);
      c = c.getSuperclass();
    }

    
    return (T)Proxy.newProxyInstance(implementation.getClass().getClassLoader(), interfaces.toArray(new Class<?>[interfaces.size()]), new SafeHandler<T>(implementation, logger));
  }
  
  
  private static class SafeHandler<T> implements InvocationHandler {
    
    private T impl;
    private Logger logger;
    
    private SafeHandler(T impl, Logger logger) {
      this.impl = impl;
      this.logger = logger;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      Throwable t;
      try {
        return method.invoke(impl, args);
      } catch (InvocationTargetException ite) {
        t = ite.getCause();
      } catch (Throwable tt) {
        t = tt;
      }
      logger.log(Level.WARNING, "Implementation "+impl.getClass().getName() + "." + method.getName()+" threw exception "+t.getClass().getName()+"("+t.getMessage()+")", t);
      return null;
    }
  }

}
