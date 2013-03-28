
package genj.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
    
    return (T)Proxy.newProxyInstance(implementation.getClass().getClassLoader(), implementation.getClass().getInterfaces(), new SafeHandler<T>(implementation, logger));
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
      }
      logger.log(Level.WARNING, "Implementation "+impl.getClass().getName() + "." + method.getName()+" threw exception "+t.getClass().getName()+"("+t.getMessage()+")", t);
      return null;
    }
  }

}
