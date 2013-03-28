

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
  
  
  public void setMnemonic(javax.swing.AbstractButton obj, int mnemonic);

  
  public void setMnemonic(javax.swing.AbstractButton obj, char mnemonic);

  
  public void setMnemonic(javax.swing.ButtonModel obj, int mnemonic);

  
  public void setMnemonicAt(javax.swing.JTabbedPane obj, int tabIndex, int mnemonic);
  
  
  public boolean canRegisterFileExtensions();
  
  
  public boolean registerDrJavaFileExtensions();

  
  public boolean unregisterDrJavaFileExtensions();
  
  
  public boolean areDrJavaFileExtensionsRegistered();
  
  
  public boolean registerJavaFileExtension();
  
  
  public boolean unregisterJavaFileExtension();
  
  
  public boolean isJavaFileExtensionRegistered();
}
