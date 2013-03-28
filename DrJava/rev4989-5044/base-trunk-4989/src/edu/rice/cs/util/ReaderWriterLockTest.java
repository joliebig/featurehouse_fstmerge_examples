

package edu.rice.cs.util;

import edu.rice.cs.drjava.DrJavaTestCase;


public class ReaderWriterLockTest extends DrJavaTestCase {

  protected ReaderWriterLock _lock;

  
  public void setUp() throws Exception {
    super.setUp();
    _lock = new ReaderWriterLock();
  }

  

  
  private int _notifyCount = 0;

  
  private final Object _notifyObject = new Object();

  
  private void _notify() {
    synchronized(_notifyObject) {
      _notifyCount--;
      if (_notifyCount <= 0) {
        _notifyObject.notify();
        _notifyCount = 0;
      }
    }
  }

  
  public void testMultipleReaders() throws InterruptedException {
    final StringBuilder buf = new StringBuilder();

    
    ReaderThread r1 = new PrinterReaderThread("r1 ", buf);
    ReaderThread r2 = new PrinterReaderThread("r2 ", buf);
    ReaderThread r3 = new PrinterReaderThread("r3 ", buf);

    
    _notifyCount = 3;

    
    synchronized(_notifyObject) {
      r1.start();
      r2.start();
      r3.start();
      _notifyObject.wait();
    }


  }

  
  public void testMultipleWriters() throws InterruptedException {
    final StringBuilder buf = new StringBuilder();

    
    WriterThread w1 = new PrinterWriterThread("w1 ", buf);
    WriterThread w2 = new PrinterWriterThread("w2 ", buf);
    WriterThread w3 = new PrinterWriterThread("w3 ", buf);

    
    _notifyCount = 3;

    
    synchronized(_notifyObject) {
      w1.start();
      w2.start();
      w3.start();
      _notifyObject.wait();
    }
    String output = buf.toString();
    

    
    assertTrue("w1 writes should happen in order", output.indexOf("w1 w1 w1 ") != -1);
    assertTrue("w2 writes should happen in order", output.indexOf("w2 w2 w2 ") != -1);
    assertTrue("w1 writes should happen in order", output.indexOf("w3 w3 w3 ") != -1);
  }

  
  public void testReaderMultipleReads() throws InterruptedException {
    
    _lock.startRead();
    _lock.startRead();
    _lock.endRead();
    _lock.endRead();

    
    
    _lock.startRead();
    Thread w = new Thread() {
      public void run() {
        synchronized(_lock) {
          _lock.notifyAll();
          _lock.startWrite();
          
          _lock.endWrite();
        }
      }
    };
    synchronized(_lock) {
      w.start();
      _lock.wait();
    }
    _lock.startRead();
    _lock.endRead();
    _lock.endRead();
  }

  
  public void testCannotWriteInARead() {
    try {
      _lock.startRead();
      _lock.startWrite();
      fail("Should have caused an IllegalStateException!");
    }
    catch (IllegalStateException ise) {
      
    }
  }

  
  public void testCannotWriteInAWrite() {
    try {
      _lock.startWrite();
      _lock.startWrite();
      fail("Should have caused an IllegalStateException!");
    }
    catch (IllegalStateException ise) {
      
    }
  }

  
  public void testCannotReadInAWrite() {
    try {
      _lock.startWrite();
      _lock.startRead();
      fail("Should have caused an IllegalStateException!");
    }
    catch (IllegalStateException ise) {
      
    }
  }


  
  public void testMultipleReadersAndWriters() throws InterruptedException {
    final StringBuilder buf = new StringBuilder();

    
    WriterThread w1 = new PrinterWriterThread("w1 ", buf);
    WriterThread w2 = new PrinterWriterThread("w2 ", buf);
    WriterThread w3 = new PrinterWriterThread("w3 ", buf);

    ReaderThread r1 = new PrinterReaderThread("r1 ", buf);
    ReaderThread r2 = new PrinterReaderThread("r2 ", buf);
    ReaderThread r3 = new PrinterReaderThread("r3 ", buf);
    ReaderThread r4 = new PrinterReaderThread("r4 ", buf);
    ReaderThread r5 = new PrinterReaderThread("r5 ", buf);

    
    _notifyCount = 8;

    
    synchronized(_notifyObject) {
      w1.start();
      w2.start();
      r1.start();
      r2.start();
      w3.start();
      r3.start();
      r4.start();
      r5.start();
      _notifyObject.wait();
    }
    String output = buf.toString();
    

    
    assertTrue("w1 writes should happen in order", output.indexOf("w1 w1 w1 ") != -1);
    assertTrue("w2 writes should happen in order",  output.indexOf("w2 w2 w2 ") != -1);
    assertTrue("w1 writes should happen in order", output.indexOf("w3 w3 w3 ") != -1);
  }


  
  public abstract class ReaderThread extends Thread {
    public abstract void read() throws Throwable;
    public void run() {
      _lock.startRead();
      try { read(); }
      catch (Throwable t) { t.printStackTrace(); }
      _lock.endRead();
    }
  }

  
  public abstract class WriterThread extends Thread {
    public abstract void write() throws Throwable;
    public void run() {
      _lock.startWrite();
      try { write(); }
      catch (Throwable t) { t.printStackTrace(); }
      _lock.endWrite();
    }
  }

  
  public class PrinterReaderThread extends ReaderThread {
    PrintCommand _command;
    public PrinterReaderThread(String msg, final StringBuilder buf) { _command = new PrintCommand(msg, buf); }
    public void read() { _command.print(); }
  }

  
  public class PrinterWriterThread extends WriterThread {
    PrintCommand _command;
    public PrinterWriterThread(String msg, final StringBuilder buf) { _command = new PrintCommand(msg, buf); }
    public void write() { _command.print(); }
  }

  
  public class PrintCommand {
    
    int _numIterations = 3;
    
    int _waitMillis = 5;
    
    final StringBuilder _buf;
    
    final String _msg;
    
    public PrintCommand(String msg, StringBuilder buf) {
      _msg = msg;
      _buf = buf;
    }
    
    public void print() {
      for (int i=0; i < _numIterations; i++) {
        _buf.append(_msg);
        try { Thread.sleep(_waitMillis); }
        catch (InterruptedException e) { _buf.append(e); }
      }
      _notify();
    }
  }
}
