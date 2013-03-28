

package edu.rice.cs.plt.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;


public class LinkedInputAndOutputStream {
  
  private final DirectInputStream _linkedInputStream;
  private final DirectOutputStream _linkedOutputStream;
  private long _readIndex; 
  private boolean _eof;
  private long _writeIndex;
  
  
  public LinkedInputAndOutputStream(final InputStream in, final OutputStream out) {
    _readIndex = 0;
    _writeIndex = 0;
    _eof = false;
    final ExpandingByteBuffer buffer = new ExpandingByteBuffer();
    final InputStream fromBuffer = buffer.inputStream();
    final DirectOutputStream toBuffer = buffer.outputStream();
    
    _linkedInputStream = new DirectInputStream() {
      
      public int read(byte[] bbuf, int offset, int bytes) throws IOException {
        int read = 0;
        
        if (!buffer.isEmpty()) {
          int bufferReadResult = fromBuffer.read(bbuf, offset, bytes);
          if (bufferReadResult < 0) {
            throw new IllegalStateException("Unexpected negative result from ExpandingByteBuffer read");
          }
          else if (bufferReadResult > 0) { read += bufferReadResult; bytes -= bufferReadResult; }
        }
        
        if (buffer.isEmpty() && bytes >= 0) {
          int readResult = in.read(bbuf, offset + read, bytes);
          if (readResult < 0) {
            _eof = true;
            return read > 0 ? read : readResult;
          }
          else {
            read += readResult;
            bytes -= readResult;
            _readIndex += readResult;
          }
        }
        return read;
      }
      
      public void close() throws IOException { in.close(); }
      
      public int available() throws IOException { return in.available(); }
    };
    
    _linkedOutputStream = new DirectOutputStream() {
      public void write(byte[] bbuf, int offset, int bytes) throws IOException {
        long newIndex = _writeIndex + bytes;
        while (newIndex > _readIndex) {
          
          if (_eof) { _readIndex = newIndex; }
          else {
            int bufferWriteResult = toBuffer.write(in, (int) (newIndex - _readIndex));
            if (bufferWriteResult < 0) { _eof = true; }
            else { _readIndex += bufferWriteResult; }
          }
        }
        out.write(bbuf, offset, bytes);
        _writeIndex = newIndex;
      }
      
      public void close() throws IOException { out.close(); }
      
      public void flush() throws IOException { out.flush(); }
    };
  }
  
  public DirectInputStream inputStream() { return _linkedInputStream; }
  
  public DirectOutputStream outputStream() { return _linkedOutputStream; }
    
}
