

package edu.rice.cs.drjava.ui;

import javax.swing.JButton;
import java.util.ArrayList;
import javax.swing.JFrame;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.drjava.config.OptionConstants;

import edu.rice.cs.util.Log;


public class DrJavaErrorHandler implements Thread.UncaughtExceptionHandler {
  
  public static final DrJavaErrorHandler INSTANCE = new DrJavaErrorHandler();
  
  private DrJavaErrorHandler() { }
  
  
  public void uncaughtException(Thread t, Throwable thrown) {
    record(thrown);
  }
  
  
  private static ArrayList<Throwable> _errors = new ArrayList<Throwable>();
  
  
  private static JButton _errorsButton;
  
  
  public static void setButton(JButton b) { _errorsButton = b; }  
  
  
  public static JButton getButton() { return _errorsButton; }  
  
  
  public static int getErrorCount() { return _errors.size(); }
  
  
  public static Throwable getError(int index) {
    if ((index >= 0) && (index < _errors.size())) {
      return _errors.get(index);
    }
    else {
      return new UnexpectedException("Error in DrJavaErrorHandler");
    }
  }
  
  
  public static void clearErrors() { _errors.clear(); }

  
  public static void record(final Throwable thrown) {
    Utilities.invokeLater(new Runnable() {
      public void run() {
        try { 
          if (thrown instanceof OutOfMemoryError) {
            
            Runtime.getRuntime().gc();
            JFrame f = DrJavaErrorWindow.getFrame();
            if (f instanceof MainFrame) {
              MainFrame mf = (MainFrame)f;
              mf.askToIncreaseMasterMaxHeap();
            }
          }
          else if (thrown.toString().startsWith("com.sun.jdi.VMOutOfMemoryException")) {
            
            JFrame f = DrJavaErrorWindow.getFrame();
            if (f instanceof MainFrame) {
              MainFrame mf = (MainFrame)f;
              mf.askToIncreaseSlaveMaxHeap();
            }
          }
          else if (isSwingBugArrayIndexOufOfBoundsExceptionInCharWidth(thrown)) {
            
            return;
          }
          _errors.add(thrown);
          if (_errorsButton != null) {
            _errorsButton.setVisible(true);
          }
          if (_errors.size() == 1 && ! Utilities.TEST_MODE &&
              DrJava.getConfig().getSetting(OptionConstants.DIALOG_DRJAVA_ERROR_POPUP_ENABLED).booleanValue()) {
            DrJavaErrorPopup popup = new DrJavaErrorPopup(DrJavaErrorWindow.getFrame(), thrown);
            Utilities.setPopupLoc(popup, popup.getOwner());
            popup.setVisible(true);
          }
        }
        catch(Throwable t) {  }
      }
    });
  }

  
  public static boolean isSwingBugArrayIndexOufOfBoundsExceptionInCharWidth(Throwable thrown) {
    
    if (!edu.rice.cs.plt.reflect.JavaVersion.CURRENT_FULL.vendor().
          equals(edu.rice.cs.plt.reflect.JavaVersion.VendorType.SUN)) return false;
    
    
    if (edu.rice.cs.plt.reflect.JavaVersion.parseFullVersion("6.0_18","Sun","Sun").
          compareTo(edu.rice.cs.plt.reflect.JavaVersion.CURRENT_FULL)<=0) return false;
    
    if (!(thrown instanceof ArrayIndexOutOfBoundsException)) return false;
    
    StackTraceElement[] stes = new StackTraceElement[] {
      new StackTraceElement("sun.font.FontDesignMetrics","charsWidth",null,-1),
      new StackTraceElement("javax.swing.text.Utilities","getTabbedTextOffset",null,-1),
      new StackTraceElement("javax.swing.text.Utilities","getTabbedTextOffset",null,-1),
      new StackTraceElement("javax.swing.text.Utilities","getTabbedTextOffset",null,-1),
      new StackTraceElement("javax.swing.text.PlainView","viewToModel",null,-1),
      new StackTraceElement("javax.swing.plaf.basic.BasicTextUI$RootView","viewToModel",null,-1),
      new StackTraceElement("javax.swing.plaf.basic.BasicTextUI","viewToModel",null,-1)
    };
    
    StackTraceElement[] stesBottom = new StackTraceElement[] {
      new StackTraceElement("java.awt.EventQueue","dispatchEvent",null,-1),
      new StackTraceElement("java.awt.EventDispatchThread","pumpOneEventForFilters",null,-1),
      new StackTraceElement("java.awt.EventDispatchThread","pumpEventsForFilter",null,-1),
      new StackTraceElement("java.awt.EventDispatchThread","pumpEventsForHierarchy",null,-1),
      new StackTraceElement("java.awt.EventDispatchThread","pumpEvents",null,-1),
      new StackTraceElement("java.awt.EventDispatchThread","pumpEvents",null,-1),
      new StackTraceElement("java.awt.EventDispatchThread","run",null,-1)
    };

    StackTraceElement[] tst = thrown.getStackTrace();
    
    if (tst.length<stes.length+stesBottom.length) return false;
    
    for(int i=0; i<stes.length; ++i) {
      if (!stes[i].equals(tst[i])) return false;
    }

    for(int i=0; i<stesBottom.length; ++i) {
      if (!stesBottom[stesBottom.length-i-1].equals(tst[tst.length-i-1])) return false;
    }
    
    return true;
  }

  
  public static void simulateSwingBugArrayIndexOufOfBoundsExceptionInCharWidth() {      
    StackTraceElement[] stes = new StackTraceElement[] {
      new StackTraceElement("sun.font.FontDesignMetrics","charsWidth",null,-1),
        new StackTraceElement("javax.swing.text.Utilities","getTabbedTextOffset",null,-1),
        new StackTraceElement("javax.swing.text.Utilities","getTabbedTextOffset",null,-1),
        new StackTraceElement("javax.swing.text.Utilities","getTabbedTextOffset",null,-1),
        new StackTraceElement("javax.swing.text.PlainView","viewToModel",null,-1),
        new StackTraceElement("javax.swing.plaf.basic.BasicTextUI$RootView","viewToModel",null,-1),
        new StackTraceElement("javax.swing.plaf.basic.BasicTextUI","viewToModel",null,-1),
        
        new StackTraceElement("foo","bar",null,-1),
        new StackTraceElement("foo","bar",null,-1),
        
        new StackTraceElement("java.awt.EventQueue","dispatchEvent",null,-1),
        new StackTraceElement("java.awt.EventDispatchThread","pumpOneEventForFilters",null,-1),
        new StackTraceElement("java.awt.EventDispatchThread","pumpEventsForFilter",null,-1),
        new StackTraceElement("java.awt.EventDispatchThread","pumpEventsForHierarchy",null,-1),
        new StackTraceElement("java.awt.EventDispatchThread","pumpEvents",null,-1),
        new StackTraceElement("java.awt.EventDispatchThread","pumpEvents",null,-1),
        new StackTraceElement("java.awt.EventDispatchThread","run",null,-1)
    };
    ArrayIndexOutOfBoundsException t = new ArrayIndexOutOfBoundsException(63);
    t.setStackTrace(stes);
    t.printStackTrace(System.out);
    throw t;     
  }

  
  public static void log(String message) { record(new LoggedCondition(message)); }
  
  
  public static class LoggedCondition extends Throwable {
    public LoggedCondition(String s) { super(s); }
  }
}
