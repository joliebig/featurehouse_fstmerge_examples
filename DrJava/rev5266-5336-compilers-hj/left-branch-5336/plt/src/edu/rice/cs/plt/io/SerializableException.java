

package edu.rice.cs.plt.io;


public class SerializableException extends RuntimeException {
  private final String _originalClass;
  
  public SerializableException(Throwable original) {
    this(original, (original.getCause() == null) ? null : IOUtil.ensureSerializable(original.getCause()));
  }
  
  
  protected SerializableException(Throwable original, Throwable serializableCause) {
    super(original.getMessage(), serializableCause);
    _originalClass = original.getClass().getName();
    setStackTrace(original.getStackTrace());
  }
  
  public String originalClass() { return _originalClass; }
  
  public String toString() {
    String result = "Copy of " + _originalClass;
    if (getMessage() != null) { result += ": " + getMessage(); }
    return result;
  }

}
