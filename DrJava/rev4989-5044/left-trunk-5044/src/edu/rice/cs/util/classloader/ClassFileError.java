
package edu.rice.cs.util.classloader;

public class ClassFileError extends LinkageError {
  
  private String _className;
  private String _path;
  private LinkageError _error;
  public ClassFileError(String c, String p, LinkageError e) {
    _className = c;
    _path = p;
    _error = e;
  }
  public String getClassName() { return _className; }
  public String getCanonicalPath() { return _path; }
  public LinkageError getError() { return _error; }
}
  