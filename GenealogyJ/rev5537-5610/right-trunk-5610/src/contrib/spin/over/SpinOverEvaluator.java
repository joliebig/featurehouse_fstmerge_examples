
package spin.over;

import javax.swing.SwingUtilities;

import spin.Invocation;
import spin.Evaluator;


public class SpinOverEvaluator extends Evaluator {

    private static boolean defaultWait = true;
    
    private boolean wait;

    
    public SpinOverEvaluator() {
        this(defaultWait);
    }

    
    public SpinOverEvaluator(boolean wait) {
        this.wait = wait;
    }

    
    public final void evaluate(final Invocation invocation) throws Throwable {

        if (SwingUtilities.isEventDispatchThread()) {
            invocation.evaluate();
        } else {
            Runnable runnable = new Runnable() {
                public void run() {
                    invocation.evaluate();
                }
            };
            if (wait) {
                SwingUtilities.invokeAndWait(runnable);
            } else {
                if (invocation.getMethod().getReturnType() != Void.TYPE) {
                    onInvokeLaterNonVoidReturnType(invocation);
                }
                SwingUtilities.invokeLater(runnable);
            }
        }
    }

    protected void onInvokeLaterNonVoidReturnType(Invocation invocation) throws IllegalArgumentException {
        throw new IllegalArgumentException("invokeLater with non-void return type");
    }

    public static boolean getDefaultWait() {
        return defaultWait;
    }

    public static void setDefaultWait(boolean wait) {
        defaultWait = wait;
    }
}