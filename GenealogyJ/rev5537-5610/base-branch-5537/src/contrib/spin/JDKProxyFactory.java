
package spin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;


public class JDKProxyFactory extends ProxyFactory {

  
  public Object createProxy(Object object, Evaluator evaluator) {
    Class clazz = object.getClass();

    return Proxy.newProxyInstance(clazz.getClassLoader(),
        getAccessibleInterfaces(clazz), new SpinInvocationHandler(
            object, evaluator));
  }

  
  private static Class[] getAccessibleInterfaces(Class clazz) {
    ClassLoader loader = clazz.getClassLoader();

    Set interfaces = new HashSet();
    while (clazz != null) {
      Class[] candidates = clazz.getInterfaces();
      for (int c = 0; c < candidates.length; c++) {
        Class candidate = candidates[c];

        if (!Modifier.isPublic(candidate.getModifiers())) {
          if (candidate.getClassLoader() != loader) {
            
            continue;
          }
        }

        interfaces.add(candidate);
      }

      clazz = clazz.getSuperclass();
    }

    return (Class[]) interfaces.toArray(new Class[interfaces.size()]);
  }

  public boolean isProxy(Object object) {

    if (object == null) {
      return false;
    }

    if (!Proxy.isProxyClass(object.getClass())) {
      return false;
    }

    return (Proxy.getInvocationHandler(object) instanceof SpinInvocationHandler);
  }

  protected boolean areProxyEqual(Object proxy1, Object proxy2) {

    SpinInvocationHandler handler1 = (SpinInvocationHandler) Proxy
        .getInvocationHandler(proxy1);
    SpinInvocationHandler handler2 = (SpinInvocationHandler) Proxy
        .getInvocationHandler(proxy2);

    return handler1.object.equals(handler2.object);
  }

  
  private class SpinInvocationHandler implements InvocationHandler {

    private Object object;

    private Evaluator evaluator;

    
    public SpinInvocationHandler(Object object, Evaluator evaluator) {
      this.object = object;
      this.evaluator = evaluator;
    }

    
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {

      return evaluteInvocation(evaluator, proxy, new Invocation(
          this.object, method, args));
    }
  }
}