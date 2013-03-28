

package edu.rice.cs.drjava;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import javax.swing.UIManager;
import javax.swing.*;

import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.OutputStreamRedirector;
import edu.rice.cs.util.newjvm.ExecJVM;
import edu.rice.cs.util.classloader.ToolsJarClassLoader;
import edu.rice.cs.util.swing.Utilities;

import edu.rice.cs.drjava.ui.MainFrame;
import edu.rice.cs.drjava.ui.SplashScreen;
import edu.rice.cs.drjava.ui.ClassPathFilter;
import edu.rice.cs.drjava.ui.AWTExceptionHandler;
import edu.rice.cs.drjava.ui.SimpleInteractionsWindow;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.compiler.*;
import edu.rice.cs.drjava.config.FileConfiguration;
import edu.rice.cs.drjava.config.*;

import static edu.rice.cs.drjava.config.OptionConstants.*;


public class DrJavaRoot {
  
  
  public static final int FULL_JAVA = 0;
  public static final int ELEMENTARY_LEVEL = 1;
  public static final int INTERMEDIATE_LEVEL = 2;
  public static final int ADVANCED_LEVEL = 3;
  public static final String[] LANGUAGE_LEVEL_EXTENSIONS = new String[] {".java", ".dj0", ".dj1", ".dj2"};
  
  
  public static final String TEST_DEBUGGER_CLASS = "com.sun.jdi.Bootstrap";

  private static final PrintStream _consoleOut = System.out;
  private static final PrintStream _consoleErr = System.err;
  


  
  private static String[] _filesToOpen = new String[0];
  private static boolean _attemptingAugmentedClassPath = false;
  private static boolean _showDrJavaDebugConsole = false;
  private static SimpleInteractionsWindow _debugConsole = null;
  
  

  
  private static File _propertiesFile = new File(System.getProperty("user.home"), ".drjava");


  public static void main(String[] _filesToOpen) {
    

    
    boolean _showDebugConsole = false;
    int len = _filesToOpen.length;
    if (len > 0 && _filesToOpen[len - 1] == System.getProperty("path.separator")) {
      _showDebugConsole = true;
      len--;
    }
      
    
    try {
      String configLAFName = DrJava.getConfig().getSetting(LOOK_AND_FEEL);
      String currLAFName = UIManager.getLookAndFeel().getClass().getName();
      if (! configLAFName.equals(currLAFName)) UIManager.setLookAndFeel(configLAFName);
      
      
      
      

      
      final MainFrame mf = new MainFrame();
      

      
      
      AWTExceptionHandler.setFrame(mf);
      System.setProperty("sun.awt.exception.handler", "edu.rice.cs.drjava.ui.AWTExceptionHandler");
      
      _openCommandLineFiles(mf, _filesToOpen, len);
      
      
      SwingUtilities.invokeLater(new Runnable(){ public void run(){mf.setVisible(true);}});
      
      
      System.setOut(new PrintStream(new OutputStreamRedirector() {
        public void print(String s) { mf.getModel().systemOutPrint(s); }
      }));
      
      
      System.setErr(new PrintStream(new OutputStreamRedirector() {
        public void print(String s) { mf.getModel().systemErrPrint(s); }
      }));
      

      
      if (_showDebugConsole) showDrJavaDebugConsole(mf);
    }
    catch (Throwable t) {
      
      _consoleErr.println(t.getClass().getName() + ": " + t.getMessage());
      t.printStackTrace(_consoleErr);System.out.println("error thrown");
      new AWTExceptionHandler().handle(t);
    }
  }

  
  static void openCommandLineFiles(final MainFrame mf, final String[] filesToOpen) { 
    openCommandLineFiles(mf, filesToOpen, filesToOpen.length);
  }
  
  
  static void openCommandLineFiles(final MainFrame mf, final String[] filesToOpen, final int len) { 
    Utilities.invokeAndWait(new Runnable() { public void run() { _openCommandLineFiles(mf, filesToOpen, len); }});
  }
      
  private static void _openCommandLineFiles(MainFrame mf, String[] filesToOpen, int len) {

    for (int i = 0; i < len; i++) {
      String currFileName = filesToOpen[i];
      boolean isProjectFile = currFileName.endsWith(".pjt");
      final File file = new File(currFileName).getAbsoluteFile();
      FileOpenSelector command = new FileOpenSelector() {
        public File[] getFiles() { return new File[] {file}; }
      };
      try {
        if (isProjectFile) mf.openProject(command);
        else mf.getModel().openFile(command);
      }

      catch (FileNotFoundException ex) {
        
      }
      catch (SecurityException se) {
        
      }
      catch (AlreadyOpenException aoe) {
        
      }
      catch (FileMovedException aoe) {
        
      }
      catch (IOException ex) {
        
      }
      catch (Exception ex) { throw new UnexpectedException(ex); }  
    }
  }

  
  public static void showDrJavaDebugConsole(MainFrame mf) {
    if (_debugConsole == null) {
      _debugConsole = new SimpleInteractionsWindow("DrJava Debug Console") {
        protected void close() {
          dispose();
          _debugConsole = null;
        }
      };
      _debugConsole.defineConstant("mainFrame", mf);
      _debugConsole.defineConstant("model", mf.getModel());
      _debugConsole.defineConstant("config", DrJava.getConfig());
      _debugConsole.setInterpreterPrivateAccessible(true);
      _debugConsole.setVisible(true);
    }
    else  _debugConsole.toFront();
  }

  
  public static PrintStream consoleErr() { return  _consoleErr; }

  
  public static PrintStream consoleOut() { return  _consoleOut; }
  
}

