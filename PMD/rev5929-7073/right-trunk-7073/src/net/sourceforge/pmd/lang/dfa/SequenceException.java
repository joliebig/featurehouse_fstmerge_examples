
package net.sourceforge.pmd.lang.dfa;


public class SequenceException extends Exception {

    public SequenceException() {
        super("Sequence error."); 
    }

    public SequenceException(String message) {
        super(message);
    }
}
