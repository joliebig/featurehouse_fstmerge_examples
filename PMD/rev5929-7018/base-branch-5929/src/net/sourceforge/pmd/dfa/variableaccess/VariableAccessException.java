
package net.sourceforge.pmd.dfa.variableaccess;


public class VariableAccessException extends Exception {

    public VariableAccessException() {
        super("VariableAccess error."); 
    }

    public VariableAccessException(String message) {
        super(message);
    }
}
