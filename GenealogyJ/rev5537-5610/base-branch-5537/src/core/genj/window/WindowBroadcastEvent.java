
package genj.window;

import java.awt.Component;


public abstract class WindowBroadcastEvent {

  private Component source;
  private boolean isOutbound = true;
  
  
  protected WindowBroadcastEvent(Component source) {
    this.source = source;
  }
  
  
  public Component getSource() {
    return source;
  }
  
  
  public boolean isOutbound() {
    return isOutbound;
  }
  
  
  public boolean isInbound() {
    return !isOutbound;
  }
  
  
   void setInbound() {
    isOutbound = false;
  }

  
  protected void setBroadcasted() {
    
  }
  
}
