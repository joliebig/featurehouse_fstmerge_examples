












package edu.rice.cs.util;

import java.io.*;

public class JoinInputStream extends InputStream { 
  
  public JoinInputStream(InputStream[] streams, int bufferSize) { 
    openedStreams = nReaders = streams.length;
    reader = new ReaderThread[nReaders];
    for (int i = 0; i < nReaders; i++) { 
      reader[i] = new ReaderThread(this, streams[i], bufferSize);
      reader[i].start();
    }
    currentReader = 0;
  }
  
  
  public JoinInputStream(InputStream one, InputStream two, int bufferSize) { 
    this(new InputStream[] { one, two}, bufferSize);
  }
  
  
  public JoinInputStream(InputStream one, InputStream two) {
    this(one, two, defaultBufferSize); 
  }
  
  
  public synchronized int read() throws IOException { 
    while (openedStreams != 0) { 
      for (int i = 0; i < nReaders; i++) { 
        ReaderThread rd = reader[currentReader];
        if (rd.available > 0) { 
          return rd.read();
        }
        currentReader += 1;
        if (currentReader == nReaders) { 
          currentReader = 0;
        }
      } 
      try { 
        wait();
      } catch(InterruptedException ex) { 
        break;
      }
    }
    return -1;
  }
  
  
  public synchronized int read(byte b[], int off, int len) throws IOException {
    while (openedStreams != 0) { 
      for (int i = 0; i < nReaders; i++) { 
        ReaderThread rd = reader[currentReader];
        if (rd.available > 0) { 
          return rd.read(b, off, len);
        }
        currentReader += 1;
        if (currentReader == nReaders) { 
          currentReader = 0;
        }
      }  
      try { 
        wait();
      } catch(InterruptedException ex) { 
        break;
      }
    }
    return -1;
  } 
  
  
  public void close() throws IOException { 
    for (int i = 0; i < nReaders; i++) { 
      if (reader[i].available >= 0) { 
        reader[i].close();
      }
    }
  }
  
  
  public final int getStreamIndex() { 
    return currentReader;
  }
  
  
  static public int defaultBufferSize = 4096;
  
  protected int            nReaders;
  protected ReaderThread[] reader;
  protected int            currentReader;
  protected int            openedStreams;
}

class ReaderThread extends Thread { 
  volatile int    available;
  volatile int    pos;
  byte[]          buffer; 
  InputStream     stream;
  IOException     exception;
  JoinInputStream monitor;
  
  ReaderThread(JoinInputStream monitor, InputStream stream, int bufferSize) { 
    this.stream = stream;
    this.monitor = monitor;
    buffer = new byte[bufferSize];
    available = 0;
    pos = 0;
    exception = null;
  }
  
  public synchronized void run() { 
    while (true) { 
      int len;
      try { 
        len = stream.read(buffer);
      } catch(IOException ex) { 
        exception = ex;
        len = -1;
      }
      synchronized (monitor) { 
        available = len;
        pos = 0;
        monitor.notify();
        if (len < 0) {  
          try { 
            stream.close();
          } catch(IOException ex) {}
          monitor.openedStreams -= 1;
          return;
        }
      }
      do { 
        try { 
          wait();
        } catch(InterruptedException ex) { 
          return;
        }
      } while(available != 0); 
    }
  }
  
  synchronized int read() throws IOException { 
    if (exception != null) { 
      throw exception;
    }
    int ch = buffer[pos] & 0xFF;
    if (++pos == available) { 
      available = 0;
      notify();
    }
    return ch;
  }
  
  synchronized int read(byte[] b, int off, int len) throws IOException { 
    if (exception != null) { 
      throw exception;
    }
    if (available - pos <= len) { 
      len = available - pos;
      available = 0;
      notify();
    }
    System.arraycopy(buffer, pos, b, off, len);
    pos += len;
    return len;
  }
  
  void close() throws IOException {
    
    interrupt();
    stream.close();
  }
}
