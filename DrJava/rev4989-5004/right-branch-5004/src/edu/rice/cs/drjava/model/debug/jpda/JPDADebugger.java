

package edu.rice.cs.drjava.model.debug.jpda;

import java.awt.EventQueue;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;


import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.repl.DefaultInteractionsModel;
import edu.rice.cs.drjava.model.repl.DummyInteractionsListener;
import edu.rice.cs.drjava.model.repl.InteractionsListener;
import edu.rice.cs.drjava.model.repl.newjvm.InterpreterJVM;
import edu.rice.cs.drjava.model.compiler.LanguageLevelStackTraceMapper;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.util.Log;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.drjava.model.debug.*;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;


import com.sun.jdi.*;
import com.sun.jdi.connect.*;
import com.sun.jdi.request.*;

import static edu.rice.cs.plt.debug.DebugUtil.error;
import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class JPDADebugger implements Debugger {
  
  
  private static final Log _log = new Log("JPDADebugger.txt", false);
  
  private static final int OBJECT_COLLECTED_TRIES = 5;
  
  private static final String ADD_INTERPRETER_SIG =
    "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Class;" +
    "[Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Class;)V";
  
  private static final String GET_VARIABLE_SIG = "(Ljava/lang/String;)[Ljava/lang/Object;";
  
  private static final String NEW_INSTANCE_SIG = "(Ljava/lang/Class;I)Ljava/lang/Object;";
  
  
  private volatile GlobalModel _model;
  
  
  private volatile VirtualMachine _vm;
  
  
  private volatile EventRequestManager _eventManager;
  
  
  private final ArrayList<DebugWatchData> _watches = new ArrayList<DebugWatchData>();
  
  
  private final PendingRequestManager _pendingRequestManager = new PendingRequestManager(this);
  
  
  final DebugEventNotifier _notifier = new DebugEventNotifier();
  
  
  private volatile ThreadReference _runningThread;
  
  
  private volatile RandomAccessStack _suspendedThreads;
  
  
  private volatile ObjectReference _interpreterJVM;
  
  private volatile InteractionsListener _watchListener;
  
  
  private volatile Throwable _eventHandlerError;
  
  
  private volatile boolean _isAutomaticTraceEnabled = false;
      
  
  public JPDADebugger(GlobalModel model) {
    _model = model;
    _vm = null;
    _eventManager = null;
    
    _suspendedThreads = new RandomAccessStack();
    _runningThread = null;
    _interpreterJVM = null;
    _eventHandlerError = null;
    
    _watchListener = new DummyInteractionsListener() {
      public void interactionEnded() { _updateWatches(); }
    };
  }
  
  
  private void _log(String message) { _log.log(message); }
  
  
  private void _log(String message, Throwable t) { _log.log(message, t); }
  
  
  
  public void addListener(DebugListener listener) {
    _notifier.addListener(listener);
    _model.getBreakpointManager().addListener(listener);
  }
  
  
  public void removeListener(DebugListener listener) {
    _notifier.removeListener(listener);
    _model.getBreakpointManager().removeListener(listener);
  }
  
  
  public boolean isAvailable() { return true; }
  
  public DebugModelCallback callback() { return new DebugModelCallback() {}; }
  
  
  public boolean isReady() { return _vm != null; }
  
  
  public  void startUp() throws DebugException {
    assert EventQueue.isDispatchThread();
    if (! isReady()) {
      _eventHandlerError = null;
      
      for (OpenDefinitionsDocument doc: _model.getOpenDefinitionsDocuments()) {
        doc.checkIfClassFileInSync();
      }
      
      try { _attachToVM(); }
      catch(DebugException e1) {  
        try { 
          try { Thread.sleep(100); } 
          catch (InterruptedException e) {  }
          _attachToVM(); 
          error.log("Two attempts required for debugger to attach to slave JVM");
        }
        catch(DebugException e2) {
          try { Thread.sleep(500); } 
          catch (InterruptedException e) {  }
          _attachToVM();
          error.log("Three attempts required for debugger to attach to slave JVM");
        }  
      }
      
      
      ThreadDeathRequest tdr = _eventManager.createThreadDeathRequest();
      tdr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
      tdr.enable();
      
      
      EventHandlerThread eventHandler = new EventHandlerThread(this, _vm);
      eventHandler.start();
      
      _model.getInteractionsModel().addListener(_watchListener);

      
      ArrayList<Breakpoint> oldBreakpoints = new ArrayList<Breakpoint>(_model.getBreakpointManager().getRegions());
      _model.getBreakpointManager().clearRegions();  
      for (int i = 0; i < oldBreakpoints.size(); i++) {
        Breakpoint bp = oldBreakpoints.get(i);
        bp.update();
        OpenDefinitionsDocument odd = bp.getDocument();
        setBreakpoint(new JPDABreakpoint(odd, bp.getLineStartOffset(), bp.isEnabled(), this)); 
      }
    }
    
    else
      
      throw new IllegalStateException("Debugger has already been started.");
  }
  
  
  
  public  void shutdown() {
    assert EventQueue.isDispatchThread();
    if (isReady()) {
      Runnable command = new Runnable() { public void run() { _model.getInteractionsModel().removeListener(_watchListener); } };
      
      
      EventQueue.invokeLater(command);
      
      _removeAllDebugInterpreters();
      
      try { _vm.dispose(); }
      catch (VMDisconnectedException vmde) {  }
      finally {
        _model.getInteractionsModel().setToDefaultInterpreter();
        _vm = null;
        _suspendedThreads = new RandomAccessStack();
        _eventManager = null;
        _runningThread = null;
        _updateWatches();
      }
    }
  }
  
  
  
  public  void setCurrentThread(DebugThreadData threadData) throws DebugException {
    assert EventQueue.isDispatchThread();
    _ensureReady();
    
    if (threadData == null) {
      throw new IllegalArgumentException("Cannot set current thread to null.");
    }
    
    ThreadReference threadRef = _getThreadFromDebugThreadData(threadData);
    
    
    
    
    
    




    
    
    
    if (_suspendedThreads.contains(threadRef.uniqueID())) _suspendedThreads.remove(threadRef.uniqueID());
    
    if (!threadRef.isSuspended()) {
      throw new IllegalArgumentException("Given thread must be suspended.");


















      
    }
    
    _suspendedThreads.push(threadRef);
    
    try {
      if (threadRef.frameCount() <= 0) {
        printMessage(threadRef.name() + " could not be suspended since it has no stackframes.");
        resume();
        return;
      }
    }
    catch (IncompatibleThreadStateException e) {
      throw new DebugException("Could not suspend thread: " + e);
    }
    
    
    _switchToInterpreterForThreadReference(threadRef);
    _switchToSuspendedThread();
    printMessage("The current thread has changed.");
  }
  
  
  ThreadReference getCurrentThread() {
    
    return _suspendedThreads.peek();
  }
  
  
  public  boolean hasSuspendedThreads() throws DebugException {
    assert EventQueue.isDispatchThread();
    if (! isReady()) return false;
    return _suspendedThreads.size() > 0;
  }
  
  
  public  boolean isCurrentThreadSuspended() throws DebugException {
    assert EventQueue.isDispatchThread();
    if (! isReady()) return false;
    return hasSuspendedThreads() && ! hasRunningThread();
  }
  
  
  public  boolean hasRunningThread() throws DebugException {
    assert EventQueue.isDispatchThread();
    if (! isReady()) return false;
    return _runningThread != null;
  }
  
  
  public  void resume() throws DebugException {
    assert EventQueue.isDispatchThread();
    _ensureReady();
    _resumeHelper(false);
  }
  
  
  public  void setAutomaticTraceEnabled(boolean e) {
    _isAutomaticTraceEnabled = e;
  }
    
   
  public boolean isAutomaticTraceEnabled() {
    return _isAutomaticTraceEnabled;
  }
  
  
  public  void resume(DebugThreadData threadData) throws DebugException {
    assert EventQueue.isDispatchThread();
    _ensureReady();
    ThreadReference thread = _suspendedThreads.remove(threadData.getUniqueID());
    _resumeThread(thread, false);
  }
  
  
  public  void step(StepType type) throws DebugException {
    assert EventQueue.isDispatchThread();
    _ensureReady();
    _stepHelper(type, true);
  }
  
  
  public  void addWatch(String field) throws DebugException {
    
    assert EventQueue.isDispatchThread();
    final DebugWatchData w = new DebugWatchData(field);
    _watches.add(w);
    _updateWatches();
    

      _notifier.watchSet(w); 

  }
  
  
  public  void removeWatch(String field) throws DebugException {
    
    assert EventQueue.isDispatchThread();
    for (int i=0; i < _watches.size(); i++) {
      final DebugWatchData watch = _watches.get(i);
      if (watch.getName().equals(field)) {
        _watches.remove(i);

          _notifier.watchRemoved(watch); 

      }
    }
  }
  
  
  public  void removeWatch(int index) throws DebugException {
    
    assert EventQueue.isDispatchThread();
    if (index < _watches.size()) {
      final DebugWatchData watch = _watches.get(index);
      _watches.remove(index);

        _notifier.watchRemoved(watch); 

    }
  }
  
  
  public  void removeAllWatches() throws DebugException {
    
    assert EventQueue.isDispatchThread();
    while (_watches.size() > 0) {
      removeWatch( _watches.get(0).getName());
    }
  }
  
  
  public  void notifyBreakpointChange(Breakpoint breakpoint) {
    assert EventQueue.isDispatchThread();
    _model.getBreakpointManager().changeRegion(breakpoint, new Lambda<Breakpoint, Object>() {
      public Object value(Breakpoint bp) {
        
        return null;
      }
    });
  }
  
  
  public boolean toggleBreakpoint(OpenDefinitionsDocument doc, int offset, boolean isEnabled) 
    throws DebugException {
    assert EventQueue.isDispatchThread();
    
    offset = doc._getLineStartPos(offset);
    if (offset < 0) return false;
    
    Breakpoint breakpoint = _model.getBreakpointManager().getRegionAt(doc, offset);
    
    if (breakpoint == null) {  
      if (offset == doc._getLineEndPos(offset)) {  
        Utilities.show("Cannot set a breakpoint on an empty line.");
        return false;
      }
      else {  
        try {
          setBreakpoint(new JPDABreakpoint(doc, offset, isEnabled, this));
          return true;
        }
        catch(LineNotExecutableException lne) { 
          Utilities.showMessageBox(lne.getMessage(), "Error Toggling Breakpoint");
          return false;
        }
      }
    }
    else { 
      _model.getBreakpointManager().removeRegion(breakpoint);
      return false;
    }
  }

  
  
  
  public  void setBreakpoint(final Breakpoint breakpoint) throws DebugException {    
    assert EventQueue.isDispatchThread();
    breakpoint.getDocument().checkIfClassFileInSync();   
    _model.getBreakpointManager().addRegion(breakpoint);
  }
  
  
  public int LLBreakpointLineNum(Breakpoint breakpoint){
    int line = breakpoint.getLineNumber();
    File f = breakpoint.getFile();
    
    if (LanguageLevelStackTraceMapper.isLLFile(f)) {
      f = LanguageLevelStackTraceMapper.getJavaFileForLLFile(f);
      TreeMap<Integer, Integer> tM = getLLSTM().ReadLanguageLevelLineBlockRev(f);
      line = tM.get(breakpoint.getLineNumber());
    }
    return line;
  }
  
  
  public StackTraceElement getLLStackTraceElement(Location l, List<File> files) {
    
    int lineNum = l.lineNumber();
    String sourceName = null;
    try {
      sourceName = l.sourceName();
    }
    catch(com.sun.jdi.AbsentInformationException aie) { sourceName = null; }
    StackTraceElement ste = new StackTraceElement(l.declaringType().name(),
                                                  l.method().name(),
                                                  sourceName,
                                                  l.lineNumber());
    return getLLSTM().replaceStackTraceElement(ste, files);
  }
  
  
  public Location getLLLocation(Location l, List<File> files) {
    StackTraceElement ste = getLLStackTraceElement(l, files); 
    return new DelegatingLocation(ste.getFileName(), ste.getLineNumber(), l);
  }
  
  
  public  void removeBreakpoint(Breakpoint bp) throws DebugException {
    assert EventQueue.isDispatchThread();
    if (!(bp instanceof JPDABreakpoint)) { throw new IllegalArgumentException("Unsupported breakpoint"); }
    else {
      JPDABreakpoint breakpoint = (JPDABreakpoint) bp;
      Vector<BreakpointRequest> requests = breakpoint.getRequests();
      if (requests.size() > 0 && _eventManager != null) {
        
        try {
          for (int i=0; i < requests.size(); i++) {
            _eventManager.deleteEventRequest(requests.get(i));
          }
        }
        catch (VMMismatchException vme) {
          
          
          _log("VMMismatch when removing breakpoint.", vme);
        }
        catch (VMDisconnectedException vmde) {
          
          
          _log("VMDisconnected when removing breakpoint.", vmde);
        }
      }
      
      
      _pendingRequestManager.removePendingRequest(breakpoint);
    }
  }
  
  
  public ArrayList<DebugWatchData> getWatches() throws DebugException {
    
    return _watches;
  }
  
  
  public  ArrayList<DebugThreadData> getCurrentThreadData() throws DebugException {
    assert EventQueue.isDispatchThread();
    if (! isReady()) { return new ArrayList<DebugThreadData>(); }
    Iterable<ThreadReference> listThreads;
    try { listThreads = _vm.allThreads(); }
    catch (VMDisconnectedException vmde) {
      
      return new ArrayList<DebugThreadData>();
    }
    
    ArrayList<DebugThreadData> threads = new ArrayList<DebugThreadData>();
    for (ThreadReference ref : listThreads) {
      try { threads.add(new JPDAThreadData(ref)); }
      catch (ObjectCollectedException e) {
        
      }
    }
    return threads;
  }
  
  
  public  ArrayList<DebugStackData> getCurrentStackFrameData() throws DebugException {
    assert EventQueue.isDispatchThread();
    if (! isReady()) return new ArrayList<DebugStackData>();
    
    if (_runningThread != null || _suspendedThreads.size() <= 0) {
      throw new DebugException("No suspended thread to obtain stack frames.");
    }
    
    try {
      ThreadReference thread = _suspendedThreads.peek();
      ArrayList<DebugStackData> frames = new ArrayList<DebugStackData>();
      
      final List<File> files = new ArrayList<File>();
      for(OpenDefinitionsDocument odd: _model.getLLOpenDefinitionsDocuments()){ files.add(odd.getRawFile()); }
      for (StackFrame f : thread.frames()) {
        
        String method = JPDAStackData.methodName(f);
        StackTraceElement ste = getLLStackTraceElement(f.location(), files);
        frames.add(new JPDAStackData(method, ste.getLineNumber()));
      }
      return frames;
    }
    catch (IncompatibleThreadStateException itse) {
      error.log("Unable to obtain stack frame.", itse);
      return new ArrayList<DebugStackData>();
    }
    catch (VMDisconnectedException vmde) {
      error.log("VMDisconnected when getting the current stack frame data.", vmde);
      return new ArrayList<DebugStackData>();
    }
    catch (InvalidStackFrameException isfe) {
      error.log("The stack frame requested is invalid.", isfe);
      return new ArrayList<DebugStackData>();
    }
  }
  
  
  public OpenDefinitionsDocument preloadDocument(Location location) {
    assert EventQueue.isDispatchThread();
    OpenDefinitionsDocument doc = null;
    
    String fileName;
    try {
      final List<File> files = new ArrayList<File>();
      for(OpenDefinitionsDocument odd: _model.getLLOpenDefinitionsDocuments()){ files.add(odd.getRawFile()); }
      Location lll = getLLLocation(location, files);
      
      fileName = lll.sourcePath();

      
      File f = _model.getSourceFile(fileName);
      if (f != null) {
        
        try { doc = _model.getDocumentForFile(f); }
        catch (IOException ioe) {
          doc = null;
        }
      }

    }
    catch(AbsentInformationException e) {
      
      final List<File> files = new ArrayList<File>();
      for(OpenDefinitionsDocument odd: _model.getLLOpenDefinitionsDocuments()){ files.add(odd.getRawFile()); }

      ReferenceType rt = location.declaringType();
      fileName = null;
      try { fileName = getPackageDir(rt.name()) + rt.sourceName(); }
      catch (AbsentInformationException aie) {
        
        
        fileName = null;
        String className = rt.name().replace('.', File.separatorChar);
        
        
        int indexOfDollar = className.indexOf('$');
        if (indexOfDollar > -1) {
          className = className.substring(0, indexOfDollar);
        }
        
        for(File f: files) {
          if (f.getName().equals(className+".java") ||
              f.getName().equals(className+".dj0") ||
              f.getName().equals(className+".dj1") ||
              f.getName().equals(className+".dj2")) {
            fileName = f.getName();
            break;
          }
        }
        if (fileName==null) {
          fileName = className + ".java";
        }
      }
      
      if (fileName!=null) {
        
        File f = _model.getSourceFile(fileName);
        if (f != null) {
          
          try { doc = _model.getDocumentForFile(f); }
          catch (IOException ioe) {
            
          }
        }
      }
    }
    
    return doc;
  }
  
  
  public  void scrollToSource(DebugStackData stackData) throws DebugException {
    
    assert EventQueue.isDispatchThread();
    _ensureReady();
    if (_runningThread != null) {
      throw new DebugException("Cannot scroll to source unless thread is suspended.");
    }
    
    ThreadReference threadRef = _suspendedThreads.peek();
    Iterator<StackFrame> i;
    
    try {
      if (threadRef.frameCount() <= 0 ) {
        printMessage("Could not scroll to source. The current thread had no stack frames.");
        return;
      }
      i = threadRef.frames().iterator();
    }
    catch (IncompatibleThreadStateException e) {
      throw new DebugException("Unable to find stack frames: " + e);
    }

    final List<File> files = new ArrayList<File>();
    for(OpenDefinitionsDocument odd: _model.getLLOpenDefinitionsDocuments()){ files.add(odd.getRawFile()); }
    
    
    while (i.hasNext()) {
      StackFrame frame = i.next();

      Location lll = getLLLocation(frame.location(), files);
      
      if (lll.lineNumber() == stackData.getLine() &&
          stackData.getMethod().equals(frame.location().declaringType().name() + "." +
                                       frame.location().method().name())) {
        scrollToSource(lll, false);
      }
    }
  }
  
  
  public  void scrollToSource(Breakpoint bp) {
    scrollToSource(bp, false);
  }

  
  public  void scrollToSource(Breakpoint bp, boolean shouldHighlight) {
    
    openAndScroll(bp.getDocument(), bp.getLineNumber(), bp.getClassName(), shouldHighlight);
  }
  
  
  public  Breakpoint getBreakpoint(int line, String className) {
    assert EventQueue.isDispatchThread();
    for (int i = 0; i < _model.getBreakpointManager().getRegions().size(); i++) {
      Breakpoint bp = _model.getBreakpointManager().getRegions().get(i);
      if ((LLBreakpointLineNum(bp)== line) && (bp.getClassName().equals(className))) {
        return bp;
      }
    }
    
    return null;
  }
  
  
  
  
  
  VirtualMachine getVM() { return _vm; }
  
  
  EventRequestManager getEventRequestManager() { return _eventManager; }
  
  
  PendingRequestManager getPendingRequestManager() { return _pendingRequestManager; }
  
  
  ThreadReference getThreadAt(int i) { return _suspendedThreads.peekAt(i); }
  
  
  ThreadReference getCurrentRunningThread() { return _runningThread; }
  
  
  
  
  private void _ensureReady() throws DebugException {
    if (! isReady()) throw new IllegalStateException("Debugger is not active.");
    
    if (_eventHandlerError != null) {
      Throwable t = _eventHandlerError;
      _eventHandlerError = null;
      throw new DebugException("Error in Debugger Event Handler: " + t);
    }
  }
  
  
  void eventHandlerError(Throwable t) {
    _log("Error in EventHandlerThread: " + t);
    _eventHandlerError = t;
  }
  
  
  private void _attachToVM() throws DebugException {
    assert EventQueue.isDispatchThread();

    
    
    AttachingConnector connector = _getAttachingConnector();
    
    
    Map<String, Connector.Argument> args = connector.defaultArguments();
    Connector.Argument port = args.get("port");
    Connector.Argument host = args.get("hostname");
    try {
      int debugPort = _model.getDebugPort();
      port.setValue("" + debugPort);
      host.setValue("127.0.0.1"); 
      _vm = connector.attach(args);
      _eventManager = _vm.eventRequestManager();
    }
    catch(Exception e) { 

      throw new DebugException("Could not connect to VM: " + e); 
    }
    
    _interpreterJVM = (ObjectReference) _getStaticField(_getClass(InterpreterJVM.class.getName()), "ONLY");

  }
  
  
  private AttachingConnector _getAttachingConnector() throws DebugException {
    VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
    List<AttachingConnector> connectors = vmm.attachingConnectors();
    AttachingConnector connector = null;
    for (AttachingConnector conn: connectors) {
      if (conn.name().equals("com.sun.jdi.SocketAttach"))  connector = conn;
    }
    if (connector == null) throw new DebugException("Could not find an AttachingConnector!");
    return connector;
  }
  
  
  boolean setCurrentThread(ThreadReference thread) {
    assert EventQueue.isDispatchThread();
    if (! thread.isSuspended()) {
      throw new IllegalArgumentException("Thread must be suspended to set as current.  Given: " + thread);
    }
    
    try {
      if ((_suspendedThreads.isEmpty() || ! _suspendedThreads.contains(thread.uniqueID())) &&
          (thread.frameCount() > 0)) {
        _suspendedThreads.push(thread);
        return true;
      }
      else return false;
    }
    catch (IncompatibleThreadStateException itse) {
      
      
      throw new UnexpectedException(itse);
    }
  }
  
  
   Vector<ReferenceType> getReferenceTypes(String className, int lineNumber) {
    assert EventQueue.isDispatchThread();
    
    List<ReferenceType> classes;
    
    try { classes = _vm.classesByName(className); }
    catch (VMDisconnectedException vmde) {
      
      return new Vector<ReferenceType>();
    }
    
    
    Vector<ReferenceType> refTypes = new Vector<ReferenceType>();
    ReferenceType ref;
    for (int i=0; i < classes.size(); i++) {
      ref = classes.get(i);
      
      if (lineNumber != DebugAction.ANY_LINE) {
        List<Location> lines = new LinkedList<Location>();
        try {
          lines = ref.locationsOfLine(lineNumber);
        }
        catch (AbsentInformationException aie) {
          
        }
        catch (ClassNotPreparedException cnpe) {
          
          continue;
        }
        
        if (lines.size() == 0) {
          
          
          List<ReferenceType> innerRefs = ref.nestedTypes();
          ref = null;
          for (int j = 0; j < innerRefs.size(); j++) {
            try {
              ReferenceType currRef = innerRefs.get(j);
              lines = currRef.locationsOfLine(lineNumber);
              if (lines.size() > 0) {
                ref = currRef;
                break;
              }
            }
            catch (AbsentInformationException aie) {
              
            }
            catch (ClassNotPreparedException cnpe) {
              
            }
          }
        }
      }
      if ((ref != null) && ref.isPrepared()) {
        refTypes.add(ref);
      }
    }
    return refTypes;
  }
  
  
  private ThreadReference _getThreadFromDebugThreadData(DebugThreadData d) throws NoSuchElementException {
    List<ThreadReference> threads = _vm.allThreads(); 
    Iterator<ThreadReference> iterator = threads.iterator();
    while (iterator.hasNext()) {
      ThreadReference threadRef = iterator.next();
      if (threadRef.uniqueID() == d.getUniqueID()) {
        return threadRef;
      }
    }
    
    throw new NoSuchElementException("Thread " + d.getName() + " not found in virtual machine!");
  }
  
  
  
  
  
  
  private void _resumeFromStep() throws DebugException { _resumeHelper(true); }
  
  
  private void _resumeHelper(boolean fromStep) throws DebugException {
    try {
      ThreadReference thread = _suspendedThreads.pop();
      
      _log.log("In resumeThread()");
      _resumeThread(thread, fromStep);
    }
    catch (NoSuchElementException e) { throw new DebugException("No thread to resume."); }
  }
  
  
  private void _resumeThread(ThreadReference thread, boolean fromStep) throws DebugException {
    if (thread == null) {
      throw new IllegalArgumentException("Cannot resume a null thread");
    }
    
    int suspendCount = thread.suspendCount();
    _log.log("Getting suspendCount = " + suspendCount);
    
    
    _runningThread = thread;
    if (!fromStep) {
      
      _copyVariablesFromInterpreter();
      _updateWatches();
    }
    try {
      _removeCurrentDebugInterpreter(fromStep);
      _currThreadResumed();
    }
    catch(DebugException e) { throw new UnexpectedException(e); }
    
    
    for (int i = suspendCount; i>0; i--) thread.resume();
    
    
    
    
    if (! fromStep && ! _suspendedThreads.isEmpty()) _switchToSuspendedThread();
  }
  
  
  private void _stepHelper(StepType type, boolean shouldNotify) throws DebugException {
    if (_suspendedThreads.size() <= 0 || _runningThread != null) {
      throw new IllegalStateException("Cannot step if the current thread is not suspended.");
    }
    
    _log.log(this + "is About to peek ...");
    
    ThreadReference thread = _suspendedThreads.peek();
    _log.log(this + "is Stepping " + thread.toString());
    
    
    
    
    _runningThread = thread;
    _copyVariablesFromInterpreter();
    
    _log.log(this + " is Deleting pending requests ...");
    
    
    
    List<StepRequest> steps = _eventManager.stepRequests();
    for (int i = 0; i < steps.size(); i++) {
      StepRequest step = steps.get(i);
      if (step.thread().equals(thread)) {
        _eventManager.deleteEventRequest(step);
        break;
      }
    }
    
    _log.log(this + " Issued step request");
    int stepFlag = Integer.MIN_VALUE; 
    switch (type) {
      case STEP_INTO: stepFlag = StepRequest.STEP_INTO; break;
      case STEP_OVER: stepFlag = StepRequest.STEP_OVER; break;
      case STEP_OUT: stepFlag = StepRequest.STEP_OUT; break;
    }
    new Step(this, StepRequest.STEP_LINE, stepFlag);
    if (shouldNotify) notifyStepRequested();
    _log.log(this + " About to resume");
    _resumeFromStep();
  }
  
  
  
   void reachedBreakpoint(BreakpointRequest request) {

    assert EventQueue.isDispatchThread();
    Object property = request.getProperty("debugAction");
    if (property != null && (property instanceof JPDABreakpoint)) {
      final JPDABreakpoint breakpoint = (JPDABreakpoint) property;
      breakpoint.update();
      printMessage("Breakpoint hit in class " + breakpoint.getClassName() + "  [line " + breakpoint.getLineNumber() + "]");
      
      EventQueue.invokeLater(new Runnable() { public void run() { _notifier.breakpointReached(breakpoint); } });
    }
    else {
      
      error.log("Reached a breakpoint without a debugAction property: " + request);
    }
  }
  
  















  
  
  private void scrollToSource(Location location) {
    scrollToSource(location, true);
  }
  
  
  private void scrollToSource(Location location, boolean shouldHighlight) {
    
    
    
    assert EventQueue.isDispatchThread();
    OpenDefinitionsDocument doc = preloadDocument(location);
    openAndScroll(doc, location, shouldHighlight);
  }
  
  
  private void openAndScroll(OpenDefinitionsDocument doc, Location location, boolean shouldHighlight) {
    
    
    
    openAndScroll(doc, location.lineNumber(), location.declaringType().name(), shouldHighlight);
  }
  
  
  private void openAndScroll(final OpenDefinitionsDocument doc, int line, String className, 
                             final boolean shouldHighlight) {
    assert EventQueue.isDispatchThread();
    
    if (doc != null) { 
      doc.checkIfClassFileInSync();
      if (LanguageLevelStackTraceMapper.isLLFile(doc.getRawFile())) {
        
      }
      final int llLine = line;
      
      EventQueue.invokeLater(new Runnable() { public void run() { _notifier.threadLocationUpdated(doc, llLine, shouldHighlight); } });
    }
    else printMessage("  (Source for " + className + " not found.)");
  }
  
  
  static String getPackageDir(String className) {
    
    int lastDotIndex = className.lastIndexOf(".");
    if (lastDotIndex == -1) {
      
      return "";
    }
    else {
      String packageName = className.substring(0, lastDotIndex);
      packageName = packageName.replace('.', File.separatorChar);
      return packageName + File.separatorChar;
    }
  }
  
  
  void printMessage(String message) {
    _model.printDebugMessage(message);
  }
  
  
  private void _hideWatches() {
    for (int i = 0; i < _watches.size(); i++) {
      DebugWatchData currWatch = _watches.get(i);
      currWatch.hideValueAndType();
    }
  }
  
  
  private  void _updateWatches() {
    assert EventQueue.isDispatchThread();
    if (! isReady()) return;
    
    for (DebugWatchData w : _watches) {
      String name = w.getName();
      String val = "";
      String type = "";
      ArrayList<Integer> arr_index = new ArrayList<Integer>();
      
      if(name.indexOf("[")!=-1 && name.indexOf("]")!=-1) {
        name = name.substring(0, name.indexOf("["));
        arr_index.add(Integer.parseInt(w.getName().substring(w.getName().indexOf("[")+1, w.getName().indexOf("]"))));      
        if(w.getName().indexOf("]")<(w.getName().length()-1)) {
          String iter = w.getName().substring(w.getName().indexOf("]")+1, w.getName().length());
          while(iter.indexOf("[")!=-1 && iter.indexOf("]")!=-1) {
            arr_index.add(Integer.parseInt(iter.substring(iter.indexOf("[")+1, iter.indexOf("]"))));      
            if(iter.indexOf("]")<(iter.length()-1))
              iter = iter.substring(iter.indexOf("]")+1, iter.length());
            else 
              iter = "";
          }
        }
      }
     
      int [] indices = new int[arr_index.size()];
      for (int i = 0; i < arr_index.size(); i++) {
        indices[i] = arr_index.get(i);
      }
      val = _model.getInteractionsModel().getVariableToString(name, indices);
      type = _model.getInteractionsModel().getVariableType(name, indices);
      
      if (val == null) { w.setNoValue(); }
      else { w.setValue(val); }
      if (type == null) { w.setNoType(); }
      else { w.setType(type); }
    }
  }
  
  
  private void _dumpVariablesIntoInterpreterAndSwitch() throws DebugException {
    _log.log(this + " invoked dumpVariablesIntoInterpreterAndSwitch");
    List<ObjectReference> toRelease = new LinkedList<ObjectReference>();
    try {
      ThreadReference thread = _suspendedThreads.peek();
      
      
      String interpreterName = _getUniqueThreadName(thread);
      ObjectReference mirroredName = _mirrorString(interpreterName, toRelease);
      ObjectReference thisVal = thread.frame(0).thisObject();
      ClassObjectReference thisClass = thread.frame(0).location().declaringType().classObject();
      
      List<ObjectReference> localVars = new LinkedList<ObjectReference>();
      List<StringReference> localVarNames = new LinkedList<StringReference>();
      List<ClassObjectReference> localVarClasses = new LinkedList<ClassObjectReference>();
      try {
        
        
        for (LocalVariable v : thread.frame(0).visibleVariables()) {
          try {
            
            Type t = v.type();
            if (t instanceof ReferenceType) {
              localVarClasses.add(((ReferenceType) t).classObject());
            }
            else {
              
              localVarClasses.add(null);
            }
            localVarNames.add(_mirrorString(v.name(), toRelease));
            Value val = thread.frame(0).getValue(v);
            if (val == null || val instanceof ObjectReference) { localVars.add((ObjectReference) val); }
            else { localVars.add(_box((PrimitiveValue) val, thread, toRelease)); }
          }
          catch (ClassNotLoadedException e) {
            
            
            
          }
        }
      }
      catch (AbsentInformationException e) {  }
      ArrayReference mirroredVars = _mirrorArray("java.lang.Object", localVars, thread, toRelease);
      ArrayReference mirroredVarNames = _mirrorArray("java.lang.String", localVarNames, thread, toRelease);
      ArrayReference mirroredVarClasses = _mirrorArray("java.lang.Class", localVarClasses, thread, toRelease);
      
      _invokeMethod(thread, _interpreterJVM, "addInterpreter", ADD_INTERPRETER_SIG,
                    mirroredName, thisVal, thisClass, mirroredVars, mirroredVarNames, mirroredVarClasses);
      
      
      String prompt = _getPromptString(thread);
      _log.log(this + " is setting active interpreter");
      _model.getInteractionsModel().setActiveInterpreter(interpreterName, prompt);
    }
    catch (IncompatibleThreadStateException e) { throw new DebugException(e); }
    finally {
      for (ObjectReference ref : toRelease) { ref.enableCollection(); }
    }
  }
  
  
  private String _getPromptString(ThreadReference threadRef) {
    return "[" + threadRef.name() + "] > ";
  }
  
  
  private StringReference _mirrorString(String s, List<ObjectReference> toRelease) throws DebugException {
    for (int tries = 0; tries < OBJECT_COLLECTED_TRIES; tries++) {
      try {
        StringReference result = _vm.mirrorOf(s);
        result.disableCollection();
        if (!result.isCollected()) {
          toRelease.add(result);
          return result;
        }
      }
      catch (ObjectCollectedException e) {  }
    }
    throw new DebugException("Ran out of OBJECT_COLLECTED_TRIES");
  }
  
  
  private ArrayReference _mirrorArray(String elementClass, List<? extends ObjectReference> elts,
                                      ThreadReference thread, List<ObjectReference> toRelease)
    throws DebugException {
    ClassType arrayC = (ClassType) _getClass("java.lang.reflect.Array");
    ReferenceType elementC = _getClass(elementClass);
    for (int tries = 0; tries < OBJECT_COLLECTED_TRIES; tries++) {
      try {
        ArrayReference result =
          (ArrayReference) _invokeStaticMethod(thread, arrayC, "newInstance", NEW_INSTANCE_SIG,
                                               elementC.classObject(), _vm.mirrorOf(elts.size()));
        result.disableCollection();
        if (!result.isCollected()) {
          toRelease.add(result);
          try { result.setValues(elts); }
          catch (InvalidTypeException e) { throw new DebugException(e); }
          catch (ClassNotLoadedException e) { throw new DebugException(e); }
          return result;
        }
      }
      catch (ObjectCollectedException e) {  }
    }
    throw new DebugException("Ran out of OBJECT_COLLECTED_TRIES");
  }
  
  
  private ObjectReference _box(PrimitiveValue val, ThreadReference thread,
                               List<ObjectReference> toRelease) throws DebugException {
    String c = null;
    String prim = null;
    if (val instanceof BooleanValue) { c = "java.lang.Boolean"; prim = "Z"; }
    else if (val instanceof IntegerValue) { c = "java.lang.Integer"; prim = "I"; }
    else if (val instanceof DoubleValue) { c = "java.lang.Double"; prim = "D"; }
    else if (val instanceof CharValue) { c = "java.lang.Character"; prim = "C"; }
    else if (val instanceof ByteValue) { c = "java.lang.Byte"; prim = "B"; }
    else if (val instanceof ShortValue) { c = "java.lang.Short"; prim = "S"; }
    else if (val instanceof LongValue) { c = "java.lang.Long"; prim = "J"; }
    else if (val instanceof FloatValue) { c = "java.lang.Float"; prim = "F"; }
    ClassType location = (ClassType) _getClass(c);
    for (int tries = 0; tries < OBJECT_COLLECTED_TRIES; tries++) {
      try {
        ObjectReference result;
        try {
          String valueOfSig = "(" + prim + ")L" + c.replace('.', '/') + ";";
          result = (ObjectReference) _invokeStaticMethod(thread, location, "valueOf",
                                                         valueOfSig, val);
        }
        catch (DebugException e) {
          
          debug.log("Can't invoke valueOf()", e);
          String consSig = "(" + prim + ")V";
          result = (ObjectReference) _invokeConstructor(thread, location, consSig, val);
        }
        
        result.disableCollection();
        if (!result.isCollected()) {
          toRelease.add(result);
          return result;
        }
      }
      catch (ObjectCollectedException e) {  }
    }
    throw new DebugException("Ran out of OBJECT_COLLECTED_TRIES");
  }
  
  
  
  private PrimitiveValue _unbox(ObjectReference val, ThreadReference thread) throws DebugException {
    if (val == null) { throw new DebugException("Value can't be unboxed"); }
    String type = val.referenceType().name();
    String m = null;
    String sig = null;
    if (type.equals("java.lang.Boolean")) { m = "booleanValue"; sig = "()Z"; }
    else if (type.equals("java.lang.Integer")) { m = "intValue"; sig = "()I"; }
    else if (type.equals("java.lang.Double")) { m = "doubleValue"; sig = "()D"; }
    else if (type.equals("java.lang.Character")) { m = "charValue"; sig = "()C"; }
    else if (type.equals("java.lang.Byte")) { m = "byteValue"; sig = "()B"; }
    else if (type.equals("java.lang.Short")) { m = "shortValue"; sig = "()S"; }
    else if (type.equals("java.lang.Long")) { m = "longValue"; sig = "()J"; }
    else if (type.equals("java.lang.Float")) { m = "floatValue"; sig = "()F"; }
    
    if (m == null) { throw new DebugException("Value can't be unboxed"); }
    else { return (PrimitiveValue) _invokeMethod(thread, val, m, sig); }
  }
  
  
  
  private ReferenceType _getClass(String name) throws DebugException {
    List<ReferenceType> classes = _vm.classesByName(name);
    if (classes.isEmpty()) {
      throw new DebugException("Class '" + name + "' is not loaded");
    }
    else {
      for (ReferenceType t : classes) {
        
        if (t.classLoader() == null) { return t; }
      }
      return classes.get(0);
    }
  }
  
  
  
   void currThreadSuspended() {
    assert EventQueue.isDispatchThread();
    try {
      _dumpVariablesIntoInterpreterAndSwitch();
      _switchToSuspendedThread();
    }
    catch(DebugException de) { throw new UnexpectedException(de); }
  }

  
   void currThreadSuspended(BreakpointRequest request) {
    assert EventQueue.isDispatchThread();
    try {
      _dumpVariablesIntoInterpreterAndSwitch();
      _switchToSuspendedThread(request);
    }
    catch(DebugException de) { throw new UnexpectedException(de); }
  }
  
  
  private void _switchToSuspendedThread() throws DebugException { _switchToSuspendedThread(null, true); }

  
  private void _switchToSuspendedThread(BreakpointRequest request) throws DebugException { _switchToSuspendedThread(request, true); }
  
  
  private void _switchToSuspendedThread(BreakpointRequest request, boolean updateWatches) throws DebugException {
    _log.log(this + " executing _switchToSuspendedThread()");
    _runningThread = null;
    if (updateWatches) _updateWatches();
    final ThreadReference currThread = _suspendedThreads.peek();
    _notifier.currThreadSuspended();
    
    
    
    _notifier.currThreadSet(new JPDAThreadData(currThread));

    boolean usedBreakpointLine = false;
    if (request!=null) {
      
      Object property = request.getProperty("debugAction");
      if (property != null && (property instanceof JPDABreakpoint)) {
        final JPDABreakpoint breakpoint = (JPDABreakpoint) property;
        breakpoint.update();
        scrollToSource(breakpoint, true);
        usedBreakpointLine = true;
      }
    }
    if (!usedBreakpointLine) {
      try {
        if (currThread.frameCount() > 0) {
          final List<File> files = new ArrayList<File>();
          for(OpenDefinitionsDocument odd: _model.getLLOpenDefinitionsDocuments()){ files.add(odd.getRawFile()); }
          scrollToSource(getLLLocation(currThread.frame(0).location(), files));
        }
      }
      catch (IncompatibleThreadStateException itse) {
        throw new UnexpectedException(itse);
      }
    }
  }
  
  
  private String _getUniqueThreadName(ThreadReference thread) {
    return Long.toString(thread.uniqueID());
  }
  
  
  private void _copyVariablesFromInterpreter() throws DebugException {
    
    
    List<ObjectReference> toRelease = new LinkedList<ObjectReference>();
    try {
      
      
      for (LocalVariable var : _runningThread.frame(0).visibleVariables()) {
        Value oldVal = _runningThread.frame(0).getValue(var);
        StringReference name = _mirrorString(var.name(), toRelease);
        ArrayReference wrappedVal =
          (ArrayReference) _invokeMethod(_runningThread, _interpreterJVM, "getVariable",
                                         GET_VARIABLE_SIG, name);
        if ((wrappedVal!=null) && (wrappedVal.length() == 1)) { 
          try {
            Value val = wrappedVal.getValue(0);
            if (var.type() instanceof PrimitiveType) {
              try { val = _unbox((ObjectReference) val, _runningThread); }
              catch (DebugException e) { error.log("Can't unbox variable", e); }
            }
            if ((oldVal==null) || (!oldVal.equals(val))) {
              try { _runningThread.frame(0).setValue(var, val); }
              catch (InvalidTypeException e) { error.log("Can't set variable", e); }
              catch (ClassNotLoadedException e) { error.log("Can't set variable", e); }
            }
          }
          catch (ClassNotLoadedException e) {  }
        }
      }
    }
    catch (AbsentInformationException e) {  }
    catch (IncompatibleThreadStateException e) { throw new DebugException(e); }
    finally {
      for (ObjectReference ref : toRelease) { ref.enableCollection(); }
    }
  }
  
  
  private void _removeAllDebugInterpreters() {
    DefaultInteractionsModel interactionsModel = _model.getInteractionsModel();
    String oldInterpreterName;
    if (_runningThread != null) {
      oldInterpreterName = _getUniqueThreadName(_runningThread);
      interactionsModel.removeInterpreter(oldInterpreterName);
    }
    while (!_suspendedThreads.isEmpty()) {
      ThreadReference threadRef = _suspendedThreads.pop();
      oldInterpreterName = _getUniqueThreadName(threadRef);
      interactionsModel.removeInterpreter(oldInterpreterName);
    }
  }
  
  
  private void _removeCurrentDebugInterpreter(boolean fromStep) {
    DefaultInteractionsModel interactionsModel =
      _model.getInteractionsModel();
    
    if (fromStep || _suspendedThreads.isEmpty()) {
      interactionsModel.setToDefaultInterpreter();
    }
    else {
      ThreadReference threadRef = _suspendedThreads.peek();
      _switchToInterpreterForThreadReference(threadRef);
    }
    String oldInterpreterName = _getUniqueThreadName(_runningThread);
    interactionsModel.removeInterpreter(oldInterpreterName);
  }
  
  
  private void _currThreadResumed() throws DebugException {
    _log.log(this + " is executing _currThreadResumed()");
    EventQueue.invokeLater(new Runnable() { public void run() { _notifier.currThreadResumed(); } });
  }
  
  
  private void _switchToInterpreterForThreadReference(ThreadReference threadRef) {
    String threadName = _getUniqueThreadName(threadRef);
    String prompt = _getPromptString(threadRef);
    _model.getInteractionsModel().setActiveInterpreter(threadName, prompt);
  }
  
  void threadStarted() {
    EventQueue.invokeLater(new Runnable() { public void run() { _notifier.threadStarted(); } });
  }
  
  
 void currThreadDied() throws DebugException {
    assert EventQueue.isDispatchThread();
    printMessage("The current thread has finished.");
    _runningThread = null;
    
    _updateWatches();
    
    if (_suspendedThreads.size() > 0) {
      ThreadReference thread = _suspendedThreads.peek();
      _switchToInterpreterForThreadReference(thread);
      
      try {
        if (thread.frameCount() <= 0) {
          printMessage("Could not scroll to source for " + thread.name() + ". It has no stackframes.");
        }
        else scrollToSource(thread.frame(0).location());
      }
      catch(IncompatibleThreadStateException e) { throw new UnexpectedException(e); }
      
      
      
      _switchToSuspendedThread();
    }
    _notifier.currThreadDied();
  }
  
  void nonCurrThreadDied() {
    EventQueue.invokeLater(new Runnable() { public void run() { _notifier.nonCurrThreadDied(); } }); 
  }
  
  
  void notifyDebuggerShutdown() {
    EventQueue.invokeLater(new Runnable() { public void run() { _notifier.debuggerShutdown(); } });
  }
  
  
  void notifyDebuggerStarted() {
    EventQueue.invokeLater(new Runnable() { public void run() { _notifier.debuggerStarted(); } });
  }
  
  
  void notifyStepRequested() {
    EventQueue.invokeLater(new Runnable() { public void run() { _notifier.stepRequested(); } });
  }
  
  
  private static Value _invokeMethod(ThreadReference thread, ObjectReference receiver, String name,
                                     String signature, Value... args) throws DebugException {
    try {
      ClassType c = (ClassType) receiver.referenceType();
      Method m = c.concreteMethodByName(name, signature);
      if (m == null) { throw new DebugException("Cannot find method '" + name + "'"); }
      return receiver.invokeMethod(thread, m, Arrays.asList(args),
                                   ObjectReference.INVOKE_SINGLE_THREADED);
    }
    catch (ClassNotPreparedException e) { throw new DebugException(e); }
    catch (IllegalArgumentException e) { throw new DebugException(e); }
    catch (ClassNotLoadedException e) { throw new DebugException(e); }
    catch (IncompatibleThreadStateException e) { throw new DebugException(e); }
    catch (InvocationException e) { throw new DebugException(e); }
    catch (InvalidTypeException e) { throw new DebugException(e); }
  }
  
  
  private static Value _invokeStaticMethod(ThreadReference thread, ClassType location, String name,
                                           String signature, Value... args) throws DebugException {
    try {
      Method m = location.concreteMethodByName(name, signature);
      if (m == null) { throw new DebugException("Cannot find method '" + name + "'"); }
      return location.invokeMethod(thread, m, Arrays.asList(args),
                                   ClassType.INVOKE_SINGLE_THREADED);
    }
    catch (ClassNotPreparedException e) { throw new DebugException(e); }
    catch (IllegalArgumentException e) { throw new DebugException(e); }
    catch (ClassNotLoadedException e) { throw new DebugException(e); }
    catch (IncompatibleThreadStateException e) { throw new DebugException(e); }
    catch (InvocationException e) { throw new DebugException(e); }
    catch (InvalidTypeException e) { throw new DebugException(e); }
  }
  
  
  private static Value _invokeConstructor(ThreadReference thread, ClassType location,
                                          String signature, Value... args) throws DebugException {
    try {
      Method m = location.concreteMethodByName("<init>", signature);
      if (m == null) { throw new DebugException("Cannot find requested constructor"); }
      return location.newInstance(thread, m, Arrays.asList(args), ClassType.INVOKE_SINGLE_THREADED);
    }
    catch (ClassNotPreparedException e) { throw new DebugException(e); }
    catch (IllegalArgumentException e) { throw new DebugException(e); }
    catch (ClassNotLoadedException e) { throw new DebugException(e); }
    catch (IncompatibleThreadStateException e) { throw new DebugException(e); }
    catch (InvocationException e) { throw new DebugException(e); }
    catch (InvalidTypeException e) { throw new DebugException(e); }
  }
  
  
  private static Value _getStaticField(ReferenceType location, String name) throws DebugException {
    try {
      Field f = location.fieldByName(name);
      if (f == null) { throw new DebugException("Cannot find field '" + name + "'"); }
      return location.getValue(f);
    }
    catch (ClassNotPreparedException e) { throw new DebugException(e); }
  }
  
  
  
  
  
  
  private static class RandomAccessStack extends Stack<ThreadReference> {
    
    public ThreadReference peekAt(int i) { return get(i); }
    
    public ThreadReference remove(long id) throws NoSuchElementException {
      synchronized(this) {
        for (int i = 0; i < size(); i++) {
          if (get(i).uniqueID() == id) {
            ThreadReference t = get(i);
            remove(i);
            return t;
          }
        }
      }
      
      throw new NoSuchElementException("Thread " + id + " not found in debugger suspended threads stack!");
    }
    
    public synchronized boolean contains(long id) {
      for (int i = 0; i < size(); i++) {
        if (get(i).uniqueID() == id) return true;
      }
      return false;
    }
    
    public boolean isEmpty() { return empty(); }
  }
  
  
  
  public LanguageLevelStackTraceMapper getLLSTM(){
    
    return _model.getCompilerModel().getLLSTM();
  }
  
  
  protected static class DelegatingLocation implements Location {
    protected Location _delegee;
    protected String _sourceName;
    protected String _sourcePath;
    protected int _lineNumber;
    public DelegatingLocation(String sourceName, int lineNumber, Location delegee) {
      _sourceName = sourceName;
      try {
        _sourcePath = delegee.sourcePath();
        int pos = _sourcePath.lastIndexOf(File.separator);
        if (pos>=0) {
          _sourcePath = _sourcePath.substring(0, pos) + File.separator +_sourceName;
        }
        else {
          _sourcePath = _sourceName;
        }
      }
      catch(AbsentInformationException e) {
        _sourcePath = null;
      }
      _lineNumber = lineNumber;
      _delegee = delegee;
    }
    public long codeIndex() { return _delegee.codeIndex(); }
    public ReferenceType declaringType() { return _delegee.declaringType(); }
    public boolean equals(Object obj) {
      if (!(obj instanceof DelegatingLocation)) return false;
      DelegatingLocation other = (DelegatingLocation)obj;
      return _sourceName.equals(other._sourceName)
        && (_lineNumber==other._lineNumber)
        && _delegee.equals(other._delegee); 
    }
    public int hashCode() { return _delegee.hashCode(); }
    public int lineNumber() { return _lineNumber; }
    public int lineNumber(String stratum) { return _lineNumber;  }
    public Method method() { return _delegee.method(); }
    public String sourceName() { return _sourceName; }
    public String sourceName(String stratum) { return _sourceName;  }
    public String sourcePath() throws AbsentInformationException {
      if (_sourcePath!=null) return _sourcePath;
      else return _delegee.sourcePath();
    }
    public String sourcePath(String stratum) throws AbsentInformationException {
      if (_sourcePath!=null) return _sourcePath;
      else return _delegee.sourcePath(); 
    }
    public String toString() { return _delegee.toString(); }
    public VirtualMachine virtualMachine() { return _delegee.virtualMachine(); } 
    public int compareTo(Location o) { return _delegee.compareTo(o); }
  }
}
