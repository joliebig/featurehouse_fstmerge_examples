

package edu.rice.cs.drjava.platform;


public class PlatformFactory {
  
  
  public static final PlatformSupport ONLY = getPlatformSupport();
  
  
  private static PlatformSupport getPlatformSupport() {
    
    
    String os = System.getProperty("os.name").toLowerCase();
    
    if (os.startsWith("mac os x")) return MacPlatform.ONLY;
    else if (os.startsWith("windows")) return WindowsPlatform.ONLY;
    else return DefaultPlatform.ONLY; 
  }
}
