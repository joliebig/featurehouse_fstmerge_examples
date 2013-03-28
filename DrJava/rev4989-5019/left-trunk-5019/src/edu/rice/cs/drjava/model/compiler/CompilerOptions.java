

package edu.rice.cs.drjava.model.compiler;

import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.DrJava;

import java.util.HashMap;



public class CompilerOptions implements OptionConstants {
  
  private static boolean SHOW_UNCHECKED = DrJava.getConfig().getSetting(SHOW_UNCHECKED_WARNINGS);
  private static boolean SHOW_DEPRECATION = DrJava.getConfig().getSetting(SHOW_DEPRECATION_WARNINGS);
  private static boolean SHOW_PATH = DrJava.getConfig().getSetting(SHOW_PATH_WARNINGS);
  private static boolean SHOW_SERIAL = DrJava.getConfig().getSetting(SHOW_SERIAL_WARNINGS);
  private static boolean SHOW_FINALLY = DrJava.getConfig().getSetting(SHOW_FINALLY_WARNINGS);
  private static boolean SHOW_FALLTHROUGH = DrJava.getConfig().getSetting(SHOW_FALLTHROUGH_WARNINGS);
   
  private static WarningOptionListener wol = new WarningOptionListener();
  
  
  private static class WarningOptionListener implements OptionListener<Boolean> {
    public void optionChanged(OptionEvent<Boolean> oce) {
      updateWarnings();
    }
  }
  
  public static void updateWarnings() {
    SHOW_UNCHECKED = DrJava.getConfig().getSetting(SHOW_UNCHECKED_WARNINGS);
    SHOW_DEPRECATION = DrJava.getConfig().getSetting(SHOW_DEPRECATION_WARNINGS);
    SHOW_PATH = DrJava.getConfig().getSetting(SHOW_PATH_WARNINGS);
    SHOW_SERIAL = DrJava.getConfig().getSetting(SHOW_SERIAL_WARNINGS);
    SHOW_FINALLY = DrJava.getConfig().getSetting(SHOW_FINALLY_WARNINGS);
    SHOW_FALLTHROUGH = DrJava.getConfig().getSetting(SHOW_FALLTHROUGH_WARNINGS);
  }
  
  
  static {
    DrJava.getConfig().addOptionListener( OptionConstants.SHOW_UNCHECKED_WARNINGS, wol);
    DrJava.getConfig().addOptionListener( OptionConstants.SHOW_DEPRECATION_WARNINGS, wol);
    DrJava.getConfig().addOptionListener( OptionConstants.SHOW_PATH_WARNINGS, wol);
    DrJava.getConfig().addOptionListener( OptionConstants.SHOW_SERIAL_WARNINGS, wol);
    DrJava.getConfig().addOptionListener( OptionConstants.SHOW_FINALLY_WARNINGS, wol);
    DrJava.getConfig().addOptionListener( OptionConstants.SHOW_FALLTHROUGH_WARNINGS, wol);    
  }
  
  public static HashMap<String,String> getOptions(boolean warningsEnabled) {    
    HashMap<String,String> options = new HashMap<String,String>();
    if (warningsEnabled) {
      if (SHOW_UNCHECKED) {
        options.put("-Xlint:unchecked","");
      }
      
      if (SHOW_DEPRECATION) {
        options.put("-Xlint:deprecation","");
      }
      
      if (SHOW_PATH) {
        options.put("-Xlint:path","");
      }
      
      if (SHOW_SERIAL) {
        options.put("-Xlint:serial","");
      }
      
      if (SHOW_FINALLY) {
        options.put("-Xlint:finally","");
      }
      
      if (SHOW_FALLTHROUGH) {
        options.put("-Xlint:fallthrough","");
        options.put("-Xlint:switchcheck",""); 
      }
    }
    
    
    return options;
  }
}