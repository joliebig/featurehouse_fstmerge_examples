

package org.jmol.api;

public interface JmolCallbackListener {


  public void setCallbackFunction(String callbackType, String callbackFunction);
  
  public void notifyCallback(int type, Object[] data);

  public boolean notifyEnabled(int callback_pick);

}
