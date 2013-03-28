

package org.jmol.smiles;


public class InvalidSmilesException extends Exception {

  
  public InvalidSmilesException() {
    super();
  }

  
  public InvalidSmilesException(String message) {
    super(message);
  }

  
  public InvalidSmilesException(Throwable cause) {
    super(cause);
  }

  
  public InvalidSmilesException(String message, Throwable cause) {
    super(message, cause);
  }
}
