
package spin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Invocation {

    
    private Object object;

    
    private Method method;

    
    private Object[] args;

    
    private boolean evaluated;

    
    private Throwable throwable;

    
    private Object result;

    
    public Invocation(Object object, Method method, Object[] args) {
        this.object = object;
        this.method = method;
        this.args   = args;
    }
    
    
    public void setObject(Object object) {
        this.object = object;
    }

    
    public Object getObject() {
        return object;
    }

    
    public void setMethod(Method method) {
        this.method = method;
    }

    
    public Method getMethod() {
        return method;
    }

    
    public void setArguments(Object[] args) {
        this.args = args;
    }

    
    public Object[] getArguments() {
        return args;
    }

    
    public Object getResult() {
        return result;
    }

    
    public void setResult(Object result) {
        this.result = result;
    }

    
    public Throwable getThrowable() {
        return throwable;
    }

    
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    
    public void evaluate() {
        if (evaluated) {
            throw new IllegalStateException("already evaluated");
        }

        try {
            result = method.invoke(object, args);
        } catch (InvocationTargetException ex) {
            this.throwable = ex.getTargetException();
        } catch (Throwable throwable) {
            this.throwable = throwable;
        }

        evaluated = true;
    }

    
    public boolean isEvaluated() {
        return evaluated;
    }

    
    public Object resultOrThrow() throws Throwable {
        if (throwable != null) {
            throw throwable;
        } else {
            return result;
        }
    }
}