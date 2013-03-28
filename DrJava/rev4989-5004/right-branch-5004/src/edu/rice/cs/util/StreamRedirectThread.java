

package edu.rice.cs.util;

import java.io.*;
import edu.rice.cs.plt.concurrent.CompletionMonitor;


public class StreamRedirectThread extends Thread {
  
  private Reader in;
  
  
  private final Writer out;
  
  
  private static final int BUFFER_SIZE = 2048;
  
  
  private volatile boolean stop = false;
  
  
  private volatile boolean close = true;
  
  
  private volatile boolean keepRunning = false;
  
  
  private volatile CompletionMonitor cm = new CompletionMonitor();
  
  
  public StreamRedirectThread(String name, InputStream in, OutputStream out, boolean close) {
    this(name,in,out,close,false);
  }
  
  
  public StreamRedirectThread(String name, InputStream in, OutputStream out, ThreadGroup tg) {
    this(name,in,out,true,tg,false);
  }
  
  public StreamRedirectThread(String name, InputStream in, OutputStream out, boolean close, ThreadGroup tg) {
    this(name,in,out,close,tg,false);
  }

  
  public StreamRedirectThread(String name, InputStream in, OutputStream out) {
    this(name,in,out,true,false);
  }
  
  
  public StreamRedirectThread(String name, InputStream in, OutputStream out, boolean close, boolean keepRunning) {
    super(name);
    this.keepRunning = keepRunning;
    this.close = close;
    this.in = new BufferedReader(new InputStreamReader(in));
    this.out = new BufferedWriter(new OutputStreamWriter(out));
    setPriority(Thread.MAX_PRIORITY - 1);
  }
  
  
  public StreamRedirectThread(String name, InputStream in, OutputStream out, ThreadGroup tg, boolean keepRunning) {
    this(name,in,out,true,tg,keepRunning);
  }
  
  
  public StreamRedirectThread(String name, InputStream in, OutputStream out, boolean close, ThreadGroup tg, boolean keepRunning) {
    super(tg,name);
    this.keepRunning = keepRunning;
    this.close = close;
    this.in = new BufferedReader(new InputStreamReader(in));
    this.out = new BufferedWriter(new OutputStreamWriter(out));
    setPriority(Thread.MAX_PRIORITY - 1);
  }
  
  
  public void setInputStream(InputStream in) {
    this.in = new BufferedReader(new InputStreamReader(in));
    cm.signal();
  }
  
  
  public void run() {
    do {
      try {
        char[] cbuf = new char[BUFFER_SIZE];
        int count;
        while ((!stop) && ((count = in.read(cbuf, 0, BUFFER_SIZE)) >= 0)) {
          try {
            out.write(cbuf, 0, count);
            out.flush();
          }
          catch (IOException exc) {
            GeneralProcessCreator.LOG.log("StreamRedirectThread "+getName()+" had IOException while writing: "+exc);
            throw new StreamRedirectException("An error occurred during stream redirection, while piping data into a process.",
                                              exc);
          }
        }
        GeneralProcessCreator.LOG.log("StreamRedirectThread "+getName()+" finished copying");
        out.flush();
        if (close) {
          in.close();
        }
      }
      catch (IOException exc) {
        GeneralProcessCreator.LOG.log("StreamRedirectThread "+getName()+" had IOException: "+exc);
        throw new StreamRedirectException("An error occurred during stream redirection, while piping data out of a process.",
                                          exc);
      }
      if (keepRunning) {
        
        while(!cm.attemptEnsureSignaled());
        cm.reset();
      }
    } while(keepRunning && !close);
    if (close) {
      try {
        out.close();
      }
      catch (IOException exc) {
        GeneralProcessCreator.LOG.log("StreamRedirectThread "+getName()+" had IOException: "+exc);
        throw new StreamRedirectException("An error occurred during stream redirection, while piping data out of a process.",
                                          exc);
      }
    }
  }
  
  
  public void setStopFlag() {
    stop = true;
  }
}
