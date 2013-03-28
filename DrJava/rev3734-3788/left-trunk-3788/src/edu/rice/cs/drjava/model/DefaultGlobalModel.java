

package edu.rice.cs.drjava.model;

import javax.swing.text.BadLocationException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.SwingUtilities;

import edu.rice.cs.util.ClassPathVector;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.drjava.model.FileSaveSelector;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.util.swing.Utilities;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.config.OptionListener;

import edu.rice.cs.drjava.model.definitions.ClassNameNotFoundException;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;
import edu.rice.cs.drjava.model.debug.Breakpoint;
import edu.rice.cs.drjava.model.debug.Debugger;
import edu.rice.cs.drjava.model.debug.DebugException;
import edu.rice.cs.drjava.model.debug.JPDADebugger;
import edu.rice.cs.drjava.model.debug.NoDebuggerAvailable;
import edu.rice.cs.drjava.model.debug.DebugListener;
import edu.rice.cs.drjava.model.debug.DebugWatchData;
import edu.rice.cs.drjava.model.debug.DebugThreadData;
import edu.rice.cs.drjava.model.repl.DefaultInteractionsModel;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.model.repl.InteractionsListener;
import edu.rice.cs.drjava.model.repl.InteractionsScriptModel;
import edu.rice.cs.drjava.model.repl.newjvm.MainJVM;
import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.drjava.model.compiler.CompilerModel;
import edu.rice.cs.drjava.model.compiler.DefaultCompilerModel;
import edu.rice.cs.drjava.model.junit.DefaultJUnitModel;
import edu.rice.cs.drjava.model.junit.JUnitModel;

import java.io.*;


public class DefaultGlobalModel extends AbstractGlobalModel {
  
  
  
  
  
  
  protected final InteractionsDJDocument _interactionsDocument;
  
  
  final MainJVM _jvm = new MainJVM(getWorkingDirectory());
  
  
  protected DefaultInteractionsModel _interactionsModel;
  
  
  protected InteractionsListener _interactionsListener = new InteractionsListener() {
    public void interactionStarted() { }
    
    public void interactionEnded() { }
    
    public void interactionErrorOccurred(int offset, int length) { }
    
    public void interpreterResetting() { }
    
    public void interpreterReady(File wd) {
      File buildDir = _state.getBuildDirectory();
      if (buildDir != null) {
        
        try {
          _jvm.addBuildDirectoryClassPath(new File(buildDir.getAbsolutePath()).toURL());
        } catch(MalformedURLException murle) {
          
          throw new RuntimeException(murle);
        }
      }
    }
    
    public void interpreterResetFailed(Throwable t) { }
    
    public void interpreterExited(int status) { }
    
    public void interpreterChanged(boolean inProgress) { }
    
    public void interactionIncomplete() { }
    
    public void slaveJVMUsed() { }
  };
  
  private CompilerListener _clearInteractionsListener =
    new CompilerListener() {
    public void compileStarted() { }
    
    public void compileEnded(File workDir, File[] excludedFiles) {
      
      if ( ((_compilerModel.getNumErrors() == 0) || (_compilerModel.getCompilerErrorModel().hasOnlyWarnings()))
            && ! _junitModel.isTestInProgress() && _resetAfterCompile) {
        resetInteractions(workDir);  
      }
    }
    public void saveBeforeCompile() { }
    public void saveUntitled() { }
  };
    
  
  
  
  private final CompilerModel _compilerModel = new DefaultCompilerModel(this);
  
  
  private boolean _resetAfterCompile = true;
  
  
  
  
  private final DefaultJUnitModel _junitModel = new DefaultJUnitModel(_jvm, _compilerModel, this);
  
  
  
  
  protected JavadocModel _javadocModel = new DefaultJavadocModel(this);
  
  
  
  
  private Debugger _debugger = NoDebuggerAvailable.ONLY;
  
  
  
  
  public DefaultGlobalModel() {
    super();

    _interactionsDocument = new InteractionsDJDocument();

    _interactionsModel = new DefaultInteractionsModel(this, _jvm, _interactionsDocument, getWorkingDirectory());
    _interactionsModel.addListener(_interactionsListener);
    _jvm.setInteractionsModel(_interactionsModel);
    _jvm.setJUnitModel(_junitModel);
    
    _jvm.setOptionArgs(DrJava.getConfig().getSetting(JVM_ARGS));
    DrJava.getConfig().addOptionListener(JVM_ARGS, new OptionListener<String>() {
      public void optionChanged(OptionEvent<String> oe) {
        _jvm.setOptionArgs(oe.value);
      }
    }); 

    _createDebugger();
        
    
    _interactionsModel.addListener(_notifier);
    _compilerModel.addListener(_notifier);
    _junitModel.addListener(_notifier);
    _javadocModel.addListener(_notifier);
    

        
    
    
    
    _compilerModel.addListener(_clearInteractionsListener);
    
    
    _jvm.startInterpreterJVM();
    


  }
  






  


  
  
  public void setBuildDirectory(File f) {
    _state.setBuildDirectory(f);
    if (f != null) {
      
      try {
        _jvm.addBuildDirectoryClassPath(new File(f.getAbsolutePath()).toURL());
      }
      catch(MalformedURLException murle) {
        
        
        throw new RuntimeException(murle);
      }
    }
    
    _notifier.projectBuildDirChanged();
    setProjectChanged(true);
  }
  
  protected FileGroupingState 
    makeProjectFileGroupingState(File pr, File main, File bd, File wd, File project, File[] files, ClassPathVector cp, File cjf, int cjflags) {
    return new ProjectFileGroupingState(pr, main, bd, wd, project, files, cp, cjf, cjflags);
  }
  
  class ProjectFileGroupingState extends AbstractGlobalModel.ProjectFileGroupingState {
      
    ProjectFileGroupingState(File pr, File main, File bd, File wd, File project, File[] files, ClassPathVector cp, File cjf, int cjflags) {
      super(pr, main, bd, wd, project, files, cp, cjf, cjflags);
    }

    




















































    
    
    public void jarAll() { }
  }
  
  protected FileGroupingState makeFlatFileGroupingState() { return new FlatFileGroupingState(); }
  
  class FlatFileGroupingState extends AbstractGlobalModel.FlatFileGroupingState {
    

    public void jarAll() { }
  }
  
  
  public String getSourceBinTitle() { return "[ Source Files ]"; }
  
  
  public String getExternalBinTitle() { return "[ External Files ]"; }
  
  
  public String getAuxiliaryBinTitle() { return "[ Included External Files ]"; }
  
  
  














  
  
  public DefaultInteractionsModel getInteractionsModel() { return _interactionsModel; }
  
  
  public InteractionsDJDocument getSwingInteractionsDocument() {
    return _interactionsDocument;
  }
  
  public InteractionsDocument getInteractionsDocument() { return _interactionsModel.getDocument(); }
  
  
  public CompilerModel getCompilerModel() { return _compilerModel; }
  
  
  public JUnitModel getJUnitModel() { return _junitModel; }
  
  
  public JavadocModel getJavadocModel() { return _javadocModel; }
  
  
  public void dispose() {
    
    _jvm.killInterpreter(null);
    
    super.dispose();
  }
 
  
  public void resetInteractions(File wd) {
    if (! _jvm.slaveJVMUsed() && wd.equals(_interactionsModel.getWorkingDirectory())) {
      

      _interactionsModel._notifyInterpreterReady(wd);
      return; 
    }

    _interactionsModel.resetInterpreter(wd);
  }

  
  public void interpretCurrentInteraction() { _interactionsModel.interpretCurrentInteraction(); }

  
  public void loadHistory(FileOpenSelector selector) throws IOException { _interactionsModel.loadHistory(selector); }

  
  public InteractionsScriptModel loadHistoryAsScript(FileOpenSelector selector)
    throws IOException, OperationCanceledException {
    return _interactionsModel.loadHistoryAsScript(selector);
  }

  
  public void clearHistory() { _interactionsModel.getDocument().clearHistory(); }

  
  public void saveHistory(FileSaveSelector selector) throws IOException {
    _interactionsModel.getDocument().saveHistory(selector);
  }

  
  public void saveHistory(FileSaveSelector selector, String editedVersion) throws IOException {
    _interactionsModel.getDocument().saveHistory(selector, editedVersion);
  }

  
  public String getHistoryAsStringWithSemicolons() {
    return _interactionsModel.getDocument().getHistoryAsStringWithSemicolons();
  }

  
  public String getHistoryAsString() {
    return _interactionsModel.getDocument().getHistoryAsString();
  }

  
  public void printDebugMessage(String s) {
    _interactionsModel.getDocument().
      insertBeforeLastPrompt(s + "\n", InteractionsDocument.DEBUGGER_STYLE);
  }

  
  public void waitForInterpreter() { _jvm.ensureInterpreterConnected(); }


  
  public ClassPathVector getClassPath() { return _jvm.getClassPath(); }
  
  
  void setResetAfterCompile(boolean shouldReset) { _resetAfterCompile = shouldReset; }

  
  public Debugger getDebugger() { return _debugger; }

  
  public int getDebugPort() throws IOException { return _interactionsModel.getDebugPort(); }

  

  
  class ConcreteOpenDefDoc extends AbstractGlobalModel.ConcreteOpenDefDoc {
   
    
    ConcreteOpenDefDoc(File f) throws IOException { super(f); }
    
    
    ConcreteOpenDefDoc() { super(); }
    
    
    public void startCompile() throws IOException { _compilerModel.compile(ConcreteOpenDefDoc.this); }
    
    private InteractionsListener _runMain;

    
    public void runMain() throws ClassNameNotFoundException, IOException {
      
      
      final String className = getDocument().getQualifiedClassName();
      final InteractionsDocument iDoc = _interactionsModel.getDocument();
      if (! checkIfClassFileInSync()) {
        iDoc.insertBeforeLastPrompt(DOCUMENT_OUT_OF_SYNC_MSG, InteractionsDocument.ERROR_STYLE);
        return;
      }
      
      final boolean wasDebuggerEnabled = getDebugger().isReady();
      
      _runMain = new DummyGlobalModelListener() {
        public void interpreterReady(File wd) {
          
          if (wasDebuggerEnabled && (!getDebugger().isReady())) {
            try { getDebugger().startup(); } catch(DebugException de) {  }
          }
          
          
          iDoc.clearCurrentInput();
          iDoc.append("java " + className, null);
          
          
          _interactionsModel.interpretCurrentInteraction();
          _notifier.runStarted(ConcreteOpenDefDoc.this);
          SwingUtilities.invokeLater(new Runnable() {
            public void run() { 
              
              _interactionsModel.removeListener(_runMain);
            }
          });
          
        }
      };
      
      _interactionsModel.addListener(_runMain);
      
      
      resetInteractions(getSourceRoot());  
    }

    
    public void startJUnit() throws ClassNotFoundException, IOException { _junitModel.junit(this); }

    
    public void generateJavadoc(FileSaveSelector saver) throws IOException {
      
      _javadocModel.javadocDocument(this, saver, getClassPath().toString());
    }
    
    
    public Breakpoint getBreakpointAt(int offset) {
      

      for (int i = 0; i < _breakpoints.size(); i++) {
        Breakpoint bp = _breakpoints.get(i);
        if (offset >= bp.getStartOffset() && offset <= bp.getEndOffset()) return bp;
      }
      return null;
    }

    
    public void addBreakpoint(Breakpoint breakpoint) {
      

      for (int i=0; i< _breakpoints.size();i++) {
        Breakpoint bp = _breakpoints.get(i);
        int oldStart = bp.getStartOffset();
        int newStart = breakpoint.getStartOffset();
        
        if ( newStart < oldStart) {
          
          _breakpoints.add(i, breakpoint);
          return;
        }
        if ( newStart == oldStart) {
          
          int oldEnd = bp.getEndOffset();
          int newEnd = breakpoint.getEndOffset();
          
          if ( newEnd < oldEnd) {
            
            _breakpoints.add(i, breakpoint);
            return;
          }
        }
      }
      _breakpoints.add(breakpoint);
    }
    
    
    public void removeBreakpoint(Breakpoint breakpoint) { _breakpoints.remove(breakpoint); }
    
    
    public Vector<Breakpoint> getBreakpoints() { return _breakpoints; }
    
    
    public void clearBreakpoints() { _breakpoints.clear(); }
    
    
    public void removeFromDebugger() {
      if (_debugger.isAvailable() && (_debugger.isReady())) {
        try {
          while (_breakpoints.size() > 0) {
            _debugger.removeBreakpoint(_breakpoints.get(0));
          }
        }
        catch (DebugException de) {
          
          throw new UnexpectedException(de);
        }
      }
      else clearBreakpoints();
    }
  } 
  
  
  protected ConcreteOpenDefDoc _createOpenDefinitionsDocument() { return new ConcreteOpenDefDoc(); }
  
   
  protected ConcreteOpenDefDoc _createOpenDefinitionsDocument(File f) throws IOException { return new ConcreteOpenDefDoc(f); }
  
  
  protected void addDocToClassPath(OpenDefinitionsDocument doc) {
    try {
      File classPath = doc.getSourceRoot();
      try {
        if (doc.isAuxiliaryFile())
          _interactionsModel.addProjectFilesClassPath(classPath.toURI().toURL());
        else _interactionsModel.addExternalFilesClassPath(classPath.toURI().toURL());
      }
      catch(MalformedURLException murle) {   }
    }
    catch (InvalidPackageException e) {
      
    }
  }
   
  
  private void _createDebugger() {
    try {
      _debugger = new JPDADebugger(this);
      _jvm.setDebugModel((JPDADebugger) _debugger);
      
      
      _debugger.addListener(new DebugListener() {
        public void debuggerStarted() { }
        public void debuggerShutdown() { }
        public void threadLocationUpdated(OpenDefinitionsDocument doc, int lineNumber, boolean shouldHighlight) { }
        public void breakpointSet(final Breakpoint bp) {
          setProjectChanged(true);
        }
        public void breakpointReached(final Breakpoint bp) { }
        public void breakpointChanged(final Breakpoint bp) {
          setProjectChanged(true);
        }    
        public void breakpointRemoved(final Breakpoint bp) {
          setProjectChanged(true);
        }    
        public void watchSet(final DebugWatchData w) {
          setProjectChanged(true);
        }
        public void watchRemoved(final DebugWatchData w) {
          setProjectChanged(true);
        }    
        public void stepRequested() { }
        public void currThreadSuspended() { }
        public void currThreadResumed() { }
        public void threadStarted() { }
        public void currThreadDied() { }
        public void nonCurrThreadDied() {  }
        public void currThreadSet(DebugThreadData thread) { }
      });
    }
    catch( NoClassDefFoundError ncdfe ) {
      
      _debugger = NoDebuggerAvailable.ONLY;
    }
    catch( UnsupportedClassVersionError ucve ) {
      
      _debugger = NoDebuggerAvailable.ONLY;
    }
    catch( Throwable t ) {
      
      _debugger = NoDebuggerAvailable.ONLY;
    }
  }
  
  
  public void resetInteractionsClassPath() {
    ClassPathVector projectExtras = getExtraClassPath();
    
    if (projectExtras != null)  for (URL cpE : projectExtras) { _interactionsModel.addProjectClassPath(cpE); }
    
    Vector<File> cp = DrJava.getConfig().getSetting(EXTRA_CLASSPATH);
    if (cp != null) {
      for (File f : cp) {
        try { _interactionsModel.addExtraClassPath(f.toURL()); }
        catch(MalformedURLException murle) {
          System.out.println("File " + f + " in your extra classpath could not be parsed to a URL; " +
                             "it may contain un-URL-encodable characters.");
        }
      }
    }
    
    List<OpenDefinitionsDocument> odds = getAuxiliaryDocuments();
    for (OpenDefinitionsDocument odd: odds) {
      
      try { _interactionsModel.addProjectFilesClassPath(odd.getSourceRoot().toURL()); }
      catch(MalformedURLException murle) {  }
      catch(InvalidPackageException e) {   }
    }
    
    odds = getNonProjectDocuments();

    for (OpenDefinitionsDocument odd: odds) {
      
      try { 
        File sourceRoot = odd.getSourceRoot();
        if (sourceRoot != null) _interactionsModel.addExternalFilesClassPath(sourceRoot.toURL()); 
      }
      catch(MalformedURLException murle) {  }
      catch(InvalidPackageException e) {  }
    }
    
    
    
    try { _interactionsModel.addProjectFilesClassPath(getProjectRoot().toURL()); }
    catch(MalformedURLException murle) {  } 
  }
  














  
}
