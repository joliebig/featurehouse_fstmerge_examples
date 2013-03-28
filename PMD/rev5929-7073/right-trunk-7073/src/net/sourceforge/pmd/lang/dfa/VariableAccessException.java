
package net.sourceforge.pmd.lang.dfa;


public class VariableAccessException extends Exception {

    public VariableAccessException() {
        super("VariableAccess error."); 
    }

    public VariableAccessException(String message) {
        super(message);
    }
}
