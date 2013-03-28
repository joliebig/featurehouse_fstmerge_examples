

package edu.rice.cs.drjava.model.repl;

import java.io.File;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.model.DefaultGlobalModel;
import edu.rice.cs.drjava.model.repl.newjvm.MainJVM;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.text.*;
import edu.rice.cs.util.swing.Utilities;


public class DefaultInteractionsModel extends RMIInteractionsModel {
  



  
  protected final DefaultGlobalModel _model;

  
  public DefaultInteractionsModel(DefaultGlobalModel model, MainJVM control, EditDocumentInterface adapter, File wd) {
    super(control, adapter, wd, DrJava.getConfig().getSetting(OptionConstants.HISTORY_MAX_SIZE).intValue(),
          DefaultGlobalModel.WRITE_DELAY);
    _model = model;
    
    Boolean allow = DrJava.getConfig().getSetting(OptionConstants.JAVAC_ALLOW_ASSERT);
    _jvm.setAllowAssertions(allow.booleanValue());
    
    
    DrJava.getConfig().addOptionListener(OptionConstants.HISTORY_MAX_SIZE, _document.getHistoryOptionListener());
    DrJava.getConfig().addOptionListener(OptionConstants.JAVAC_ALLOW_ASSERT,
                                         new OptionListener<Boolean>() {
      public void optionChanged(OptionEvent<Boolean> oce) {
        _jvm.setAllowAssertions(oce.value.booleanValue());
      }
    });
  }


  
  public void replSystemOutPrint(String s) {
    super.replSystemOutPrint(s);
    _model.systemOutPrint(s);
  }

  
  public void replSystemErrPrint(String s) {
    super.replSystemErrPrint(s);
    _model.systemErrPrint(s);
  }

  
  protected void _interpreterResetFailed(Throwable t) {
    _document.insertBeforeLastPrompt("Reset Failed! See the console tab for details." + _newLine,
                                     InteractionsDocument.ERROR_STYLE);
    
    _model.systemErrPrint(StringOps.getStackTrace(t));
  }

  
  public void interpreterReady(File wd) {

    _model.resetInteractionsClassPath();
    super.interpreterReady(wd);
  }

  
  protected void _notifyInteractionStarted() { 
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interactionStarted(); } });
  }
  
  
  protected void _notifyInteractionEnded() { 
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interactionEnded(); } });
  }

  
  protected void _notifySyntaxErrorOccurred(final int offset, final int length) {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interactionErrorOccurred(offset,length); } });
  }

  
  protected void _notifyInterpreterChanged(final boolean inProgress) {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interpreterChanged(inProgress); } });
  }

  
  protected void _notifyInterpreterResetting() { 
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interpreterResetting(); } });
  }

  
  public void _notifyInterpreterReady(final File wd) { 

    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interpreterReady(wd); } });
  }

  
  protected void _notifySlaveJVMUsed(final File wd) { 
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.slaveJVMUsed(); } });
  }
  
  
  protected void _notifyInterpreterExited(final int status) {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interpreterExited(status); } });
  }

  
  protected void _notifyInterpreterResetFailed(final Throwable t) {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interpreterResetFailed(t); } });
  }

  
  protected void _notifyInteractionIncomplete() {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interactionIncomplete(); } });
  }
  
   
  protected void _notifySlaveJVMUsed() {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.slaveJVMUsed(); } });
  }
  
  public ConsoleDocument getConsoleDocument() { return _model.getConsoleDocument(); }
}
