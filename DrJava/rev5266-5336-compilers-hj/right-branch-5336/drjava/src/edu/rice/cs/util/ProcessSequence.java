

package edu.rice.cs.util;

import java.io.*;

import edu.rice.cs.drjava.ui.DrJavaErrorHandler;



public class ProcessSequence extends Process {  
  
  protected ProcessCreator[] _creators;

  
  protected Process[] _processes;

  
  protected volatile int _index = 0;
  
  
  protected volatile boolean _aborted = false;
  
  
  protected StreamRedirectThread _stdOutRedirector;

  
  protected StreamRedirectThread _stdErrRedirector;

  
  protected PipedInputStream _combinedInputStream;
  
  
  protected PipedOutputStream _combinedStdOutStream;

  
  protected JoinInputStream _combinedInputJoinedWithDebugStream;

  
  protected PrintWriter _debugOutput;

  
  protected PipedInputStream _debugInputStream;
  protected PipedOutputStream _debugOutputStream;

  
  protected PipedInputStream _combinedErrorStream;
  
  
  protected PipedOutputStream _combinedStdErrStream;
  
  
  protected volatile OutputStream _combinedOutputStream;
  
  
  protected Thread _deathThread;
  
  
  public ProcessSequence(ProcessCreator[] pcs) {
    _creators = pcs;
    _processes = new Process[_creators.length];
    for(int i = 0; i < _processes.length; ++i) { _processes[i] = null; }
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
    
    _deathThread = new Thread(new Runnable() {
      public void run() {
        GeneralProcessCreator.LOG.log("ProcessSequence._deathThread running");
        boolean interrupted = false;
        
        while(_index < _processes.length) {
          GeneralProcessCreator.LOG.log("Waiting for process " + _index);
          do {
            interrupted = false;
            try {
              _processes[_index].waitFor();
            }
            catch(InterruptedException e) { interrupted = true; }
          } while(interrupted);
          GeneralProcessCreator.LOG.log("Process " + _index + " terminated");
          
          if (_index < _processes.length-1) {
            
            ++_index;
            try {
              _processes[_index] = _creators[_index].start();
              GeneralProcessCreator.LOG.log("Process " + _index + " started");
              connectProcess(_processes[_index]);
            }
            catch(IOException e) {
              GeneralProcessCreator.LOG.log("\nIOException in external process: " + e.getMessage() + "\nCheck your command line.\n");
              
              _debugOutput.println("\nIOException in external process: " + e.getMessage() + "\nCheck your command line.\n");
              _debugOutput.flush();
              _processes[_index] = DUMMY_PROCESS;
            }
          }
          else {
            ++_index;
            GeneralProcessCreator.LOG.log("Closing StdOut and StdErr streams.");
            try {
              stopAllRedirectors();
              _combinedStdOutStream.flush();
              _combinedStdOutStream.close();
              _combinedStdErrStream.flush();
              _combinedStdErrStream.close();
            }
            catch(IOException e) {  }
          }
        }
      }
    },"Process Sequence Death Thread");
    _index = 0;
    try {
      _processes[_index] = _creators[_index].start();
    }
    catch(IOException e) {
      GeneralProcessCreator.LOG.log("\nIOException in external process: " + e.getMessage() + "\nCheck your command line.\n");
      
      _processes[_index] = DUMMY_PROCESS;
      _debugOutput.println("\nIOException in external process: " + e.getMessage() + "\nCheck your command line.\n");
      _debugOutput.flush();
    }
    connectProcess(_processes[_index]);
    _deathThread.start();


  }
  
  
  public OutputStream getOutputStream() {
    return new BufferedOutputStream(new OutputStream() {
      public void write(int b) throws IOException {
        _combinedOutputStream.write(b);
      }
      public void flush() throws IOException {
        _combinedOutputStream.flush();
      }
      public void close() throws IOException {
        _combinedOutputStream.close();
      }
    });
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
      while((!_aborted) && (_processes[i] == null)) {
        try {
          
          Thread.sleep(100);
        }
        catch(InterruptedException e) {  }
      }
      if (_aborted) { return -1; }
      exitCode = _processes[i].waitFor();
    }
    stopAllRedirectors();
    return exitCode;
  }
  
  
  public int exitValue() {
    if (_aborted) { return -1; }
    if ((_index < _processes.length-1) || (_processes[_processes.length-1] == null)) {
      throw new IllegalThreadStateException("Process sequence has not terminated yet, exit value not available.");
    }
    
    
    return _processes[_processes.length-1].exitValue();
  }
  
  
  public void destroy() {
    _aborted = true;
    for(int i = 0; i < _processes.length; ++i) {
      if (_processes[i] != null) { _processes[i].destroy(); }
    }
    stopAllRedirectors();
  }
  
  
  protected void stopAllRedirectors() {
    _stdOutRedirector.setStopFlag();
    _stdErrRedirector.setStopFlag();
  }
  
  
  protected void connectProcess(Process p) {
    
    
    
    if (_stdOutRedirector == null) {
      _stdOutRedirector = new StreamRedirectThread("stdout Redirector " + _index,
                                                   p.getInputStream(),
                                                   _combinedStdOutStream,
                                                   false,
                                                   new ProcessSequenceThreadGroup(this),
                                                   true);
      _stdOutRedirector.start();
    }
    else {
      _stdOutRedirector.setInputStream(p.getInputStream());
    }
    if (_stdErrRedirector == null) {
      _stdErrRedirector = new StreamRedirectThread("stderr Redirector " + _index,
                                                   p.getErrorStream(),
                                                   _combinedStdErrStream,
                                                   false,
                                                   new ProcessSequenceThreadGroup(this),
                                                   true);
      _stdErrRedirector.start();
    }
    else {
      _stdErrRedirector.setInputStream(p.getErrorStream());
    }
    _combinedOutputStream = p.getOutputStream();
  }

  
  protected static class ProcessSequenceThreadGroup extends ThreadGroup {
    private PrintWriter _debugOut;
    public ProcessSequenceThreadGroup(ProcessSequence seq) {
      super("Process Sequence Thread Group");
      _debugOut = seq._debugOutput;
    }
    public void uncaughtException(Thread t, Throwable e) {
      if ((e instanceof StreamRedirectException) &&
          (e.getCause() instanceof IOException)) {
        _debugOut.println("\n\n\nAn exception occurred during the execution of the command line:\n" + 
                          e.toString() + "\n\n");
      }
      else {
        DrJavaErrorHandler.record(e);
      }
    }
  }

  
  protected static final Process DUMMY_PROCESS = new Process() {
    public void destroy() { }
    public int exitValue() { return -1; }
    public InputStream getErrorStream() { return new InputStream() {
      public int read() { return -1; }
    }; }
    public InputStream getInputStream() { return new InputStream() {
      public int read() { return -1; }
    }; }
    public OutputStream  getOutputStream() { return new OutputStream() {
      public void write(int b) { }
    }; }
    public int waitFor() { return -1; }
  };
}
