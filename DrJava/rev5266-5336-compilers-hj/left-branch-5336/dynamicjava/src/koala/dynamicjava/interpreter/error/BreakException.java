

package koala.dynamicjava.interpreter.error;


public class BreakException extends RuntimeException {
    
    private String label;

    
    public BreakException(String m) {
 super(m);
    }

    
    public BreakException(String m, String l) {
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
