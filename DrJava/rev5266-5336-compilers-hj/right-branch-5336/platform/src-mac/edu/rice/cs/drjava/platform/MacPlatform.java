

package edu.rice.cs.drjava.platform;

import java.net.URL;
import com.apple.eawt.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;



class MacPlatform extends DefaultPlatform {
  
  public static MacPlatform ONLY = new MacPlatform();
  
  
  protected MacPlatform() {};
 
  public boolean openURL(URL address) {
    
    if (super.openURL(address)) {
      return true;
    }
    else {
      try {
        
        
        
        
        String addressString = address.toString();
        if (addressString.startsWith("file:/")) {
          String suffix = addressString.substring("file:/".length(), addressString.length());
          addressString = "file:///" + suffix;
        }

        
        
        Runtime.getRuntime().exec(new String[] { "open", addressString });
      }
      catch (Throwable t) {
        
        return false;
      }
    }
    
    
    return true;
  }
  
  
  public void beforeUISetup() {
    System.setProperty("apple.laf.useScreenMenuBar","true");
    
    
    ApplicationListener appListener = new ApplicationAdapter() {
      public void handleOpenFile(ApplicationEvent event) {
        if (event.getFilename()!=null) {
          edu.rice.cs.drjava.DrJavaRoot.handleRemoteOpenFile(new java.io.File(event.getFilename()), -1);
          event.setHandled(true);
        }
      }
    };
    
    
    Application appl = new Application();
    appl.addApplicationListener(appListener);
  }
   
  
  public void afterUISetup(final Action about, final Action prefs, final Action quit) {    
    ApplicationListener appListener = new ApplicationAdapter() {
      public void handleAbout(ApplicationEvent e) {
        about.actionPerformed(new ActionEvent(this, 0, "About DrJava"));
        e.setHandled(true);
      }

      public void handlePreferences(ApplicationEvent e) {
        prefs.actionPerformed(new ActionEvent(this, 0, "Preferences..."));
        e.setHandled(true);
      }

      public void handleQuit(ApplicationEvent e) {
        
        
        final ApplicationEvent ae = e;
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            quit.actionPerformed(new ActionEvent(this, 0, "Quit DrJava"));
            ae.setHandled(true);
          }
        });
      }
    };
    
    
    Application appl = new Application();
    appl.setEnabledPreferencesMenu(true);
    appl.addApplicationListener(appListener);
  }
  
  
  public boolean isMacPlatform() {
    return true;
  }
  
  
  public void setMnemonic(javax.swing.AbstractButton obj, int mnemonic) {
    
  }

  
  public void setMnemonic(javax.swing.AbstractButton obj, char mnemonic) {
    
  }

  
  public void setMnemonic(javax.swing.ButtonModel obj, int mnemonic) {
    
  }

  
  public void setMnemonic(javax.swing.JTabbedPane obj, int tabIndex, int mnemonic) {
    
  }

  
  public void setMnemonic(javax.swing.JTabbedPane obj, int mnemonic) {
    
  }
}
