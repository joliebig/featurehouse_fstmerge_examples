

package edu.rice.cs.util.text;


public class EditDocumentException extends RuntimeException {
  private Throwable _value;

   
  public EditDocumentException(Throwable value) {
    super(value.toString());
    _value = value;
  }

   
  public EditDocumentException(Throwable value, String msg) {
    super(msg + ": " + value.toString());
    _value = value;
  }

  
  public Throwable getContainedThrowable() {
    return _value;
  }
}
