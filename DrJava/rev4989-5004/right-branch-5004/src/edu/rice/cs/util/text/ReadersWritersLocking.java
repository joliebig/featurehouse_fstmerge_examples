

package edu.rice.cs.util.text;



public interface ReadersWritersLocking {
  
  
  public static final int UNREADLOCKED = 0;
  
  
  
  
  public void acquireReadLock();
  
  
  public void releaseReadLock();
  
  
  public void acquireWriteLock();
  
  
  public void releaseWriteLock();
  
  
  public boolean isReadLocked();

  
  public boolean isWriteLocked();
}

