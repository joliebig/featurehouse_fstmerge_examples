

package edu.rice.cs.drjava.ui;

import javax.swing.JButton;
import java.util.List;
import java.util.ArrayList;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.config.OptionConstants;


public class DrJavaErrorHandler {
  
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

  
  public void handle(Throwable thrown) {
    System.out.println("Unhandled exception: " + thrown);
    record(thrown);
  }
  
  
  public static void record(Throwable thrown) {
    _errors.add(thrown);
    if (_errorsButton!=null) {
      _errorsButton.setVisible(true);
    }
    if ((_errors.size()==1) && (DrJava.getConfig().getSetting(OptionConstants.DIALOG_DRJAVA_ERROR_POPUP_ENABLED).booleanValue())) {
      new DrJavaErrorPopup(DrJavaErrorWindow.getFrame(), thrown).setVisible(true);
    }
  }
  
  
  public static void log(String message) {
    record(new LoggedCondition(message));
  }
  
  
  public static class LoggedCondition extends Throwable {
    public LoggedCondition(String s) { super(s); }
  }
}
