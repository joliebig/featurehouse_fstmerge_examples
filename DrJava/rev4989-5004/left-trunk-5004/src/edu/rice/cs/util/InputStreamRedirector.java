

package edu.rice.cs.util;

import java.io.*;
import java.util.ArrayList;


public abstract class InputStreamRedirector extends InputStream {
  
  protected ArrayList<Character> _buffer;

  
  public InputStreamRedirector() { _buffer = new ArrayList<Character>(60); }

  
  protected abstract String _getInput() throws IOException;

  
  private void _readInputIntoBuffer() throws IOException {
    String input = _getInput();

    for(int i = 0; i < input.length(); i++) {
      _buffer.add(new Character(input.charAt(i)));
    }
  }

  
  public synchronized int read(byte[] b) throws IOException { return read(b, 0, b.length); }

  
  public synchronized int read(byte[] b, int off, int len) throws IOException {
    int numRead = 0;
    if (available() == 0) {
      _readInputIntoBuffer();
      if (available() == 0) return -1;
    }

    for(int i = off; i < off + len; i++) {
      if (available() == 0) break;
      else {
        b[i] = (byte) _buffer.remove(0).charValue();
        numRead++;
      }
    }
    return numRead;
  }

  
  public synchronized int read() throws IOException {
    if (available() == 0) {
      _readInputIntoBuffer();
      if (available() == 0) return -1;
    }
    return _buffer.remove(0).charValue();
  }

  
  public int available() { return _buffer.size(); }
}

