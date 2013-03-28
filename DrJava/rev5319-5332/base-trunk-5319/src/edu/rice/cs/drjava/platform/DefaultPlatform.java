

package edu.rice.cs.drjava.platform;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.Configuration;
import edu.rice.cs.drjava.config.OptionConstants;

import edu.rice.cs.util.ArgumentTokenizer;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.StringOps;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.List;


class DefaultPlatform implements PlatformSupport {
  
  public static DefaultPlatform ONLY = new DefaultPlatform();

  
  protected DefaultPlatform() { }

  
  public boolean isUsingSystemLAF() {
    String sysLAF = UIManager.getSystemLookAndFeelClassName();
    String curLAF = UIManager.getLookAndFeel().getClass().getName();
    return (sysLAF.equals(curLAF));
  }
  
  
  public void beforeUISetup() { }

  
  public void afterUISetup(Action about, Action prefs, Action quit) { }

  
  public boolean isMacPlatform() { return false; }

  
  public boolean isWindowsPlatform() { return false; }

  
  public String getJavaSpecVersion() {
    return System.getProperty("java.specification.version");
  }

  
  @SuppressWarnings("unchecked")
  private boolean _javadocMainHasExecuteMethod(Class main) {
    try {
      Class<String[]>[] arr = new Class[]{String[].class};
      main.getMethod("execute", arr);
      return true;
    }
    catch (Throwable t) { return false; }
  }

  
  public boolean openURL(URL address) {
    
    Configuration config = DrJava.getConfig();
    File exe = config.getSetting(OptionConstants.BROWSER_FILE);
    String command = config.getSetting(OptionConstants.BROWSER_STRING);

    
    if ((exe == FileOps.NULL_FILE) && (command.equals(""))) {
      
      return false;
    }
    else {
      String addr = address.toString();
      if (command.equals("")) {
        
        command = addr;
      }
      else {
        
        String tag = "<URL>";
        if (command.indexOf(tag) != -1) {
          command = StringOps.replace(command, tag, addr);
        }
        else {
          
          command = command + " " + addr;
        }
      }

      
      List<String> args = ArgumentTokenizer.tokenize(command);

      
      if (exe != FileOps.NULL_FILE) args.add(0, exe.getAbsolutePath());

      
      try {
        
        Runtime.getRuntime().exec(args.toArray(new String[args.size()]));
      }
      catch (Throwable t) {
        
        return false;
      }
    }

    
    return true;
  }
  
  
  public void setMnemonic(javax.swing.AbstractButton obj, int mnemonic) {
    
    obj.setMnemonic(mnemonic);
  }

  
  public void setMnemonic(javax.swing.AbstractButton obj, char mnemonic) {
    
    obj.setMnemonic(mnemonic);
  }
  
  
  public void setMnemonic(javax.swing.ButtonModel obj, int mnemonic) {
    
    obj.setMnemonic(mnemonic);
  }
  
  
  public void setMnemonicAt(javax.swing.JTabbedPane obj, int tabIndex, int mnemonic) {
    
    obj.setMnemonicAt(tabIndex, mnemonic);
  }
  
  
  public boolean canRegisterFileExtensions() { return false; }
  
  
  public boolean registerDrJavaFileExtensions() { return false; }

  
  public boolean unregisterDrJavaFileExtensions() { return false; }
  
  
  public boolean areDrJavaFileExtensionsRegistered() { return false; }
  
  
  public boolean registerJavaFileExtension() { return false; }
  
  
  public boolean unregisterJavaFileExtension() { return false; }
  
  
  public boolean isJavaFileExtensionRegistered() { return false; }
}
