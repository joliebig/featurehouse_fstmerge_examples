

package edu.rice.cs.plt.io;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.io.InterruptedIOException;


public class ExpandingCharBuffer extends ExpandingBuffer<char[]> {

  
  private boolean _eof;
  
  public ExpandingCharBuffer() {
    super();
    _eof = false;
  }
  
  
  public synchronized void end() { _eof = true; notifyAll(); }
  
  public synchronized boolean isEnded() { return _eof; }
  
  protected char[] allocateBuffer(int size) { return new char[size]; }
  
  
  public DirectWriter writer() {
    return new DirectWriter() {
      @Override public void close() {}
      
      @Override public void flush() {}
      
      @Override public void write(int c) throws IOException {
        synchronized (ExpandingCharBuffer.this) {
          if (_eof) { throw new IOException("Buffer has been ended"); }
          allocate();
          lastBuffer()[lastIndex()] = (char) c;
          recordWrite(1);
          ExpandingCharBuffer.this.notifyAll();
        }
      }
      
      @Override public void write(char[] cbuf) throws IOException { write(cbuf, 0, cbuf.length); }
      
      @Override public void write(char[] cbuf, int off, int chars) throws IOException {
        if (chars == 0) { return; }
        synchronized (ExpandingCharBuffer.this) {
          if (_eof) { throw new IOException("Buffer has been ended"); }
          while (chars > 0) {
            int space = allocate();
            int toWrite = (space > chars) ? chars : space;
            System.arraycopy(cbuf, off, lastBuffer(), lastIndex(), toWrite);
            recordWrite(toWrite);
            chars -= toWrite;
          }
          ExpandingCharBuffer.this.notifyAll();
        }
      }
      
      @Override public int write(Reader r, int chars) throws IOException {
        if (chars == 0) { return 0; }
        synchronized (ExpandingCharBuffer.this) {
          if (_eof) { throw new IOException("Buffer has been ended"); }
          int charsRead = 0;
          int totalRead = 0;
          while (chars > 0 && charsRead >= 0) {
            int space = allocate();
            charsRead = r.read(lastBuffer(), lastIndex(), space);
            if (charsRead >= 0) {
              recordWrite(charsRead);
              chars -= charsRead;
              totalRead += charsRead;
            }
          }
          ExpandingCharBuffer.this.notifyAll();
          if (totalRead == 0) { return -1; }
          else { return totalRead; }
        }
      }
      
      @Override public int write(Reader r, int chars, int bufferSize) throws IOException {
        return write(r, chars);
      }
      
      @Override public int write(Reader r, int chars, char[] buffer) throws IOException {
        return write(r, chars);
      }
      
      @Override public int writeAll(Reader r) throws IOException {
        synchronized (ExpandingCharBuffer.this) {
          int charsRead;
          long totalRead = 0;
          do {
            int space = allocate();
            charsRead = r.read(lastBuffer(), lastIndex(), space);
            if (charsRead >= 0) {
              recordWrite(charsRead);
              totalRead += charsRead;
            }
          } while (charsRead >= 0);
          ExpandingCharBuffer.this.notifyAll();
          
          if (totalRead == 0) { return -1; }
          else if (totalRead > Integer.MAX_VALUE) { return Integer.MAX_VALUE; }
          else { return (int) totalRead; }
        }
      }
      
      @Override public int writeAll(Reader r, int bufferSize) throws IOException {
        return writeAll(r);
      }
      
      @Override public int writeAll(Reader r, char[] buffer) throws IOException {
        return writeAll(r);
      }
      
    };
  }
  
  
  
  public DirectReader reader() {
    return new DirectReader() {
      @Override public void close() {}
      
      @Override public boolean ready() { return !isEmpty(); }

      @Override public int read() throws IOException {
        synchronized (ExpandingCharBuffer.this) {
          waitForInput();
          if (isEmpty()) { return -1; }
          else {
            char result = firstBuffer()[firstIndex()];
            recordRead(1);
            deallocate();
            return result;
          }
        }
      }
      
      @Override public int read(char[] cbuf) throws IOException { return read(cbuf, 0, cbuf.length); }
      
      @Override public int read(char[] cbuf, int offset, int chars) throws IOException {
        if (chars <= 0) { return 0; }
        synchronized (ExpandingCharBuffer.this) {
          waitForInput();
          if (isEmpty()) { return -1; }
          else {
            int totalRead = 0;
            while (chars > 0 && !isEmpty()) {
              int inFirstBuffer = elementsInFirstBuffer();
              int toRead = (inFirstBuffer > chars) ? chars : inFirstBuffer;
              System.arraycopy(firstBuffer(), firstIndex(), cbuf, offset, toRead);
              recordRead(toRead);
              chars -= toRead;
              totalRead += toRead;
              deallocate();
            }
            return totalRead;
          }
        }
      }
      
      @Override public int read(Writer w, int chars) throws IOException {
        if (chars <= 0) { return 0; }
        synchronized (ExpandingCharBuffer.this) {
          waitForInput();
          if (isEmpty()) { return -1; }
          else {
            int totalRead = 0;
            while (chars > 0 && !isEmpty()) {
              int inFirstBuffer = elementsInFirstBuffer();
              int toRead = (inFirstBuffer > chars) ? chars : inFirstBuffer;
              w.write(firstBuffer(), firstIndex(), toRead);
              recordRead(toRead);
              chars -= toRead;
              totalRead += toRead;
              deallocate();
            }
            return totalRead;
          }
        }
      }
      
      @Override public int read(Writer w, int chars, int bufferSize) throws IOException {
        return read(w, chars);
      }
      
      @Override public int read(Writer w, int chars, char[] buffer) throws IOException {
        return read(w, chars);
      }
      
      @Override public int readAll(Writer w) throws IOException {
        synchronized (ExpandingCharBuffer.this) {
          long totalRead = 0;
          do {
            waitForInput();
            while (!isEmpty()) {
              int toRead = elementsInFirstBuffer();
              w.write(firstBuffer(), firstIndex(), toRead);
              recordRead(toRead);
              totalRead += toRead;
              deallocate();
            }
          } while (!_eof);
          
          if (totalRead == 0) { return -1; }
          else if (totalRead > Integer.MAX_VALUE) { return Integer.MAX_VALUE; }
          else { return (int) totalRead; }
        }
      }
      
      @Override public int readAll(Writer w, int bufferSize) throws IOException { return readAll(w); }
      
      @Override public int readAll(Writer w, char[] buffer) throws IOException { return readAll(w); }

      @Override public long skip(long chars) throws IOException {
        if (chars <= 0) { return 0; }
        synchronized (ExpandingCharBuffer.this) {
          waitForInput();
          long size = size();
          if (chars > size) { chars = size; }
          recordRead(chars);
          while (deallocate()) {}
          return chars;
        }
      }
      
      
      private void waitForInput() throws InterruptedIOException {
        while (!_eof && isEmpty()) {
          try { ExpandingCharBuffer.this.wait(); }
          catch (InterruptedException e) { throw new InterruptedIOException(); }
        }
      }

    };
  }

}
