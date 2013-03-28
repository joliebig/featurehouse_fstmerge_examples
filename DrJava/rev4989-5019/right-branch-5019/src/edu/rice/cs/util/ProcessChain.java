

package edu.rice.cs.util;

import java.io.*;
import java.util.Set;
import java.util.HashSet;

import edu.rice.cs.drjava.ui.DrJavaErrorHandler;
import edu.rice.cs.util.JoinInputStream;



public class ProcessChain extends Process {
  
  public static final char PROCESS_SEPARATOR_CHAR = '#';

  
  public static final String PROCESS_SEPARATOR = String.valueOf(PROCESS_SEPARATOR_CHAR);

  
  public static final char PIPE_SEPARATOR_CHAR = '|';
  
  
  public static final String PIPE_SEPARATOR = String.valueOf(PIPE_SEPARATOR_CHAR);
  
  
  protected ProcessCreator[] _creators;

  
  protected Process[] _processes;
  
  
  protected boolean _aborted = false;
  
  
  protected Set<StreamRedirectThread> _redirectors = new HashSet<StreamRedirectThread>();

  
  protected PipedInputStream _combinedInputStream;
  
  
  protected PipedOutputStream _combinedStdOutStream;
  
  
  protected JoinInputStream _combinedInputJoinedWithDebugStream;

  
  protected PrintWriter _debugOutput;

  
  protected PipedInputStream _debugInputStream;
  protected PipedOutputStream _debugOutputStream;

  
  protected PipedInputStream _combinedErrorStream;
  
  
  protected PipedOutputStream _combinedStdErrStream;

  
  
  
  
  public ProcessChain(ProcessCreator[] pcs) {
    _creators = pcs;
    _processes = new Process[_creators.length];

    _combinedInputStream = new PipedInputStream();
    try {
      _combinedStdOutStream = new PipedOutputStream(_combinedInputStream);
      _combinedInputStream.connect(_combinedStdOutStream);
    }
    catch(IOException e) {  }
    
    _debugInputStream = new PipedInputStream();
    try {
      _debugOutputStream = new PipedOutputStream(_debugInputStream);
      _debugInputStream.connect(_debugOutputStream);
    }
    catch(IOException e) {  }
     _combinedInputJoinedWithDebugStream = new JoinInputStream(_combinedInputStream, _debugInputStream);
    _debugOutput = new PrintWriter(new OutputStreamWriter(_debugOutputStream));

    _combinedErrorStream = new PipedInputStream();
    try {
      _combinedStdErrStream = new PipedOutputStream(_combinedErrorStream);
      _combinedErrorStream.connect(_combinedStdErrStream);
    }
    catch(IOException e) {  }

    
    for(int i = 0; i < _processes.length; ++i) {

      try {
        _processes[i] = _creators[i].start();














      }
      catch(IOException e) {
        GeneralProcessCreator.LOG.log("\nIOException in external process: "+e.getMessage()+"\nCheck your command line.\n");
        
        _debugOutput.println("\nIOException in external process: "+e.getMessage()+"\nCheck your command line.\n");
        _debugOutput.flush();
        _aborted = true;
        destroy();
        return;
      }
    }
    
    for(int i = 0; i < _processes.length-1; ++i) {
      
      
      StreamRedirectThread r = new StreamRedirectThread("stdout Redirector "+i,
                                                        _processes[i].getInputStream(),
                                                        _processes[i+1].getOutputStream(),
                                                        new ProcessChainThreadGroup(this));
      _redirectors.add(r);
      r.start();
      r = new StreamRedirectThread("stderr Redirector "+i,
                                   _processes[i].getErrorStream(),
                                   _processes[i+1].getOutputStream(),
                                   new ProcessChainThreadGroup(this));
      _redirectors.add(r);
      r.start();
    }
    
    StreamRedirectThread r = new StreamRedirectThread("stdout Redirector "+(_processes.length-1),
                                                      _processes[_processes.length-1].getInputStream(),
                                                      _combinedStdOutStream,
                                                      new ProcessChainThreadGroup(this));
    _redirectors.add(r);
    r.start();
    r = new StreamRedirectThread("stderr Redirector "+(_processes.length-1),
                                 _processes[_processes.length-1].getErrorStream(),
                                 _combinedStdErrStream,
                                 new ProcessChainThreadGroup(this));
    _redirectors.add(r);
    r.start();


  }
  












































  
  
  public OutputStream getOutputStream() {
    if (_aborted) {
      return new OutputStream() {
        public void write(int b) throws IOException { }
      };
    }
    else {
      return new BufferedOutputStream(_processes[0].getOutputStream());
    }
  }
  
  
  public InputStream getErrorStream() {
    return _combinedErrorStream;
  }
  
  
  public InputStream getInputStream() {
    return _combinedInputJoinedWithDebugStream;
  }
  
  
  public int waitFor() throws InterruptedException {
    if (_aborted) { return -1; }
    int exitCode = 0;
    for(int i = 0; i < _processes.length; ++i) {
      exitCode = _processes[i].waitFor();












    }

    return exitCode;
  }
  
  
  public int exitValue() {
    if (_aborted) { return -1; }
    int exitCode = 0;
    
    
    
    
    for(int i = 0; i < _processes.length; ++i) {
      exitCode = _processes[i].exitValue();
    }
    return exitCode;
  }
  
  
  public void destroy() {
    _aborted = true;
    for(int i = 0; i < _processes.length; ++i) {
      _processes[i].destroy();
    }
    stopAllRedirectors();
  }
  
  
  protected void stopAllRedirectors() {
    for(StreamRedirectThread r: _redirectors) { r.setStopFlag(); }
    _redirectors.clear();
  }
  
  
  protected static class ProcessChainThreadGroup extends ThreadGroup {
    private ProcessChain _chain;
    private PrintWriter _debugOut;
    public ProcessChainThreadGroup(ProcessChain chain) {
      super("Process Chain Thread Group");
      _chain = chain;
      _debugOut = _chain._debugOutput;
    }
    public void uncaughtException(Thread t, Throwable e) {
      destroy();
      if ((e instanceof StreamRedirectException) &&
          (e.getCause() instanceof java.io.IOException)) {
        _debugOut.println("\n\n\nAn exception occurred during the execution of the command line:\n"+
                          e.toString()+"\n\n");
      }
      else {
        DrJavaErrorHandler.record(e);
      }
    }
  }
}
