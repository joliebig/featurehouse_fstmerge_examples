
package org.jmol.multitouch.jni;

import org.jmol.api.JmolMultiTouchAdapter;
import org.jmol.api.JmolMultiTouchClient;
import org.jmol.multitouch.JmolMultiTouchClientAdapter;
import org.jmol.viewer.Viewer;

public class JmolJniClientAdapter extends JmolMultiTouchClientAdapter implements
    JmolMultiTouchAdapter {

  
  

  static {
    System.loadLibrary("JmolMultiTouchJNI");
  }  
 
  native void nativeMethod(); 
  
  public void dispose() {
    
  }

  public void setMultiTouchClient(Viewer viewer, JmolMultiTouchClient client,
                                  boolean isSimulation) {
    try {
      
      
      
      nativeMethod();
    } catch (Exception e) {
      System.out.println("JmolJniClientAdapter error -- nativeMethod");
    }
  }
  
  public void callback() {
    
    
    System.out.println("In Java");
  }

}
