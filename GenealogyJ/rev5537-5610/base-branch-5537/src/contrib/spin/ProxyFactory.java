
package spin;

import java.lang.reflect.Method;


public abstract class ProxyFactory {

    
    private static final Method equalsMethod;
    static {
        try {
          
          equalsMethod = Object.class.getMethod("equals", new Class[]{Object.class});
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    
    public abstract Object createProxy(Object object, Evaluator evaluator);
    
    
    public abstract boolean isProxy(Object object);

    
    protected abstract boolean areProxyEqual(Object proxy1, Object proxy2);
    
    
    protected Object evaluteInvocation(Evaluator evaluator, Object proxy, Invocation invocation) throws Throwable {
        if (equalsMethod.equals(invocation.getMethod())) {
            return new Boolean(isProxy(invocation.getArguments()[0]) &&
                               areProxyEqual(proxy, invocation.getArguments()[0]));
        } else {
            evaluator.evaluate(invocation);
          
            return invocation.resultOrThrow();
        }
    }
}