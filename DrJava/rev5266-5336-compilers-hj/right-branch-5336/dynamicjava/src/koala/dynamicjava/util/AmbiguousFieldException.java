

package koala.dynamicjava.util;



public class AmbiguousFieldException extends Exception {
    
    public AmbiguousFieldException() {
        this("");
    }

    
    public AmbiguousFieldException(String s) {
        super(s);
    }
}
