

package edu.rice.cs.drjava.model.repl;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.model.DefaultGlobalModel;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.compiler.LanguageLevelStackTraceMapper;
import edu.rice.cs.drjava.model.repl.newjvm.MainJVM;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.text.ConsoleDocumentInterface;
import edu.rice.cs.util.swing.Utilities;


public class DefaultInteractionsModel extends RMIInteractionsModel {
  


  
  
  protected final DefaultGlobalModel _model;
  
  
  public DefaultInteractionsModel(DefaultGlobalModel model, MainJVM jvm, ConsoleDocumentInterface cDoc, File wd) {
    super(jvm, cDoc, wd, DrJava.getConfig().getSetting(OptionConstants.HISTORY_MAX_SIZE).intValue(),
          WRITE_DELAY);
    _model = model;
    
    Boolean allow = DrJava.getConfig().getSetting(OptionConstants.RUN_WITH_ASSERT);
    _jvm.setAllowAssertions(allow.booleanValue());
    
    
    DrJava.getConfig().addOptionListener(OptionConstants.HISTORY_MAX_SIZE, _document.getHistoryOptionListener());
    DrJava.getConfig().addOptionListener(OptionConstants.RUN_WITH_ASSERT,
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
  
  
  public String getConsoleInput() { 
    String s = super.getConsoleInput();

    _model.systemInEcho(s);
    return s; 
  }
  
  
  protected void _interpreterResetFailed(final Throwable t) {
    Utilities.invokeLater(new Runnable() { 
      public void run() {
        _document.insertBeforeLastPrompt("Reset Failed! See the console tab for details." + StringOps.NEWLINE,
                                         InteractionsDocument.ERROR_STYLE);
         
        _model.systemErrPrint(StringOps.getStackTrace(t));  
      }
    });
  }   
  
  protected void _interpreterWontStart(final Exception e) {
    Utilities.invokeLater(new Runnable() { 
      public void run() {
        _document.insertBeforeLastPrompt("JVM failed to start.  Make sure a firewall is not blocking " +
                                         StringOps.NEWLINE +
                                         "inter-process communication.  See the console tab for details." +
                                         StringOps.NEWLINE,
                                         InteractionsDocument.ERROR_STYLE);
         
        _model.systemErrPrint(StringOps.getStackTrace(e));  
      }
    });
  }
  
  
  public void interpreterReady(File wd) {
    _model.resetInteractionsClassPath();  
    super.interpreterReady(wd);
  }
  
  
  public void _notifyInteractionStarted() { 
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
  
  
  protected void _notifyInterpreterExited(final int status) {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interpreterExited(status); } });
  }
  
  
  protected void _notifyInterpreterResetFailed(final Throwable t) {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interpreterResetFailed(t); } });
  }
  
  
  protected void _notifyInteractionIncomplete() {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interactionIncomplete(); } });
  }
  
  public ConsoleDocument getConsoleDocument() { return _model.getConsoleDocument(); }
  
  
  public StackTraceElement[] replaceLLException(StackTraceElement[] stackTrace) {
    
    LanguageLevelStackTraceMapper LLSTM = _model.getCompilerModel().getLLSTM();
    final List<File> files = new ArrayList<File>();
    for(OpenDefinitionsDocument odd: _model.getLLOpenDefinitionsDocuments()) { files.add(odd.getRawFile()); }
    
   return (LLSTM.replaceStackTrace(stackTrace,files));
  }  
  
  
  public List<File> getCompilerBootClassPath() {
    return _model.getCompilerModel().getActiveCompiler().additionalBootClassPathForInteractions();
  }
  
  
  public String transformCommands(String interactionsString) {
    return _model.getCompilerModel().getActiveCompiler().transformCommands(interactionsString);
  }
}
