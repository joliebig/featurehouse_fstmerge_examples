
package org.openscience.jmol.app.webexport;


import java.io.*;
import javax.swing.*;

import org.jmol.export.history.HistoryFile;
import org.jmol.i18n.GT;

public class WebMaker extends JPanel {

  
  
  private final static String WEB_MAKER_WINDOW_NAME = "JmolWebPageMaker";

  public static void main(String[] args) {
    System.out
        .println("Jmol_Web_Page_Maker is running as a standalone application");
    
    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        WebExport.createAndShowGUI(null, historyFile, WEB_MAKER_WINDOW_NAME);
      }
    });
  }
  
  static HistoryFile historyFile;
  
  static HistoryFile getHistoryFile() {
    return historyFile;
  }

  static {
    if (System.getProperty("javawebstart.version") != null) {

      
      
      System.setSecurityManager(null);
    }
    if (System.getProperty("user.home") == null) {
      System.err.println(
          GT._("Error starting Jmol: the property 'user.home' is not defined."));
      System.exit(1);
    }
    File ujmoldir = new File(new File(System.getProperty("user.home")),
                      ".jmol");
    ujmoldir.mkdirs();
    historyFile = new HistoryFile(new File(ujmoldir, "history"),
        "Jmol's persistent values");
  }
}
