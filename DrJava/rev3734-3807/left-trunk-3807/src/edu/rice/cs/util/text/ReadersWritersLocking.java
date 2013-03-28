

package edu.rice.cs.util.text;



public interface ReadersWritersLocking {
  
  
  
  
  public void readLock();
  
  
  public void readUnlock();
  
  
  public void modifyLock();
  
  
  public void modifyUnlock();
}

