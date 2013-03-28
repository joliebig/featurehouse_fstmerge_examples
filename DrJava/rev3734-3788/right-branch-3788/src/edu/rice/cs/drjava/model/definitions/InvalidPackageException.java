

package edu.rice.cs.drjava.model.definitions;


public class InvalidPackageException extends RuntimeException {
  private final int _location;

   
  public InvalidPackageException(int location, String message) {
    super(message);
    _location = location;
  }

  
  public int getLocation() {
    return _location;
  }
}
