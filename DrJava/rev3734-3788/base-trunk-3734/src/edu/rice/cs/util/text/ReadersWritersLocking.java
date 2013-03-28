

package edu.rice.cs.util.text;



public interface ReadersWritersLocking {
  
  
  
  
  public void acquireReadLock();
  
  
  public void releaseReadLock();
  
  
  public void acquireWriteLock();
  
  
  public void releaseWriteLock();
}

