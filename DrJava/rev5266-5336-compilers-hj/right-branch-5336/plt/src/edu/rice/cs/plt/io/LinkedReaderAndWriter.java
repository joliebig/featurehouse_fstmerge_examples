

package edu.rice.cs.plt.io;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;


public class LinkedReaderAndWriter {
  
  private final DirectReader _linkedReader;
  private final DirectWriter _linkedWriter;
  private long _readIndex; 
  private boolean _eof;
  private long _writeIndex;
  
  
  public LinkedReaderAndWriter(final Reader r, final Writer w) {
    _readIndex = 0;
    _writeIndex = 0;
    _eof = false;
    final ExpandingCharBuffer buffer = new ExpandingCharBuffer();
    final Reader fromBuffer = buffer.reader();
    final DirectWriter toBuffer = buffer.writer();
    
    _linkedReader = new DirectReader() {
      public int read(char[] cbuf, int offset, int chars) throws IOException {
        int read = 0;
        
        if (!buffer.isEmpty()) {
          int bufferReadResult = fromBuffer.read(cbuf, offset, chars);
          if (bufferReadResult < 0) {
            throw new IllegalStateException("Unexpected negative result from ExpandingCharBuffer read");
          }
          else if (bufferReadResult > 0) { read += bufferReadResult; chars -= bufferReadResult; }
        }
        
        if (buffer.isEmpty() && chars >= 0) {
          int readResult = r.read(cbuf, offset + read, chars);
          if (readResult < 0) {
            _eof = true;
            return read > 0 ? read : readResult;
          }
          else {
            read += readResult;
            chars -= readResult;
            _readIndex += readResult;
          }
        }
        return read;
      }
      
      public void close() throws IOException { r.close(); }
      
      public boolean ready() throws IOException { return r.ready(); }
    };
    
    _linkedWriter = new DirectWriter() {
      public void write(char[] cbuf, int offset, int chars) throws IOException {
        long newIndex = _writeIndex + chars;
        while (newIndex > _readIndex) {
          
          if (_eof) { _readIndex = newIndex; }
          else {
            int bufferWriteResult = toBuffer.write(r, (int) (newIndex - _readIndex));
            if (bufferWriteResult < 0) { _eof = true; }
            else { _readIndex += bufferWriteResult; }
          }
        }
        w.write(cbuf, offset, chars);
        _writeIndex = newIndex;
      }
      
      public void close() throws IOException { w.close(); }
      
      public void flush() throws IOException { w.flush(); }
    };
  }
  
  public DirectReader reader() { return _linkedReader; }
  
  public DirectWriter writer() { return _linkedWriter; }
    
}
