
package genj.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SafeProxy {

  
  public static<T> T harden(final T implementation) {
    return harden(implementation, Logger.getAnonymousLogger());
  }
  
  public static<T> List<T> harden(final List<T> ts, Logger logger) {
    for (ListIterator<T> li = ts.listIterator(); li.hasNext(); ) {
     li.set(harden(li.next(), logger));
    }
    return ts;
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
    
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      
      
      if ("equals".equals(method.getName()) && args.length==1) try {
        return impl.equals( ((SafeHandler<T>)Proxy.getInvocationHandler(args[0])).impl );
      } catch (IllegalArgumentException e) {
        return false;
      }
      
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
