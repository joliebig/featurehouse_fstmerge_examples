package net.sf.jabref;


public interface ErrorMessageDisplay {

    
    public void reportError(String errorMessage);

    
    public void reportError(String errorMessage, Exception exception);

}
