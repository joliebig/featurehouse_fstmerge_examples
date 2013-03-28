

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
  
  private DrJavaErrorHandler() {}
  
  
  public void uncaughtException(Thread t, Throwable thrown) {
    record(thrown);
  }
  
  
  private static ArrayList<Throwable> _errors = new ArrayList<Throwable>();
  
  
  private static JButton _errorsButton;
  
  
  public static void setButton(JButton b) { _errorsButton = b; }  
  
  
  public static JButton getButton() { return _errorsButton; }  
  
  
  public static int getErrorCount() { return _errors.size(); }
  
  
  public static Throwable getError(int index) {
    if ((index>=0) && (index<_errors.size())) {
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
          else {
            try {
              if (thrown instanceof com.sun.jdi.VMOutOfMemoryException) {
                
                JFrame f = DrJavaErrorWindow.getFrame();
                if (f instanceof MainFrame) {
                  MainFrame mf = (MainFrame)f;
                  mf.askToIncreaseSlaveMaxHeap();
                }
              }
            }
            catch(NoClassDefFoundError ncdfe) {  }
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
  
  
  public static void log(String message) { record(new LoggedCondition(message)); }
  
  
  public static class LoggedCondition extends Throwable {
    public LoggedCondition(String s) { super(s); }
  }
}
