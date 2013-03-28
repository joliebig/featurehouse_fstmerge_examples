

package koala.dynamicjava.interpreter.error;


public class ContinueException extends RuntimeException {
    
    private String label;

    
    public ContinueException(String m) {
 super(m);
    }

    
    public ContinueException(String m, String l) {
        super(m);
 label = l;
    }

    
    public boolean isLabeled() {
 return label != null;
    }

    
    public String getLabel() {
 return label;
    }
}
