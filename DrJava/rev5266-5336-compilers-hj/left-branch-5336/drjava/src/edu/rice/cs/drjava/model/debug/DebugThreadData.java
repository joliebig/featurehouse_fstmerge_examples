

package edu.rice.cs.drjava.model.debug;


public abstract class DebugThreadData {
  private final String _name;
  private final String _status;
  private final long _uniqueID;
  
  public DebugThreadData(String name, String status, long uniqueID) {
    _name = name;
    _status = status;
    _uniqueID = uniqueID;
  }
  
  
  public String getName() {
    return _name;
  }
  
  
  public String getStatus() {
    return _status;
  }
  
  public long getUniqueID() {
    return _uniqueID;
  }
  
  
  public abstract boolean isSuspended();
  
}
