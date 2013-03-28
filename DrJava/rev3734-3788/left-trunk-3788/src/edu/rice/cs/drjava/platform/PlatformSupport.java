

package edu.rice.cs.drjava.platform;

import javax.swing.Action;
import java.net.URL;


public interface PlatformSupport {
  
  
  public boolean isMacPlatform();
  
  
  public boolean isWindowsPlatform();
  
  
  public String getJavaSpecVersion();
  
  
  public boolean has13ToolsJar();
  
  
  public boolean has14ToolsJar();
  
  
  public boolean isUsingSystemLAF();
  
  
  public void beforeUISetup();
  
  
  public void afterUISetup(Action about, Action prefs, Action quit);
  
  
  public boolean openURL(URL address);
}
