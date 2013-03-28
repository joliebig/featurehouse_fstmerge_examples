
package net.sourceforge.pmd.dfa;


public class SequenceException extends Exception {

    public SequenceException() {
        super("Sequence error."); 
    }

    public SequenceException(String message) {
        super(message);
    }
}
