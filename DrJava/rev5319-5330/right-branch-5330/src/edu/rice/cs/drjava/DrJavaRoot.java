

package edu.rice.cs.drjava;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.dnd.*;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.UIManager;
import javax.swing.*;

import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.OutputStreamRedirector;
import edu.rice.cs.util.swing.Utilities;

import edu.rice.cs.drjava.ui.MainFrame;
import edu.rice.cs.drjava.ui.DrJavaErrorWindow;
import edu.rice.cs.drjava.ui.DrJavaErrorHandler;
import edu.rice.cs.drjava.ui.SimpleInteractionsWindow;
import edu.rice.cs.drjava.ui.SplashScreen;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.platform.PlatformFactory;
import edu.rice.cs.drjava.config.*;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;

import static edu.rice.cs.drjava.config.OptionConstants.*;
import static edu.rice.cs.plt.debug.DebugUtil.error;
import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class DrJavaRoot {  
  
  public static final String TEST_DEBUGGER_CLASS = "com.sun.jdi.Bootstrap";
  
  public static final String PLASTIC_THEMES_PACKAGE = "com.jgoodies.looks.plastic.theme";
  
  private static final PrintStream _consoleOut = System.out;
  private static final PrintStream _consoleErr = System.err;
  

  
  private static SimpleInteractionsWindow _debugConsole = null;
  
  private static boolean anyLineNumbersSpecified = false;
  
  
  private static MainFrame _mainFrame = null;
  
  
  
  public static void main(final String[] args) {
    debug.log("Starting up");
    
    PlatformFactory.ONLY.beforeUISetup();
    

    
    if (!DrJava.handleCommandLineArgs(args)) {
      System.exit(0);
    }
    
    DrJava.warnIfLinuxWithCompiz();
    new SplashScreen().flash();
    
    final String[] filesToOpen = DrJava.getFilesToOpen();
    final int numFiles = filesToOpen.length;
    
    
    
    
    
    
    try {
      String configLAFName = DrJava.getConfig().getSetting(LOOK_AND_FEEL);
      String currLAFName = UIManager.getLookAndFeel().getClass().getName();
      String failureMessage =
        "DrJava could not load the configured theme for the Plastic Look and Feel.\n" +
        "If you've manually edited your configuration file, try \n" +
        "removing the key \"plastic.theme\" and restarting DrJava.\n" +
        "In the meantime, the system default Look and Feel will be used.\n";
      String failureTitle = "Theme not found";
      if(Utilities.isPlasticLaf(configLAFName)) {
        String themeName = PLASTIC_THEMES_PACKAGE + "." + DrJava.getConfig().getSetting(PLASTIC_THEMES);
        try {
          PlasticTheme theme = (PlasticTheme) Class.forName(themeName).getConstructor(new Class<?>[]{ }).newInstance();
          PlasticLookAndFeel.setPlasticTheme(theme);
          PlasticLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);
          com.jgoodies.looks.Options.setPopupDropShadowEnabled(true);
          if(! configLAFName.equals(currLAFName)) UIManager.setLookAndFeel(configLAFName);
        } catch(NoSuchMethodException nsmex) {
          JOptionPane.showMessageDialog(null, failureMessage, failureTitle, JOptionPane.ERROR_MESSAGE);
        } catch(SecurityException sex) {
          JOptionPane.showMessageDialog(null, failureMessage, failureTitle, JOptionPane.ERROR_MESSAGE);
        } catch(InstantiationException iex) {
          JOptionPane.showMessageDialog(null, failureMessage, failureTitle, JOptionPane.ERROR_MESSAGE);
        } catch(IllegalAccessException iaex) {
          JOptionPane.showMessageDialog(null, failureMessage, failureTitle, JOptionPane.ERROR_MESSAGE);
        } catch(IllegalArgumentException iaex) {
          JOptionPane.showMessageDialog(null, failureMessage, failureTitle, JOptionPane.ERROR_MESSAGE);
        } catch(InvocationTargetException itex) {
          JOptionPane.showMessageDialog(null, failureMessage, failureTitle, JOptionPane.ERROR_MESSAGE);
        }
      } else if (! configLAFName.equals(currLAFName)) {
        UIManager.setLookAndFeel(configLAFName);
      }
      
      
      
      

      _mainFrame = new MainFrame();

      
      
      DrJavaErrorWindow.setFrame(_mainFrame);
      Thread.setDefaultUncaughtExceptionHandler(DrJavaErrorHandler.INSTANCE);
      
      
      EventQueue.invokeLater(new Runnable(){ 
        public void run(){ 
          _mainFrame.start();
          
          
          
          
          _openCommandLineFiles(_mainFrame, filesToOpen, numFiles, true);
        } 
      });
      
      
      System.setOut(new PrintStream(new OutputStreamRedirector() {
        public void print(String s) { _mainFrame.getModel().systemOutPrint(s); }
      }));
      
      
      System.setErr(new PrintStream(new OutputStreamRedirector() {
        public void print(String s) { _mainFrame.getModel().systemErrPrint(s); }
      }));
      

      
      if (DrJava.getShowDebugConsole()) showDrJavaDebugConsole(_mainFrame);
    }
    catch(Throwable t) {
      error.log(t);
      
      _consoleErr.println(t.getClass().getName() + ": " + t.getMessage());
      t.printStackTrace(_consoleErr);
      System.out.println("error thrown");
      DrJavaErrorHandler.record(t);
    }
  }
  
  
  static void openCommandLineFiles(final MainFrame mf, final String[] filesToOpen, boolean jump) { 
    openCommandLineFiles(mf, filesToOpen, filesToOpen.length, jump);
  }
  
  
  static void openCommandLineFiles(final MainFrame mf, final String[] filesToOpen, final int len, final boolean jump) { 
    Utilities.invokeAndWait(new Runnable() { public void run() { _openCommandLineFiles(mf, filesToOpen, len, jump); }});
  }
  
  private static void _openCommandLineFiles(final MainFrame mf, String[] filesToOpen, int len, boolean jump) {
    
    

    anyLineNumbersSpecified = false;
    for (int i = 0; i < len; i++) {
      String currFileName = filesToOpen[i];
      
      
      
      
      int lineNo = -1;
      int pathSepIndex = currFileName.indexOf(File.pathSeparatorChar);
      if (pathSepIndex >= 0) {
        try {
          lineNo = Integer.valueOf(currFileName.substring(pathSepIndex+1));
          anyLineNumbersSpecified = true;
        }
        catch(NumberFormatException nfe) { lineNo = -1; }
        currFileName = currFileName.substring(0,pathSepIndex);
      }
      
      boolean isProjectFile =
        currFileName.endsWith(OptionConstants.PROJECT_FILE_EXTENSION) ||
        currFileName.endsWith(OptionConstants.PROJECT_FILE_EXTENSION2) ||
        currFileName.endsWith(OptionConstants.OLD_PROJECT_FILE_EXTENSION);
      final File file = new File(currFileName).getAbsoluteFile();
      FileOpenSelector command = new FileOpenSelector() {
        public File[] getFiles() { return new File[] {file}; }
      };
      try {
        if (isProjectFile) mf.openProject(command);
        else if (currFileName.endsWith(OptionConstants.EXTPROCESS_FILE_EXTENSION)) MainFrame.openExtProcessFile(file);
        else {
          if (jump && (lineNo >= 0)) {
            
            mf.open(command);
            mf._jumpToLine(lineNo); 
          }
          else {
            
            mf.getModel().openFile(command);
          }
        }
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
      



      _debugConsole.setVisible(true);
    }
    else  _debugConsole.toFront();
  }
  
  
  public static PrintStream consoleErr() { return  _consoleErr; }
  
  
  public static PrintStream consoleOut() { return  _consoleOut; }
  
  
  public static void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
    _mainFrame.dragEnter(dropTargetDragEvent);
  }
  
  
  public static void drop(DropTargetDropEvent dropTargetDropEvent) {
    _mainFrame.drop(dropTargetDropEvent);
  }

  
  public static void installModalWindowAdapter(final Window w,
                                               final Runnable1<? super WindowEvent> toFrontAction,
                                               final Runnable1<? super WindowEvent> closeAction) {
    _mainFrame.installModalWindowAdapter(w, toFrontAction, closeAction);
  }
  
  
  public static void removeModalWindowAdapter(Window w) {
    _mainFrame.removeModalWindowAdapter(w);
  }
  
  
  public static void handleRemoteOpenFile(File f, int lineNo) {
    DrJava._log.log("DrJavaRoot.handleRemoteOpenFile, f=" + f);
    if (_mainFrame != null) { 
      DrJava._log.log("\tcalling _mainFrame");
      _mainFrame.handleRemoteOpenFile(f, lineNo);
    }
    else {
      DrJava._log.log("\tadded to _filesToOpen");
      DrJava.addFileToOpen(f.getAbsolutePath());
    }
  }
}

