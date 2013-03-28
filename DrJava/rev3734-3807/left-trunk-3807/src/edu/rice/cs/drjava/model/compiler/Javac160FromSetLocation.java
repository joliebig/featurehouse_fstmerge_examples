

package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.FileOption;


public class Javac160FromSetLocation extends CompilerProxy implements OptionConstants {
  

  
  private static final String VERSION = System.getProperty("java.specification.version");

  
  public Javac160FromSetLocation() {
    super("edu.rice.cs.drjava.model.compiler.Javac160Compiler", _getClassLoader());
  }

  private static ClassLoader _getClassLoader() {
    
    File loc = DrJava.getConfig().getSetting(JAVAC_LOCATION);
    if (loc == FileOption.NULL_FILE) throw new RuntimeException("javac location not set");

    try {
      
      URL url = loc.toURL();
      return new URLClassLoader(new URL[] { url });
    }
    catch (MalformedURLException e) { throw new RuntimeException("malformed url exception"); }
  }

  public boolean isAvailable() { return VERSION.equals("1.6") && super.isAvailable(); }
  public String getName() { return "javac 1.6.0"; }
}