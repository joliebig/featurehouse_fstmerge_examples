
package spin;

import spin.over.SpinOverEvaluator;


public class Spin {

    private static ProxyFactory defaultProxyFactory = new JDKProxyFactory();

    private static Evaluator defaultOverEvaluator = new SpinOverEvaluator();

    private Object proxy;

    
    public Spin(Object object, Evaluator evaluator) {
        this(object, defaultProxyFactory, evaluator);
    }

    
    public Spin(Object object, ProxyFactory proxyFactory, Evaluator evaluator) {
        if (object == null) {
            throw new IllegalArgumentException("object must not be null");
        }
        if (proxyFactory == null) {
            throw new IllegalArgumentException("proxyFactory must not be null");
        }
        if (evaluator == null) {
            throw new IllegalArgumentException("evaluator must not be null");
        }

        proxy = proxyFactory.createProxy(object, evaluator);
    }

    
    public Object getProxy() {
        return proxy;
    }

    
    public static Object over(Object object) {
        return new Spin(object, defaultProxyFactory, defaultOverEvaluator).getProxy();
    }
}