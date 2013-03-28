

package edu.rice.cs.drjava;

import edu.rice.cs.drjava.config.FileConfiguration;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.plt.io.IOUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public final class ConfigFileTest extends DrJavaTestCase {
  private static final String CUSTOM_PROPS =
    "indent.level = 5\n" +
    "history.max.size = 1\n" +
    "definitions.keyword.color = #0000ff\n";
  
  
  
  public ConfigFileTest(String name) { super(name); }
  
  
  public void testCustomConfigFile() throws IOException {
    final File propsFile = IOUtil.createAndMarkTempFile("DrJavaProps", ".txt");
    IOUtil.writeStringToFile(propsFile, CUSTOM_PROPS);
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        DrJava.setPropertiesFile(propsFile.getAbsolutePath());
        DrJava._initConfig(); 
      } 
    });
    Utilities.clearEventQueue();
    Utilities.clearEventQueue();
    
    FileConfiguration config = DrJava.getConfig();
    assertEquals("custom indent level", 5, config.getSetting(OptionConstants.INDENT_LEVEL).intValue());
    assertEquals("custom history size", 1, config.getSetting(OptionConstants.HISTORY_MAX_SIZE).intValue());
    
    assertEquals("definitions.keyword.color", Color.blue, config.getSetting(OptionConstants.DEFINITIONS_KEYWORD_COLOR));
    assertEquals("default javac location", OptionConstants.JAVAC_LOCATION.getDefault(),
                 config.getSetting(OptionConstants.JAVAC_LOCATION));
  }
}