
package net.sourceforge.pmd.dfa;


public class LinkerException extends Exception {

    public LinkerException() {
        super("An error occured by computing the data flow paths"); 
    }

    public LinkerException(String message) {
        super(message);
    }

}
